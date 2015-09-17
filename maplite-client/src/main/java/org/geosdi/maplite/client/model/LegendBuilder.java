/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.model;

import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.shared.AnyRtlDirectionEstimator;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.shared.ClientRasterInfo;
import org.geosdi.maplite.shared.GPFolderClientInfo;
import org.geosdi.maplite.shared.IGPFolderElements;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class LegendBuilder {

    private static final String GET_LEGEND_REQUEST = "?REQUEST=GetLegendGraphic"
            + "&VERSION=1.0.0&FORMAT=image/png&LAYER=";
    private final static Logger logger = Logger.getLogger("");

    private LegendBuilder() {
    }

    public static VerticalPanel generateLegendImage(final ClientRasterInfo raster,
            final Map map, final boolean visible) {
        VerticalPanel legendImagePanel = new VerticalPanel();
        StringBuilder imageURL = new StringBuilder();
        imageURL.append(raster.getDataSource()).append(GET_LEGEND_REQUEST)
                .append(raster.getLayerName()).append("&scale=").append(map.getScale())
                .append("&service=WMS&style=")
                .append(raster.getStyles().size() > 0 ? raster.getStyles().get(0).getStyleString() : "")
                .append("&LEGEND_OPTIONS=forceRule:True;forceLabels=on");

        final String layerName;
        if (GPSharedUtils.isNotEmpty(raster.getAlias())) {
            layerName = raster.getAlias();
        } else {
            layerName = raster.getTitle();
        }
        final Label layerNameLabel = new Label(layerName);
        final Button cqlButton = new Button("cql");
        cqlButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getCQLDialogBox(raster, map).show();
            }
        });
        final Button refreshButton = new Button("refresh");
        refreshButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getRefreshDialogBox(raster, map).show();
//                Window.confirm("CQL");
            }
        });
        HorizontalPanel layerRow = new HorizontalPanel();
        layerRow.add(layerNameLabel.asWidget());
        layerRow.add(cqlButton);
        layerRow.add(refreshButton);
        layerRow.setStyleName("layerRow");
        
        
        final Image legendImage = new Image(imageURL.toString());
        legendImage.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                logger.info("addLoadHandler: " + event);
                layerNameLabel.setStyleName(visible ? "textEnabled" : "textDisabled");
                refreshButton.setEnabled(visible);
                cqlButton.setEnabled(visible);
                legendImage.setVisible(visible);
