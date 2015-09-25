/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.websocket;

import com.google.gwt.json.client.JSONObject;
import java.util.logging.Logger;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public abstract class AbstractWebSocketOp implements IWebSocketOp {

    protected final static Logger logger = Logger.getLogger("");

    public AbstractWebSocketOp(String operationMessageType) {
        WebSocketOpsRepository.addOperation(operationMessageType, this);
    }

    protected boolean checkLayerName(JSONObject jsonObject) {
        boolean result = false;
        if (jsonObject.get("layerName") != null
                && jsonObject.get("layerName").isString() != null) {
            result = true;
        }
        return result;
    }

}
