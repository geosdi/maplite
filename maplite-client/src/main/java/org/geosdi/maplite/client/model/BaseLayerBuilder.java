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
                defaultMapOptions.setNumZoomLevels(31);
                defaultMapOptions.setProjection("EPSG:3857");
                defaultMapOptions.setIsBaseLayer(true);
                double[] resolutions = {156543.03390625d, 78271.516953125d,
                    39135.7584765625d, 19567.87923828125d, 9783.939619140625d,
                    4891.9698095703125d, 2445.9849047851562d, 1222.9924523925781d,
                    611.4962261962891d, 305.74811309814453d, 152.87405654907226d,
                    76.43702827453613d, 38.218514137268066d, 19.109257068634033d,
                    9.554628534317017d, 4.777314267158508d, 2.388657133579254d,
                    1.194328566789627d, 0.5971642833948135d, 0.29858214169740677d,
                    0.14929107084870338d, 0.07464553542435169d, 0.037322767712175846d,
                    0.018661383856087923d, 0.009330691928043961d,
                    0.004665345964021981d, 0.0023326729820109904d,
                    0.0011663364910054952d, 5.831682455027476E-4d,
                    2.915841227513738E-4d, 1.457920613756869E-4d};
                defaultMapOptions.setResolutions(resolutions);
                defaultMapOptions.crossOriginFix();
                osm = OSM.THIS("MapQuest-OSM Tiles",
                        "http://otile1.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
                        defaultMapOptions);
        }
        return osm;
    }

}
