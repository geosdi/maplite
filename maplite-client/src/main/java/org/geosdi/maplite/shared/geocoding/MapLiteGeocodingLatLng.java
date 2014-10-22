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
 * The latitude of this location.
 */
public class MapLiteGeocodingLatLng implements Serializable {

    private static final long serialVersionUID = -7867555074767651501L;

    public double lat;

    /**
     * The longitude of this location.
     */
    public double lng;

    public MapLiteGeocodingLatLng() {
    }

    /**
     * Construct a location with a latitude longitude pair.
     */
    public MapLiteGeocodingLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

//    @Override
    public String toUrlValue() {
        // Enforce Locale to English for double to string conversion
//        return String.format(Locale.ENGLISH, "%f,%f", lat, lng);
        return "";
    }

    @Override
    public String toString() {
        return "MapLiteGeocodingLatLng{" + "lat=" + lat + ", lng=" + lng + '}';
    }

}
