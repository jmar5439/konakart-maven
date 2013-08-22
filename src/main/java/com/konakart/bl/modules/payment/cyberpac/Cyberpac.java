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
package com.konakart.bl.modules.payment.cyberpac;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.codec.binary.Hex;

import com.konakart.app.KKException;
import com.konakart.app.NameValue;
import com.konakart.app.Order;
import com.konakart.app.OrderTotal;
import com.konakart.app.PaymentDetails;
import com.konakart.app.SSOToken;
import com.konakart.appif.KKEngIf;
import com.konakart.appif.SSOTokenIf;
import com.konakart.bl.modules.BaseModule;
import com.konakart.bl.modules.ordertotal.OrderTotalMgr;
import com.konakart.bl.modules.payment.BasePaymentModule;
import com.konakart.bl.modules.payment.PaymentInfo;
import com.konakart.bl.modules.payment.PaymentInterface;

/**
 * La Caixa Cyberpac module
 */
public class Cyberpac extends BasePaymentModule implements PaymentInterface
{
    // Module name must be the same as the class name although it can be all in lowercase
    private static String code = "cyberpac";

    private static String bundleName = BaseModule.basePackage + ".payment.cyberpac.Cyberpac";

    private static HashMap<Locale, ResourceBundle> resourceBundleMap = new HashMap<Locale, ResourceBundle>();

    /** Hash Map that contains the static data */
    private static Map<String, StaticData> staticDataHM = Collections
            .synchronizedMap(new HashMap<String, StaticData>());

    private static String mutex = "cyberpacMutex";

    // Configuration Keys

    /**
     * Used to put the gateway online / offline
     */
    private final static String MODULE_PAYMENT_CYBERPAC_STATUS = "MODULE_PAYMENT_CYBERPAC_STATUS";

    /**
     * The Cyberpac zone, if greater than zero, should reference a GeoZone. If the DeliveryAddress
     * of the order isn't within that GeoZone, then we throw an exception
     */
    private final static String MODULE_PAYMENT_CYBERPAC_ZONE = "MODULE_PAYMENT_CYBERPAC_ZONE";

    /**
     * The order for displaying this payment gateway on the UI
     */
    private final static String MODULE_PAYMENT_CYBERPAC_SORT_ORDER = "MODULE_PAYMENT_CYBERPAC_SORT_ORDER";

    /**
     * The Cyberpac Url used to POST the payment request. "https://sis.sermepa.es/sis/realizarPago"
     */
    private final static String MODULE_PAYMENT_CYBERPAC_REQUEST_URL = "MODULE_PAYMENT_CYBERPAC_REQUEST_URL";

    /**
     * Callback username and password
     */
    private static final String MODULE_PAYMENT_CYBERPAC_CALLBACK_USERNAME = "MODULE_PAYMENT_CYBERPAC_CALLBACK_USERNAME";

    private static final String MODULE_PAYMENT_CYBERPAC_CALLBACK_PASSWORD = "MODULE_PAYMENT_CYBERPAC_CALLBACK_PASSWORD";

    /**
     * FUC code assigned to the merchant.
     */
    private final static String MODULE_PAYMENT_CYBERPAC_MERCHANT_CODE = "MODULE_PAYMENT_CYBERPAC_MERCHANT_CODE";

    /**
     * Merchant URL where transaction data will be sent by POST.
     */
    private final static String MODULE_PAYMENT_CYBERPAC_CALLBACK_URL = "MODULE_PAYMENT_CYBERPAC_CALLBACK_URL";

    /**
     * Customer is redirected to this URL when the payment completes successfully
     */
    private final static String MODULE_PAYMENT_CYBERPAC_MERCHANT_URL_OK = "MODULE_PAYMENT_CYBERPAC_MERCHANT_URL_OK";

    /**
     * Customer is redirected to this URL when the payment doesn't complete successfully
     */
    private final static String MODULE_PAYMENT_CYBERPAC_MERCHANT_URL_KO = "MODULE_PAYMENT_CYBERPAC_MERCHANT_URL_KO";

    /**
     * Terminal number assigned by bank
     */
    private final static String MODULE_PAYMENT_CYBERPAC_MERCHANT_TERMINAL_NUMBER = "MODULE_PAYMENT_CYBERPAC_MERCHANT_TERMINAL_NUMBER";

