package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import core.actors.units.Unit;
import utils.Vector2d;


public class Capture extends UnitAction
{
    private int targetCityId; // This can be a city or a village.
    private Types.TERRAIN captureType; //City or village

    public Capture(int unitId)
    {
        super(Types.ACTION.CAPTURE);
        super.unitId = unitId;
    }

    public void setTargetCity(int targetCityId) {this.targetCityId = targetCityId;}
    public int getTargetCity() {
        return targetCityId;
    }
    public Types.TERRAIN getCaptureType() {
        return captureType;
    }
    public void setCaptureType(Types.TERRAIN captureType) {
        this.captureType = captureType;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Unit unit = (Unit) gs.getActor(this.unitId);
        if(!unit.isFresh()) return false;

        Board b = gs.getBoard();
        Tribe thisTribe = b.getTribe(unit.getTribeId());


        /**
         * the following lines show the negative effect of player being on a position where rain is placed,
         */

        if (b.getWeatherAt(unit.getPosition().x,unit.getPosition().y) == Types.WEATHER.RAIN) {
            if (thisTribe.getType() != Types.TRIBE.ATHENIAN) {
                return false;
            }
        }

        if(captureType == Types.TERRAIN.CITY)
        {
            // If unit not in city, city belongs to the units tribe or if city is null then action is not feasible
            City targetCity = (City) gs.getActor(this.targetCityId);
            if(targetCity == null) return false;

            Vector2d targetPos = targetCity.getPosition();
            if(b.getUnitAt(targetPos.x,targetPos.y) == null) return false;

            Vector2d unitPos = unit.getPosition();
            if(!targetPos.equals(unitPos)) return false;

            return targetCity.getTribeId() != unit.getTribeId();

        }else if(captureType == Types.TERRAIN.VILLAGE)
        {
            Vector2d unitPos = unit.getPosition();
            return b.getTerrainAt(unitPos.x, unitPos.y) == Types.TERRAIN.VILLAGE;
        }
        return false;
    }

    @Override
    public Action copy() {
        Capture capture = new Capture(this.unitId);
        capture.setTargetCity(this.targetCityId);
        capture.setCaptureType(this.captureType);
        return capture;
    }

    public String toString()
    {
        return "CAPTURE by unit " + unitId + " of target " + captureType.toString() + ": " + targetCityId;
    }

}
