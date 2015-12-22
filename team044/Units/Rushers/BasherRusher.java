package team044.Units.Rushers;

import battlecode.common.*;
import team044.Messaging;
import team044.Unit;
import team044.Units.Basher;
import team044.Utilities;

public class BasherRusher extends Basher
{
    public BasherRusher(RobotController rc)
    {
        super(rc);

        nav.setAvoidTowers(false);
        nav.setAvoidHQ(false);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        RobotInfo[] allies = rc.senseNearbyRobots(24, us);

        target = Utilities.getRushLocation(rc);

        if (allies.length > 4 || rc.getLocation().distanceSquaredTo(target) > 48)
        {
            // just keep advancing
            nav.setAvoidTowers(false);
            nav.setAvoidHQ(false);
        }
        else if (allies.length > 0)
        {
            nav.setAvoidTowers(true);
            nav.setAvoidHQ(true);
            int closestToTower = 999999;
            MapLocation closestAlly = target;

            for (int i = allies.length; --i>=0; )
            {
                MapLocation ally = allies[i].location;
                int dist = ally.distanceSquaredTo(target);
                if (dist < closestToTower)
                {
                    closestToTower = dist;
                    closestAlly = ally;
                }
            }

            target = closestAlly;
        }
        else
        {
            nav.setAvoidTowers(true);
            nav.setAvoidHQ(true);
        }

        rc.setIndicatorString(1, "Target: " + target);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }
}
