package com.konakart.app;

import com.konakart.appif.*;

/**
 *  The KonaKart Custom Engine - GetTag - Generated by CreateKKCustomEng
 */
@SuppressWarnings("all")
public class GetTag
{
    KKEng kkEng = null;

    /**
     * Constructor
     */
     public GetTag(KKEng _kkEng)
     {
         kkEng = _kkEng;
     }

     public TagIf getTag(int tagId, boolean getProdCount, int languageId) throws KKException
     {
         return kkEng.getTag(tagId, getProdCount, languageId);
     }
}
