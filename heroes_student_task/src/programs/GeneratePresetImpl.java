package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class GeneratePresetImpl implements GeneratePreset {

    public static final int UNITS_TYPE_COUNT = 11;
    public static final int HEIGHT_ARMY = 21;
    public static final int WIDTH_ARMY = 3;

    private final Random random = new Random();


    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        var result = new Army();
        var unitCount = new int[unitList.size()];
        var generatedUnits = new ArrayList<Unit>();
        var occupiedCoordinates = new HashSet<Coordinate>();

        unitList.sort(Comparator.comparingDouble(unit -> -((double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost())));

        int totalPoints = 0;

        for (Unit unit : unitList) {
            while (unitCount[unitList.indexOf(unit)] < UNITS_TYPE_COUNT && totalPoints + unit.getCost() <= maxPoints) {
                addUnit(unit, generatedUnits, occupiedCoordinates, unitList.indexOf(unit));
                unitCount[unitList.indexOf(unit)]++; // увеличиваем счетчик
                totalPoints += unit.getCost();
            }
        }

        result.setUnits(generatedUnits);
        result.setPoints(totalPoints);

        return result;
    }

    /**
     * Добавляет юнит в армию противника.
     */
    private void addUnit(Unit unit, ArrayList<Unit> generatedUnits, Set<Coordinate> occupiedCoordinates, int index) {
        var coordinate = new Coordinate(random.nextInt(WIDTH_ARMY), random.nextInt(HEIGHT_ARMY));
        while (occupiedCoordinates.contains(coordinate)) {
            coordinate.setxCoordinate(random.nextInt(WIDTH_ARMY));
            coordinate.setyCoordinate(random.nextInt(HEIGHT_ARMY));
        }

        occupiedCoordinates.add(coordinate);

        var newUnit = new Unit(unit.getUnitType() + " " + index,
                unit.getUnitType(),
                unit.getHealth(),
                unit.getBaseAttack(),
                unit.getCost(),
                unit.getAttackType(),
                unit.getAttackBonuses(),
                unit.getDefenceBonuses(),
                coordinate.getxCoordinate(),
                coordinate.getyCoordinate());

        generatedUnits.add(newUnit);
    }

    /**
     * Класс с координатами, используется для определения координат юнита.
     */
    private static class Coordinate {
        private int xCoordinate;
        private int yCoordinate;

        Coordinate(int xCoordinate, int yCoordinate) {
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
        }

        public int getxCoordinate() {
            return xCoordinate;
        }

        public int getyCoordinate() {
            return yCoordinate;
        }

        public void setxCoordinate(int xCoordinate) {
            this.xCoordinate = xCoordinate;
        }

        public void setyCoordinate(int yCoordinate) {
            this.yCoordinate = yCoordinate;
        }
    }

}