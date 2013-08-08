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

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.Basket;
import com.konakart.app.KKException;
import com.konakart.app.WishListItem;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.OptionIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.WishListIf;
import com.konakart.appif.WishListItemIf;

/**
 * Base Action for add to cart or add to wish list actions
 */
public class AddToCartOrWishListBaseAction extends BaseAction
{
    private static final long serialVersionUID = 1L;

    protected int prodId = -1;

    // When not null, need to forward to prod details page to set options
    protected String redirectURL = null;

    // Array of basket items
    protected BasketIf[] items = null;

    // Array of wish list items
    protected WishListItemIf[] wlItems = null;

    // Array of formatted prices for basket items
    protected String[] formattedPrices = null;

    // Formatted Basket total
    protected String basketTotal = "";

    // Formatted WishList total
    protected String wishListTotal = "";

    // Number of items
    protected int numberOfItems = 0;

    // Base path for images
    protected String imgBase;

    // Message catalog strings
    protected String shoppingCartMsg;

    protected String checkoutMsg;

    protected String subtotalMsg;

    protected String quantityMsg;

    protected String wishListMsg;

    protected String emptyWishListMsg;

    protected void setMsgs(KKAppEng kkAppEng)
    {
        // Message catalog strings
        shoppingCartMsg = kkAppEng.getMsg("cart.tile.shoppingcart");
        checkoutMsg = kkAppEng.getMsg("common.checkout");
        subtotalMsg = kkAppEng.getMsg("common.subtotal");
        quantityMsg = kkAppEng.getMsg("cart.tile.quantity");
        wishListMsg = kkAppEng.getMsg("wishlist.tile.wishlist");
        emptyWishListMsg = kkAppEng.getMsg("wishlist.tile.empty");
    }

    protected void addToCart(KKAppEng kkAppEng, ProductIf prod, OptionIf[] opts)
            throws KKAppException, KKException
    {
        if (prod != null)
        {
            if (kkAppEng.getQuotaMgr().canAddToBasket(prod.getId(), opts) > 0)
            {
                BasketIf b = new Basket();
                b.setQuantity(1);
                b.setOpts(opts);
                b.setProductId(prod.getId());
                kkAppEng.getBasketMgr().addToBasket(b, /* refresh */true);
            }

            items = kkAppEng.getCustomerMgr().getCurrentCustomer().getBasketItems();
            if (items != null)
            {
                formattedPrices = new String[items.length];
                for (int i = 0; i < items.length; i++)
                {
                    BasketIf b = items[i];
                    if (kkAppEng.displayPriceWithTax())
                    {
                        formattedPrices[i] = kkAppEng.formatPrice(b.getFinalPriceIncTax());
                    } else
                    {
                        formattedPrices[i] = kkAppEng.formatPrice(b.getFinalPriceExTax());
                    }
                }
            }
            basketTotal = kkAppEng.getBasketMgr().getFormattedBasketTotal();
            numberOfItems = kkAppEng.getBasketMgr().getNumberOfItems();
        }
    }

    protected void addToWishList(KKAppEng kkAppEng, ProductIf prod, OptionIf[] opts, int wishListId)
            throws KKAppException, KKException
    {
        if (prod != null)
        {
            /*
             * Create a wish list item. Only the product id is required to save the wish list item.
             * WishListId defaults to -1 to pick up default wish list
             */
            WishListItemIf wli = new WishListItem();
            wli.setOpts(opts);
            wli.setProductId(prod.getId());
            wli.setWishListId(wishListId);
            // Medium priority
            wli.setPriority(3);
            // Quantity = 1
            wli.setQuantityDesired(1);
            // Add the item
            kkAppEng.getWishListMgr().addToWishList(wli);
            // Refresh the customer's wish list
            kkAppEng.getWishListMgr().fetchCustomersWishLists();

            kkAppEng.getWishListMgr().fetchCustomersWishLists();
            // Get number of items
            CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
            for (int i = 0; i < currentCustomer.getWishLists().length; i++)
            {
                WishListIf wishList = currentCustomer.getWishLists()[i];
                if (wishList.getListType() == com.konakart.al.WishListMgr.WISH_LIST_TYPE
                        && wishList.getWishListItems() != null)
                {
                    if (kkAppEng.displayPriceWithTax())
                    {
                        wishListTotal = kkAppEng.formatPrice(wishList.getFinalPriceIncTax());
                    } else
                    {
                        wishListTotal = kkAppEng.formatPrice(wishList.getFinalPriceExTax());
                    }
                    wlItems = wishList.getWishListItems();
                    if (wlItems != null)
                    {
                        numberOfItems = wlItems.length;
                        formattedPrices = new String[wlItems.length];
                        for (int j = 0; j < wlItems.length; j++)
                        {
                            WishListItemIf w = wlItems[j];
                            if (kkAppEng.displayPriceWithTax())
                            {
                                formattedPrices[j] = kkAppEng.formatPrice(w.getFinalPriceIncTax());
                            } else
                            {
                                formattedPrices[j] = kkAppEng.formatPrice(w.getFinalPriceExTax());
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean checkIfCanAddToWishList(KKAppEng kkAppEng, HttpServletRequest request,
            HttpServletResponse response) throws KKException, KKAppException
    {
        // Check to see whether the user is logged in if adding to wish list
        boolean allowWLBool = kkAppEng.getWishListMgr().allowWishListWhenNotLoggedIn();
        int custId = this.loggedIn(request, response, kkAppEng, "PreSelectProd");

        /*
         * If the customer isn't logged in, or is logged in but not registered then he may not be
         * allowed to add products to the wish list.
         */
        if (!allowWLBool
                && ((custId < 0) || (kkAppEng.getCustomerMgr().getCurrentCustomer() != null && kkAppEng
                        .getCustomerMgr().getCurrentCustomer().getType() == com.konakart.bl.CustomerMgr.CUST_TYPE_NON_REGISTERED_CUST)))
        {
            redirectURL = getRedirectURL(request) + "/LogIn.action";
            return false;
        }
        return true;
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

}
