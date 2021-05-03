package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.BurnForest;
import core.actions.cityactions.GrowForest;
import core.actors.Building;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class GrowForestCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        GrowForest action = (GrowForest)a;
        if (action.isFeasible(gs)){
            Board b = gs.getBoard();
            int cityId = action.getCityId();
            City city = (City) gs.getActor(cityId);
            Tribe t = gs.getTribe(city.getTribeId());


            Vector2d targetPos = action.getTargetPos();
            b.setTerrainAt(targetPos.x, targetPos.y, Types.TERRAIN.FOREST);
            b.setResourceAt(targetPos.x, targetPos.y, null);

            /**
             * the following lines show the negative effect of player being on a position where rain is placed,
             */

            if (b.getWeatherAt(targetPos.x, targetPos.y) == Types.WEATHER.RAIN) {
                if (t.getType() != Types.TRIBE.ATHENIAN) {
                    t.subtractStars(TribesConfig.GROW_FOREST_COST *2);
                }
            }

            gs.getTribe(city.getTribeId()).subtractStars(TribesConfig.GROW_FOREST_COST);

            return true;
        }
        return false;
    }
}
