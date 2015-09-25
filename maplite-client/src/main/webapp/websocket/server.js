// http://ejohn.org/blog/ecmascript-5-strict-mode-json-and-more/
"use strict";

// Optional. You will see this name in eg. 'ps' or 'top' command
process.title = 'websocket MapLite server';

// Port where we'll run the websocket server
var webSocketsServerPort = 1337;

// websocket and http servers
var webSocketServer = require('websocket').server;
var http = require('http');

/**
 * Global variables
 */
// list of currently connected clients (users)
var clients = new Map();

/**
 * Helper function for escaping input strings
 */
function htmlEntities(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;')
            .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

/**
 * HTTP server
 */
var server = http.createServer(function (request, response) {
    // Not important for us. We're writing WebSocket server, not HTTP server
});
server.listen(webSocketsServerPort, function () {
    console.log((new Date()) + " Server is listening on port " + webSocketsServerPort);
});

/**
 * WebSocket server
 */
var wsServer = new webSocketServer({
    // WebSocket server is tied to a HTTP server. WebSocket request is just
    // an enhanced HTTP request. For more info http://tools.ietf.org/html/rfc6455#page-6
    httpServer: server
});

// This callback function is called every time someone
// tries to connect to the WebSocket server
wsServer.on('request', function (request) {
    console.log((new Date()) + ' Connection from origin ' + request.origin + '.');

    // accept connection - you should check 'request.origin' to make sure that
    // client is connecting from your website
    // (http://en.wikipedia.org/wiki/Same_origin_policy)
    var connection = request.accept(null, request.origin);

    var uuid = false;

    console.log((new Date()) + ' Connection accepted.');

    //receving messages
    connection.on('message', function (message) {
        if (message.type === 'utf8') { // accept only text
            var jsonReceived = JSON.parse(message.utf8Data);
            if (uuid === false && jsonReceived.UUID) { // Check if UUID is not null or undefined
                console.log('Received UUID: ', jsonReceived.UUID);
                uuid = jsonReceived.UUID;
//                uuid = htmlEntities(message.utf8Data);
                clients.set(jsonReceived.UUID, connection);
                //connection.sendUTF(JSON.stringify({type: 'color', data: userColor}));
                console.log((new Date()) + ' User is known as: ' + uuid);

            } else if (jsonReceived.messageType) {
                console.log((new Date()) + ' Received Message from '
                        + uuid + ': ' + message.utf8Data);

                var messageType = jsonReceived.messageType;
                switch (messageType) {
                    case 'enableLayer':
                        console.log('enableLayer: ', jsonReceived.enabled, ' on mapLiteInstance: ' , jsonReceived.mapLiteInstance);
                        break;
                    case 'cqlFilter':
                        console.log('cqlFilter: ', jsonReceived.cqlFilter, ' on mapLiteInstance: ' , jsonReceived.mapLiteInstance);
                        break;
                }

                // send message to the MapLite clients
                clients.get(jsonReceived.mapLiteInstance).sendUTF(message.utf8Data);
            }
        }
    });

    // user disconnected
    connection.on('close', function (connection) {
        if (uuid !== false) {
            console.log((new Date()) + " Peer "
                    + connection.remoteAddress + " disconnected.");
            // remove user from the list of connected clients
            clients.delete(uuid);
        }
    });

});


