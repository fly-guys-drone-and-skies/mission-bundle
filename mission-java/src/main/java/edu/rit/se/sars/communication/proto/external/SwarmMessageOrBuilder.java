// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: external/External.proto

package edu.rit.se.sars.communication.proto.external;

public interface SwarmMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:SwarmMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.SwarmMessage.AcknowledgementMessage acknowledgement = 1;</code>
   * @return Whether the acknowledgement field is set.
   */
  boolean hasAcknowledgement();
  /**
   * <code>.SwarmMessage.AcknowledgementMessage acknowledgement = 1;</code>
   * @return The acknowledgement.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.AcknowledgementMessage getAcknowledgement();
  /**
   * <code>.SwarmMessage.AcknowledgementMessage acknowledgement = 1;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.AcknowledgementMessageOrBuilder getAcknowledgementOrBuilder();

  /**
   * <code>.SwarmMessage.PingMessage ping = 2;</code>
   * @return Whether the ping field is set.
   */
  boolean hasPing();
  /**
   * <code>.SwarmMessage.PingMessage ping = 2;</code>
   * @return The ping.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.PingMessage getPing();
  /**
   * <code>.SwarmMessage.PingMessage ping = 2;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.PingMessageOrBuilder getPingOrBuilder();

  /**
   * <code>.SwarmMessage.MissionDefinitionMessage missionDefinition = 3;</code>
   * @return Whether the missionDefinition field is set.
   */
  boolean hasMissionDefinition();
  /**
   * <code>.SwarmMessage.MissionDefinitionMessage missionDefinition = 3;</code>
   * @return The missionDefinition.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.MissionDefinitionMessage getMissionDefinition();
  /**
   * <code>.SwarmMessage.MissionDefinitionMessage missionDefinition = 3;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.MissionDefinitionMessageOrBuilder getMissionDefinitionOrBuilder();

  /**
   * <code>.SwarmMessage.CoverageStateMessage coverageState = 4;</code>
   * @return Whether the coverageState field is set.
   */
  boolean hasCoverageState();
  /**
   * <code>.SwarmMessage.CoverageStateMessage coverageState = 4;</code>
   * @return The coverageState.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.CoverageStateMessage getCoverageState();
  /**
   * <code>.SwarmMessage.CoverageStateMessage coverageState = 4;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.CoverageStateMessageOrBuilder getCoverageStateOrBuilder();

  /**
   * <code>.SwarmMessage.NewPointTaskMessage newPoint = 5;</code>
   * @return Whether the newPoint field is set.
   */
  boolean hasNewPoint();
  /**
   * <code>.SwarmMessage.NewPointTaskMessage newPoint = 5;</code>
   * @return The newPoint.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.NewPointTaskMessage getNewPoint();
  /**
   * <code>.SwarmMessage.NewPointTaskMessage newPoint = 5;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.NewPointTaskMessageOrBuilder getNewPointOrBuilder();

  /**
   * <code>.SwarmMessage.LocationQueryMessage locationQuery = 6;</code>
   * @return Whether the locationQuery field is set.
   */
  boolean hasLocationQuery();
  /**
   * <code>.SwarmMessage.LocationQueryMessage locationQuery = 6;</code>
   * @return The locationQuery.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.LocationQueryMessage getLocationQuery();
  /**
   * <code>.SwarmMessage.LocationQueryMessage locationQuery = 6;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.LocationQueryMessageOrBuilder getLocationQueryOrBuilder();

  /**
   * <code>.SwarmMessage.LocationStatusMessage locationStatus = 7;</code>
   * @return Whether the locationStatus field is set.
   */
  boolean hasLocationStatus();
  /**
   * <code>.SwarmMessage.LocationStatusMessage locationStatus = 7;</code>
   * @return The locationStatus.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.LocationStatusMessage getLocationStatus();
  /**
   * <code>.SwarmMessage.LocationStatusMessage locationStatus = 7;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.LocationStatusMessageOrBuilder getLocationStatusOrBuilder();

  /**
   * <code>.SwarmMessage.PointCompletedMessage pointCompleted = 8;</code>
   * @return Whether the pointCompleted field is set.
   */
  boolean hasPointCompleted();
  /**
   * <code>.SwarmMessage.PointCompletedMessage pointCompleted = 8;</code>
   * @return The pointCompleted.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.PointCompletedMessage getPointCompleted();
  /**
   * <code>.SwarmMessage.PointCompletedMessage pointCompleted = 8;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.PointCompletedMessageOrBuilder getPointCompletedOrBuilder();

  /**
   * <code>.SwarmMessage.TargetDetectionMessage targetDetection = 9;</code>
   * @return Whether the targetDetection field is set.
   */
  boolean hasTargetDetection();
  /**
   * <code>.SwarmMessage.TargetDetectionMessage targetDetection = 9;</code>
   * @return The targetDetection.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.TargetDetectionMessage getTargetDetection();
  /**
   * <code>.SwarmMessage.TargetDetectionMessage targetDetection = 9;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.TargetDetectionMessageOrBuilder getTargetDetectionOrBuilder();

  /**
   * <code>.SwarmMessage.FocusDetectionCandidateMessage focusDetectionCandidate = 10;</code>
   * @return Whether the focusDetectionCandidate field is set.
   */
  boolean hasFocusDetectionCandidate();
  /**
   * <code>.SwarmMessage.FocusDetectionCandidateMessage focusDetectionCandidate = 10;</code>
   * @return The focusDetectionCandidate.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.FocusDetectionCandidateMessage getFocusDetectionCandidate();
  /**
   * <code>.SwarmMessage.FocusDetectionCandidateMessage focusDetectionCandidate = 10;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.FocusDetectionCandidateMessageOrBuilder getFocusDetectionCandidateOrBuilder();

  /**
   * <code>.SwarmMessage.FocusDetectionMessage focusDetection = 11;</code>
   * @return Whether the focusDetection field is set.
   */
  boolean hasFocusDetection();
  /**
   * <code>.SwarmMessage.FocusDetectionMessage focusDetection = 11;</code>
   * @return The focusDetection.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.FocusDetectionMessage getFocusDetection();
  /**
   * <code>.SwarmMessage.FocusDetectionMessage focusDetection = 11;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.FocusDetectionMessageOrBuilder getFocusDetectionOrBuilder();

  /**
   * <code>.SwarmMessage.ReturnHomeMessage returnHome = 12;</code>
   * @return Whether the returnHome field is set.
   */
  boolean hasReturnHome();
  /**
   * <code>.SwarmMessage.ReturnHomeMessage returnHome = 12;</code>
   * @return The returnHome.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.ReturnHomeMessage getReturnHome();
  /**
   * <code>.SwarmMessage.ReturnHomeMessage returnHome = 12;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.ReturnHomeMessageOrBuilder getReturnHomeOrBuilder();

  /**
   * <code>.SwarmMessage.RemoveDroneMessage removeDrone = 13;</code>
   * @return Whether the removeDrone field is set.
   */
  boolean hasRemoveDrone();
  /**
   * <code>.SwarmMessage.RemoveDroneMessage removeDrone = 13;</code>
   * @return The removeDrone.
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.RemoveDroneMessage getRemoveDrone();
  /**
   * <code>.SwarmMessage.RemoveDroneMessage removeDrone = 13;</code>
   */
  edu.rit.se.sars.communication.proto.external.SwarmMessage.RemoveDroneMessageOrBuilder getRemoveDroneOrBuilder();

  public edu.rit.se.sars.communication.proto.external.SwarmMessage.MessageCase getMessageCase();
}
