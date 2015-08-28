if (window.pitalium) {
    return;
}

var el = document.createElement('script');
el.type = 'text/javascript';
el.src = '//${host}:${port}/res/pitalium-functions.js';
el.onload = function () {
    pitalium.capabilitiesId('${capabilitiesId}');
    pitalium.remoteHostname('${host}');
    pitalium.remotePort('${port}');
};

document.head.appendChild(el);