/**
 *
 * geo-platform Rich webgis framework http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2014 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.geosdi.maplite.client.model;

import org.gwtopenmaps.openlayers.client.OpenLayers;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.layer.Bing;
import org.gwtopenmaps.openlayers.client.layer.BingOptions;
import org.gwtopenmaps.openlayers.client.layer.BingType;
import org.gwtopenmaps.openlayers.client.layer.EmptyLayer;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.protocol.ProtocolType;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public enum BaseLayerEnum {

    OPEN_STREET_MAP(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createOSMBaseLayer();
        }

    }),
    MAP_QUEST_OSM(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createMapQuestOSMBaseLayer();
        }

    }),
    GOOGLE_NORMAL(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createGoogleNormalBaseLayer();
        }

    }),
    GOOGLE_SATELLITE(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createGoogleSatelliteBaseLayer();
        }

    }),
    GOOGLE_HYBRID(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createGoogleHybridBaseLayer();
        }

    }),
    BING_ROAD_LAYER(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createBingRoadBaseLayer();
        }

    }),
    BING_HYBRID(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createBingHybridBaseLayer();
        }

    }),
    BING_AERIAL(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createBingAerialBaseLayer();
        }

    }),
    METACARTA(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createMetacartaBaseLayer();
        }

    }),
    GEOSDI_BASE(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createGeoSdiBaseLayer();
        }

    }),
    GEOSDI_NULL_BASE(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createGeoSdiNullMapBaseLayer();
        }

    }),
    EMPTY(new IBaseLayerCreator() {

        @Override
        public Layer createBaseLayer() {
            return createGeoSdiEmptyMapBaseLayer();
        }

    });

    BaseLayerEnum(IBaseLayerCreator baseLayerCreator) {
        this.baseLayerCreator = baseLayerCreator;
    }

    private final IBaseLayerCreator baseLayerCreator;
    private final static String bingKey = "Apd8EWF9Ls5tXmyHr22O"
            + "uL1ay4HRJtI4JG4jgluTDVaJdUXZV6lpSBpX-TwnoRDG";
    public final static double[] baseMapResolutions = {156543.03390625d, 78271.516953125d,
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

    public IBaseLayerCreator getBaseLayerCreator() {
        return baseLayerCreator;
    }

    private static Layer createOSMBaseLayer() {
        OSMOptions osmOption = new OSMOptions();
        osmOption.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        osmOption.setResolutions(baseMapResolutions);
        Layer osm = OSM.Mapnik("OpenStreetMap", osmOption);
//        Layer osm = OSM.THIS("OpenStreetMap", OpenLayers.getProxyHost()
//                + "http://tile.openstreetmap.org/${z}/${x}/${y}.png", osmOption);
        osm.setIsBaseLayer(Boolean.TRUE);
        return osm;
    }

    private static Layer createGeoSdiBaseLayer() {
        WMSParams wmsParams = new WMSParams();
        wmsParams.setFormat("image/png");
        wmsParams.setLayers("Mappa_di_Base");
        wmsParams.setStyles("");
        WMSOptions wmsLayerParams = new WMSOptions();
//        wmsLayerParams.setResolutions(baseMapResolutions);
        wmsLayerParams.setProjection(
                CoordinateReferenceSystem.WGS_84.getCode());
        wmsLayerParams.setTileSize(new Size(256, 256));
        Layer geoSdi = new WMS("geoSdi", "http://dpc.geosdi.org/geoserver/wms",
                wmsParams, wmsLayerParams);
        geoSdi.setIsBaseLayer(Boolean.TRUE);

        return geoSdi;
    }

    private static Layer createMapQuestOSMBaseLayer() {
        OSMOptions defaultMapOptions = new OSMOptions();
        defaultMapOptions.setProjection(CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        defaultMapOptions.setIsBaseLayer(true);
        defaultMapOptions.crossOriginFix();
        defaultMapOptions.setTileSize(new Size(256, 256));
        defaultMapOptions.setResolutions(baseMapResolutions);
        Layer mapQuestOSMBaseLayer = OSM.THIS("MapQuest OSM", OpenLayers.getProxyHost()
                + "http://otile1.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
                defaultMapOptions);
        mapQuestOSMBaseLayer.setIsBaseLayer(Boolean.TRUE);
        return mapQuestOSMBaseLayer;
    }

    private static Layer createGeoSdiNullMapBaseLayer() {
        WMSParams wmsParams = new WMSParams();
        wmsParams.setFormat("image/png");
        wmsParams.setLayers("StratiDiBase:nullMap");
        wmsParams.setStyles("");
        WMSOptions wmsLayerParams = new WMSOptions();
//        wmsLayerParams.setResolutions(baseMapResolutions);
        wmsLayerParams.setProjection(
                CoordinateReferenceSystem.WGS_84.getCode());
        wmsLayerParams.setTileSize(new Size(256, 256));
        WMS geoSdi = new WMS("geoSdi No Map",
                "http://dpc.geosdi.org/geoserver/wms",
                wmsParams, wmsLayerParams);
        geoSdi.setIsBaseLayer(Boolean.TRUE);

        return geoSdi;
    }

    private static Layer createMetacartaBaseLayer() {
        WMSParams wmsParams = new WMSParams();
        wmsParams.setFormat("image/png");
        wmsParams.setLayers("basic");
        wmsParams.setStyles("");
        WMSOptions wmsLayerParams = new WMSOptions();
//        wmsLayerParams.setResolutions(baseMapResolutions);
        wmsLayerParams.setProjection(
                CoordinateReferenceSystem.WGS_84.getCode());
        wmsLayerParams.setTileSize(new Size(256, 256));
        WMS metacarta = new WMS("Metacarta",
                "http://vmap0.tiles.osgeo.org/wms/vmap0",
                wmsParams, wmsLayerParams);
        metacarta.setIsBaseLayer(Boolean.TRUE);

        return metacarta;
    }

    private static Layer createGoogleNormalBaseLayer() {
        GoogleV3Options option = new GoogleV3Options();
        option.setType(GoogleV3MapType.G_NORMAL_MAP);
        option.setSphericalMercator(Boolean.TRUE);
        option.setTransitionEffect(TransitionEffect.RESIZE);
        option.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        option.setResolutions(baseMapResolutions);
        Layer google = new GoogleV3("Google Normal", option);
        google.setIsBaseLayer(Boolean.TRUE);

        return google;
    }

    private static Layer createGoogleSatelliteBaseLayer() {
        GoogleV3Options opSatellite = new GoogleV3Options();
        opSatellite.setType(GoogleV3MapType.G_SATELLITE_MAP);
        opSatellite.setSphericalMercator(Boolean.TRUE);
        opSatellite.setTransitionEffect(TransitionEffect.RESIZE);
        opSatellite.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        opSatellite.setResolutions(baseMapResolutions);
        Layer satellite = new GoogleV3("Google Satellite", opSatellite);
        satellite.setIsBaseLayer(Boolean.TRUE);

        return satellite;
    }

    private static Layer createGoogleHybridBaseLayer() {
        GoogleV3Options opHybrid = new GoogleV3Options();
        opHybrid.setType(GoogleV3MapType.G_HYBRID_MAP);
        opHybrid.setSphericalMercator(Boolean.TRUE);
        opHybrid.setTransitionEffect(TransitionEffect.RESIZE);
        opHybrid.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        opHybrid.setResolutions(baseMapResolutions);
        Layer hybrid = new GoogleV3("Google Hybrid", opHybrid);
        hybrid.setIsBaseLayer(Boolean.TRUE);

        return hybrid;
    }

    private static Layer createBingRoadBaseLayer() {
        BingOptions bingOption = new BingOptions("Bing Road Layer", bingKey,
                BingType.ROAD);
        bingOption.setProtocol(ProtocolType.HTTPS);
        bingOption.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        bingOption.setResolutions(baseMapResolutions);
        Bing road = new Bing(bingOption);
        road.setIsBaseLayer(Boolean.TRUE);

        return road;
    }

    private static Layer createBingHybridBaseLayer() {
        BingOptions bingOption = new BingOptions("Bing Hybrid Layer", bingKey,
                BingType.HYBRID);
        bingOption.setProtocol(ProtocolType.HTTPS);
        bingOption.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        bingOption.setResolutions(baseMapResolutions);
        Bing hybrid = new Bing(bingOption);
        hybrid.setIsBaseLayer(Boolean.TRUE);

        return hybrid;
    }

    private static Layer createBingAerialBaseLayer() {
        BingOptions bingOption = new BingOptions("Bing Aerial Layer", bingKey,
                BingType.AERIAL);
        bingOption.setProtocol(ProtocolType.HTTPS);
        bingOption.setProjection(
                CoordinateReferenceSystem.GOOGLE_MERCATOR.getCode());
        bingOption.setResolutions(baseMapResolutions);
        Bing aerial = new Bing(bingOption);
        aerial.setIsBaseLayer(Boolean.TRUE);

        return aerial;
    }

    private static Layer createGeoSdiEmptyMapBaseLayer() {
        //And now lets create an EmptyLayer and add it to the map.
        EmptyLayer.Options emptyLayerOptions = new EmptyLayer.Options();
        emptyLayerOptions.setAttribution("EmptyLayer (c) geoSDI"); //lets set some copyright msg as attribution
        emptyLayerOptions.setIsBaseLayer(true); //make it a baselayer.
        emptyLayerOptions.setProjection(CoordinateReferenceSystem.WGS_84.getCode());
//        emptyLayerOptions.setResolutions(baseMapResolutions);
        EmptyLayer emptyLayer = new EmptyLayer("Empty layer", emptyLayerOptions);
        return emptyLayer;
    }
}
