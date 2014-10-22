package org.geosdi.maplite.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public interface UiMapLiteStyle extends ClientBundle {

    public interface UiMapLiteCss extends CssResource {

//        @ClassName("hideLegendButtonCss")
//        String hideLegendButtonCss();

        String geocodingResultsCss();
        
        String geocodingBoxCss();
        
        String geocodingTextFieldCss();

        String expandLegendButton();

        String collapseLegendButton();

        String getFeatureInfoStyle();
    }

    @Source("UiMapLiteStyle.css")
    UiMapLiteCss getStyle();

}
