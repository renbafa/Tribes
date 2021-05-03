package core.actions.tribeactions;
import core.TribesConfig;
import core.actions.Action;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;
import core.Types;

public class MakeRain extends TribeAction {

    private Vector2d position;

    private int unitId;

    public MakeRain(int tribeId)
    {
        super(Types.ACTION.MAKE_RAIN);
        this.tribeId = tribeId;
    }
    public void setPosition(Vector2d position){
        this.position = position.copy();
    }
    public Vector2d getPosition() {
        return position;
    }

    public void setUnitId(int unitId) {this.unitId = unitId;}
    public int getUnitId() {
        return unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {

        //Retrieve tribe
        Tribe tribe = gs.getTribe(tribeId);

        //Unit unit = (Unit) gs.getActor(this.unitId);
        //Board board = gs.getBoard();

        if (tribe.getType() != Types.TRIBE.ATHENIAN) {
            return false; }

        //This tribe should be able to make rain in general.
        if(!tribe.canMakeRains())
            return false;

        //... and also in this position
        return gs.getBoard().canMakeRainAt(tribeId, position.x, position.y);
    }

    @Override
    public Action copy() {
        MakeRain makeRain = new MakeRain(this.tribeId);
        makeRain.setPosition(position);
        return makeRain;
    }

    public String toString() {
        return "MAKE_RAIN by tribe " + this.tribeId + " at location " + position;
    }
}