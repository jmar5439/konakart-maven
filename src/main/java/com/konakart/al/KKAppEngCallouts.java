package com.konakart.al;

import com.konakart.app.FetchProductOptions;
import com.konakart.appif.FetchProductOptionsIf;

/**
 * Callouts to add custom code to the KKAppEng
 */
public class KKAppEngCallouts
{
    /**
     * Called at the start of startup
     * 
     * @param eng
     */
    public void beforeStartup(KKAppEng eng)
    {
         //System.out.println("Set product options for current customer");
         FetchProductOptionsIf fpo = new FetchProductOptions();
         fpo.setCalcQuantityForBundles(false);
         //fpo.setCatalogId("cat1");
         //fpo.setUseExternalPrice(true);
         //fpo.setUseExternalQuantity(true);
         eng.setFetchProdOptions(fpo);
    }

    /**
     * Called at the end of startup
     * 
     * @param eng
     */
    public void afterStartup(KKAppEng eng)
    {
    }

    /**
     * Called at the end of the RefreshCachedData method of KKAppEng
     * 
     * @param eng
     */
    public void afterRefreshCaches(KKAppEng eng)
    {
    }

    /**
     * Called by the CustomerMgr after a login has been successful
     * 
     * @param eng
     */
    public void afterLogin(KKAppEng eng)
    {

    }

}
