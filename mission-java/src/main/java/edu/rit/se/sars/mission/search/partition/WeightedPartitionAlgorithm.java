//package edu.rit.se.sars.mission.search.partition;
//
//import com.google.ortools.linearsolver.MPConstraint;
//import com.google.ortools.linearsolver.MPObjective;
//import com.google.ortools.linearsolver.MPSolver;
//import com.google.ortools.linearsolver.MPVariable;
//import edu.rit.se.sars.domain.GeoLocation2D;
//import edu.rit.se.sars.mission.search.partition.exception.NoSolutionException;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * MILP optimization load balancing implementation
// * Based on:
// *      J. Modares, F. Ghanei, N. Mastronarde and K. Dantu, "UB-ANC planner: Energy efficient coverage path planning
// *      with multiple drones," 2017 IEEE International Conference on Robotics and Automation (ICRA), 2017, pp.
// *      6182-6189, doi: 10.1109/ICRA.2017.7989732.
// */
//public class WeightedPartitionAlgorithm implements PointPartitionAlgorithm {
//    @Override
//    public List<List<GeoLocation2D>> partition(List<GeoLocation2D> points, List<GeoLocation2D> droneLocations) throws NoSolutionException {
//        int minPoints = (int) (points.size() / (double) droneLocations.size());
//
//        // Attempt to solve optimization with increasingly relaxed 'fairness' in terms of number of points
//        for (int maxPoints = minPoints; maxPoints <= points.size(); maxPoints++) {
//            MPSolver solver = new MPSolver("Solver", MPSolver.OptimizationProblemType.GLOP_LINEAR_PROGRAMMING);
//            MPObjective objective = solver.objective();
//            objective.setMinimization();
//
//            // Setup boolean variables for each point
//            MPVariable[][] nodeBoolArray = new MPVariable[droneLocations.size()][points.size()];
//            for (int a = 0; a < droneLocations.size(); a++) {
//                for (int i = 0; i < points.size(); i++) {
//                    // Weight of including this point is the drone's starting distance to it
//                    double distance = points.get(i).distMeters(droneLocations.get(a));
//
//                    nodeBoolArray[a][i] = solver.makeBoolVar(String.format("n%d%d", a, i));
//                    objective.setCoefficient(nodeBoolArray[a][i], distance);
//                }
//            }
//
//            // Exclusivity constraint - each point must only be assigned to one drone
//            for (int i = 0; i < points.size(); i++) {
//                MPConstraint constraint = solver.makeConstraint(1, 1, String.format("c%d", i));
//                for (int a = 0; a < droneLocations.size(); a++) {
//                    constraint.setCoefficient(nodeBoolArray[a][i], 1);
//                }
//            }
//
//            // Fairness constraint - try to give drones near-equal number of points
//            for (int a = 0; a < droneLocations.size(); a++) {
//                MPConstraint constraint = solver.makeConstraint(minPoints, maxPoints, String.format("ac%d", a));
//                for (int i = 0; i < points.size(); i++) {
//                    constraint.setCoefficient(nodeBoolArray[a][i], 1);
//                }
//            }
//
//            final MPSolver.ResultStatus resultStatus = solver.solve();
//
//            if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
//                // Optimization successful, retrieve output from solver
//                List<List<GeoLocation2D>> output = new ArrayList<>();
//
//                for (int a = 0; a < droneLocations.size(); a++) {
//                    List<GeoLocation2D> droneOutput = new ArrayList<>();
//                    for (int i = 0; i < points.size(); i++) {
//                        if (nodeBoolArray[a][i].solutionValue() == 1) {
//                            // If boolean variable is true, optimizer assigned point to given drone
//                            droneOutput.add(points.get(i));
//                        }
//                    }
//                    output.add(droneOutput);
//                }
//                System.out.println("GETTING TO A PARTITION");
//                return output;
//            }
//        }
//
//        throw new NoSolutionException();
//    }
//}

