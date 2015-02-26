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

import org.geosdi.maplite.client.resources.UiMapLiteImages;
import org.gwtopenmaps.openlayers.client.Style;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 *
 */
public class VectorFeatureStyle {

    private Style vectorStyle;

    public static Style generateEarthquakePointStyle(double magnitude) {
        Style style = new Style();
        String color = "#FF0000";//red
        style.setPointRadius(magnitude + 2);
        if (magnitude < 3) {
            color = "#66CD00";//green
        } else if (magnitude >= 3 && magnitude < 5) {
            color = "#FFA500";//orange
        }
        style.setFillColor(color);
        style.setStrokeColor(color);
        style.setStrokeWidth(2);
        style.setGraphicOpacity(1);
        style.setLabelSelect(false);
        style.setFontWeight("normal");
//        style.setGraphicSize(25, 41);
        return style;
    }

    public static Style generateGeocodingStyle() {
        Style style = new Style();
        String color = "#FF0000";
        style.setFillColor(color);
        style.setStrokeColor(color);
        style.setStrokeWidth(2);
        style.setGraphicSize(25, 41);
        style.setGraphicOpacity(1);
        style.setLabelSelect(false);
        style.setFontWeight("normal");
        style.setExternalGraphic(UiMapLiteImages.INSTANCE.markerIcon().getSafeUri().asString());
        return style;
    }

    public VectorFeatureStyle() {
        this.createStyle();
    }

    /**
     * Create A Style for Vector Feature
     */
    private void createStyle() {
        this.vectorStyle = new Style();
        vectorStyle.setStrokeColor("#000000");
        vectorStyle.setStrokeWidth(1);
        vectorStyle.setFillColor("#FF0000");
        vectorStyle.setFillOpacity(0.5);
        vectorStyle.setPointRadius(5);
        vectorStyle.setStrokeOpacity(1.0);
    }

    /**
     * @return the vectorStyle
     */
    public Style getVectorStyle() {
        return vectorStyle;
    }

    /**
     * @param vectorStyle the vectorStyle to set
     */
    public void setVectorStyle(Style vectorStyle) {
        this.vectorStyle = vectorStyle;
    }
}
