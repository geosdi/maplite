/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.widget;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.List;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.client.model.FeatureInfoControlFactory;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.control.WMSGetFeatureInfo;
import org.gwtopenmaps.openlayers.client.event.GetFeatureInfoListener;
import org.gwtopenmaps.openlayers.client.layer.WMS;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class MapLiteGetFeatureInfoTool {

    private final static Logger logger = Logger.getLogger("");

    private VerticalPanel getFeatureInfoVerticalPanel;
    private ScrollPanel getFeatureInfoScrollPanel;
    private int count = 0;
    private final List<WMSGetFeatureInfo> wMSGetFeatureInfos
            = Lists.<WMSGetFeatureInfo>newArrayList();

    private final PopupPanel getFeatureInfoPanel;
    //    DialogBoxWithCloseButton getFeatureInfoPanel;
    private final Map map;

    public MapLiteGetFeatureInfoTool(PopupPanel getFeatureInfoPanel, Map map) {
        this.getFeatureInfoPanel = getFeatureInfoPanel;
        this.map = map;
        this.initGetGeatureInfoTool();
    }

    private void initGetGeatureInfoTool() {
        this.getFeatureInfoVerticalPanel = new VerticalPanel();
        getFeatureInfoPanel.setAutoHideEnabled(false);
        getFeatureInfoPanel.setAnimationEnabled(false);
        getFeatureInfoPanel.setModal(false);
        getFeatureInfoPanel.setSize("100%", "350px");
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
        this.getFeatureInfoScrollPanel.setAlwaysShowScrollBars(true);
        this.getFeatureInfoScrollPanel.setSize("100%", "300px");
        this.getFeatureInfoScrollPanel.addStyleName("getFeatureInfoBackgroud");
        Button btnClose = new Button("Close");
        btnClose.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getFeatureInfoPanel.hide();
            }
        });
        this.getFeatureInfoScrollPanel.add(this.getFeatureInfoVerticalPanel);
        VerticalPanel vp = new VerticalPanel();
        vp.add(btnClose);
        vp.add(getFeatureInfoScrollPanel);
        this.getFeatureInfoPanel.add(vp);
    }

    public void addGetFeatureInfoToWMS(final WMS wmsLayer) {
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
                                    logger.info("*** Layers Visible::::::::::::::  "+ wmsLayer.isVisible());
                                    //if(map.getLayers().length == 2)
                                        //getFeatureInfoVerticalPanel.clear();
                                    getFeatureInfoVerticalPanel.setSize("100%","300px");
                                    getFeatureInfoVerticalPanel.add(new HTMLPanel(eventObject.getText()));
                                    if (!getFeatureInfoPanel.isVisible()) {

                                        getFeatureInfoPanel.show();
                                        //getFeatureInfoPanel.center();
                                        int top = Window.getClientHeight() - getFeatureInfoPanel.getOffsetHeight();
                                        Element elem = getFeatureInfoPanel.getElement();
                                        elem.getStyle().setPropertyPx("left", 47);
                                        elem.getStyle().setPropertyPx("top", 41);
                                        elem.getStyle().setProperty("zIndex", "40");
                                        elem.getStyle().setProperty("background", "rgb(219, 227, 236) none repeat scroll 0% 0%");
                                        elem.getStyle().setProperty("boxShadow", "0px 4px 5px rgba(0, 0, 0, 0.3)");
                                        elem.getStyle().setPropertyPx("padding", 5);
                                        elem.getStyle().setPropertyPx("right", 0);
                                        elem.getStyle().setPropertyPx("bottom", 0);
                                        elem.getStyle().setPropertyPx("width", 600);
                                        elem.getStyle().setPropertyPx("maxHeight",321 );
                                        elem.getStyle().setPropertyPx("height", 321);
                                        elem.getStyle().setProperty("overflow", "visible");
                                        elem.getStyle().setProperty("border", "1px solid rgb(204, 204, 204)");

                                        getFeatureInfoPanel.setVisible(true);
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

}
