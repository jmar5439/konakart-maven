package com.konakart.app;

import com.konakart.appif.*;

/**
 *  The KonaKart Custom Engine - CreateWishList - Generated by CreateKKCustomEng
 */
@SuppressWarnings("all")
public class CreateWishList
{
    KKEng kkEng = null;

    /**
     * Constructor
     */
     public CreateWishList(KKEng _kkEng)
     {
         kkEng = _kkEng;
     }

     public int createWishList(String sessionId, WishListIf wishList) throws KKException
     {
         return kkEng.createWishList(sessionId, wishList);
     }
}
