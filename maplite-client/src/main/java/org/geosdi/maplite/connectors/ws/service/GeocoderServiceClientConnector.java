/**
 * ------------------------------------------------------------------ --
 * Copyright <Creation_date>-<Last modification date> SELEX Sistemi -- Integrati
 * S.p.A. all rights reserved. -- This software is the property of SELEX Sistemi
 * Integrati S.p.A. -- and can not be reproduced, used to prepare Derivative
 * Works of, -- publicly displayed, publicly performed, sublicensed, and it --
 * cannot be distributed as the Work itself and such Derivative -- Works in
 * Source or Object form except under a license agreement -- granted by SELEX
 * Sistemi Integrati S.p.A.
 * ------------------------------------------------------------------
 *
 */
package org.geosdi.maplite.connectors.ws.service;

import org.geosdi.geoplatform.configurator.bootstrap.Production;
import org.geosdi.geoplatform.connectors.ws.rest.RestClientConnector;
import org.geosdi.geoplatform.services.GeocoderRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
@Component(value = "geocoderServiceClientConnector")
@Production
public class GeocoderServiceClientConnector extends RestClientConnector<GeocoderRestService> {

    private @Value("geocoderws{rest_geocoder_endpoint_address}")
    String address;

    public GeocoderServiceClientConnector() {
        super(GeocoderRestService.class);
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(String theAddress) {
        this.address = theAddress;
    }

    @Override
    protected Class<?>[] getExtraClasses() {
        return null;
    }
}
