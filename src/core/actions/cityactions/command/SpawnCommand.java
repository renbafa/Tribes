package core.actions.cityactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.cityactions.ResourceGathering;
import core.actions.cityactions.Spawn;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

public class SpawnCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Spawn action = (Spawn)a;
        if (action.isFeasible(gs)){
            int cityId = action.getCityId();
            Board b = gs.getBoard();
            Types.UNIT unit_type = action.getUnitType();
            City city = (City) gs.getActor(cityId);
            Vector2d cityPos = city.getPosition();
            Tribe tribe = gs.getTribe(city.getTribeId());

            Unit newUnit = Types.UNIT.createUnit(new Vector2d(cityPos.x, cityPos.y), 0, false, city.getActorId(), city.getTribeId(), unit_type);
            gs.getBoard().addUnit(city, newUnit);

            /**
             * the following lines show the negative effect of player being on a position where rain is placed,
             */

            if (b.getWeatherAt(newUnit.getPosition().x,newUnit.getPosition().y) == Types.WEATHER.RAIN) {
                if (tribe.getType() != Types.TRIBE.ATHENIAN) {
                    tribe.subtractStars(unit_type.getCost() *2);
                }
            }

            tribe.subtractStars(unit_type.getCost());
            tribe.addScore(unit_type.getPoints());

            return true;
        }
        return false;
    }
}
