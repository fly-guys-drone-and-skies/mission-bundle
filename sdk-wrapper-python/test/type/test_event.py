from type.event import *
from test_util import check_protobuf_serde


def test_serde_BatteryStatusEvent():
    check_protobuf_serde(
        BatteryStatusEvent(
            percentage_remaining=75.0
        ),
        DroneEventDeserializer()
    )


def test_serde_PositionStatusEvent():
    check_protobuf_serde(
        PositionStatusEvent(
            location=GeoLocation3D(
                latitude_deg=-46.4,
                longitude_deg=35.0,
                altitude_relative_m=54.2
            )
        ),
        DroneEventDeserializer()
    )


def test_serde_OrientationStatusEvent():
    check_protobuf_serde(
        OrientationStatusEvent(
            orientation=Orientation(
                pitch_deg=-46.4,
                yaw_deg=35.0,
                roll_deg=54.2
            )
        ),
        DroneEventDeserializer()
    )


def test_serde_ConnectionStateEvent():
    check_protobuf_serde(
        ConnectionStateEvent(is_connected=True),
        DroneEventDeserializer()
    )
