package team044.Units.Defenders;


import battlecode.common.*;
import team044.*;
import team044.Units.Rushers.LauncherRusher;
import team044.Units.DefensiveUnits;

public class DefensiveLauncher extends DefensiveUnits
{
    public DefensiveLauncher(RobotController rc)
    {
        super(rc);
        // override supers range as it is 0 but missiles can go a long ways
        range = 35;

        nav.setAvoidTowers(false);
        nav.setAvoidHQ(false);
    }

    // override to follow Commander
    public void collectData() throws GameActionException
    {
        nearByEnemies = rc.senseNearbyRobots(range, opponent);
        nearByAllies = rc.senseNearbyRobots(range, us);
        if(nearByEnemies != null)
        {
            tracker.record(nearByEnemies);
        }

        int x = rc.readBroadcast(Messaging.CommanderLocX.ordinal());
        int y = rc.readBroadcast(Messaging.CommanderLocY.ordinal());

        if (x != 0 && y != 0)
        {
            target = new MapLocation(x, y);
        }
        else
        {
            collectData2();
        }
    }

    // currently not being used
    public void collectData2() throws GameActionException
    {
        if (rc.readBroadcast(Messaging.Attack.ordinal()) == 1)
        {
            MapLocation[] enemyTower = rc.senseEnemyTowerLocations();
            if (enemyTower.length > 0)
            {
                target = enemyTower[0];
            }
            else
            {
                target = rc.senseEnemyHQLocation();
            }
        }
        else
        {
            target = Utilities.getTowerClosestToEnemyHQ(rc);
        }
    }

    public void handleMessages() throws GameActionException
    {
        // if we are getting low on supply and are near other robots send out request
        if (rc.getSupplyLevel() < 40 && nearByAllies.length > 1)
        {
            MapLocation mySpot = rc.getLocation();
            rc.broadcast(Messaging.FirstNeedSupplyX.ordinal(), mySpot.x);
            rc.broadcast(Messaging.FirstNeedSupplyY.ordinal(), mySpot.y);
        }

        Utilities.handleMessageCounter(rc, Messaging.NumbOfLaunchersOdd.ordinal(), Messaging.NumbOfLaunchersEven.ordinal());
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (nearByEnemies.length > 0)
        {
            return false;
        }
        else if (Utilities.nearEnemyTower(rc))
        {
            return false;
        }
        else
        {
            return nav.takeNextStep(target);
        }
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

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
