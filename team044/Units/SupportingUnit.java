package team044.Units;

import battlecode.common.*;
import team044.Messaging;
import team044.Unit;

public class SupportingUnit extends Unit
{
    public int group;
    public SupportingUnit(RobotController rc)
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

        MapLocation goal = new MapLocation(x,y);
        Direction dir = goal.directionTo(ourHQ);
        goal = goal.add(dir, 3);

        RobotInfo[] allies = rc.senseNearbyRobots(24, us);

        int closestDist = 25;
        MapLocation closest = null;

        for (int i = allies.length; --i>=0; )
        {
            if (allies[i].type == RobotType.LAUNCHER)
            {
                MapLocation ally = allies[i].location;
                int dist = ally.distanceSquaredTo(rc.getLocation());
                if (dist < closestDist)
                {
                    closestDist = dist;
                    closest = ally;
                }
            }
        }

        if (rc.getLocation().distanceSquaredTo(goal) > 15)
        {
            target = goal;
        }
        else if (closest != null && rc.getLocation().isAdjacentTo(closest))
        {
            target = rc.getLocation();
        }
        else if (closest != null && dir != null)
        {
            target = closest.add(dir);
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
