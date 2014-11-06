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

import javax.annotation.Resource;
import org.geosdi.geoplatform.configurator.bootstrap.Production;
import org.geosdi.geoplatform.services.GeocoderRestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
@Configuration
@Production
public class GeocoderClientConnectorConfiguration {

    @Resource
    private GeocoderServiceClientConnector geocoderServiceClientConnector;

    @Bean(name = {"geocoderRestClient"})
    @Scope(value = "prototype")
    public GeocoderRestService geocoderServiceClient() {
        return this.geocoderServiceClientConnector.getEndpointService();
    }
}
