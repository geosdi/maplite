package org.geosdi.maplite.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public interface UiMapLiteImages extends ClientBundle {

    UiMapLiteImages INSTANCE = GWT.create(UiMapLiteImages.class);

    @Source("img/marker-icon.png")
    ImageResource markerIcon();

}
