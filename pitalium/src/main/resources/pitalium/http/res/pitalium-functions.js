(function () {
    'use strict';

    var pitalium = window.pitalium;
    if (pitalium) {
        return;
    }

    pitalium = window.pitalium = {};
    pitalium.CAPABILITIES_ID_KEY = 'ptl.capabilitiesId';
    pitalium.REMOTE_HOSTNAME_KEY = 'ptl.remoteHostname';
    pitalium.REMOTE_PORT_KEY = 'ptl.remotePort';

    /**
     * クッキーの値を取得します。
     *
     * @param key
     */
    pitalium._getCookieValue = function (key) {
        var cookies = document.cookie.split('; ');
        for (var i = 0; i < cookies.length; i++) {
            var split = cookies[i].split('=');
            if (split[0] == key) {
                return split[1];
            }
        }

        return null;
    };

    pitalium.capabilitiesId = function (capabilitiesId) {
        if (capabilitiesId) {
            pitalium._capabilitiesId = capabilitiesId;
            document.cookie = pitalium.CAPABILITIES_ID_KEY + '=' + capabilitiesId;
            return;
        }

        if (pitalium._capabilitiesId) {
            return pitalium._capabilitiesId;
        }

        var cookieValue = pitalium._getCookieValue(pitalium.CAPABILITIES_ID_KEY);
        if (!cookieValue) {
            return null;
        }

        pitalium._capabilitiesId = cookieValue;
        return cookieValue;
    };

    pitalium.remoteHostname = function (remoteHostname) {
        if (remoteHostname) {
            pitalium._remoteHostname = remoteHostname;
            document.cookie = pitalium.REMOTE_HOSTNAME_KEY + '=' + remoteHostname;
            return;
        }

        if (pitalium._remoteHostname) {
            return pitalium._remoteHostname;
        }

        var cookieValue = pitalium._getCookieValue(pitalium.REMOTE_HOSTNAME_KEY);
        if (!cookieValue) {
            return null;
        }

        pitalium._remoteHostname = cookieValue;
        return cookieValue;
    };

    pitalium.remotePort = function (remotePort) {
        if (remotePort) {
            pitalium._remotePort = remotePort;
            document.cookie = pitalium.REMOTE_PORT_KEY + '=' + remotePort;
            return;
        }

        if (pitalium._remotePort) {
            return pitalium._remotePort;
        }

        var cookieValue = pitalium._getCookieValue(pitalium.REMOTE_PORT_KEY);
        if (!cookieValue) {
            return null;
        }

        pitalium._remotePort = cookieValue;
        return cookieValue;
    };

    /**
     * リモートホストとポートが設定されたURLを取得します。
     *
     * @param pathAndQuery
     */
    pitalium.getURL = function (pathAndQuery) {
        return 'http://' + pitalium.remoteHostname() + ':' + pitalium.remotePort() + '/' + pathAndQuery;
    };

    /**
     * スクリーンショット撮影を要求します。
     *
     * @param capabilitiesId
     */
    pitalium.sendUnlockRequest = function (capabilitiesId) {
        capabilitiesId = capabilitiesId || pitalium.capabilitiesId();
        if (!capabilitiesId) {
            console.debug('capabilitiesId is required.');
            return;
        }

        var url = pitalium.getURL('unlockThread?id=' + capabilitiesId);
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url);
        xhr.send();
    };
})();
