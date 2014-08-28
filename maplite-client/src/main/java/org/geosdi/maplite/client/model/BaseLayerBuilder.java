/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.model;

import com.google.common.base.Strings;
import java.util.logging.Logger;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class BaseLayerBuilder {

    private final static Logger logger = Logger.getLogger("");

    private BaseLayerBuilder() {
    }

    public static OSM buildBaseMap(String baseMapNumber) {
        OSM osm;
        int baseMapChoise = 0;
        if (!Strings.isNullOrEmpty(baseMapNumber)) {
            try {
                baseMapChoise = Integer.parseInt(baseMapNumber);
            } catch (NumberFormatException nfe) {
                logger.warning("The passed value for baseMap is not a number: " + baseMapNumber);
            }
        }
        switch (baseMapChoise) {
            case 1:
                osm = OSM.Mapnik("Mapnik");
                osm.setIsBaseLayer(true);
                break;
            default:
                OSMOptions defaultMapOptions = new OSMOptions();
                defaultMapOptions.setNumZoomLevels(19);
                defaultMapOptions.setProjection("EPSG:3857");
                defaultMapOptions.setIsBaseLayer(true);
                defaultMapOptions.crossOriginFix();
                osm = OSM.THIS("MapQuest-OSM Tiles",
                        "http://otile1.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
                        defaultMapOptions);
        }
        return osm;
    }

}
