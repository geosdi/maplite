/*
 *  geo-platform
 *  Rich webgis framework
 *  http://geo-platform.org
 * ====================================================================
 *
 * Copyright (C) 2008-2013 geoSDI Group (CNR IMAA - Potenza - ITALY).
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. This program is distributed in the 
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. You should have received a copy of the GNU General 
 * Public License along with this program. If not, see http://www.gnu.org/licenses/ 
 *
 * ====================================================================
 *
 * Linking this library statically or dynamically with other modules is 
 * making a combined work based on this library. Thus, the terms and 
 * conditions of the GNU General Public License cover the whole combination. 
 * 
 * As a special exception, the copyright holders of this library give you permission 
 * to link this library with independent modules to produce an executable, regardless 
 * of the license terms of these independent modules, and to copy and distribute 
 * the resulting executable under terms of your choice, provided that you also meet, 
 * for each linked independent module, the terms and conditions of the license of 
 * that module. An independent module is a module which is not derived from or 
 * based on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obligated to do so. If you do not 
 * wish to do so, delete this exception statement from your version. 
 *
 */
package org.geosdi.maplite.server.gwt;

import java.util.List;
import org.geosdi.maplite.client.service.MapLiteServiceRemote;
import org.geosdi.maplite.server.service.impl.MapLiteService;
import org.geosdi.maplite.server.spring.GPAutoInjectingRemoteServiceServlet;
import org.geosdi.maplite.shared.GPClientProject;
import org.geosdi.maplite.shared.MapLiteException;
import org.geosdi.maplite.shared.geocoding.MapLiteGeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class MapLiteRemoteImpl extends GPAutoInjectingRemoteServiceServlet
        implements MapLiteServiceRemote {

    private static final long serialVersionUID = -509159998560861567L;
    //
    @Autowired
    private MapLiteService mapLiteService;

    @Override
    public GPClientProject loadProject(Long projectId, Long accountId) throws MapLiteException {
        return this.mapLiteService.loadProject(projectId, accountId,
                super.getThreadLocalRequest());
    }

    @Override
    public List<MapLiteGeocodingResult> executeGeoCoding(String address) throws MapLiteException {
        return this.mapLiteService.executeGeocodign(address);
    }

}
