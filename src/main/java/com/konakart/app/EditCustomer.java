package com.konakart.app;

import com.konakart.appif.*;

/**
 *  The KonaKart Custom Engine - EditCustomer - Generated by CreateKKCustomEng
 */
@SuppressWarnings("all")
public class EditCustomer
{
    KKEng kkEng = null;

    /**
     * Constructor
     */
     public EditCustomer(KKEng _kkEng)
     {
         kkEng = _kkEng;
     }

     public void editCustomer(String sessionId, CustomerIf cust) throws KKException
     {
         kkEng.editCustomer(sessionId, cust);
     }
}
