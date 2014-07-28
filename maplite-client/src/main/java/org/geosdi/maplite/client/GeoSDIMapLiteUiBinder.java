package org.geosdi.maplite.client;

import com.google.common.base.Strings;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

public class GeoSDIMapLiteUiBinder extends Composite {

    private Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");

    protected static final String GET_LEGEND_REQUEST = "?REQUEST=GetLegendGraphic"
            + "&VERSION=1.0.0&FORMAT=image/png&LAYER=";

    protected static final String wmsUrl = "http://geoserver.wfppal.org/geoserver/wms";

    private Map map;

    interface MapLiteUiBinder extends UiBinder<Widget, GeoSDIMapLiteUiBinder> {
    }

    private static MapLiteUiBinder ourUiBinder = GWT.create(MapLiteUiBinder.class);

    @UiField
    VerticalPanel mapPanel;

    @UiField
    VerticalPanel mapInfoPanel;

    @UiField
    VerticalPanel layersPanel;

    Image image;

    WMS wmsLayer;

    public GeoSDIMapLiteUiBinder() {
        initWidget(ourUiBinder.createAndBindUi(this));
        mapInfoPanel.getElement().setId("mapInfoPanel");
        layersPanel.getElement().setId("layersPanel");

        mapInfoPanel.add(new HTML("<span><h4>#Gaza</h4></span>"));
        mapInfoPanel.add(new HTML("<span><h6>Names, ages of casualties in Gaza from July 8th.</h6></span>"));

        mapPanel.add(initMap());

    }

    private MapWidget initMap() {

        String mapID = Window.Location.getParameter("mapID");
        String x = Window.Location.getParameter("x");
        String y = Window.Location.getParameter("y");
        String zoom = Window.Location.getParameter("zoom");

        MapOptions defaultMapOptions = new MapOptions();
        defaultMapOptions.setNumZoomLevels(19);
        defaultMapOptions.setDisplayProjection(new Projection("EPSG:4326"));

        // Create a MapWidget and add a OSM layer using an url
        MapWidget mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
        OSMOptions options = new OSMOptions();
        options.setNumZoomLevels(19);
        options.setProjection("EPSG:3857");
        options.crossOriginFix(); // fixes pink tiles in FF
        options.setAttribution("geoSDI & WFP Lite Map");
        OSM osm = new OSM("Tiled Maps",
                "http://a.tile.opencyclemap.org/cycle/${z}/${x}/${y}.png",
                options);
        osm.setIsBaseLayer(true);
        map = mapWidget.getMap();
        map.addLayer(osm);
        mapPanel.getElement().setId("map");

        if (!Strings.isNullOrEmpty(mapID)) {
            mapID = URL.decode(mapID);
            if (mapID.indexOf('-') != -1) {
                String project = mapID.substring(0, mapID.indexOf('-'));
                String account = mapID.substring(mapID.indexOf('-'), mapID.length());
                System.out.println("Project: " + project);
                System.out.println("Account: " + account);
            }
        }

//        if (mapID != null) {
//            String[] layerArray = mapID.split(";");
//
//            for (int i = 0; i < layerArray.length; i++) {
//                final String layerName = new String(layerArray[i]);
//                WMSOptions wmsLayerParams = new WMSOptions();
//                wmsLayerParams.setTileSize(new Size(256, 256));
//                wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
//                wmsLayerParams.setProjection("EPSG:3857");
//
//                WMSParams wmsParams = new WMSParams();
//                wmsParams.setFormat("image/png");
//                wmsParams.setLayers(layerName);
//                wmsParams.setStyles("");
//                wmsParams.setTransparent(true);
//
//                wmsLayer = new WMS(layerName, wmsUrl, wmsParams, wmsLayerParams);
//                wmsLayer.setIsBaseLayer(false);
////                wmsLayer.setSingleTile(true);
//                map.addLayer(wmsLayer);
//
//                StringBuilder imageURL = new StringBuilder();
//                imageURL.append(wmsUrl).append(GET_LEGEND_REQUEST)
//                        .append(layerName)
//                        .append("&scale=" + map.getScale() + "&service=WMS");
//
//                image = new Image(imageURL.toString());
//
//                final CheckBox check = new CheckBox(layerName);
//                check.setValue(true);
//                check.setTitle(layerName);
//                check.addClickHandler(new ClickHandler() {
//
//                    @Override
//                    public void onClick(ClickEvent event) {
//                        CheckBox checkBox = (CheckBox) event.getSource();
//                        
//                        manageLayerVisibility(checkBox.getValue(),
//                                layerName);
//                    }
//                });
//                
//                
//
//                layersPanel.add(check);
////                layersPanel.add(new HTMLPanel("<div id='clearfix'/>"));
//                layersPanel.add(image);
//
//            }
//
//        }
        // Lets add some default controls to the map
//        map.addControl(new ScaleLine()); // Display the scaleline
        map.addControl(new MousePosition());

        double lon = 16.17582;
        double lat = 42.76989;
        int zommLevel = 5;
        if (!Strings.isNullOrEmpty(x) && !Strings.isNullOrEmpty(y)
                && !Strings.isNullOrEmpty(zoom)) {

            lon = Double.parseDouble(x);
            lat = Double.parseDouble(y);
            zommLevel = Integer.parseInt(zoom);
            // Center and zoom to a location
            // system
        }
        LonLat lonLat = new LonLat(lon, lat);
        lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(),
                map.getProjection()); // transform lonlat to OSM coordinate
        map.setCenter(lonLat, zommLevel);

        return mapWidget;
    }

    private void manageLayerVisibility(Boolean value, String layerName) {
        if (value) {
            map.getLayerByName(layerName).setIsVisible(true);
        } else {
            map.getLayerByName(layerName).setIsVisible(false);
        }
    }

}
