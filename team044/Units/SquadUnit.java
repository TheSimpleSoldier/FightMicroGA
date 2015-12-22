package team044.Units;

import battlecode.common.*;
import team044.Messaging;
import team044.Unit;

public class SquadUnit extends Unit
{
    public int group = 0;
    public SquadUnit(RobotController rc)
    {
        super(rc);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        int x,y;

        if (group == 1)
        {
            x = rc.readBroadcast(Messaging.FirstGroupX.ordinal());
            y = rc.readBroadcast(Messaging.FirstGroupY.ordinal());
        }
        else if (group == 2)
        {
            x = rc.readBroadcast(Messaging.SeconGroupX.ordinal());
            y = rc.readBroadcast(Messaging.SecondGroupY.ordinal());
        }
        else
        {
            x = rc.readBroadcast(Messaging.ThirdGroupX.ordinal());
            y = rc.readBroadcast(Messaging.ThirdGroupY.ordinal());
        }

        target = new MapLocation(x, y);
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (target == null)
        {
            return false;
        }
        return nav.takeNextStep(target);
    }

    public boolean fight() throws GameActionException
    {
        return fighter.advancedFightMicro(nearByEnemies);
    }
}
