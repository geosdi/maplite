package org.geosdi.maplite.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.client.model.BaseLayerBuilder;
import org.geosdi.maplite.client.model.CoordinateReferenceSystem;
import org.geosdi.maplite.client.model.LegendBuilder;
import org.geosdi.maplite.client.model.MapLiteUrlRewriter;
import org.geosdi.maplite.client.resources.UiMapLiteStyle;
import org.geosdi.maplite.client.service.MapLiteServiceRemote;
import org.geosdi.maplite.client.widget.MapLiteGeocodingTools;
import org.geosdi.maplite.client.widget.MapLiteGetFeatureInfoTool;
import org.geosdi.maplite.shared.ClientRasterInfo;
import org.geosdi.maplite.shared.GPClientProject;
import org.geosdi.maplite.shared.GPFolderClientInfo;
import org.geosdi.maplite.shared.IGPFolderElements;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.OpenLayers;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.event.MapMoveEndListener;
import org.gwtopenmaps.openlayers.client.event.MapZoomListener;
import org.gwtopenmaps.openlayers.client.layer.Layer;
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
    private final static int NUM_ZOOM_LEVEL = 31;

    private Map map;

    interface MapLiteUiBinder extends UiBinder<Widget, GeoSDIMapLiteUiBinder> {
    }

    private static MapLiteUiBinder ourUiBinder = GWT.create(MapLiteUiBinder.class);

    @UiField
    HTMLPanel mapPanel;

    @UiField
    VerticalPanel mapInfoPanel;

    @UiField
    HTMLPanel layersPanel;

    @UiField
    PopupPanel getFeatureInfoPanel;

    @UiField
    LayoutPanel layoutPanel;

    @UiField
    Button hideLegendButton;

    @UiField
    UiMapLiteStyle mapLiteStyle;

    @UiField
    TextBox geocodingTextBox;

    @UiField
    ListBox geocodingListBox;

    private final java.util.Map<GPFolderClientInfo, VerticalPanel> legendPanels
            = Maps.<GPFolderClientInfo, VerticalPanel>newHashMap();
    private MapLiteGetFeatureInfoTool getFeatureInfoTool;
    //Temporary working items
    private VerticalPanel tmpPanel;
    private GPFolderClientInfo tmpFolder;

    public GeoSDIMapLiteUiBinder() {
        initWidget(ourUiBinder.createAndBindUi(this));

        mapLiteStyle.getStyle().ensureInjected();

        layoutPanel.setSize("99.8%", "99.8%");
        mapInfoPanel.getElement().setId("mapInfoPanel");
        mapInfoPanel.setVisible(false);
        layersPanel.getElement().setId("layersPanel");
        layersPanel.setVisible(false);
        mapPanel.add(initMap());
        mapPanel.getElement().setId("map");

    }

    private MapWidget initMap() {
        OpenLayers.setProxyHost("gwtOpenLayersProxy?targetURL=");
        String mapID = Window.Location.getParameter("mapID");
        String x = Window.Location.getParameter("x");
        String y = Window.Location.getParameter("y");
        String zoom = Window.Location.getParameter("zoom");
        final String baseMap = Window.Location.getParameter("baseMap");

        MapOptions defaultMapOptions = new MapOptions();
        defaultMapOptions.setNumZoomLevels(NUM_ZOOM_LEVEL);
        defaultMapOptions.setDisplayProjection(new Projection(CoordinateReferenceSystem.WGS_84.getCode()));
        MapWidget mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
        map = mapWidget.getMap();
        map.addMapMoveEndListener(new MapMoveEndListener() {

            @Override
            public void onMapMoveEnd(MapMoveEndListener.MapMoveEndEvent eventObject) {
                Map map = eventObject.getSource();
                LonLat lonLat = map.getCenter();
                lonLat.transform(map.getProjection(), CoordinateReferenceSystem.WGS_84.getCode());
                MapLiteUrlRewriter.rewriteParameterURL("x", "" + lonLat.lon());
                MapLiteUrlRewriter.rewriteParameterURL("y", "" + lonLat.lat());
            }
        });
        map.addMapZoomListener(new MapZoomListener() {

            @Override
            public void onMapZoom(MapZoomListener.MapZoomEvent eventObject) {
                Map map = eventObject.getSource();
                MapLiteUrlRewriter.rewriteParameterURL("zoom", "" + map.getZoom());
                LegendBuilder.rebuildLegend(legendPanels, map);
            }
        });

        // Lets add some default controls to the map
        map.addControl(new ScaleLine()); // Display the scaleline
        map.addControl(new MousePosition());

        double lon = 16.17582;
        double lat = 42.76989;
        int calculateZoomLevel = 5;
        if (!Strings.isNullOrEmpty(x) && !Strings.isNullOrEmpty(y)
                && !Strings.isNullOrEmpty(zoom)) {

            lon = Double.parseDouble(x);
            lat = Double.parseDouble(y);
            calculateZoomLevel = Integer.parseInt(zoom);
            // Center and zoom to a location
            // system
        }
        final int zoomLevel = calculateZoomLevel;
        final LonLat lonLat = new LonLat(lon, lat);

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
                                map.addLayer(BaseLayerBuilder.buildBaseMap((baseMap)));
                                lonLat.transform(CoordinateReferenceSystem.WGS_84.getCode(),
                                        map.getProjection()); // transform lonlat to OSM coordinate
                                map.setCenter(lonLat, zoomLevel);
                            }

                            @Override
                            public void onSuccess(GPClientProject result) {
                                logger.finest("Loaded project from stack: " + result.toString());
                                mapInfoPanel.add(new HTML("<span><h4>#" + result.getName() + "</h4></span>"));
                                Layer baseLayer;
                                if (GPSharedUtils.isNotEmpty(baseMap)) {
                                    baseLayer = BaseLayerBuilder.buildBaseMap((baseMap));
                                } else {
                                    baseLayer = BaseLayerBuilder.buildBaseMap((result.getBaseLayer()));
                                }
                                map.addLayer(baseLayer);
                                lonLat.transform(CoordinateReferenceSystem.WGS_84.getCode(),
                                        map.getProjection()); // transform lonlat to OSM coordinate
                                map.setCenter(lonLat, zoomLevel);
                                addResourcesToTheMap(result);
                            }
                        });
            }
        }

        //Initialize geocoding tools
        MapLiteGeocodingTools geocodingTools = new MapLiteGeocodingTools(
                this.map, this.geocodingListBox, this.geocodingTextBox);

        this.getFeatureInfoTool = new MapLiteGetFeatureInfoTool(
                this.getFeatureInfoPanel, map);
        return mapWidget;
    }

    private void addResourcesToTheMap(GPClientProject clientProject) {
        if (clientProject != null) {
            for (GPFolderClientInfo folder : GPSharedUtils.safeList(clientProject.getRootFolders())) {
                this.addFolderElementsToTheMap(Lists.<IGPFolderElements>newArrayList(folder));
            }
        }
    }

    private void addFolderElementsToTheMap(List<IGPFolderElements> folderElements) {
        logger.finest("**** addFolderElementsToheMap: " + folderElements.toString());
        for (final IGPFolderElements folderElement : GPSharedUtils.safeList(folderElements)) {
            logger.finest(folderElement.toString());
            if (folderElement instanceof GPFolderClientInfo) {
                this.tmpFolder = (GPFolderClientInfo) folderElement;
                if (GPSharedUtils.isNotEmpty(tmpFolder.getFolderElements())) {
                    final CheckBox check = new CheckBox(tmpFolder.getLabel());
                    tmpPanel = new VerticalPanel();
                    legendPanels.put((GPFolderClientInfo) folderElement, tmpPanel);
                    check.setValue(tmpFolder.isChecked());
                    check.setTitle(tmpFolder.getLabel());
                    check.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            CheckBox checkBox = (CheckBox) event.getSource();
                            ((GPFolderClientInfo) folderElement).setChecked(checkBox.getValue());
                            manageLayerVisibility(checkBox.getValue(), (GPFolderClientInfo) folderElement);
                        }
                    });
                    layersPanel.add(check);
                    layersPanel.add(tmpPanel);
                    this.addFolderElementsToTheMap(((GPFolderClientInfo) folderElement).getFolderElements());
                }
            } else if (folderElement instanceof ClientRasterInfo) {
                ClientRasterInfo raster = (ClientRasterInfo) folderElement;
                final String layerName = raster.getLayerName();
                WMSOptions wmsLayerParams = new WMSOptions();
                wmsLayerParams.setTileSize(new Size(256, 256));
                wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
                wmsLayerParams.setProjection("EPSG:3857");

                WMSParams wmsParams = new WMSParams();
                wmsParams.setFormat("image/png");
                wmsParams.setLayers(layerName);
                if (GPSharedUtils.isNotEmpty(raster.getStyles())) {
                    wmsParams.setStyles(raster.getStyles().get(0).getStyleString());
                } else {
                    wmsParams.setStyles("");
                }
                if (GPSharedUtils.isNotEmpty(raster.getCqlFilter())) {
                    wmsParams.setCQLFilter(raster.getCqlFilter());
                }
                logger.info("Style for layer: " + layerName + " - style: " + wmsParams.getStyles());
                wmsParams.setTransparent(true);

                WMS wmsLayer = new WMS(layerName, raster.getDataSource(), wmsParams, wmsLayerParams);
                wmsLayer.setIsBaseLayer(Boolean.FALSE);
                wmsLayer.setSingleTile(raster.isSingleTileRequest());
                wmsLayer.setOpacity(raster.getOpacity());
                logger.log(Level.INFO, "The layer: " + raster.getLayerName() + " is visible: " + raster.isChecked());
                wmsLayer.setZIndex(raster.getzIndex());
                logger.log(Level.INFO, "Z-Index value: " + wmsLayer.getZIndex());
                wmsLayer.setIsVisible(tmpFolder.isChecked() ? raster.isChecked() : false);

                //Adds getFeatureInfo
                this.getFeatureInfoTool.addGetFeatureInfoToWMS(wmsLayer);

                map.addLayer(wmsLayer);
                map.setLayerZIndex(wmsLayer, raster.getzIndex());
                //Add another property to the raster to retrieve easly the layer on map
                raster.setWMSLayerId(wmsLayer.getId());

                VerticalPanel legendImage = LegendBuilder.generateLegendImage(raster, map,
                        tmpFolder.isChecked() ? raster.isChecked() : false);
                tmpPanel.add(legendImage);
            }
        }
    }

    private void manageLayerVisibility(Boolean visible, IGPFolderElements folderElement) {
        if (folderElement instanceof GPFolderClientInfo) {
            GPFolderClientInfo folderObj = (GPFolderClientInfo) folderElement;
            VerticalPanel legendPanel = this.legendPanels.get(folderObj);
            legendPanel.setVisible(visible);
            for (IGPFolderElements innerElement : GPSharedUtils.safeList(folderObj.getFolderElements())) {
                this.manageLayerVisibility(visible, innerElement);
            }
            if (visible) {
                LegendBuilder.rebuildLegend(folderObj, legendPanel, map);
            }
        } else {
            ClientRasterInfo raster = (ClientRasterInfo) folderElement;
            String layerId = raster.getWmsLayerId();
            String layerName = raster.getLayerName();
            Layer layer = map.getLayer(layerId);
            if (layer != null) {
                if (layer.getName().equalsIgnoreCase(layerName)) {
                    layer.setIsVisible(visible ? raster.isChecked() : false);
                }
            }
        }
    }

    @UiHandler("hideLegendButton")
    void handleClick(ClickEvent e) {
        boolean isVisible = this.mapInfoPanel.isVisible();
        this.layersPanel.setVisible(!isVisible);
        this.mapInfoPanel.setVisible(!isVisible);
        if (isVisible) {
            this.hideLegendButton.setStyleName(this.mapLiteStyle.getStyle().expandLegendButton());
        } else {
            this.hideLegendButton.setStyleName(this.mapLiteStyle.getStyle().collapseLegendButton());
        }
    }

}
