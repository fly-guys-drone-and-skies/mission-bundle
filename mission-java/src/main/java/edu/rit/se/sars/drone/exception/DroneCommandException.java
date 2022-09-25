package edu.rit.se.sars.drone.exception;

public class DroneCommandException extends RuntimeException {
    public DroneCommandException(Exception exception) {
        super(exception);
    }
}
