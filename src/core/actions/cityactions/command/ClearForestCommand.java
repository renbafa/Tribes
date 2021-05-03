package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.Build;
import core.actions.cityactions.ClearForest;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class ClearForestCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        ClearForest action = (ClearForest)a;

        if (action.isFeasible(gs)){

            int cityId = action.getCityId();
            City city = (City) gs.getActor(cityId);
            Board b = gs.getBoard();
            Tribe t = gs.getTribe(city.getTribeId());

            Vector2d targetPos = action.getTargetPos();
            gs.getBoard().setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);

            /**
             * the following lines show the negative effect of player being on a position where rain is placed,
             */

            if (b.getWeatherAt(targetPos.x, targetPos.y) == Types.WEATHER.RAIN) {
                if (t.getType() != Types.TRIBE.ATHENIAN) {
                    t.subtractStars(TribesConfig.CLEAR_FOREST_STAR /2);
                }
            }

            gs.getTribe(city.getTribeId()).addStars(TribesConfig.CLEAR_FOREST_STAR);
            return true;
        }
        return false;
    }
}
