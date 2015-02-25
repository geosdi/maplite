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
package org.geosdi.maplite.client.map;

import com.google.gwt.user.client.ui.HTML;
import java.util.logging.Logger;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener.FeatureSelectedEvent;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.RendererOptions;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class EarlyWarningVector {

    private final static Logger logger = Logger.getLogger("");

    public final static String EARLYWARNING_VECTOR_ID = "earlyWarningVectorID";
    public final static String EARLYWARNING_FEATURE_TEXT = "earlyWarningFeatureID";
    private final static String EARLYWARNING_VECTOR_NAME = "BioCase Vector Layer";
    private static Vector earlyWarningVector;
    private final Map map;

    public EarlyWarningVector(Map map) {
        this.map = map;
        initializeEarlyWarningVectorLayer();
    }

    public Vector get() {
        return earlyWarningVector;
    }

    private void initializeEarlyWarningVectorLayer() {
        VectorOptions vectorOption = new VectorOptions();
        VectorFeatureStyle style = new VectorFeatureStyle();
        vectorOption.setStyle(style.getVectorStyle());
        vectorOption.setDisplayInLayerSwitcher(false);
        RendererOptions rendererOptions = new RendererOptions();
        rendererOptions.setZIndexing(Boolean.TRUE);
        vectorOption.setRendererOptions(rendererOptions);
        earlyWarningVector = new Vector(EARLYWARNING_VECTOR_NAME, vectorOption);
        //
        SelectFeature selectFeature = new SelectFeature(earlyWarningVector);
        selectFeature.setAutoActivate(true);
        selectFeature.setClickOut(true);
        map.addControl(selectFeature);

        earlyWarningVector.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {

            @Override
            public void onFeatureSelected(FeatureSelectedEvent eventObject) {
                logger.info("On Feature Selected");
                final VectorFeature selectedFeature = eventObject.getVectorFeature();
                String featureText = selectedFeature.getAttributes().
                        getAttributeAsString(EARLYWARNING_FEATURE_TEXT);
                featureText = new HTML(featureText).getHTML();

                StringBuilder htmlForPopup = new StringBuilder("<h1 style=\"color:red\">Info:</h1>");
                htmlForPopup.append("<b>");
                htmlForPopup.append(featureText);
                htmlForPopup.append("</b>");

                final Popup popup = new FramedCloud(selectedFeature.getFeatureId(),
                        selectedFeature.getCenterLonLat(), null,
                        htmlForPopup.toString(), null, true);
//                final Popup popup = new Popup("<b>" + featureText + "</b>",
//                        eventObject.getVectorFeature().getCenterLonLat(),
//                        null, featureText, false);

                popup.setPanMapIfOutOfView(true); //this set the popup in a strategic way, and pans the map if needed.
                popup.setAutoSize(true);
//                selectedFeature.setPopup(popup);

                map.addPopupExclusive(popup);
                logger.info("After Feature Selected");
            }
        });
        map.addLayer(earlyWarningVector);
        map.setLayerZIndex(earlyWarningVector, 100000);
        logger.info(">>>>> Early Warning Vector added");
    }
}
