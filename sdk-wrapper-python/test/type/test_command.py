from type.command import *
from test_util import get_random_uuid, check_protobuf_serde


def test_serde_TakeoffCommand():
    check_protobuf_serde(
        TakeoffCommand(
            uuid=get_random_uuid()
        ),
        CommandDeserializer()
    )


def test_serde_LandCommand():
    check_protobuf_serde(
        LandCommand(
            uuid=get_random_uuid()
        ),
        CommandDeserializer()
    )


def test_serde_MoveToCommand_without_heading():
    check_protobuf_serde(
        MoveToCommand(
            uuid=get_random_uuid(),
            to_location=GeoLocation3D(
                latitude_deg=36.9123123,
                longitude_deg=-50.64343,
                altitude_relative_m=0.57
            )
        ),
        CommandDeserializer()
    )


def test_serde_MoveToCommand_with_heading():
    check_protobuf_serde(
        MoveToCommand(
            uuid=get_random_uuid(),
            to_location=GeoLocation3D(
                latitude_deg=36.9123123,
                longitude_deg=-50.64343,
                altitude_relative_m=0.57
            ),
            heading_deg=123.45
        ),
        CommandDeserializer()
    )


def test_serde_MoveGimbalCommand():
    check_protobuf_serde(
        MoveGimbalCommand(
            uuid=get_random_uuid(),
            to_orientation=Orientation(
                pitch_deg=50.0,
                yaw_deg=35.6,
                roll_deg=-5.1
            )
        )
    )


def test_serde_ChangeZoomLevelCommand():
    check_protobuf_serde(
        ChangeZoomLevelCommand(
            uuid=get_random_uuid(),
            zoom_level=1.3
        )
    )


def test_serde_GetPositionCommand():
    check_protobuf_serde(
        GetPositionCommand(
            uuid=get_random_uuid()
        )
    )


def test_serde_GetOrientationCommand():
    check_protobuf_serde(
        GetOrientationCommand(
            uuid=get_random_uuid()
        )
    )


def test_serde_GetConnectionStateCommand():
    check_protobuf_serde(
        GetConnectionStateCommand(
            uuid=get_random_uuid()
        )
    )
