//
// (c) 2013 DS Data Systems UK Ltd, All rights reserved.
//
// DS Data Systems and KonaKart and their respective logos, are 
// trademarks of DS Data Systems UK Ltd. All rights reserved.
//
// The information in this document is free software; you can redistribute 
// it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This software is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
//
package com.konakart.actions.ipn;

import java.security.MessageDigest;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.konakart.actions.gateways.BaseGatewayAction;
import com.konakart.al.KKAppEng;
import com.konakart.app.IpnHistory;
import com.konakart.app.KKException;
import com.konakart.app.OrderUpdate;
import com.konakart.appif.IpnHistoryIf;
import com.konakart.appif.OrderUpdateIf;
import com.konakart.appif.SSOTokenIf;
import com.konakart.bl.ConfigConstants;

/**
 * This class is an Action class for what to do when a payment notification callback is received
 * from Cyberpac.
 */
public class CyberpacAction extends BaseGatewayAction
{
    /**
     * The <code>Log</code> instance for this application.
     */
    protected Log log = LogFactory.getLog(CyberpacAction.class);

    // Module name must be the same as the class name although it can be all in lowercase
    private static String code = "cyberpac";

    // Cyberpac constants
    private static final String Ds_Amount = "Ds_Amount";

    private static final String Ds_Currency = "Ds_Currency";

    private static final String Ds_Order = "Ds_Order";

    private static final String Ds_MerchantCode = "Ds_MerchantCode";

    private static final String Ds_Signature = "Ds_Signature";

    private static final String Ds_Response = "Ds_Response";

    private static final String Ds_MerchantData = "Ds_MerchantData";

    private static final String Ds_AuthorisationCode = "Ds_AuthorisationCode";

    // Return codes and descriptions
    private static final int RET0 = 0;

    private static final String RET0_DESC = "Transaction OK";

    private static final int RET4 = -4;

    private static final String RET4_DESC = "There has been an unexpected exception. Please look at the log.";

    private static final int RET5 = -5;

    private static final String RET5_DESC = "The signature does not match the expected signature so the data may have been tampered with";

    // Order history comments. These comments are associated with the order.
    private static final String ORDER_HISTORY_COMMENT_OK = "Cyberpac payment successful. Cyberpac TransactionId = ";

    private static final String ORDER_HISTORY_COMMENT_KO = "Cyberpac payment not successful. Cyberpac Payment Status = ";

    private static final long serialVersionUID = 1L;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        // Cyberpac Callback parameters
        if (log.isDebugEnabled())
        {
            log.debug("*********** Cyberpac Callback");
        }

        // Create the outside of try / catch since they are needed in the case of a general
        // exception
        IpnHistoryIf ipnHistory = new IpnHistory();
        ipnHistory.setOrderId(-1);
        ipnHistory.setModuleCode(code);

        String sessionId = null;

        KKAppEng kkAppEng = null;

