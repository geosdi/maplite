/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geosdi.maplite.client.websocket;

import com.google.gwt.json.client.JSONObject;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public interface IWebSocketOp {

    void execute(JSONObject jsonObject);
    
}
