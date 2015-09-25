/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.websocket;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import java.util.List;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.client.GeoSDIMapLiteUiBinder;
import org.geosdi.maplite.client.model.IGeoPlatformExecutor;
import org.geosdi.maplite.client.model.LegendBuilder;
import org.gwtopenmaps.openlayers.client.layer.Layer;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class EnableLayerWebSockOp extends AbstractWebSocketOp {

    public EnableLayerWebSockOp() {
        super("enableLayer");
    }

    @Override
    public void execute(JSONObject jsonObject) {
//        logger.info("EnableLayerWebSockOp : " + jsonObject.toString());
        if (super.checkLayerName(jsonObject) && jsonObject.get("enabled") != null) {
//                && jsonObject.get("enabled").isBoolean() != null) {
            String layerName = jsonObject.get("layerName").isString().stringValue();
            String stringEnableValue = jsonObject.get("enabled").isString().stringValue();
            if (GPSharedUtils.isEmpty(stringEnableValue)) {
                Window.alert("Error: received a non boolean value for the layer enable websocket elaboration process");
            } else {
                boolean enableValue = Boolean.parseBoolean(stringEnableValue);
                List<IGeoPlatformExecutor> executors = enableValue
                        ? LegendBuilder.getLayerLegendEnablers(layerName)
                        : LegendBuilder.getLayerLegendDisablers(layerName);
                for (IGeoPlatformExecutor executor : GPSharedUtils.safeList(executors)) {
                    executor.execute();
                }
                Layer[] layers = GeoSDIMapLiteUiBinder.map.getLayersByName(layerName);
                if (layers != null) {
                    for (Layer layer : layers) {
                        layer.setIsVisible(enableValue);
                    }
                }
            }
        }
    }

}
