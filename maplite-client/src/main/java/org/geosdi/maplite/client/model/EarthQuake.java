/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.model;

import java.io.Serializable;
import java.util.Date;
import org.geosdi.maplite.client.map.GPLonLat;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class EarthQuake implements Serializable {

    private static final long serialVersionUID = -7632565678071066806L;

    private Date earthquakeDate;
    private String formattedDate;
    private Double magnitude;
    private String place;
    private GPLonLat coordinates;
    private double[] bbox;

    public EarthQuake() {
    }

    public Date getEarthquakeDate() {
        return earthquakeDate;
    }

    public void setEarthquakeDate(Date earthquakeDate) {
        this.earthquakeDate = earthquakeDate;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public GPLonLat getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GPLonLat coordinates) {
        this.coordinates = coordinates;
    }

    public double[] getBbox() {
        return bbox;
    }

    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    @Override
    public String toString() {
        return "EarthQuake{" + "earthquakeDate=" + earthquakeDate
                + ", formattedDate=" + formattedDate + ", magnitude="
                + magnitude + ", place=" + place + ", coordinates="
                + coordinates + ", bbox=" + bbox + '}';
    }

}
