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
package org.geosdi.maplite.server.service.impl;

import com.google.common.collect.Lists;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import org.geosdi.geoplatform.core.model.GPBBox;
import org.geosdi.geoplatform.exception.ResourceNotFoundFault;
import org.geosdi.geoplatform.gui.shared.GPLayerType;
import org.geosdi.geoplatform.responce.FolderDTO;
import org.geosdi.geoplatform.responce.IElementDTO;
import org.geosdi.geoplatform.responce.ProjectDTO;
import org.geosdi.geoplatform.responce.RasterLayerDTO;
import org.geosdi.geoplatform.responce.ShortLayerDTO;
import org.geosdi.geoplatform.services.GeoPlatformService;
import org.geosdi.maplite.server.service.IMapLiteService;
import org.geosdi.maplite.shared.BBoxClientInfo;
import org.geosdi.maplite.shared.ClientRasterInfo;
import org.geosdi.maplite.shared.GPClientProject;
import org.geosdi.maplite.shared.GPFolderClientInfo;
import org.geosdi.maplite.shared.GPLayerClientInfo;
import org.geosdi.maplite.shared.GPStyleStringBeanModel;
import org.geosdi.maplite.shared.IGPFolderElements;
import org.geosdi.maplite.shared.MapLiteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
@Service("mapLiteService")
public class MapLiteService implements IMapLiteService {

    protected static final Logger logger = LoggerFactory.getLogger(
            MapLiteService.class);

    public final static String LayerTimeFilterWidget_LAYER_TIME_DELIMITER = " - [";

    //
    private GeoPlatformService geoPlatformServiceClient;
    //

    private GeoServerRESTReader sharedRestReader;

    /**
     * @param geoPlatformServiceClient the geoPlatformServiceClient to set
     */
    @Autowired
    public void setGeoPlatformServiceClient(
            @Qualifier("geoPlatformServiceClient") GeoPlatformService geoPlatformServiceClient) {
        this.geoPlatformServiceClient = geoPlatformServiceClient;
    }

    public GPClientProject convertToGPClientProject(ProjectDTO projectDTO) {
        GPClientProject clientProject = new GPClientProject();
        clientProject.setId(projectDTO.getId());
        clientProject.setName(projectDTO.getName());
        clientProject.setNumberOfElements(projectDTO.getNumberOfElements());
        if (projectDTO.isShared() != null) {
            clientProject.setShared(projectDTO.isShared());
        }
        clientProject.setRootFolders(this.convertOnlyFolders(
                projectDTO.getRootFolders()));
        return clientProject;
    }

    public ArrayList<GPFolderClientInfo> convertOnlyFolders(
            Collection<FolderDTO> folders) {
        ArrayList<GPFolderClientInfo> foldersClient = Lists.<GPFolderClientInfo>newArrayList();
        if (folders != null) {
            for (Iterator<FolderDTO> it = folders.iterator(); it.hasNext();) {
                GPFolderClientInfo folder = this.convertFolderElement(it.next());
                foldersClient.add(folder);
            }
        }
        return foldersClient;
    }

    private GPFolderClientInfo convertFolderElement(FolderDTO folderDTO) {
        GPFolderClientInfo folder = new GPFolderClientInfo();
        folder.setLabel(folderDTO.getName());
        folder.setId(folderDTO.getId());
        // folder.setzIndex(folderDTO.getPosition());
        folder.setNumberOfDescendants(folderDTO.getNumberOfDescendants());
        folder.setChecked(folderDTO.isChecked());
        folder.setExpanded(folderDTO.isExpanded());
        folder.setFolderElements(this.convertFolderElements(
                folderDTO.getElementList()));
        return folder;
    }

    private List<IGPFolderElements> convertFolderElements(
            List<IElementDTO> folderElements) {
        List<IGPFolderElements> clientFolderElements = Lists.<IGPFolderElements>newArrayList();
        Iterator<IElementDTO> iterator = folderElements.iterator();
        while (iterator.hasNext()) {
            clientFolderElements.add(this.convertElement(iterator.next()));
        }
        return clientFolderElements;
    }

    private IGPFolderElements convertElement(IElementDTO element) {
        IGPFolderElements folderElement = null;
        if (element instanceof RasterLayerDTO) {
            folderElement = this.convertRasterElement((RasterLayerDTO) element);
        } else if (element instanceof FolderDTO) {
            folderElement = this.convertFolderElement((FolderDTO) element);
        }
        return folderElement;
    }

