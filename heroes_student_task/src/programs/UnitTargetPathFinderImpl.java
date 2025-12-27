package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;
import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    public Object printBattleLog;
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{1,0},{-1,0},{0,1},{0,-1}};

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit.equals(targetUnit) || areAdjacent(attackUnit, targetUnit)) {
            return Collections.emptyList();
        }

        boolean[][] obstacles = new boolean[WIDTH][HEIGHT];
        if (existingUnitList != null) {
            for (Unit unit : existingUnitList) {
                if (unit != null && unit.isAlive() && !unit.equals(attackUnit) && !unit.equals(targetUnit)) {
                    int x = unit.getxCoordinate();
                    int y = unit.getyCoordinate();
                    if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                        obstacles[x][y] = true;
                    }
                }
            }
        }

        return aStarSearch(
                attackUnit.getxCoordinate(), attackUnit.getyCoordinate(),
                targetUnit.getxCoordinate(), targetUnit.getyCoordinate(),
                obstacles
        );
    }

    private List<Edge> aStarSearch(int startX, int startY, int targetX, int targetY, boolean[][] obstacles) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        Map<String, Node> allNodes = new HashMap<>();
        Set<String> closedSet = new HashSet<>();

        Node startNode = new Node(startX, startY, null, 0, manhattan(startX, startY, targetX, targetY));
        openSet.add(startNode);
        allNodes.put(key(startX, startY), startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(current);
            }
            closedSet.add(key(current.x, current.y));

            for (int[] dir : DIRECTIONS) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (!isValid(nx, ny) || obstacles[nx][ny] || closedSet.contains(key(nx, ny))) continue;

                int tentativeG = current.g + 1;
                String neighborKey = key(nx, ny);
                if (!allNodes.containsKey(neighborKey) || tentativeG < allNodes.get(neighborKey).g) {
                    Node neighbor = new Node(nx, ny, current, tentativeG, manhattan(nx, ny, targetX, targetY));
                    allNodes.put(neighborKey, neighbor);
                    if (!openSet.contains(neighbor)) openSet.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean areAdjacent(Unit u1, Unit u2) {
        return Math.abs(u1.getxCoordinate() - u2.getxCoordinate()) +
                Math.abs(u1.getyCoordinate() - u2.getyCoordinate()) == 1;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private String key(int x, int y) {
        return x + ":" + y;
    }

    private int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<Edge> reconstructPath(Node endNode) {
        List<Edge> path = new LinkedList<>();
        Node current = endNode;
        while (current != null && current.parent != null) {
            path.add(0, new Edge(current.x, current.y));
            current = current.parent;
        }
        return path;
    }

    private static class Node {
        int x, y, g, h;
        Node parent;
        Node(int x, int y, Node parent, int g, int h) {
            this.x = x; this.y = y; this.parent = parent; this.g = g; this.h = h;
        }
        int getF() { return g + h; }
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }
        public int hashCode() { return Objects.hash(x, y); }
    }
}