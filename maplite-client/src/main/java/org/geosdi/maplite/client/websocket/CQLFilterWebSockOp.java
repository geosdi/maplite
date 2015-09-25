package org.geosdi.maplite.client.websocket;

import com.google.gwt.json.client.JSONObject;
import org.geosdi.maplite.client.GeoSDIMapLiteUiBinder;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class CQLFilterWebSockOp extends AbstractWebSocketOp {

    public CQLFilterWebSockOp() {
        super("cqlFilter");
    }

    @Override
    public void execute(JSONObject jsonObject) {
//        logger.info("CQLFilterWebSockOp : " + jsonObject.toString());
        if (super.checkLayerName(jsonObject) && jsonObject.get("cqlFilter") != null) {
            String layerName = jsonObject.get("layerName").isString().stringValue();
//            logger.info("Layer name to modify: " + layerName);
            Layer[] layers = GeoSDIMapLiteUiBinder.map.getLayersByName(layerName);
            if (layers != null) {
                for (Layer layer : layers) {
//                    if (layer.getName().equalsIgnoreCase(layerName)) {

                    WMS wms = WMS.narrowToLayer(layer.getJSObject());
                    WMSParams params;
                    String cqlFilter = jsonObject.get("cqlFilter").isString() != null
                            ? jsonObject.get("cqlFilter").isString().stringValue() : "";
                    if (cqlFilter == null || cqlFilter.trim().equals("")) {
                        params = wms.getParams();
                        params.removeCQLFilter();
                    } else {
                        params = new WMSParams();
                        params.setCQLFilter(cqlFilter);
                    }
//                    logger.info("Filtro CQL: " + cqlFilter);
                    wms.mergeNewParams(params);
//                    }
                }
            }
        }
    }

}
