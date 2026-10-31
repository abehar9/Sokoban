package sokoban;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PDDLProblemGenerator {

    private final List<String> map;
    private final int height;
    private final int width;
    private final int playerX;
    private final int playerY;
    private final List<int[]> boxes;

    private final Map<String, String> positions = new HashMap<>();
    private final List<String> goals = new ArrayList<>();
    private final List<String> sortedPositionKeys = new ArrayList<>();

    private static final int[][] DIRECTIONS = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
    private static final String[] PREDICATES = {"adjacent-up", "adjacent-down", "adjacent-left", "adjacent-right"};

    public PDDLProblemGenerator(
            List<String> map,
            int playerX,
            int playerY,
            List<int[]> boxes) {

        this.map = map;
        this.height = map.size();
        this.width = map.stream().mapToInt(String::length).max().orElse(0);
        this.playerX = playerX;
        this.playerY = playerY;
        this.boxes = boxes;

        computeWalkableTiles();
    }

    private char at(int x, int y) {
        if (y < 0 || y >= height) return '#';
        String row = map.get(y);
        if (x < 0 || x >= row.length()) return '#';
        return row.charAt(x);
    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    private void computeWalkableTiles() {
        Set<String> visited = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{playerX, playerY});
        visited.add(key(playerX, playerY));

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int cx = curr[0];
            int cy = curr[1];

            for (int[] d : DIRECTIONS) {
                int nx = cx + d[0];
                int ny = cy + d[1];
                String nk = key(nx, ny);

                if (at(nx, ny) != '#' && !visited.contains(nk)) {
                    visited.add(nk);
                    queue.add(new int[]{nx, ny});
                }
            }
        }

        List<int[]> coords = new ArrayList<>();
        for (String k : visited) {
            String[] parts = k.split(",");
            coords.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
        }

        coords.sort((a, b) -> {
            if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
            return Integer.compare(a[0], b[0]);
        });

        int nextPosition = 0;
        for (int[] c : coords) {
            String k = key(c[0], c[1]);
            String pName = "p" + nextPosition++;
            positions.put(k, pName);
            sortedPositionKeys.add(k);

            char ch = at(c[0], c[1]);
            if (ch == '.' || ch == '*' || ch == '+') {
                goals.add(pName);
            }
        }
    }

    private String position(int x, int y) {
        return positions.get(key(x, y));
    }

    public void write(Path output) throws IOException {
        StringBuilder out = new StringBuilder();

        out.append("(define (problem sokoban-level)\n");
        out.append("  (:domain Sokoban)\n"); // Matches new domain name

        // 1. Define Objects
        out.append("  (:objects\n    ");
        out.append(String.join(" ", positions.values()));
        out.append(" - position\n    ");

        List<String> boxNames = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            boxNames.add("b" + i);
        }
        out.append(String.join(" ", boxNames)).append(" - box\n  )\n");

        // 2. Initial State Definitions
        out.append("  (:init\n");
        out.append("    (player-at ").append(position(playerX, playerY)).append(")\n");

        for (int i = 0; i < boxes.size(); i++) {
            int[] b = boxes.get(i);
            out.append("    (box-at b").append(i).append(" ").append(position(b[0], b[1])).append(")\n");
        }

        // Free tiles
        for (String k : sortedPositionKeys) {
            String[] xy = k.split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);

            boolean occupied = (x == playerX && y == playerY);
            for (int[] b : boxes) {
                if (b[0] == x && b[1] == y) {
                    occupied = true;
                    break;
                }
            }

            if (!occupied) {
                out.append("    (free ").append(positions.get(k)).append(")\n");
            }
        }

        // Directional Adjacency Connections
        for (String k : sortedPositionKeys) {
            String[] xy = k.split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            String currentPos = positions.get(k);

            for (int d = 0; d < DIRECTIONS.length; d++) {
                int nx = x + DIRECTIONS[d][0];
                int ny = y + DIRECTIONS[d][1];
                String nk = key(nx, ny);

                if (positions.containsKey(nk)) {
                    out.append("    (").append(PREDICATES[d]).append(" ")
                            .append(currentPos).append(" ")
                            .append(positions.get(nk)).append(")\n");
                }
            }
        }

        out.append("  )\n");

        // 3. Goal Definition
        out.append("  (:goal (and\n");
        int limit = Math.min(boxes.size(), goals.size());
        for (int i = 0; i < limit; i++) {
            out.append("    (box-at b").append(i).append(" ").append(goals.get(i)).append(")\n");
        }
        out.append("  ))\n)\n");

        Files.write(output, output.toString().getBytes(StandardCharsets.UTF_8)); // Fixed output string target
        Files.write(output, out.toString().getBytes(StandardCharsets.UTF_8));
    }
}