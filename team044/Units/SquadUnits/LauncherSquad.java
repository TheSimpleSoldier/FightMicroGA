package team044.Units.SquadUnits;

import battlecode.common.*;
import team044.BuildOrderMessaging;
import team044.FightMicroUtilities;
import team044.Messaging;
import team044.Unit;
import team044.Units.Rushers.LauncherRusher;
import team044.Units.SquadUnit;

public class LauncherSquad extends SquadUnit
{
    public LauncherSquad(RobotController rc) throws GameActionException
    {
        super(rc);
        group = rc.readBroadcast(Messaging.LauncherGroup.ordinal());
        rc.broadcast(Messaging.LauncherGroup.ordinal(), -1);
        rc.setIndicatorString(0, "Squad Launcher group: " + group);
        range = 35;
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        if (group < 1)
        {
            group = rc.readBroadcast(Messaging.LauncherGroup.ordinal());
            rc.broadcast(Messaging.LauncherGroup.ordinal(), -1);
        }
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        // if we are getting low on supply and are near other robots send out request
        if (rc.getSupplyLevel() < 40)
        {
            MapLocation mySpot = rc.getLocation();
            rc.broadcast(Messaging.FirstNeedSupplyX.ordinal(), mySpot.x);
            rc.broadcast(Messaging.FirstNeedSupplyY.ordinal(), mySpot.y);
        }
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (target == null)
        {
            return false;
        }
        if (rc.getMissileCount() == 5)
        {
            Direction dir = rc.getLocation().directionTo(target);
            RobotInfo[] allies = rc.senseNearbyRobots(rc.getLocation().add(dir, 6), 24, rc.getTeam());
            if (allies.length == 0 && !FightMicroUtilities.alliesInPath(rc.senseNearbyRobots(100, rc.getTeam()), dir, rc.getLocation()))
            {
                rc.launchMissile(dir);
            }
        }
        return nav.takeNextStep(target);
    }



    public boolean fight() throws GameActionException
    {
        return fighter.launcherAttack(nearByEnemies);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if (rc.readBroadcast(Messaging.RushEnemyBase.ordinal()) == 1)
        {
            return new LauncherRusher(rc);
        }
        return current;
    }
}
