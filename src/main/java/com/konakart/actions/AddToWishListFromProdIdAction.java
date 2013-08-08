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
import com.konakart.appif.BasketIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.WishListItemIf;

/**
 * Adds a product to the wish list based on the parameter "prodId"
 */
public class AddToWishListFromProdIdAction extends AddToCartOrWishListBaseAction
{
    private static final long serialVersionUID = 1L;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        try
        {
            KKAppEng kkAppEng = this.getKKAppEng(request, response);

            setImgBase(kkAppEng.getImageBase());

            if (getProdId() == -1)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("No prodId parameter present");
                }
                return SUCCESS;
            }

            if (log.isDebugEnabled())
            {
                log.debug("Product Id of selected product from application = " + getProdId());
            }
            
            boolean canAdd = this.checkIfCanAddToWishList(kkAppEng, request, response);
            if (!canAdd)
            {
                return SUCCESS;
            }

            // Get the product from its Id
            kkAppEng.getProductMgr().fetchSelectedProduct(getProdId());
            ProductIf selectedProd = kkAppEng.getProductMgr().getSelectedProduct();
            if (selectedProd == null)
            {
                return SUCCESS;
            }

            /*
             * If the product has options then we can't add it to the wish list directly. We must
             * first go to the product details page so that the options can be selected.
             */
            if (selectedProd.getOpts() != null && selectedProd.getOpts().length > 0)
            {
                setRedirectURL(getRedirectURL() + "/SelectProd.action?prodId=" + getProdId());
                return SUCCESS;
            }

            // Common code for adding to wish list
            addToWishList(kkAppEng, selectedProd, null, -1);

            // Common code for setting messages
            this.setMsgs(kkAppEng);

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }
    }
    
    /**
     * @return the prodId
     */
    public int getProdId()
    {
        return prodId;
    }

    /**
     * @param prodId
     *            the prodId to set
     */
    public void setProdId(int prodId)
    {
        this.prodId = prodId;
    }

    /**
     * @return the items
     */
    public BasketIf[] getItems()
    {
        return items;
    }

    /**
     * @param items
     *            the items to set
     */
    public void setItems(BasketIf[] items)
    {
        this.items = items;
    }

    /**
     * @return the numberOfItems
     */
    public int getNumberOfItems()
    {
        return numberOfItems;
    }

    /**
     * @param numberOfItems
     *            the numberOfItems to set
     */
    public void setNumberOfItems(int numberOfItems)
    {
        this.numberOfItems = numberOfItems;
    }

    /**
     * @return the redirectURL
     */
    public String getRedirectURL()
    {
        return redirectURL;
    }

    /**
     * @param redirectURL
     *            the redirectURL to set
     */
    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    /**
     * @return the imgBase
     */
    public String getImgBase()
    {
        return imgBase;
    }

    /**
     * @param imgBase
     *            the imgBase to set
     */
    public void setImgBase(String imgBase)
    {
        this.imgBase = imgBase;
    }

    /**
     * @return the subtotalMsg
     */
    public String getSubtotalMsg()
    {
        return subtotalMsg;
    }

    /**
     * @param subtotalMsg
     *            the subtotalMsg to set
     */
    public void setSubtotalMsg(String subtotalMsg)
    {
        this.subtotalMsg = subtotalMsg;
    }

    /**
     * @return the formattedPrices
     */
    public String[] getFormattedPrices()
    {
        return formattedPrices;
    }

    /**
     * @param formattedPrices
     *            the formattedPrices to set
     */
    public void setFormattedPrices(String[] formattedPrices)
    {
        this.formattedPrices = formattedPrices;
    }

    /**
     * @return the wlItems
     */
    public WishListItemIf[] getWlItems()
    {
        return wlItems;
    }

    /**
     * @param wlItems
     *            the wlItems to set
     */
    public void setWlItems(WishListItemIf[] wlItems)
    {
        this.wlItems = wlItems;
    }

    /**
     * @return the wishListMsg
     */
    public String getWishListMsg()
    {
        return wishListMsg;
    }

    /**
     * @param wishListMsg
     *            the wishListMsg to set
     */
    public void setWishListMsg(String wishListMsg)
    {
        this.wishListMsg = wishListMsg;
    }

    /**
     * @return the emptyWishListMsg
     */
    public String getEmptyWishListMsg()
    {
        return emptyWishListMsg;
    }

    /**
     * @param emptyWishListMsg
     *            the emptyWishListMsg to set
     */
    public void setEmptyWishListMsg(String emptyWishListMsg)
    {
        this.emptyWishListMsg = emptyWishListMsg;
    }

    /**
     * @return the wishListTotal
     */
    public String getWishListTotal()
    {
        return wishListTotal;
    }

    /**
     * @param wishListTotal
     *            the wishListTotal to set
     */
    public void setWishListTotal(String wishListTotal)
    {
        this.wishListTotal = wishListTotal;
    }

}
