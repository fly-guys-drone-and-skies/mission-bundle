function endMission() {
    $.ajax("/api/mission", {
        type: 'DELETE'
    });
    
    $("#button-end-mission").prop("disabled", true);
    $("#button-end-mission").html("Ending...");
}

const dronesTable = $('#drones-table');
dronesTable.bootstrapTable({
    data: [],
    formatNoMatches: function() { return "None configured"; }
});

const detectionsTable = $('#detections-table');
detectionsTable.bootstrapTable({
    data: [],
    formatNoMatches: function() { return "None yet"; }
});

var detectionID = null;
const detections = {};
setInterval(function() {
    $.get("/api/mission/detections", function(data) {
        detectionsTable.bootstrapTable('removeAll');
        for (detection of data) {
            detections[detection['id']] = detection;
            detectionsTable.bootstrapTable('append', [{
                "id": detection['id'],
                "timestamp": new Date(detection['timestampMillis']).toISOString(),
                "location": detection['location']['latitude'] + ", " + detection['location']['longitude'],
                "confidence": detection['confidence'] * 100 + "%",
                "view": '<button type="button" class="btn btn-primary button-view" data-bs-toggle="modal" data-bs-target="#view-detection-modal">View</button>'
            }]);
        }
        
        $('.button-view').on('click', function(event) {
            // TODO: this is a little sketchy
            detectionID = event.target.parentElement.parentElement.querySelector("td").textContent;

            document.getElementById("image-detection").setAttribute("src", "/api/mission/detections/" + detectionID);
        });
    }, "json");
}, 3000); // 3000ms = 3 seconds


$("#button-start-mission").click(function() {
    $.ajax("/api/mission", {
        data: JSON.stringify({
            area: {
                vertices:
                    drawnItems.getLayers()[0]
                        .getLatLngs()[0]
                        .map(function(latLng) {
                            return {
                                "latitude": latLng['lat'],
                                "longitude": latLng['lng']
                            }
                        })
            },
            minAltitudeMeters: parseFloat($("#input-mission-altitude").val()),
            altitudeSeparationMeters: parseFloat($("#input-mission-altitude-sep").val()),
            drones: dronesTable.bootstrapTable('getData').map(function(row) {
                return row['host'] + ":" + row['port'];
            })
        }),
        contentType: 'application/json',
        type: 'POST'
    });
    
    // Swap start and end buttons, disable adding drones
    $("#button-start-mission").addClass("hidden");
    $("#button-end-mission").removeClass("hidden");
    $("#button-open-add-drone-modal").prop("disabled", true);
});

$("#button-end-mission").click(function() {
    endMission();
});

const addDroneSubmit = $("#button-add-drone");
addDroneSubmit.click(function() {
    const droneHost = $("#input-drone-host").val();
    const dronePort = $("#input-drone-port").val();
    
    dronesTable.bootstrapTable('append', [{"host": droneHost, "port": dronePort}]);
});


$("#button-valid-detection").click(function() {
    // Add detection location to map
    const location = detections[detectionID].location;
    L.marker([location.latitude, location.longitude]).addTo(map);

    endMission();
});

$("#button-focus-detection").click(function() {
    $.ajax("/api/mission/detections/" + detectionID + "/focus", {
        type: 'POST'
    });
});
