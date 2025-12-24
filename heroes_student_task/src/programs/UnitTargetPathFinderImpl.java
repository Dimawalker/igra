package programs;

import com.battle.heroes.army.*;
import com.battle.heroes.army.programs.*;
import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;  // Ширина поля (0-26)
    private static final int HEIGHT = 21; // Высота поля (0-20)

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        System.out.println("Поиск пути от (" + attackUnit.getxCoordinate() + "," + attackUnit.getyCoordinate() +
                ") к (" + targetUnit.getxCoordinate() + "," + targetUnit.getyCoordinate() + ")");

        // Если атакующий и цель в одной клетке
        if (attackUnit.getxCoordinate() == targetUnit.getxCoordinate() &&
                attackUnit.getyCoordinate() == targetUnit.getyCoordinate()) {
            System.out.println("Атакующий и цель в одной клетке");
            return Collections.emptyList();
        }

        // Создаём набор занятых клеток
        Set<String> blockedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && !unit.equals(attackUnit) && !unit.equals(targetUnit)) {
                blockedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Используем BFS для поиска пути
        return bfsPathFind(attackUnit, targetUnit, blockedCells);
    }

    private List<Edge> bfsPathFind(Unit start, Unit end, Set<String> blockedCells) {
        int startX = start.getxCoordinate();
        int startY = start.getyCoordinate();
        int endX = end.getxCoordinate();
        int endY = end.getyCoordinate();

        // Массив для хранения предыдущих клеток
        Edge[][] cameFrom = new Edge[WIDTH][HEIGHT];
        boolean[][] visited = new boolean[WIDTH][HEIGHT];

        // Очередь для BFS
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});
        visited[startX][startY] = true;

        // Направления движения: вверх, вниз, влево, вправо
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            // Если достигли цели
            if (x == endX && y == endY) {
                return reconstructPath(cameFrom, endX, endY);
            }

            // Проверяем соседние клетки
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isValid(newX, newY, blockedCells) && !visited[newX][newY]) {
                    visited[newX][newY] = true;
                    cameFrom[newX][newY] = new Edge(x, y);
                    queue.offer(new int[]{newX, newY});
                }
            }
        }

        System.out.println("Путь не найден!");
        return Collections.emptyList();
    }

    private boolean isValid(int x, int y, Set<String> blockedCells) {
        // Проверяем границы поля и занятость клетки
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT &&
                !blockedCells.contains(x + "," + y);
    }

    private List<Edge> reconstructPath(Edge[][] cameFrom, int endX, int endY) {
        List<Edge> path = new ArrayList<>();
        int x = endX;
        int y = endY;

        // Идём от конца к началу
        while (cameFrom[x][y] != null) {
            path.add(0, new Edge(x, y));
            Edge prev = cameFrom[x][y];
            x = prev.getX();
            y = prev.getY();
        }

        // Добавляем стартовую точку
        path.add(0, new Edge(x, y));

        System.out.println("Путь найден: " + path.size() + " точек");
        for (Edge edge : path) {
            System.out.print("(" + edge.getX() + "," + edge.getY() + ") ");
        }
        System.out.println();

        return path;
    }
}