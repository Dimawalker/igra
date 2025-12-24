package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;


    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        var playerUnits = new ArrayList<>(playerArmy.getUnits());
        var computerUnits = new ArrayList<>(computerArmy.getUnits());

        while (true) {
            playerUnits.sort(Comparator.comparingInt(unit -> -unit.getBaseAttack()));
            computerUnits.sort(Comparator.comparingInt(unit -> -unit.getBaseAttack()));

            if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
                break;
            }

            processTurns(playerUnits);
            processTurns(computerUnits);
        }
    }

    /**
     * Метод обеспечивающий атаку юнитов.
     */
    private void processTurns(List<Unit> attackingUnits) throws InterruptedException {
        for (Iterator<Unit> iterator = attackingUnits.iterator(); iterator.hasNext(); ) {
            Unit attacker = iterator.next();
            if (!attacker.isAlive()) {
                iterator.remove();
                continue;
            }

            var target = attacker.getProgram().attack();
            if (target != null) {
                printBattleLog.printBattleLog(attacker, target);
            }
        }
    }

}