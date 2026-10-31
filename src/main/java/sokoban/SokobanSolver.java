package sokoban;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.operator.Action;

import java.nio.file.Path;
import java.util.List;

public class SokobanSolver {

    public static String solve(Path domainPath, Path problemPath) {
        try {
            HSP planner = new HSP();
            //planner.setHeuristicType(fr.uga.pddl4j.heuristics.state.Heuristic.Type.FAST_FORWARD);
            planner.setDomain(domainPath.toAbsolutePath().toString());
            planner.setProblem(problemPath.toAbsolutePath().toString());

            Plan plan = planner.solve();

            if (plan != null && !plan.isEmpty()) {
                StringBuilder solutionString = new StringBuilder();
                
                // Use plan.actions() to get the list of actions
                List<Action> actions = plan.actions();
                for (Action action : actions) {
                    if (action != null) {
                        String name = action.getName().toLowerCase();
                        System.err.println("Action Name: " + name);

                        if (name.contains("up")) solutionString.append("U");
                        else if (name.contains("down")) solutionString.append("D");
                        else if (name.contains("left")) solutionString.append("L");
                        else if (name.contains("right")) solutionString.append("R");
                    }
                }

                String finalSolution = solutionString.toString();
                System.err.println("Generated Solution Plan: " + finalSolution);
                return finalSolution;
            } else {
                System.err.println("No plan found by the planner.");
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
}