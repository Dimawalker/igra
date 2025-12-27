package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;
import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {
    public Object printBattleLog;

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        if (unitsByRow == null) return suitableUnits;

        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;
            Unit suitableUnit = findSuitableUnitInRow(row, isLeftArmyTarget);
            if (suitableUnit != null) suitableUnits.add(suitableUnit);
        }
        return suitableUnits;
    }

    private Unit findSuitableUnitInRow(List<Unit> row, boolean isLeftArmyTarget) {
        int start = isLeftArmyTarget ? 0 : row.size() - 1;
        int end = isLeftArmyTarget ? row.size() : -1;
        int step = isLeftArmyTarget ? 1 : -1;

        for (int i = start; i != end; i += step) {
            Unit unit = row.get(i);
            if (unit != null && unit.isAlive() && !isCovered(row, i, isLeftArmyTarget)) {
                return unit;
            }
        }
        return null;
    }

    private boolean isCovered(List<Unit> row, int index, boolean fromLeft) {
        if (fromLeft) {
            for (int i = 0; i < index; i++) {
                if (row.get(i) != null && row.get(i).isAlive()) return true;
            }
        } else {
            for (int i = index + 1; i < row.size(); i++) {
                if (row.get(i) != null && row.get(i).isAlive()) return true;
            }
        }
        return false;
    }
}