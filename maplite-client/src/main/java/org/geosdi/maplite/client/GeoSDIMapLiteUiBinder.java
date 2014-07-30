package org.geosdi.maplite.client;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.client.service.MapLiteServiceRemote;
import org.geosdi.maplite.shared.ClientRasterInfo;
import org.geosdi.maplite.shared.GPClientProject;
import org.geosdi.maplite.shared.GPFolderClientInfo;
import org.geosdi.maplite.shared.IGPFolderElements;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class GeoSDIMapLiteUiBinder extends Composite {

    private final static Logger logger = Logger.getLogger("");

    private final static Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");

    protected static final String GET_LEGEND_REQUEST = "?REQUEST=GetLegendGraphic"
            + "&VERSION=1.0.0&FORMAT=image/png&LAYER=";

    private Map map;

    interface MapLiteUiBinder extends UiBinder<Widget, GeoSDIMapLiteUiBinder> {
    }

    private static MapLiteUiBinder ourUiBinder = GWT.create(MapLiteUiBinder.class);

    @UiField
    HTMLPanel mapPanel;

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

//        layersPanel.setHeight("150px");
//        layersPanel.setLayoutData();

//        mapPanel.setSize("100%", "100%");
        mapPanel.add(initMap());
        mapPanel.getElement().setId("map");

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

        if (!Strings.isNullOrEmpty(mapID)) {
            mapID = URL.decode(mapID);
            if (mapID.indexOf('-') != -1) {
                String project = mapID.substring(0, mapID.indexOf('-'));
                String account = mapID.substring(mapID.indexOf('-') + 1, mapID.length());
                logger.info("********** Found project and map: " + project + " - " + account);

                MapLiteServiceRemote.Util.getInstance().loadProject(
                        Long.parseLong(project), Long.parseLong(account),
                        new AsyncCallback<GPClientProject>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                logger.warning("Error loading project from stack: " + caught);
                            }

                            @Override
                            public void onSuccess(GPClientProject result) {
                                logger.finest("Loaded project from stack: " + result.toString());
                                mapInfoPanel.add(new HTML("<span><h4>#" + result.getName() + "</h4></span>"));
//                                mapInfoPanel.add(new HTML("<span><h6>Names, ages of casualties in Gaza from July 8th.</h6></span>"));
                                addResourcesToTheMap(result);
                            }
                        });
            }
        }

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

    private void addResourcesToTheMap(GPClientProject clientProject) {
        if (clientProject != null) {
            for (GPFolderClientInfo folder : GPSharedUtils.safeList(clientProject.getRootFolders())) {
                this.addFolderElementsToTheMap(folder.getFolderElements());
            }
        }
    }

    private void addFolderElementsToTheMap(List<IGPFolderElements> folderElements) {
        logger.finest("**** addFolderElementsToheMap: " + folderElements.toString());
        for (IGPFolderElements folderElement : GPSharedUtils.safeList(folderElements)) {
            if (folderElement instanceof GPFolderClientInfo) {
                this.addFolderElementsToTheMap(((GPFolderClientInfo) folderElement).getFolderElements());
            } else if (folderElement instanceof ClientRasterInfo) {
                ClientRasterInfo raster = (ClientRasterInfo) folderElement;
                final String layerName = new String(raster.getLayerName());
                WMSOptions wmsLayerParams = new WMSOptions();
                wmsLayerParams.setTileSize(new Size(256, 256));
                wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
                wmsLayerParams.setProjection("EPSG:3857");

                WMSParams wmsParams = new WMSParams();
                wmsParams.setFormat("image/png");
                wmsParams.setLayers(layerName);
                if (raster.getStyles() != null && !raster.getStyles().isEmpty()) {
                    wmsParams.setStyles(raster.getStyles().get(0).getStyleString());
                } else {
                    wmsParams.setStyles("");
                }
                wmsParams.setTransparent(true);

                wmsLayer = new WMS(layerName, raster.getDataSource(), wmsParams, wmsLayerParams);
                wmsLayer.setIsBaseLayer(false);
//                wmsLayer.setSingleTile(true);
                map.addLayer(wmsLayer);

                StringBuilder imageURL = new StringBuilder();
                imageURL.append(raster.getDataSource()).append(GET_LEGEND_REQUEST)
                        .append(layerName)
                        .append("&scale=" + map.getScale() + "&service=WMS");

                image = new Image(imageURL.toString());

                final CheckBox check = new CheckBox(layerName);
                check.setValue(true);
                check.setTitle(layerName);
                check.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        CheckBox checkBox = (CheckBox) event.getSource();

                        manageLayerVisibility(checkBox.getValue(),
                                layerName);
                    }
                });

                layersPanel.add(check);
//                layersPanel.add(new HTMLPanel("<div id='clearfix'/>"));
                layersPanel.add(image);
            }
        }
    }

    private void manageLayerVisibility(Boolean value, String layerName) {
        if (value) {
            map.getLayerByName(layerName).setIsVisible(true);
        } else {
            map.getLayerByName(layerName).setIsVisible(false);
        }
    }

}
