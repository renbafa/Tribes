package core.actions.cityactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class ClearForest extends CityAction
{

    public ClearForest(int cityId) {
        super(Types.ACTION.CLEAR_FOREST);
        super.cityId = cityId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Board b = gs.getBoard();

        City city = (City) gs.getActor(this.cityId);

        Tribe tribe = gs.getTribe(city.getTribeId());
        Board board = gs.getBoard();


        if(b.getTerrainAt(targetPos.x, targetPos.y) != Types.TERRAIN.FOREST) return false;
        if(b.getCityIdAt(targetPos.x, targetPos.y) != cityId) return false;
        return gs.getTribe(city.getTribeId()).getTechTree().isResearched(Types.TECHNOLOGY.FORESTRY);
    }

    @Override
    public Action copy() {
        ClearForest clear = new ClearForest(this.cityId);
        clear.setTargetPos(this.targetPos.copy());
        return clear;
    }

    public String toString()
    {
        return "CLEAR_FOREST by city " + this.cityId+ " at " + targetPos;
    }
}
