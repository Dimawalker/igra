package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int MAX_PER_TYPE = 11;

    private static final int FIELD_WIDTH = 27;
    private static final int FIELD_HEIGHT = 21;
    private static final int ARMY_WIDTH = 3;

    private final Random rnd = new Random();

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army result = new Army();
        result.setUnits(new ArrayList<>());
        result.setPoints(0);

        if (unitList == null || unitList.isEmpty() || maxPoints <= 0) {
            return result;
        }

        List<Unit> templates = new ArrayList<>();
        for (Unit u : unitList) {
            if (u != null && u.getCost() > 0) templates.add(u);
        }
        if (templates.isEmpty()) return result;

        Map<String, Integer> countByType = new HashMap<>();

        Set<Long> occupied = new HashSet<>();

        List<Unit> generated = new ArrayList<>();
        int pointsUsed = 0;

        while (true) {
            Unit best = null;

            for (Unit tpl : templates) {
                String type = tpl.getUnitType();
                int cnt = countByType.getOrDefault(type, 0);

                if (cnt >= MAX_PER_TYPE) continue;
                if (pointsUsed + tpl.getCost() > maxPoints) continue;

                if (best == null) {
                    best = tpl;
                } else {
                    if (compareEfficiency(tpl, best) > 0) {
                        best = tpl;
                    }
                }
            }

            if (best == null) break;

            String type = best.getUnitType();
            int newIndex = countByType.getOrDefault(type, 0) + 1;

            String uniqueName = type + " " + newIndex;

            int x, y;
            do {
                x = rnd.nextInt(ARMY_WIDTH);     // 0..2 (левая зона)
                y = rnd.nextInt(FIELD_HEIGHT);   // 0..20
            } while (!occupied.add(pack(x, y)));

            Unit created = cloneFromTemplate(best, uniqueName, x, y);
            generated.add(created);

            countByType.put(type, newIndex);
            pointsUsed += best.getCost();
        }

        result.setUnits(generated);
        result.setPoints(pointsUsed);
        return result;
    }

    private static int compareEfficiency(Unit a, Unit b) {
        double aAtk = (double) a.getBaseAttack() / a.getCost();
        double bAtk = (double) b.getBaseAttack() / b.getCost();
        int c1 = Double.compare(aAtk, bAtk);
        if (c1 != 0) return c1;

        double aHp = (double) a.getHealth() / a.getCost();
        double bHp = (double) b.getHealth() / b.getCost();
        int c2 = Double.compare(aHp, bHp);
        if (c2 != 0) return c2;

        // Если всё одинаково — предпочитаем более дешёвого
        return Integer.compare(b.getCost(), a.getCost()); // меньше cost => "лучше"
    }

    private static Unit cloneFromTemplate(Unit tpl, String uniqueName, int x, int y) {
        Map<String, Double> attackBonuses = tpl.getAttackBonuses() == null
                ? new HashMap<>()
                : new HashMap<>(tpl.getAttackBonuses());

        Map<String, Double> defenceBonuses = tpl.getDefenceBonuses() == null
                ? new HashMap<>()
                : new HashMap<>(tpl.getDefenceBonuses());

        Unit u = new Unit(
                uniqueName,
                tpl.getUnitType(),
                tpl.getHealth(),
                tpl.getBaseAttack(),
                tpl.getCost(),
                tpl.getAttackType(),
                attackBonuses,
                defenceBonuses,
                x,
                y
        );
        u.setAlive(true);
        return u;
    }

    private static long pack(int x, int y) {
        return (((long) x) << 32) ^ (y & 0xffffffffL);
    }
}