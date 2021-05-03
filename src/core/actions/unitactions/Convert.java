package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

public class Convert extends UnitAction
{
    private int targetId;

    public Convert(int unitId)
    {
        super(Types.ACTION.CONVERT);
        super.unitId = unitId;
    }

    public void setTargetId(int targetId) {this.targetId = targetId;}
    public int getTargetId() {
        return targetId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit target = (Unit) gs.getActor(this.targetId);
        Unit unit = (Unit) gs.getActor(this.unitId);
        Tribe targetTribe = gs.getTribe(target.getTribeId());
        Tribe unitTribe = gs.getTribe(unit.getTribeId());
        Board board = gs.getBoard();


        /**
         * the following lines show the negative effect of player being on a position where rain is placed,
         */

        if (board.getWeatherAt(unit.getPosition().x,unit.getPosition().y) == Types.WEATHER.RAIN) {
            if (unitTribe.getType() != Types.TRIBE.ATHENIAN) {
                return false;
            }
        }

        if (board.getWeatherAt(target.getPosition().x,target.getPosition().y) == Types.WEATHER.RAIN) {
            if (targetTribe.getType() != Types.TRIBE.ATHENIAN) {
                return false;
            }
        }

        //Only MIND_BENDER can execute this action
        if(unit.getType() != Types.UNIT.MIND_BENDER)
            return false;

        // Check if target is not null
        if(target == null || !unit.canAttack())
            return false;

        return unitInRange(unit, target, gs.getBoard());
    }


    @Override
    public Action copy() {
        Convert convert = new Convert(this.unitId);
        convert.setTargetId(this.targetId);
        return convert;
    }

    public String toString() { return "CONVERT by unit " + this.unitId + " to unit " + this.targetId;}
}
