package org.geosdi.maplite.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.client.model.BaseLayerBuilder;
import org.geosdi.maplite.client.model.FeatureInfoControlFactory;
import org.geosdi.maplite.client.model.LegendBuilder;
import org.geosdi.maplite.client.service.MapLiteServiceRemote;
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
import org.gwtopenmaps.openlayers.client.control.WMSGetFeatureInfo;
import org.gwtopenmaps.openlayers.client.event.GetFeatureInfoListener;
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

    private final static Projection DEFAULT_PROJECTION = new Projection("EPSG:4326");
    private final static int NUM_ZOOM_LEVEL = 30;

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
//    DialogBoxWithCloseButton getFeatureInfoPanel;

    @UiField
    LayoutPanel layoutPanel;

    WMS wmsLayer;

    private final List<WMSGetFeatureInfo> wMSGetFeatureInfos
            = Lists.<WMSGetFeatureInfo>newArrayList();
    private int count = 0;
    private final ScrollPanel getFeatureInfoScrollPanel;
    private VerticalPanel getFeatureInfoVerticalPanel;

    private final java.util.Map<GPFolderClientInfo, VerticalPanel> legendPanels
            = Maps.<GPFolderClientInfo, VerticalPanel>newHashMap();
    //Temporary working items
    private VerticalPanel tmpPanel;
    private GPFolderClientInfo tmpFolder;

    public GeoSDIMapLiteUiBinder() {
        initWidget(ourUiBinder.createAndBindUi(this));
        layoutPanel.setSize("99.8%", "99.8%");
        mapInfoPanel.getElement().setId("mapInfoPanel");
        layersPanel.getElement().setId("layersPanel");
        mapPanel.add(initMap());
        mapPanel.getElement().setId("map");
        this.getFeatureInfoVerticalPanel = new VerticalPanel();
        getFeatureInfoPanel.setAutoHideEnabled(true);
        getFeatureInfoPanel.setModal(false);
        getFeatureInfoPanel.setSize("450px", "350px");
        getFeatureInfoPanel.setVisible(false);
        getFeatureInfoPanel.setTitle("Get Feature Info");
        getFeatureInfoPanel.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                getFeatureInfoVerticalPanel.clear();
                getFeatureInfoPanel.setVisible(false);
            }
        });
        this.getFeatureInfoScrollPanel = new ScrollPanel();
        this.getFeatureInfoScrollPanel.setSize("400px", "250px");
        this.getFeatureInfoScrollPanel.addStyleName("getFeatureInfoBackgroud");
        this.getFeatureInfoScrollPanel.add(this.getFeatureInfoVerticalPanel);
        this.getFeatureInfoPanel.add(this.getFeatureInfoScrollPanel);
    }

    private MapWidget initMap() {
        OpenLayers.setProxyHost("gwtOpenLayersProxy?targetURL=");
        String mapID = Window.Location.getParameter("mapID");
        String x = Window.Location.getParameter("x");
        String y = Window.Location.getParameter("y");
        String zoom = Window.Location.getParameter("zoom");
        String baseMap = Window.Location.getParameter("baseMap");

        MapOptions defaultMapOptions = new MapOptions();
        defaultMapOptions.setNumZoomLevels(NUM_ZOOM_LEVEL);
        defaultMapOptions.setDisplayProjection(DEFAULT_PROJECTION);
        MapWidget mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
        map = mapWidget.getMap();
        map.addLayer(BaseLayerBuilder.buildBaseMap((baseMap)));
        map.addMapZoomListener(new MapZoomListener() {

            @Override
            public void onMapZoom(MapZoomListener.MapZoomEvent eventObject) {
                LegendBuilder.rebuildLegend(legendPanels, map);
            }
        });

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

    private void checkLastElement() {
        logger.info("@@@@@@@@@@@@@@@@@@ Cache Dimension : " + this.wMSGetFeatureInfos.size()
                + " of checked element: " + (count + 1));
        if (++count == this.wMSGetFeatureInfos.size()) {
//            getFeatureInfoPanel.setVisible(true);
//            getFeatureInfoPanel.center();
            logger.info("@@@@@@@@@@@@@@@@@@ on Displaying getFeatureInfoPanel");
            count = 0;
        }
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
                logger.info("Style for layer: " + layerName + " - style: " + wmsParams.getStyles());
                wmsParams.setTransparent(true);

                wmsLayer = new WMS(layerName, raster.getDataSource(), wmsParams, wmsLayerParams);
                wmsLayer.setIsBaseLayer(false);
                wmsLayer.setSingleTile(true);
                wmsLayer.setOpacity(raster.getOpacity());
                logger.log(Level.INFO, "The layer: " + raster.getLayerName() + " is visible: " + raster.isChecked());
                wmsLayer.setZIndex(raster.getzIndex());
                logger.log(Level.INFO, "Z-Index value: " + wmsLayer.getZIndex());
                wmsLayer.setIsVisible(tmpFolder.isChecked() ? raster.isChecked() : false);

                WMSGetFeatureInfo wmsGetFeatureInfo = FeatureInfoControlFactory.createControl(wmsLayer);
                wmsGetFeatureInfo.addGetFeatureListener(new GetFeatureInfoListener() {
//
                    @Override
                    public void onGetFeatureInfo(final GetFeatureInfoListener.GetFeatureInfoEvent eventObject) {
                        try {
                            Scheduler.get().scheduleDeferred(new Command() {
                                @Override
                                public void execute() {

                                    logger.info("...GetFeatureInfo executing...");
                                    if (textContainsNotEmptyHTMLBody(eventObject.getText())) {
                                        logger.info("*** GetFeatureInfo has a not empty body");
                                        try {
                                            getFeatureInfoVerticalPanel.add(new HTMLPanel(eventObject.getText()));
                                            if (!getFeatureInfoPanel.isVisible()) {
                                                getFeatureInfoPanel.setVisible(true);
                                                getFeatureInfoPanel.center();
                                            }
                                        } catch (Exception e) {
                                            logger.warning("**** ERROR on adding getFeatureInfo to the showing panel: " + e.toString());
                                        }
                                    }
                                    logger.finer("...GetFeatureInfo getFeatures...: " + eventObject.getFeatures());
                                    logger.finer("...GetFeatureInfo getText...: " + eventObject.getText());
                                    checkLastElement();
                                }
                            });
                        } catch (Exception e) {
                            logger.warning("Error in getFeatureInfo: " + e);
                        }
                    }
                });
                map.addControl(wmsGetFeatureInfo);
                this.wMSGetFeatureInfos.add(wmsGetFeatureInfo);
                wmsGetFeatureInfo.activate();

                map.addLayer(wmsLayer);
                map.setLayerZIndex(wmsLayer, raster.getzIndex());

                Image legendImage = LegendBuilder.generateLegendImage(raster, map,
                        tmpFolder.isChecked() ? raster.isChecked() : false);
                tmpPanel.add(legendImage);
            }
        }
    }

    private boolean textContainsNotEmptyHTMLBody(String html) {
        boolean result = false;
        if (GPSharedUtils.isNotEmpty(html) && html.toLowerCase().trim().contains("<body")) {
            String bodyHtml = html.toLowerCase().trim();
            int bodyIndex = bodyHtml.indexOf("<body") + 5;
            logger.finest("@@@@@ textContainsNotEmptyHTMLBody html to analize: " + bodyHtml);
            bodyHtml = bodyHtml.substring(bodyIndex);
            bodyIndex = bodyHtml.indexOf(">") + 1;
            String bodyContent = bodyHtml.substring(bodyIndex, bodyHtml.indexOf("</body>"));
            logger.finest("Body content: " + bodyContent);
            bodyContent = bodyContent.replaceAll("\r", "");
            bodyContent = bodyContent.replaceAll("\n", "");
            logger.finest("Body content replaced new lines: " + bodyContent);
            if (!bodyContent.trim().isEmpty()) {
                result = true;
                logger.finest("****** Found body content: " + bodyContent.trim());
            }
        }
        return result;
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
            String layerName = raster.getLayerName();
            Layer[] layers = map.getLayersByName(layerName);
            if (layers != null) {
                for (Layer layer : layers) {
                    if (layer.getName().equalsIgnoreCase(layerName)) {
                        layer.setIsVisible(visible ? raster.isChecked() : false);
                        break;
                    }
                }
            }
        }
    }

}
