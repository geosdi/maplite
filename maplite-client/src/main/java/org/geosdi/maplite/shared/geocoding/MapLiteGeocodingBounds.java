/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.shared.geocoding;

import java.io.Serializable;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
/**
 * The north east and south west points that delineate the outer bounds of a
 * map.
 */
public class MapLiteGeocodingBounds implements Serializable {

    private static final long serialVersionUID = -3892393432680733348L;

    public MapLiteGeocodingLatLng northeast;
    public MapLiteGeocodingLatLng southwest;

    public MapLiteGeocodingBounds() {
    }

    @Override
    public String toString() {
        return "MapLiteGeocodingBounds{" + "northeast=" + northeast + ", southwest=" + southwest + '}';
    }

}
