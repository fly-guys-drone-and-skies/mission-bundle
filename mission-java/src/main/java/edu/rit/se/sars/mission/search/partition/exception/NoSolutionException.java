package edu.rit.se.sars.mission.search.partition.exception;

/**
 * Exception for no solution reached in optimization/constraint solvers
 */
public class NoSolutionException extends Exception {
    public NoSolutionException() {
        super("No solution found");
    }
}