    /**
     * Defaults to 2 (confirmation)
     */
    private final static String MODULE_PAYMENT_CYBERPAC_TRANSACTION_TYPE = "MODULE_PAYMENT_CYBERPAC_TRANSACTION_TYPE";

    /**
     * The secret code used to digitally sign messages
     */
    private final static String MODULE_PAYMENT_CYBERPAC_SECRET_SIGNING_CODE = "MODULE_PAYMENT_CYBERPAC_SECRET_SIGNING_CODE";

    // Message Catalogue Keys
    private final static String MODULE_PAYMENT_CYBERPAC_TEXT_TITLE = "module.payment.cyberpac.text.title";

    private final static String MODULE_PAYMENT_CYBERPAC_TEXT_DESCRIPTION = "module.payment.cyberpac.text.description";

    private final static String MODULE_PAYMENT_CYBERPAC_CUSTOMER_MSG = "module.payment.cyberpac.customer.message";

    /**
     * Constructor
     * 
     * @param eng
     * 
     * @throws KKException
     */
    public Cyberpac(KKEngIf eng) throws KKException
    {
        super.init(eng);

        StaticData sd = staticDataHM.get(getStoreId());

        if (sd == null)
        {
            synchronized (mutex)
            {
                sd = staticDataHM.get(getStoreId());
                if (sd == null)
                {
                    setStaticVariables();
                }
            }
        }
    }

