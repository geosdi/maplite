/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.postStartup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import static org.geosdi.maplite.client.map.EarlyWarningVector.EARLYWARNING_FEATURE_TEXT;
import org.geosdi.maplite.client.map.GPLonLat;
import org.geosdi.maplite.client.map.VectorFeatureStyle;
import org.geosdi.maplite.client.model.CoordinateReferenceSystem;
import org.geosdi.maplite.client.model.EarthQuake;
import org.geosdi.maplite.client.service.MapLiteServiceRemote;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.util.Attributes;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class PostStartupManagement {

    private final static Logger logger = Logger.getLogger("");

    private final Vector earlyWarnignVector;
    private final Map map;

    public PostStartupManagement(Vector biocaseVector, Map map) {
        this.earlyWarnignVector = biocaseVector;
        this.map = map;
    }

    public void elaboratePostParams() {
        MapLiteServiceRemote.Util.getInstance().readLatestEarthquakes(new AsyncCallback<List<EarthQuake>>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.warning("Error retrieving post parameters: " + caught);
            }

            @Override
            public void onSuccess(List<EarthQuake> resp) {
//                logger.info("elaboratePostParams : " + resp.toString());
                for (EarthQuake earthQuake : GPSharedUtils.safeList(resp)) {
//                    LocationBeanModel locationBeanModel = new LocationBeanModel();
//                    locationBeanModel.setName(earthQuake.getPlace());
                    locatePointOnMap(earthQuake);
//                        logger.info("Ekkolo: " + stringBuffer.toString());
                }
            }
        });
    }

    private void locatePointOnMap(EarthQuake earthQuake) {
        GPLonLat lonLat = earthQuake.getCoordinates();
        String name = earthQuake.getPlace() + "<br/> magnitude: " + earthQuake.getMagnitude();
        LonLat geometryLocation = new LonLat(lonLat.getLongitude(), lonLat.getLatitude());
        geometryLocation.transform(CoordinateReferenceSystem.WGS_84.getCode(), map.getProjection());
//
//        Point geom = new Point(geometryLocation.lon(), geometryLocation.lat());
//
//        VectorFeature vectorFeature = new VectorFeature(geom);
//        vectorFeature.setFeatureId("" + locationBeanModel.hashCode());
//        vectorFeature.setStyle(VectorFeatureStyle.generateGeocodingStyle());
//
//        biocaseVector.addFeature(vectorFeature);

        Point geom = new Point(geometryLocation.lon(), geometryLocation.lat());

        VectorFeature vectorFeature = new VectorFeature(geom);
        Attributes vectorAttributes = new Attributes();
        vectorAttributes.setAttribute(EARLYWARNING_FEATURE_TEXT, name);
        vectorFeature.setAttributes(vectorAttributes);

        vectorFeature.setStyle(VectorFeatureStyle.generateEarthquakePointStyle(
                earthQuake.getMagnitude()));
        earlyWarnignVector.addFeature(vectorFeature);

        logger.info(">>>>> locatePointOnMap added featureVector: " + earthQuake);
    }

}