package edu.rit.se.sars.mission.search.partition;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.mission.search.partition.exception.NoSolutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * MILP optimization load balancing implementation
 * Based on:
 *      J. Modares, F. Ghanei, N. Mastronarde and K. Dantu, "UB-ANC planner: Energy efficient coverage path planning
 *      with multiple drones," 2017 IEEE International Conference on Robotics and Automation (ICRA), 2017, pp.
 *      6182-6189, doi: 10.1109/ICRA.2017.7989732.
 */
public class WeightedPartitionAlgorithm implements PointPartitionAlgorithm{

    @Override
    public List<List<GeoLocation2D>> partition(List<GeoLocation2D> points, List<GeoLocation2D> droneLocations) throws NoSolutionException {

        //placeholder code while jniortools continues to show issues
        int distribute = (int) (points.size() / (double) droneLocations.size());
        List<List<GeoLocation2D>> output = new ArrayList<>();

        for (int i = 0; i < droneLocations.size(); i++) {
            List<GeoLocation2D> flightpath = new ArrayList<>();
            for (int j = 0; j < distribute; j++) {
                if (!points.isEmpty()) {
                    flightpath.add(points.remove(0));
                }
            }
            output.add(flightpath);
        }

        return output;



//        int minPoints = (int) (points.size() / (double) droneLocations.size());
//
//        // Attempt to solve optimization with increasingly relaxed 'fairness' in terms of number of points
//        for (int maxPoints = minPoints; maxPoints <= points.size(); maxPoints++) {
//            MPSolver solver = new MPSolver("Solver", MPSolver.OptimizationProblemType.GLOP_LINEAR_PROGRAMMING);
//            MPObjective objective = solver.objective();
//            objective.setMinimization();
//
//            // Setup boolean variables for each point
//            MPVariable[][] nodeBoolArray = new MPVariable[droneLocations.size()][points.size()];
//            for (int a = 0; a < droneLocations.size(); a++) {
//                for (int i = 0; i < points.size(); i++) {
//                    // Weight of including this point is the drone's starting distance to it
//                    double distance = points.get(i).distMeters(droneLocations.get(a));
//
//                    nodeBoolArray[a][i] = solver.makeBoolVar(String.format("n%d%d", a, i));
//                    objective.setCoefficient(nodeBoolArray[a][i], distance);
//                }
//            }
//
//            // Exclusivity constraint - each point must only be assigned to one drone
//            for (int i = 0; i < points.size(); i++) {
//                MPConstraint constraint = solver.makeConstraint(1, 1, String.format("c%d", i));
//                for (int a = 0; a < droneLocations.size(); a++) {
//                    constraint.setCoefficient(nodeBoolArray[a][i], 1);
//                }
//            }
//
//            // Fairness constraint - try to give drones near-equal number of points
//            for (int a = 0; a < droneLocations.size(); a++) {
//                MPConstraint constraint = solver.makeConstraint(minPoints, maxPoints, String.format("ac%d", a));
//                for (int i = 0; i < points.size(); i++) {
//                    constraint.setCoefficient(nodeBoolArray[a][i], 1);
//                }
//            }
//
//            final MPSolver.ResultStatus resultStatus = solver.solve();
//
//            if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
//                // Optimization successful, retrieve output from solver
//                List<List<GeoLocation2D>> output = new ArrayList<>();
//
//                for (int a = 0; a < droneLocations.size(); a++) {
//                    List<GeoLocation2D> droneOutput = new ArrayList<>();
//                    for (int i = 0; i < points.size(); i++) {
//                        if (nodeBoolArray[a][i].solutionValue() == 1) {
//                            // If boolean variable is true, optimizer assigned point to given drone
//                            droneOutput.add(points.get(i));
//                        }
//                    }
//                    output.add(droneOutput);
//                }
//                return output;
//            }
//        }
//
//        throw new NoSolutionException();
    }
}