    /**
     * Sets some static variables during setup
     * 
     * @throws KKException
     * 
     */
    public void setStaticVariables() throws KKException
    {

        String val;
        int valInt;
        StaticData staticData = staticDataHM.get(getStoreId());
        if (staticData == null)
        {
            staticData = new StaticData();
            staticDataHM.put(getStoreId(), staticData);
        }

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_REQUEST_URL);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_REQUEST_URL must be set to the URL for"
                            + " sending the request to Cyberpac. (i.e. https://sis.sermepa.es/sis/services/realizarPago"
                            + " for a live system or https://sis-t.sermepa.es:25443/sis/realizarPago for testing");
        }
        staticData.setRequestUrl(val);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_CALLBACK_URL);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_CALLBACK_URL must be set to the URL of"
                            + " the action class used to manage the callback from Cyberpac");
        }
        staticData.setCallbackUrl(val);

        // Not mandatory since it may be configured in the Admin acct
        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_MERCHANT_URL_OK);
        staticData.setRedirectOKUrl(val);

        // Not mandatory since it may be configured in the Admin acct
        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_MERCHANT_URL_KO);
        staticData.setRedirectKOUrl(val);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_CALLBACK_USERNAME);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_CALLBACK_USERNAME must be set to a valid engine Username for the"
                            + " callback functionality.");
        }
        staticData.setCallbackUsername(val);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_CALLBACK_PASSWORD);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_CALLBACK_PASSWORD must be set to a valid engine Password for the"
                            + " callback functionality.");
        }
        staticData.setCallbackPassword(val);

        valInt = getConfigurationValueAsIntWithDefault(MODULE_PAYMENT_CYBERPAC_ZONE, 0);
        staticData.setZone(valInt);

        valInt = getConfigurationValueAsIntWithDefault(MODULE_PAYMENT_CYBERPAC_SORT_ORDER, 0);
        staticData.setSortOrder(valInt);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_MERCHANT_CODE);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_MERCHANT_CODE must be set to a valid merchant code.");
        }
        staticData.setMerchantCode(val);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_MERCHANT_TERMINAL_NUMBER);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_MERCHANT_TERMINAL_NUMBER must be set to a valid terminal number");
        }
        staticData.setTerminalNumber(val);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_TRANSACTION_TYPE);
        if (val == null || val.length() == 0)
        {
            val = "2";
        }
        staticData.setTransactionType(val);

        val = getConfigurationValue(MODULE_PAYMENT_CYBERPAC_SECRET_SIGNING_CODE);
        if (val == null || val.length() == 0)
        {
            throw new KKException(
                    "The Configuration MODULE_PAYMENT_CYBERPAC_SECRET_SIGNING_CODE must be set with the secret code"
                            + " used to digitally sign messages.");
        }
        staticData.setSecretSigningCode(val);

    }

    /**
     * Return a payment details object for Cyberpac
     * 
     * @param order
     * @param info
     * @return Returns information in a PaymentDetails object
     * @throws Exception
     */
    public PaymentDetails getPaymentDetails(Order order, PaymentInfo info) throws Exception
    {
        StaticData sd = staticDataHM.get(getStoreId());
        /*
         * The CyberpacZone zone, if greater than zero, should reference a GeoZone. If the
         * DeliveryAddress of the order isn't within that GeoZone, then we throw an exception
         */
        if (sd.getZone() > 0)
        {
            checkZone(info, sd.getZone());
        }

        // Get the scale for currency calculations
        int scale = new Integer(order.getCurrency().getDecimalPlaces()).intValue();

        // Get the resource bundle
        ResourceBundle rb = getResourceBundle(mutex, bundleName, resourceBundleMap,
                info.getLocale());
        if (rb == null)
        {
            throw new KKException("A resource file cannot be found for the country "
                    + info.getLocale().getCountry());
        }

        PaymentDetails pDetails = new PaymentDetails();
        pDetails.setCode(code);
        pDetails.setSortOrder(sd.getSortOrder());
        pDetails.setPaymentType(PaymentDetails.BROWSER_PAYMENT_GATEWAY);
        pDetails.setDescription(rb.getString(MODULE_PAYMENT_CYBERPAC_TEXT_DESCRIPTION));
        pDetails.setTitle(rb.getString(MODULE_PAYMENT_CYBERPAC_TEXT_TITLE));

        // Return now if the full payment details aren't required. This happens when the manager
        // just wants a list of payment gateways to display in the UI.
        if (info.isReturnDetails() == false)
        {
            return pDetails;
        }

        pDetails.setPostOrGet("post");
        pDetails.setRequestUrl(sd.getRequestUrl());

        List<NameValue> parmList = new ArrayList<NameValue>();

        /*
         * Parameters posted to gateway
         */

        // Total
        BigDecimal total = null;
        for (int i = 0; i < order.getOrderTotals().length; i++)
        {
            OrderTotal ot = (OrderTotal) order.getOrderTotals()[i];
            if (ot.getClassName().equals(OrderTotalMgr.ot_total))
            {
                total = ot.getValue().setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
        }
        if (total == null)
        {
            throw new KKException("An Order Total was not found in the order id = " + order.getId());
        }
        parmList.add(new NameValue("Ds_Merchant_Amount", total.toString()));

        // Currency
        String currCode = null;
        if (order.getCurrency().getCode().equalsIgnoreCase("EUR"))
        {
            currCode = "978";
        } else if (order.getCurrency().getCode().equalsIgnoreCase("USD"))
        {
            currCode = "840";
        } else if (order.getCurrency().getCode().equalsIgnoreCase("GBP"))
        {
            currCode = "826";
        } else if (order.getCurrency().getCode().equalsIgnoreCase("JPY"))
        {
            currCode = "392";
        } else
        {
            throw new KKException("The currency with code = " + order.getCurrency().getCode()
                    + " is not supported by the Cyberpac payment gateway.");
        }
        String ds_Merchant_Currency = currCode;
        parmList.add(new NameValue("Ds_Merchant_Currency", ds_Merchant_Currency));

        // Various
        String ds_Merchant_Order = Integer.toString(order.getId());
        parmList.add(new NameValue("Ds_Merchant_Order", ds_Merchant_Order));
        parmList.add(new NameValue("Ds_Merchant_ProductDescription", rb
                .getString(MODULE_PAYMENT_CYBERPAC_CUSTOMER_MSG) + " " + order.getId()));
        parmList.add(new NameValue("Ds_Merchant_Cardholder", order.getBillingName()));
        String ds_Merchant_MerchantCode = sd.getMerchantCode();
        parmList.add(new NameValue("Ds_Merchant_MerchantCode", ds_Merchant_MerchantCode));
        parmList.add(new NameValue("Ds_Merchant_MerchantURL", sd.getCallbackUrl()));
        if (sd.getRedirectKOUrl() != null && sd.getRedirectKOUrl().length() > 0)
        {
            parmList.add(new NameValue("Ds_Merchant_UrlKO", sd.getRedirectKOUrl()));
        }
        if (sd.getRedirectOKUrl() != null && sd.getRedirectOKUrl().length() > 0)
        {
            parmList.add(new NameValue("Ds_Merchant_UrlOK", sd.getRedirectOKUrl()));
        }

        String lang = "001"; // Default Spanish Castellano
        String langCode = order.getLocale().substring(0, 2);
        if (order.getLocale().equalsIgnoreCase("ca_ES"))
        {
            lang = "003";
        } else if (langCode.equalsIgnoreCase("en"))
        {
            lang = "002";
        } else if (langCode.equalsIgnoreCase("fr"))
        {
            lang = "004";
        } else if (langCode.equalsIgnoreCase("de"))
        {
            lang = "005";
        } else if (langCode.equalsIgnoreCase("it"))
        {
            lang = "007";
        } else if (langCode.equalsIgnoreCase("pt"))
        {
            lang = "009";
        } else if (order.getLocale().equalsIgnoreCase("eu_ES"))
        {
            lang = "013";
        } else if (langCode.equalsIgnoreCase("ru"))
        {
            lang = "014";
        }
        parmList.add(new NameValue("Ds_Merchant_ConsumerLanguage", lang));

        parmList.add(new NameValue("Ds_Merchant_Terminal", sd.getTerminalNumber()));

        if (sd.getTransactionType() != null && sd.getTransactionType().length() > 0)
        {
            parmList.add(new NameValue("Ds_Merchant_TransactionType", sd.getTransactionType()));
        } else
        {
            parmList.add(new NameValue("Ds_Merchant_TransactionType", "2"));
        }

        // Data passed to us in callback. Need to create a session
        SSOTokenIf ssoToken = new SSOToken();
        String sessionId = getEng().login(sd.getCallbackUsername(), sd.getCallbackPassword());
        if (sessionId == null)
        {
            throw new KKException(
                    "Unable to log into the engine using the Cyberpac Callback Username and Password");
        }
        ssoToken.setSessionId(sessionId);
        ssoToken.setCustom1(String.valueOf(order.getId()));
        // Save the SSOToken with a valid sessionId and the order id in custom1
        String uuid = getEng().saveSSOToken(ssoToken);
        parmList.add(new NameValue("Ds_Merchant_MerchantData", uuid));

        // Sign the data
        // Digest=SHA-1(Ds_Merchant_Amount + Ds_Merchant_Order +Ds_Merchant_MerchantCode
        // + DS_Merchant_Currency + SECRET CODE)
        String ds_Merchant_Amount = (total.multiply(new BigDecimal(100))).toString();
        String stringToSign = ds_Merchant_Amount + ds_Merchant_Order + ds_Merchant_MerchantCode
                + ds_Merchant_Currency + sd.getSecretSigningCode();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(stringToSign.getBytes("UTF8"));
        String hexEncodedDigest = (Hex.encodeHex(digest)).toString();
        parmList.add(new NameValue("Ds_Merchant_MerchantSignature", hexEncodedDigest));
        if (log.isDebugEnabled())
        {
            StringBuffer str = new StringBuffer();
            str.append("Parameters to sign:").append("\n");
            str.append("Ds_Merchant_Amount        = ").append(ds_Merchant_Amount).append("\n");
            str.append("Ds_Merchant_Order         = ").append(ds_Merchant_Order).append("\n");
            str.append("Ds_Merchant_MerchantCode  = ").append(ds_Merchant_MerchantCode).append("\n");
            str.append("Ds_Merchant_Currency      = ").append(ds_Merchant_Currency).append("\n");
            str.append("Secret Code               = ").append(sd.getSecretSigningCode()).append("\n");
            str.append("String to sign            = ").append(stringToSign).append("\n");
            str.append("SHA-1 result              = ").append(hexEncodedDigest).append("\n");
            log.debug(str);
        }

        // Put the parameters into an array
        NameValue[] nvArray = new NameValue[parmList.size()];
        parmList.toArray(nvArray);
        pDetails.setParameters(nvArray);

        if (log.isDebugEnabled())
        {
            log.debug(pDetails.toString());
        }

        return pDetails;
    }

    /**
     * Returns true or false
     * 
     * @throws KKException
     */
    public boolean isAvailable() throws KKException
    {
        return isAvailable(MODULE_PAYMENT_CYBERPAC_STATUS);
    }

    /**
     * Used to store the static data of this module
     */
    protected class StaticData
    {
        private int sortOrder = -1;

        private String requestUrl;

        private String callbackUrl;

        private String redirectOKUrl;

        private String redirectKOUrl;

        private String callbackUsername;

        private String callbackPassword;

        private String merchantCode;

        private String terminalNumber;

        private String transactionType;

        private String secretSigningCode;

        private int zone;

        /**
         * @return the sortOrder
         */
        public int getSortOrder()
        {
            return sortOrder;
        }

        /**
         * @param sortOrder
         *            the sortOrder to set
         */
        public void setSortOrder(int sortOrder)
        {
            this.sortOrder = sortOrder;
        }

        /**
         * @return the zone
         */
        public int getZone()
        {
            return zone;
        }

        /**
         * @param zone
         *            the zone to set
         */
        public void setZone(int zone)
        {
            this.zone = zone;
        }

        /**
         * @return the callbackUsername
         */
        public String getCallbackUsername()
        {
            return callbackUsername;
        }

        /**
         * @param callbackUsername
         *            the callbackUsername to set
         */
        public void setCallbackUsername(String callbackUsername)
        {
            this.callbackUsername = callbackUsername;
        }

        /**
         * @return the callbackPassword
         */
        public String getCallbackPassword()
        {
            return callbackPassword;
        }

        /**
         * @param callbackPassword
         *            the callbackPassword to set
         */
        public void setCallbackPassword(String callbackPassword)
        {
            this.callbackPassword = callbackPassword;
        }

        /**
         * @return the requestUrl
         */
        public String getRequestUrl()
        {
            return requestUrl;
        }

        /**
         * @param requestUrl
         *            the requestUrl to set
         */
        public void setRequestUrl(String requestUrl)
        {
            this.requestUrl = requestUrl;
        }

        /**
         * @return the callbackUrl
         */
        public String getCallbackUrl()
        {
            return callbackUrl;
        }

        /**
         * @param callbackUrl
         *            the callbackUrl to set
         */
        public void setCallbackUrl(String callbackUrl)
        {
            this.callbackUrl = callbackUrl;
        }

        /**
         * @return the redirectOKUrl
         */
        public String getRedirectOKUrl()
        {
            return redirectOKUrl;
        }

        /**
         * @param redirectOKUrl
         *            the redirectOKUrl to set
         */
        public void setRedirectOKUrl(String redirectOKUrl)
        {
            this.redirectOKUrl = redirectOKUrl;
        }

        /**
         * @return the redirectKOUrl
         */
        public String getRedirectKOUrl()
        {
            return redirectKOUrl;
        }

        /**
         * @param redirectKOUrl
         *            the redirectKOUrl to set
         */
        public void setRedirectKOUrl(String redirectKOUrl)
        {
            this.redirectKOUrl = redirectKOUrl;
        }

        /**
         * @return the merchantCode
         */
        public String getMerchantCode()
        {
            return merchantCode;
        }

        /**
         * @param merchantCode
         *            the merchantCode to set
         */
        public void setMerchantCode(String merchantCode)
        {
            this.merchantCode = merchantCode;
        }

        /**
         * @return the terminalNumber
         */
        public String getTerminalNumber()
        {
            return terminalNumber;
        }

        /**
         * @param terminalNumber
         *            the terminalNumber to set
         */
        public void setTerminalNumber(String terminalNumber)
        {
            this.terminalNumber = terminalNumber;
        }

        /**
         * @return the transactionType
         */
        public String getTransactionType()
        {
            return transactionType;
        }

        /**
         * @param transactionType
         *            the transactionType to set
         */
        public void setTransactionType(String transactionType)
        {
            this.transactionType = transactionType;
        }

        /**
         * @return the secretSigningCode
         */
        public String getSecretSigningCode()
        {
            return secretSigningCode;
        }

        /**
         * @param secretSigningCode
         *            the secretSigningCode to set
         */
        public void setSecretSigningCode(String secretSigningCode)
        {
            this.secretSigningCode = secretSigningCode;
        }
    }

}
