package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

public class Recover extends UnitAction
{
    public Recover(int unitId)
    {
        super(Types.ACTION.RECOVER);
        super.unitId = unitId;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
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

        float currentHP = unit.getCurrentHP();
        return unit.isFresh() && currentHP < unit.getMaxHP() && currentHP > 0;
    }


    @Override
    public Action copy() {
        return new Recover(this.unitId);
    }

    public String toString() {
        return "RECOVER by unit " + this.unitId;
    }
}
