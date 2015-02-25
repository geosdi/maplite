/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.postStartup;

import javax.persistence.Transient;
import org.geosdi.maplite.client.map.GPLonLat;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class LocationBeanModel {

    private static final long serialVersionUID = -5594291975776675593L;

    private String name;
    private String WKTPoint;
    private GPLonLat location;

    @Transient
    private String locationFeatureID;

    public LocationBeanModel() {
    }

    public String getLocationFeatureID() {
        return locationFeatureID;
    }

    public void setLocationFeatureID(String locationFeatureID) {
        this.locationFeatureID = locationFeatureID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWKTPoint() {
        return WKTPoint;
    }

    public void setWKTPoint(String WKTPoint) {
        this.WKTPoint = WKTPoint;
    }

    public GPLonLat getLocation() {
        return location;
    }

    public void setLocation(GPLonLat location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "LocationBeanModel{" + "name=" + name + ", WKTPoint="
                + WKTPoint + ", location=" + location + ", locationFeatureID="
                + locationFeatureID + '}';
    }
}
