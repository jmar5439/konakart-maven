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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.konakart.al.KKAppEng;
import com.konakart.app.PriceFacetOptions;
import com.konakart.app.ProductSearch;
import com.konakart.appif.ProductSearchIf;
import com.konakart.bl.ConfigConstants;

/**
 * Gets called to perform a quick search based on the searchText parameter.
 */
public class QuickSearchAction extends BaseAction
{
    private static final long serialVersionUID = 1L;

    private String searchText;

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

            if (log.isDebugEnabled())
            {
                log.debug("Search text from application = " + searchText);
            }

            searchText = (searchText == null) ? "" : searchText;

            ProductSearchIf ps = new ProductSearch();
            ps.setReturnCategoryFacets(true);
            ps.setReturnManufacturerFacets(true);
            ps.setManufacturerId(ProductSearch.SEARCH_ALL);
            ps.setCategoryId(ProductSearch.SEARCH_ALL);
            ps.setWhereToSearch(0);
            // Set facets if not using slider. Use default values for now
            if (!kkAppEng.getConfigAsBoolean(ConfigConstants.PRICE_FACETS_SLIDER, true))
            {
                PriceFacetOptions pfo = new PriceFacetOptions();
                pfo.setCreateEmptyFacets(false);
                ps.setPriceFacetOptions(pfo);
            }


            ps.setSearchText(searchText);
            kkAppEng.getProductMgr().fetchProducts(null, ps);

            // Set the SEARCH_STRING customer tag for this customer
            if (searchText != null && searchText.length() > 0)
            {
                kkAppEng.getCustomerTagMgr().insertCustomerTag(TAG_SEARCH_STRING, searchText);
            }

            kkAppEng.getNav().set(kkAppEng.getMsg("header.navigation.results"), request);
            
            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }
    }

    /**
     * @return the searchText
     */
    public String getSearchText()
    {
        return searchText;
    }

    /**
     * @param searchText
     *            the searchText to set
     */
    public void setSearchText(String searchText)
    {
        this.searchText = searchText;
    }
}
