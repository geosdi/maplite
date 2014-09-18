/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.model;

import com.google.gwt.user.client.Window;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class MapLiteUrlRewriter {

    public static void rewriteParameterURL(String paramName, String paramValue) {
        String newURL = Window.Location.createUrlBuilder().setParameter(paramName,
                paramValue).buildString();
        updateURLWithoutReloading(newURL);
    }

    /**
     * Every time we update the parameters it is necessary to recall the
     * share24() js function that updates the social links
     * @param newUrl 
     */
    private static native void updateURLWithoutReloading(String newUrl) /*-{
     $wnd.history.pushState(newUrl, "", newUrl);
     $wnd.share42();
     }-*/;

}
