from type.domain import GeoLocation3D, Orientation
from test_util import check_protobuf_serde


def test_serde_GeoLocation3D():
    check_protobuf_serde(
        GeoLocation3D(
            latitude_deg=36.9,
            longitude_deg=-50.10,
            altitude_relative_m=150.5
        )
    )


def test_serde_Orientation():
    check_protobuf_serde(
        Orientation(
            pitch_deg=15.5,
            yaw_deg=-30.5,
            roll_deg=-100.5
        )
    )
