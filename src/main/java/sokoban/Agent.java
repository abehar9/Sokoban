package sokoban;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Agent {

    private static final String DOMAIN_FILE = "pddl/sokoban.pddl";
    private static final String PROBLEM_FILE = "target/sokoban-level.pddl";

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        String solution = "";
        int nextMove = 0;
        int boxCount = 0;
        boolean initialized = false;

        try {
            String line;

            // Keep running and handle every turn.
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");

                // Turn 1: Initial full board setup (width, height, boxCount)
                if (parts.length == 3 && !initialized) {
                    int width = Integer.parseInt(parts[0]);
                    int height = Integer.parseInt(parts[1]);
                    boxCount = Integer.parseInt(parts[2]);

                    List<String> map = new ArrayList<String>();
                    for (int y = 0; y < height; y++) {
                        String row = reader.readLine();
                        if (row == null) {
                            throw new IllegalArgumentException("Missing board row");
                        }
                        map.add(row);
                    }

                    int[] player = readPosition(reader);
                    List<int[]> boxes = new ArrayList<int[]>();
                    for (int i = 0; i < boxCount; i++) {
                        boxes.add(readPosition(reader));
                    }

                    // Generate problem and solve ONCE on turn 1
                    Path problemPath = Paths.get(PROBLEM_FILE);
                    Files.createDirectories(problemPath.getParent());

                    PDDLProblemGenerator generator =
                            new PDDLProblemGenerator(map, player[0], player[1], boxes);

                    generator.write(problemPath);

                    solution = SokobanSolver.solve(
                            Paths.get(DOMAIN_FILE),
                            problemPath);

                    System.err.println("DEBUG SOLVED PLAN: " + solution);

                    if (solution == null) {
                        solution = "";
                    }

                    initialized = true;

                } 
                // Turn 2+: Game updates positions each tick (width, height sent, or box updates)
                else if (initialized) {
                    // Depending on codinGame format, consume the incoming frame coordinates 
                    // so the reader stays synchronized without resetting the solution.
                    if (parts.length == 2) {
                        // Consumes player or layout positions if sent as 2 parts, 
                        // or if box positions follow, loop through boxCount.
                        for (int i = 0; i < boxCount; i++) {
                            reader.readLine();
                        }
                    } else if (parts.length == 3) {
                        int height = Integer.parseInt(parts[1]);
                        for (int y = 0; y < height; y++) {
                            reader.readLine();
                        }
                        int[] player = readPosition(reader);
                        for (int i = 0; i < boxCount; i++) {
                            readPosition(reader);
                        }
                    }
                } 
                else {
                    System.err.println("Unexpected input before initialization: " + line);
                    System.out.println("U");
                    System.out.flush();
                    continue;
                }

                // Output exactly one move per turn from our pre-computed solution
                if (nextMove < solution.length()) {
                    System.out.println(solution.charAt(nextMove));
                    nextMove++;
                } else {
                    System.err.println("No remaining moves in the solution.");
                    if (!solution.isEmpty()) {
                        System.out.println(solution.charAt(solution.length() - 1));
                    } else {
                        System.out.println("U");
                    }
                }

                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static int[] readPosition(
            BufferedReader reader) throws Exception {

        String line = reader.readLine();

        if (line == null) {
            throw new IllegalArgumentException("Missing position coordinates");
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length < 2) {
            return new int[] { 0, 0 }; // Safe fallback if a single coordinate or blank line is read
        }

        return new int[] {
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
        };
    }
}