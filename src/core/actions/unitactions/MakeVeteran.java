package core.actions.unitactions;

import core.TribesConfig;
import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import core.Types;

import java.util.LinkedList;

public class MakeVeteran extends UnitAction
{
    public MakeVeteran(int unitId)
    {
        super(Types.ACTION.MAKE_VETERAN);
        super.unitId = unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Board board = gs.getBoard();
        Tribe tribe = gs.getTribe(unit.getTribeId());

        /**
         * the following lines show the negative effect of player being on a position where rain is placed,
         */


        if (board.getWeatherAt(unit.getPosition().x,unit.getPosition().y) == Types.WEATHER.RAIN) {
            if (tribe.getType() != Types.TRIBE.ATHENIAN) {
                return false;
            }
        }


        if(unit.getType() == Types.UNIT.SUPERUNIT)
            return false;
        return unit.getKills() >= TribesConfig.VETERAN_KILLS && !unit.isVeteran();
    }


    @Override
    public Action copy() {
        return new MakeVeteran(this.unitId);
    }

    public String toString() {
        return "MAKE_VETERAN by unit " + this.unitId;
    }
}
