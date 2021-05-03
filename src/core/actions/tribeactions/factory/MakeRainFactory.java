package core.actions.tribeactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.tribeactions.MakeRain;
import core.actors.Actor;
import core.actors.Tribe;
import core.game.GameState;
import utils.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

public class MakeRainFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Tribe tribe = (Tribe) actor;

        //if not, nothing to make rain on.
        if (!tribe.canMakeRains())
            return actions;

        //We are able to build roads, let's find where can this be built
        ArrayList<Vector2d> positions = gs.getBoard().getMakeRainPositions(tribe.getTribeId());
        for (Vector2d v : positions) {
            MakeRain mr = new MakeRain(tribe.getTribeId());
            mr.setPosition(v);
            if (mr.isFeasible(gs)) {
                actions.add(mr);
            }
        }

        //All the actions.
        return actions;
    }

}
