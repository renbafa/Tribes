package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.BurnForest;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class BurnForestCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        BurnForest action = (BurnForest)a;
        int cityId = action.getCityId();
        City city = (City) gs.getActor(cityId);

        if (action.isFeasible(gs)){


            Vector2d targetPos = action.getTargetPos();
            Board b = gs.getBoard();
            Tribe t = gs.getTribe(city.getTribeId());
            b.setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.PLAIN);
            b.setResourceAt(targetPos.x, targetPos.y, Types.RESOURCE.CROPS);

            /**
             * the following lines show the negative effect of player being on a position where rain is placed,
             */

            if (b.getWeatherAt(targetPos.x, targetPos.y) == Types.WEATHER.RAIN) {
                if (t.getType() != Types.TRIBE.ATHENIAN) {
                    t.subtractStars(TribesConfig.BURN_FOREST_COST *2);
                }
            }

            t.subtractStars(TribesConfig.BURN_FOREST_COST);
            return true;
        }
        return false;
    }
}
