package core.units;

import utils.Vector2d;

public class Defender extends Unit
{
    public Defender(Vector2d pos, int kills, boolean isVeteran) {
        super(1, 3, 1, 15, 1, 3, pos, kills, isVeteran);
    }
}
