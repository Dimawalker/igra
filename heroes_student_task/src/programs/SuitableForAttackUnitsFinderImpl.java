package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;


public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {


    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        var result = new ArrayList<Unit>();

        for (List<Unit> row : unitsByRow) {
            var suitableUnits = fetchSuitableUnits(row, isLeftArmyTarget);
            result.addAll(suitableUnits);
        }

        return result;
    }

    /**
     * Метод для получения подходящих юнитов в ряду.
     */
    private List<Unit> fetchSuitableUnits(List<Unit> row, boolean isLeftArmyTarget) {
        var suitableUnits = new ArrayList<Unit>();

        for (int i = 0; i < row.size(); i++) {
            var unit = row.get(i);

            if (unit != null && unit.isAlive() &&
                    (isLeftArmyTarget ? isFirstUnitFromLeft(row, i) : isLastUnitFromRight(row, i))) {
                suitableUnits.add(unit);
            }
        }

        return suitableUnits;
    }

    /**
     * Проверяет, является ли юнит первым слева в ряду.
     */
    private boolean isFirstUnitFromLeft(List<Unit> row, int index) {
        return index == 0 || !row.subList(0, index).contains(null);
    }

    /**
     * Проверяет, является ли юнит последним справа в ряду.
     */
    private boolean isLastUnitFromRight(List<Unit> row, int index) {
        return index == row.size() - 1 || !row.subList(index + 1, row.size()).contains(null);
    }
}