        try
        {

            if (request == null)
            {
                return null;
            }

            // Process the parameters sent in the callback
            StringBuffer sb = new StringBuffer();
            Enumeration<?> en = request.getParameterNames();
            while (en.hasMoreElements())
            {
                String paramName = (String) en.nextElement();
                String paramValue = request.getParameter(paramName);
                if (sb.length() > 0)
                {
                    sb.append("\n");
                }
                sb.append(paramName);
                sb.append(" = ");
                sb.append(paramValue);
            }

            if (log.isDebugEnabled())
            {
                log.debug("Cyberpac CallBack data:");
                log.debug(sb.toString());
            }

            // Some of the parameters returned by Cyberpac
            String ds_Signature = request.getParameter(Ds_Signature);
            String ds_Response = request.getParameter(Ds_Response);
            String ds_MerchantData = request.getParameter(Ds_MerchantData);
            String ds_AuthorisationCode = request.getParameter(Ds_AuthorisationCode);
            String ds_Amount = request.getParameter(Ds_Amount);
            String ds_Order = request.getParameter(Ds_Order);
            String ds_MerchantCode = request.getParameter(Ds_MerchantCode);
            String ds_Currency = request.getParameter(Ds_Currency);

            // Look up the SSO Token from the UUID
            String uuid = request.getParameter(ds_MerchantData);
            if (uuid == null)
            {
                throw new Exception(
                        "The callback from Cyberpac did not contain the 'Ds_MerchantCode' parameter.");
            }

            // Get an instance of the KonaKart engine
            kkAppEng = this.getKKAppEng(request, response);

            SSOTokenIf token = kkAppEng.getEng().getSSOToken(uuid, /* deleteToken */true);
            if (token == null)
            {
                throw new Exception("The SSOToken from the Cyberpac callback is null");
            }

            try
            {
                // Get the order id from custom1
                int orderId = Integer.parseInt(token.getCustom1());
                ipnHistory.setOrderId(orderId);
            } catch (Exception e)
            {
                throw new Exception("The SSOToken does not contain an order id");
            }

            // Use the session of the logged in user to initialise kkAppEng
            try
            {
                kkAppEng.getEng().checkSession(token.getSessionId());
            } catch (KKException e)
            {
                throw new Exception(
                        "The SessionId from the SSOToken in the Cyberpac Callback is not valid: "
                                + token.getSessionId());
            }

            // Log in the user
            kkAppEng.getCustomerMgr().loginBySession(token.getSessionId());
            sessionId = token.getSessionId();

            // See if we need to send an email, by looking at the configuration
            String sendEmailsConfig = kkAppEng.getConfig(ConfigConstants.SEND_EMAILS);
            boolean sendEmail = false;
            if (sendEmailsConfig != null && sendEmailsConfig.equalsIgnoreCase("true"))
            {
                sendEmail = true;
            }

            // Fill more details of the IPN history class
            ipnHistory.setGatewayResult(ds_Response);
            ipnHistory.setGatewayFullResponse(sb.toString());
            ipnHistory.setGatewayTransactionId(ds_AuthorisationCode);

            // Get integer value of ds_Response
            int ds_Response_int = -1;
            try
            {
                ds_Response_int = Integer.parseInt(ds_Response);
            } catch (Exception e)
            {
                throw new Exception("Ds_Response from Cyberpac does not contain a numeric value : "
                        + ds_Response);
            }

            /*
             * Flag an error if the digital signature doesn't match
             */
            String secretKey = kkAppEng.getConfig("MODULE_PAYMENT_CYBERPAC_SECRET_SIGNING_CODE");
            String stringToSign = ds_Amount + ds_Order + ds_MerchantCode + ds_Currency
                    + ds_Response + secretKey;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(stringToSign.getBytes("UTF8"));
            String hexEncodedDigest = (Hex.encodeHex(digest)).toString();
            
            if (log.isDebugEnabled())
            {
                StringBuffer str = new StringBuffer();
                str.append("Parameters to sign:").append("\n");
                str.append("ds_Amount        = ").append(ds_Amount).append("\n");
                str.append("ds_Order         = ").append(ds_Order).append("\n");
                str.append("ds_MerchantCode  = ").append(ds_MerchantCode).append("\n");
                str.append("ds_Currency      = ").append(ds_Currency).append("\n");
                str.append("ds_Response      = ").append(ds_Response).append("\n");
                str.append("secretKey        = ").append(secretKey).append("\n");
                str.append("String to sign   = ").append(stringToSign).append("\n");
                str.append("SHA-1 result     = ").append(hexEncodedDigest).append("\n");
                str.append("ds_Signature     = ").append(ds_Signature).append("\n");
                log.debug(str);
            }

            if (ds_Signature == null || !ds_Signature.equals(hexEncodedDigest))
            {
                ipnHistory.setKonakartResultDescription(RET5_DESC);
                ipnHistory.setKonakartResultId(RET5);
                kkAppEng.getEng().saveIpnHistory(sessionId, ipnHistory);
                return null;
            }

            OrderUpdateIf updateOrder = new OrderUpdate();
            updateOrder.setUpdatedById(kkAppEng.getActiveCustId());

            // If successful, we update the inventory as well as changing the state of the
            // order.
            String comment = null;
            if (ds_Response_int >= 0 && ds_Response_int <= 99)
            {
                comment = ORDER_HISTORY_COMMENT_OK + ds_AuthorisationCode;
                kkAppEng.getEng().updateOrder(sessionId, ipnHistory.getOrderId(),
                        com.konakart.bl.OrderMgr.PAYMENT_RECEIVED_STATUS, sendEmail, comment,
                        updateOrder);
                // If the order payment was approved we update the inventory
                kkAppEng.getEng().updateInventory(sessionId, ipnHistory.getOrderId());
                if (sendEmail)
                {
                    sendOrderConfirmationMail(kkAppEng, ipnHistory.getOrderId(), /* success */
                            true);
                }
            } else
            {
                comment = ORDER_HISTORY_COMMENT_KO + ds_Response;
                kkAppEng.getEng().updateOrder(sessionId, ipnHistory.getOrderId(),
                        com.konakart.bl.OrderMgr.PAYMENT_DECLINED_STATUS, sendEmail, comment,
                        updateOrder);
                if (sendEmail)
                {
                    sendOrderConfirmationMail(kkAppEng, ipnHistory.getOrderId(), /* success */
                            false);
                }
            }

            ipnHistory.setKonakartResultDescription(RET0_DESC);
            ipnHistory.setKonakartResultId(RET0);
            kkAppEng.getEng().saveIpnHistory(sessionId, ipnHistory);

            return null;

        } catch (Exception e)
        {
            try
            {
                if (sessionId != null)
                {
                    ipnHistory.setKonakartResultDescription(RET4_DESC);
                    ipnHistory.setKonakartResultId(RET4);
                    if (kkAppEng != null)
                    {
                        kkAppEng.getEng().saveIpnHistory(sessionId, ipnHistory);
                    }
                }
            } catch (KKException e1)
            {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return null;
        } finally
        {
            if (sessionId != null && kkAppEng != null)
            {
                try
                {
                    kkAppEng.getEng().logout(sessionId);
                } catch (KKException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
