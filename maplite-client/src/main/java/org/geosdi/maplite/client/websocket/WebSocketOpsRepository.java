/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geosdi.maplite.client.websocket;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class WebSocketOpsRepository {

    private final static Logger logger = Logger.getLogger("");
    private static final ListMultimap<String, IWebSocketOp> operations = ArrayListMultimap.<String, IWebSocketOp>create();

    static {
        new CQLFilterWebSockOp();
        new EnableLayerWebSockOp();
    }

    public static void addOperation(String operationMessageType, IWebSocketOp webSocketOp) {
//        logger.info("Adding operation: " + operationMessageType);
        operations.put(operationMessageType, webSocketOp);
    }

    public static List<IWebSocketOp> getOperations(String operationMessageType) {
//        logger.info("getOperations: " + operationMessageType);
        return operations.get(operationMessageType);
    }

}
