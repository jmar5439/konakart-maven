//
// (c) 2006 DS Data Systems UK Ltd, All rights reserved.
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

package com.konakart.actions;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.konakart.al.KKAppEng;
import com.konakart.al.ProductFilter;

/**
 * Filters the search based on price limits
 */
public class FilterSearchByPriceAction extends BaseAction
{
    private static final long serialVersionUID = 1L;

    private String priceFromStr;

    private String priceToStr;

    private long timestamp;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        try
        {

            int custId;

            KKAppEng kkAppEng = this.getKKAppEng(request, response);

            custId = this.loggedIn(request, response, kkAppEng, null);

            // Ensure we are using the correct protocol. Redirect if not.
            String redirForward = checkSSL(kkAppEng, request, custId, /* forceSSL */false);
            if (redirForward != null)
            {
                setupResponseForSSLRedirect(response, redirForward);
                return null;
            }

            ProductFilter filter = new ProductFilter();

            try
            {
                String from = request.getParameter("from");
                if (from != null && from.length() > 0)
                {
                    filter.setPriceFrom(new BigDecimal(from));
                }

                String to = request.getParameter("to");
                if (to != null && to.length() > 0)
                {
                    filter.setPriceTo(new BigDecimal(to));
                }

                String t = request.getParameter("t");
                if (t != null && t.length() > 0)
                {
                    timestamp = Long.parseLong(t);
                }

                if (getPriceFromStr() != null && getPriceFromStr().length() > 0)
                {
                    filter.setPriceFrom(new BigDecimal(getPriceFromStr()));
                }

                if (getPriceToStr() != null && getPriceToStr().length() > 0)
                {
                    filter.setPriceTo(new BigDecimal(getPriceToStr()));
                }

                kkAppEng.getProductMgr().filterProducts(filter, timestamp);
            } catch (Exception e)
            {
            }

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }

    }

    /**
     * @return the priceFromStr
     */
    public String getPriceFromStr()
    {
        return priceFromStr;
    }

    /**
     * @param priceFromStr
     *            the priceFromStr to set
     */
    public void setPriceFromStr(String priceFromStr)
    {
        this.priceFromStr = priceFromStr;
    }

    /**
     * @return the priceToStr
     */
    public String getPriceToStr()
    {
        return priceToStr;
    }

    /**
     * @param priceToStr
     *            the priceToStr to set
     */
    public void setPriceToStr(String priceToStr)
    {
        this.priceToStr = priceToStr;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

}
