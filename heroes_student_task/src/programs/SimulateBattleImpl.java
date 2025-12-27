package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        if (playerArmy == null || computerArmy == null) return;

        List<Unit> player = playerArmy.getUnits();
        List<Unit> computer = computerArmy.getUnits();
        if (player == null || computer == null) return;

        // Бой по раундам
        while (true) {
            boolean anyAttackThisRound = false;
            Set<Unit> acted = new HashSet<>();

            while (true) {
                List<Unit> queue = buildQueue(player, computer, acted);
                if (queue.isEmpty()) break;

                Unit attacker = queue.get(0);
                acted.add(attacker);

                Unit target = null;
                try {
                    if (attacker.getProgram() != null) {
                        target = attacker.getProgram().attack();
                    }
                } catch (Exception ignored) {
                    target = null;
                }

                if (target != null) anyAttackThisRound = true;

                if (printBattleLog != null) {
                    printBattleLog.printBattleLog(attacker, target);
                }

                if (Thread.interrupted()) throw new InterruptedException();
            }

            if (!hasAlive(player) || !hasAlive(computer)) break;

            if (!anyAttackThisRound) break;
        }
    }

    private static List<Unit> buildQueue(List<Unit> a, List<Unit> b, Set<Unit> acted) {
        List<Unit> q = new ArrayList<>();

        for (Unit u : a) if (u != null && u.isAlive() && !acted.contains(u)) q.add(u);
        for (Unit u : b) if (u != null && u.isAlive() && !acted.contains(u)) q.add(u);

        q.sort((u1, u2) -> {
            int c = Integer.compare(u2.getBaseAttack(), u1.getBaseAttack());
            if (c != 0) return c;
            return String.valueOf(u1.getName()).compareTo(String.valueOf(u2.getName()));
        });

        return q;
    }

    private static boolean hasAlive(List<Unit> units) {
        for (Unit u : units) {
            if (u != null && u.isAlive()) return true;
        }
        return false;
    }
}