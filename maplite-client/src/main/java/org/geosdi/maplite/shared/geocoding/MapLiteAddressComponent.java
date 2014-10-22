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
 * The parts of an address.
 *
 * <p>
 * See
 * <a href="https://developers.google.com/maps/documentation/geocoding/">here
 * for more detail</a>.
 */
public class MapLiteAddressComponent implements Serializable {

    private static final long serialVersionUID = 9221131208418408104L;

    public MapLiteAddressComponent() {
    }

    /**
     * {@code longName} is the full text description or name of the address
     * component as returned by the Geocoder.
     */
    public String longName;

    /**
     * {@code shortName} is an abbreviated textual name for the address
     * component, if available. For example, an address component for the state
     * of Alaska may have a longName of "Alaska" and a shortName of "AK" using
     * the 2-letter postal abbreviation.
     */
    public String shortName;

    /**
     * This indicates the type of each part of the address. Examples include
     * street number or country.
     */
    public MapLiteAddressComponentType[] types;

}
