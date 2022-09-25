L.mapquest.key = '5WQhhfexNn7ZYyiKLhONZliWZFCSdWqC';
L.mapquest.open = true; // Use open-source data (i.e. not licensed)

var baseLayer = L.mapquest.tileLayer('hybrid'); // satelite + map

var map = L.mapquest.map('map', {
    center: [43.1566, -77.6088], // Rochester
    layers: baseLayer,
    zoom: 12
});

navigator.geolocation.getCurrentPosition((pos) => {
    console.log(pos)
    const lat = pos.coords.latitude;
    const long = pos.coords.longitude;
    map.setView(new L.LatLng(lat, long),16)
},(err) => {
    console.log(err)
})

L.control.layers({
    'Map': baseLayer
}).addTo(map);

const drawnItems = L.featureGroup().addTo(map);

// Controls for drawing polygon and rectangle ONLY
const drawControlFull = new L.Control.Draw({
    edit: false,
    draw: {
        polygon: {
            allowIntersection: false,
            showArea: true
        },
        marker: false,
        polyline: false,
        circle: false,
        circlemarker: false
    }
});

// Controls for only editing existing elements
const drawControlEditOnly = new L.Control.Draw({
    edit: {
        featureGroup: drawnItems,
        poly: {
            allowIntersection: false
        }
    },
    draw: false
});

map.addControl(drawControlFull); // Start by permitting drawing

map.on(L.Draw.Event.CREATED, function (event) {
    const layer = event.layer;

    drawnItems.addLayer(layer);

    // Don't let user draw another shape
    map.removeControl(drawControlFull);
    map.addControl(drawControlEditOnly);
});

map.on(L.Draw.Event.DELETED, function(e) {
    if (drawnItems.getLayers().length === 0){
        // Existing shape was deleted, allow user to draw another
        map.removeControl(drawControlEditOnly);
        map.addControl(drawControlFull);
    }
});