    private ClientRasterInfo convertRasterElement(RasterLayerDTO rasterDTO) {
        ClientRasterInfo raster = new ClientRasterInfo();
        this.convertToLayerElementFromLayerDTO(raster, rasterDTO);
        raster.setLayerType(GPLayerType.WMS);
        raster.setOpacity(rasterDTO.getOpacity());
        raster.setMaxScale(rasterDTO.getMaxScale());
        raster.setMinScale(rasterDTO.getMinScale());
        ArrayList<GPStyleStringBeanModel> styles = Lists.<GPStyleStringBeanModel>newArrayList();
        GPStyleStringBeanModel style;
        for (String styleString : rasterDTO.getStyleList()) {
            style = new GPStyleStringBeanModel();
            style.setStyleString(styleString);
            styles.add(style);
        }
        raster.setStyles(styles);
        return raster;
    }

    private void convertToLayerElementFromLayerDTO(GPLayerClientInfo layer,
            ShortLayerDTO layerDTO) {
        layer.setAbstractText(layerDTO.getAbstractText());
        layer.setBbox(this.convertBbox(layerDTO.getBbox()));
        layer.setChecked(layerDTO.isChecked());
        layer.setCrs(layerDTO.getSrs());
        layer.setDataSource(layerDTO.getUrlServer());
        layer.setId(layerDTO.getId());
        layer.setLayerName(layerDTO.getName());
        layer.setTitle(layerDTO.getTitle());
        layer.setAlias(layerDTO.getAlias());
        layer.setCqlFilter(layerDTO.getCqlFilter());
        if ((layerDTO.getTimeFilter() != null) && !(layerDTO.getTimeFilter().equals(
                ""))) {
            layer.setTimeFilter(layerDTO.getTimeFilter());
            try {
                String dimension = this.sharedRestReader.getDimensions(
                        layerDTO.getTitle());
                if ((dimension != null) && (!dimension.contains("<h2>"))) {
                    List<String> dimensionList = Lists.<String>newArrayList(
                            dimension.split(","));

                    String[] timeFilterSplitted = layerDTO.getTimeFilter().split(
                            "/");
                    int startDimensionPosition = Integer.parseInt(
                            timeFilterSplitted[0]);

                    String variableTimeFilter = dimensionList.get(
                            dimensionList.size() - startDimensionPosition - 1);
                    if (timeFilterSplitted.length > 1) {
                        int endDimensionPosition = Integer.parseInt(
                                timeFilterSplitted[1]);
                        variableTimeFilter += "/" + dimensionList.get(
                                dimensionList.size() - endDimensionPosition - 1);
                    }
                    layer.setVariableTimeFilter(variableTimeFilter);
                    String layerAlias;
                    if (layerDTO.getAlias() != null
                            && layerDTO.getAlias().indexOf(
                                    LayerTimeFilterWidget_LAYER_TIME_DELIMITER) != -1) {
                        layerAlias = layerDTO.getAlias().substring(0,
                                layerDTO.getAlias().indexOf(
                                        LayerTimeFilterWidget_LAYER_TIME_DELIMITER));
                    } else {
                        layerAlias = layerDTO.getTitle();
                    }
                    layer.setAlias(
                            layerAlias + LayerTimeFilterWidget_LAYER_TIME_DELIMITER
                            + layer.getVariableTimeFilter() + "]");
                }
            } catch (NumberFormatException nfe) {
            } catch (MalformedURLException nfe) {
                logger.error(
                        "Impossible to retrieve time filter executing call with "
                        + "geoServerManager: " + nfe);
            }
        }
        layer.setzIndex(layerDTO.getPosition());
    }

    private BBoxClientInfo convertBbox(GPBBox gpBbox) {
        return new BBoxClientInfo(gpBbox.getMinX(), gpBbox.getMinY(),
                gpBbox.getMaxX(), gpBbox.getMaxY());
    }

    @Autowired
    public void setRestReader(
            @Qualifier(value = "sharedRestReader") GeoServerRESTReader sharedRestReader) {
        this.sharedRestReader = sharedRestReader;
    }

    @Override
    public GPClientProject loadProject(Long projectId, Long accountId,
            HttpServletRequest request) throws MapLiteException {
        logger.debug("Executing Load Project");
        ProjectDTO projectDTO = null;
        try {
            projectDTO = this.geoPlatformServiceClient.
                    getProjectWithExpandedFolders(projectId, accountId);
        } catch (ResourceNotFoundFault rnf) {
            logger.error("Returning no elements: " + rnf);
            throw new MapLiteException("Unable to find the requested projct: " + rnf);
        }
        return this.convertToGPClientProject(projectDTO);
    }

}
