package core.actions.tribeactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.tribeactions.MakeRain;
import core.actors.Tribe;
import core.game.GameState;
import utils.Vector2d;

import java.util.Vector;

public class MakeRainCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        MakeRain action = (MakeRain)a;
        if(action.isFeasible(gs))
        {
            int tribeId = action.getTribeId();
            Vector2d position = action.getPosition();
            Tribe tribe = gs.getTribe(tribeId);
            tribe.subtractStars(TribesConfig.RAIN_COST);
            gs.getBoard().addRain(position.x, position.y);

            return true;
        }
        return false;
    }
}
