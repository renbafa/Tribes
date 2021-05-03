package core.actions.unitactions.factory;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.Examine;
import core.actors.Actor;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;

import java.util.LinkedList;
import java.util.Random;

public class ExamineFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Unit unit = (Unit) actor;

        Examine ex = new Examine(unit.getActorId());
        if (ex.isFeasible(gs)) {

            actions.add(ex);
        }

        return actions;
    }

}
