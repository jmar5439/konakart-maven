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
import com.konakart.app.Option;
import com.konakart.appif.BasketIf;
import com.konakart.appif.OptionIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.WishListItemIf;

/**
 * Adds a product to the cart from the product details page using AJAX
 */
public class AddToCartOrWishListFromPostAction extends AddToCartOrWishListBaseAction
{
    private static final long serialVersionUID = 1L;

    private int[] optionId = new int[20];

    private int[] valueId = new int[20];

    private int[] type = new int[20];

    private int[] quantity = new int[20];

    private int numOptions = 0;

    private String addToWishList = "";
        
    private int wishListId=-1;

    public String execute()
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        try
        {

            // If true, we are adding to the wish list and not to the basket
            boolean addToWishListB = false;

            if (getAddToWishList() != null && getAddToWishList().equalsIgnoreCase("true"))
            {
                addToWishListB = true;
            }

            KKAppEng kkAppEng = this.getKKAppEng(request, response);
            
            setImgBase(kkAppEng.getImageBase());

            // Check to see whether the user is logged in if adding to wish list
            if (addToWishListB)
            {
                boolean canAdd = this.checkIfCanAddToWishList(kkAppEng, request, response);
                if (!canAdd)
                {
                    return SUCCESS;
                }
            }

            /*
             * Get the selected options from the form and place them in an array of option objects
             */
            OptionIf[] opts = null;
            if (getNumOptions() > 0)
            {
                OptionIf[] localOpts = new OptionIf[getNumOptions()];
                for (int i = 0; i < getNumOptions(); i++)
                {
                    OptionIf o = new Option();
                    o.setId(getOptionId()[i]);
                    o.setValueId(getValueId()[i]);
                    o.setType(getType()[i]);
                    o.setQuantity(getQuantity()[i]);
                    localOpts[i] = o;
                }
                opts = localOpts;
            }

            /*
             * Ensure that the product exists. It should already be the selected product-
             */
            ProductIf selectedProd = kkAppEng.getProductMgr().getSelectedProduct();
            if (selectedProd == null || selectedProd.getId() != getProdId())
            {
                kkAppEng.getProductMgr().fetchSelectedProduct(getProdId());
                selectedProd = kkAppEng.getProductMgr().getSelectedProduct();
                if (selectedProd == null)
                {
                    return SUCCESS;
                }
            }

            if (addToWishListB)
            {
                // Common code for adding to wish list
                addToWishList(kkAppEng, selectedProd, opts, wishListId);
            } else
            {
                // Common code for adding to cart
                this.addToCart(kkAppEng, selectedProd, opts);
            }

            // Common code for setting messages
            this.setMsgs(kkAppEng);

            return SUCCESS;

        } catch (Exception e)
        {
            return super.handleException(request, e);
        }

    }

    /**
     * @return the optionId
     */
    public int[] getOptionId()
    {
        return optionId;
    }

    /**
     * @param optionId
     *            the optionId to set
     */
    public void setOptionId(int[] optionId)
    {
        this.optionId = optionId;
    }

    /**
     * @return the valueId
     */
    public int[] getValueId()
    {
        return valueId;
    }

    /**
     * @param valueId
     *            the valueId to set
     */
    public void setValueId(int[] valueId)
    {
        this.valueId = valueId;
    }

    /**
     * @return the type
     */
    public int[] getType()
    {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(int[] type)
    {
        this.type = type;
    }

    /**
     * @return the quantity
     */
    public int[] getQuantity()
    {
        return quantity;
    }

    /**
     * @param quantity
     *            the quantity to set
     */
    public void setQuantity(int[] quantity)
    {
        this.quantity = quantity;
    }

    /**
     * @return the numOptions
     */
    public int getNumOptions()
    {
        return numOptions;
    }

    /**
     * @param numOptions
     *            the numOptions to set
     */
    public void setNumOptions(int numOptions)
    {
        this.numOptions = numOptions;
    }

    /**
     * @return the addToWishList
     */
    public String getAddToWishList()
    {
        return addToWishList;
    }

    /**
     * @param addToWishList
     *            the addToWishList to set
     */
    public void setAddToWishList(String addToWishList)
    {
        this.addToWishList = addToWishList;
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
     * @return the basketTotal
     */
    public String getBasketTotal()
    {
        return basketTotal;
    }

    /**
     * @param basketTotal
     *            the basketTotal to set
     */
    public void setBasketTotal(String basketTotal)
    {
        this.basketTotal = basketTotal;
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
     * @return the checkoutMsg
     */
    public String getCheckoutMsg()
    {
        return checkoutMsg;
    }

    /**
     * @param checkoutMsg
     *            the checkoutMsg to set
     */
    public void setCheckoutMsg(String checkoutMsg)
    {
        this.checkoutMsg = checkoutMsg;
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
     * @return the shoppingCartMsg
     */
    public String getShoppingCartMsg()
    {
        return shoppingCartMsg;
    }

    /**
     * @param shoppingCartMsg
     *            the shoppingCartMsg to set
     */
    public void setShoppingCartMsg(String shoppingCartMsg)
    {
        this.shoppingCartMsg = shoppingCartMsg;
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
     * @return the quantityMsg
     */
    public String getQuantityMsg()
    {
        return quantityMsg;
    }

    /**
     * @param quantityMsg
     *            the quantityMsg to set
     */
    public void setQuantityMsg(String quantityMsg)
    {
        this.quantityMsg = quantityMsg;
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

    /**
     * @return the wishListId
     */
    public int getWishListId()
    {
        return wishListId;
    }

    /**
     * @param wishListId the wishListId to set
     */
    public void setWishListId(int wishListId)
    {
        this.wishListId = wishListId;
    }
}
