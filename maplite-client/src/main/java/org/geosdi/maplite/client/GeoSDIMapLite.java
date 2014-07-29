package org.geosdi.maplite.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class GeoSDIMapLite implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RootLayoutPanel.get().add(new GeoSDIMapLiteUiBinder());
    }
}
