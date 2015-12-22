package team044.Units;

import battlecode.common.*;
import team044.*;


public class FollowerUnit extends Unit
{
    public FollowerUnit(RobotController rc)
    {
        super(rc);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        int x = rc.readBroadcast(Messaging.CommanderLocX.ordinal());
        int y = rc.readBroadcast(Messaging.CommanderLocY.ordinal());

        if (x != 0 || y != 0)
        {
            target = new MapLocation(x,y);

            nav.setAvoidTowers(false);
            nav.setAvoidHQ(false);
        }
        else
        {
            MapLocation[] towers = rc.senseTowerLocations();
            target = Utilities.getCentralTower(rc, towers); //enemyHQ;
        }
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (target == null)
        {
            return false;
        }
        if (target == rc.getLocation())
        {
            return false;
        }
        return nav.takeNextStep(target);
    }

    public boolean fight() throws GameActionException
    {
        return fighter.basicFightMicro(nearByEnemies);
    }
}