//                legendImagePanel.setVisible(visible);
            }
        });
        legendImage.addErrorHandler(new ErrorHandler() {

            @Override
            public void onError(ErrorEvent event) {
                logger.info("Unloadable image: " + layerName);
                layerNameLabel.setStyleName("textDisabled");
                refreshButton.setEnabled(false);
                cqlButton.setEnabled(false);
                legendImage.setVisible(false);
//                legendImagePanel.setVisible(false);
            }
        });
        legendImagePanel.add(layerRow);
        legendImagePanel.add(legendImage);
        legendImagePanel.setVisible(visible);
        return legendImagePanel;
    }

    public static void rebuildLegend(java.util.Map<GPFolderClientInfo, VerticalPanel> legendPanels,
            Map map) {
        for (GPFolderClientInfo folder : legendPanels.keySet()) {
            VerticalPanel legendPanel = legendPanels.get(folder);
            rebuildLegend(folder, legendPanel, map);
        }
    }

    public static void rebuildLegend(GPFolderClientInfo folder, VerticalPanel legendPanel, Map map) {
        legendPanel.clear();
        for (IGPFolderElements folderElement : folder.getFolderElements()) {
            if (folderElement instanceof ClientRasterInfo) {
                ClientRasterInfo raster = (ClientRasterInfo) folderElement;
                legendPanel.add(generateLegendImage(raster, map,
                        folder.isChecked() ? raster.isChecked() : false));
            }
        }
    }

    private static DialogBox getRefreshDialogBox(final ClientRasterInfo raster, final Map map) {
        // Create a dialog box and set the caption text
        final DialogBox refreshDialogBox = new DialogBox();
        refreshDialogBox.ensureDebugId("cwDialogBox");
        refreshDialogBox.setText("REFRESH TIME");

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        refreshDialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("seconds");
        dialogContents.add(details);
        dialogContents.setCellHorizontalAlignment(
                details, HasHorizontalAlignment.ALIGN_CENTER);

        final TextBox normalText = new TextBox();
        normalText.ensureDebugId("cwBasicText-textbox");
        // Set the normal text box to automatically adjust its direction according
        // to the input text. Use the Any-RTL heuristic, which sets an RTL direction
        // iff the text contains at least one RTL character.
        normalText.setDirectionEstimator(AnyRtlDirectionEstimator.get());

        dialogContents.add(normalText);
        dialogContents.setCellHorizontalAlignment(
                normalText, HasHorizontalAlignment.ALIGN_CENTER);

        // Add a close button at the bottom of the dialog
        Button closeButton = new Button(
                "Apply", new ClickHandler() {

                    private java.util.Map<ClientRasterInfo, Timer> timerMap = Maps.<ClientRasterInfo, Timer>newHashMap();

                    @Override
                    public void onClick(ClickEvent event) {
                        String value = normalText.getValue();
                        try {
                            int seconds = Integer.parseInt(value);
                            if (seconds != 0 && seconds < 30) {
                                Window.alert("The time must be greater or equal to 30 seconds");
                            } else {
                                Layer layer = map.getLayer(raster.getWmsLayerId());

                                final WMS wms = WMS.narrowToLayer(layer.getJSObject());
                                Timer timer = timerMap.get(raster);
                                if (timer == null) {
                                    timer = new Timer() {

                                        @Override
                                        public void run() {
                                            logger.info("Repeat scheduling");
                                            wms.redraw(true);
                                        }
                                    };
                                    timerMap.put(raster, timer);
                                }
                                if (seconds == 0) {
                                    timer.cancel();
                                } else {
                                    timer.scheduleRepeating(seconds * 1000);
                                }
                                refreshDialogBox.hide();
                            }
                        } catch (NumberFormatException nfe) {
                            Window.alert("The passed value is not a valid integer number");
                        }
                    }
                });
        dialogContents.add(closeButton);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_LEFT);

        } else {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
        }

        // Return the dialog box
        refreshDialogBox.setGlassEnabled(true);
        refreshDialogBox.setAnimationEnabled(true);
        refreshDialogBox.center();
        return refreshDialogBox;
    }

    private static DialogBox getCQLDialogBox(final ClientRasterInfo raster, final Map map) {
        // Create a dialog box and set the caption text
        final DialogBox cqlDialogBox = new DialogBox();
        cqlDialogBox.ensureDebugId("cwDialogBox");
        cqlDialogBox.setText("FILTER");

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        cqlDialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("CQL Filter");
        dialogContents.add(details);
        dialogContents.setCellHorizontalAlignment(
                details, HasHorizontalAlignment.ALIGN_CENTER);

        // Add a text area
        final TextArea textArea = new TextArea();
        textArea.ensureDebugId("cwBasicText-textarea");
        textArea.setVisibleLines(5);

        if (GPSharedUtils.isNotEmpty(raster.getCqlFilter())) {
            textArea.setText(raster.getCqlFilter());
        }

        dialogContents.add(textArea);
        dialogContents.setCellHorizontalAlignment(
                textArea, HasHorizontalAlignment.ALIGN_CENTER);

        // Add a close button at the bottom of the dialog
        Button closeButton = new Button(
                "Apply", new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        logger.info("Applico cql filter su raster: " + raster.getLayerName());
                        raster.setCqlFilter(textArea.getText());
                        Layer layer = map.getLayer(raster.getWmsLayerId());
                        WMS wms = WMS.narrowToLayer(layer.getJSObject());
                        WMSParams params;
                        if (raster.getCqlFilter() == null
                        || raster.getCqlFilter().trim().equals("")) {
                            params = wms.getParams();
                            params.removeCQLFilter();
                        } else {
                            params = new WMSParams();
                            params.setCQLFilter(raster.getCqlFilter());
                        }
                        logger.info("Filtro CQL: " + raster.getCqlFilter());
                        wms.mergeNewParams(params);
                        cqlDialogBox.hide();
                    }
                });
        dialogContents.add(closeButton);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_LEFT);

        } else {
            dialogContents.setCellHorizontalAlignment(
                    closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
        }

        // Return the dialog box
        cqlDialogBox.setGlassEnabled(true);
        cqlDialogBox.setAnimationEnabled(true);
        cqlDialogBox.center();
        return cqlDialogBox;
    }

}
