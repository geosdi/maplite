/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.model;

import com.google.common.base.Strings;
import java.util.logging.Logger;
import org.gwtopenmaps.openlayers.client.layer.Layer;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class BaseLayerBuilder {

    private final static Logger logger = Logger.getLogger("");

    private BaseLayerBuilder() {
    }

    public static Layer buildBaseMap(String baseMapEnum) {
        BaseLayerEnum baseMapChoise = BaseLayerEnum.MAP_QUEST_OSM;
        if (!Strings.isNullOrEmpty(baseMapEnum)) {
            try {
                baseMapChoise = BaseLayerEnum.valueOf(baseMapEnum);
            } catch (IllegalArgumentException iae) {
                logger.warning("The passed value for baseMap is not a valid option: " + baseMapEnum);
            }
        }
        return baseMapChoise.getBaseLayerCreator().createBaseLayer();
    }

}
