$(function () {
    "use strict";
    var webSocketServer = 'ws://127.0.0.1:1337';
    var mapLiteURl = 'http://127.0.0.1:8888/geoSDIMapLite.html';
    var mapID = '56-50';
    var gpStackHost = 'http://150.145.133.106:8080';
    var legendPanelVisibility = true;
    var geocodingVisibility = false;
    var sharePanelVisibility = false;
//    var mapLiteURl = 'http://maplite.geosdi.org/maplite/';
//    var mapID = '4110-215';

    var UUID = function () {
        // Math.random should be unique because of its seeding algorithm.
        // Convert it to base 36 (numbers + letters), and grab the first 9 characters
        // after the decimal.
        return Math.random().toString(36).substr(2, 10);
    };

    // if user is running mozilla then use it's built-in WebSocket
    window.WebSocket = window.WebSocket || window.MozWebSocket;

    // for better performance - to avoid searching in DOM
    var mapLite = $('#mapLite');

    // if browser doesn't support WebSocket, just show some notification and exit
    if (!window.WebSocket) {
        mapLite.html($('<p>', {text: 'Sorry, but your browser doesn\'t '
                    + 'support WebSockets.'}));
        $('span').hide();
        return;
    }

    var uuid = UUID();
    console.log('Generated: ', uuid);
    //Generating mapLite iframe
    var iframe = '<iframe src="' + mapLiteURl + '?mapID=' + mapID +
            '&x=7.519188691882006&y=43.328777606327&zoom=5&uuid=' + uuid +
            '&geocodingVisibility=' + geocodingVisibility +
            '&legendPanelVisibility=' + legendPanelVisibility +
            '&sharePanelVisibility=' + sharePanelVisibility +
            '" width="100%" height="600px"></iframe>';
    console.log(iframe);
    $('#mapLite').append(iframe);

    var errorDisplayed = false;

    // open connection
    var connection = new WebSocket(webSocketServer);

    connection.onopen = function () {
//        connection.send(JSON.stringify({type: 'UUID', data: uuid}));
    };

    connection.onerror = function (error) {
        if (!errorDisplayed) {
            alert('Sorry, but there is some problem with your '
                    + 'connection or the server is down.');
            errorDisplayed = true;
            // just in there were some problems with conenction...
//        mapLite.html($('<p>', {text: 'Sorry, but there\'s some problem with your '
//                    + 'connection or the server is down.'}));
        }
    };

    // most important part - incoming messages
    connection.onmessage = function (message) {
        // try to parse JSON message. Because we know that the server always returns
        // JSON this should work without any problem but we should make sure that
        // the massage is not chunked or otherwise damaged.
        try {
            var json = JSON.parse(message.data);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }
    };

    /**
     * This method is optional. If the server wasn't able to respond to the
     * in 3 seconds then show some error message to notify the user that
     * something is wrong.
     */
    setInterval(function () {
        if (connection.readyState !== 1 && !errorDisplayed) {
            alert('Sorry, but there is some problem with your '
                    + 'connection or the server is down.');
            errorDisplayed = true;
//            mapLite.html($('<p>', {text: 'Sorry, but there\'s some problem with your '
//                        + 'connection or the server is down.'}));
        }
    }, 5000);

    window.sendCQLFilter = function (layerName, cqlFilter) {
        console.log('Send CQL Filter called');
        connection.send(JSON.stringify(
                {
                    messageType: 'cqlFilter',
                    layerName: layerName,
                    cqlFilter: cqlFilter,
                    mapLiteInstance: uuid      //mapLiteInstance
                }
        ));
    }

    window.enableLayer = function (layerName, enabled) {
        console.log('enableLayer called');
        connection.send(JSON.stringify(
                {
                    messageType: 'enableLayer',
                    layerName: layerName,
                    enabled: enabled,
                    mapLiteInstance: uuid      //mapLiteInstance
                }
        ));
    }

    var projectID = mapID.split('-')[0];
    $.getJSON(gpStackHost + "/geoplatform-service/jsonCore/layers/getFirstLevelLayers/" + projectID, function (result) {
//        console.log(result.ShortLayerDTOContainer);
        if (result.ShortLayerDTOContainer.layer) {
            $.each(result.ShortLayerDTOContainer.layer, function (i, layer) {
                $("#layerName").append('<option value="' + layer.name + '">' + layer.name + '</option>');
            });
        }
    });
});
