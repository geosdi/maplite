/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.websocket;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;
import java.util.logging.Logger;
import org.geosdi.geoplatform.gui.shared.util.GPSharedUtils;
import org.geosdi.maplite.client.service.MapLiteServiceRemote;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class WebSocketClientConnector {

    private final static Logger logger = Logger.getLogger("");
    private final String uuid;
    private Websocket socket;

    public WebSocketClientConnector(String uuid) {
        this.uuid = uuid;
        this.initWebSocketListener();
    }

    private void initWebSocketListener() {
        MapLiteServiceRemote.Util.getInstance().getWebsocketWSAddress(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.warning("Unrecoverable Error Occurred loading the websocket address from stack: " + caught);
                Window.alert("Error preparing websocket component, if the problem persists please contact your system administrator. "
                        + "Some features may not be available");
            }

            @Override
            public void onSuccess(String websocketAddress) {
                logger.info("Using websocketAddress: " + websocketAddress);
                //socket = new Websocket("ws://hostname:port/path");
                socket = new Websocket(websocketAddress);
                socket.addListener(new WebsocketListener() {

                    @Override
                    public void onMessage(String msg) {
                        // a message is received
                        logger.info("Websocket msg received: " + msg.toString());
                        JSONValue jsonValue = JSONParser.parseStrict(msg);
                        if (jsonValue.isObject() != null && ((JSONObject) jsonValue).get("messageType") != null) {
//                            logger.info("The received message is a JSON Object and contains the messageType");
                            JSONValue jsonMessageType = jsonValue.isObject().get("messageType");
                            for (IWebSocketOp op : GPSharedUtils.safeList(
                                    WebSocketOpsRepository.getOperations(
                                            jsonMessageType.isString().stringValue()))) {
                                op.execute(jsonValue.isObject());
                            }
                        }
                    }

                    @Override
                    public void onOpen() {
                        // do something on open
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("UUID", new JSONString(uuid));
                        logger.info("MapLite instance On Open webSocket connection "
                                + "and registering uuid: " + jsonObj.toString());
                        socket.send(jsonObj.toString());
                    }

                    @Override
                    public void onClose() {
                        // do something on close
                        logger.info("MapLite instance: On Close websocket connection");
                    }
                });
                socket.open();
            }
        });
    }

//    Websocket socket = new Websocket("ws://hostname:port/path");
}
