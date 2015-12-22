package team044;

import battlecode.common.*;

public abstract class Unit
{
    public RobotController rc;
    public int range;
    public int sightRange;
    public Team us;
    public Team opponent;
    public MapLocation ourHQ;
    public MapLocation enemyHQ;
    public RobotInfo[] nearByEnemies;
    public RobotInfo[] nearByAllies;
    public RobotInfo[] enemies;
    public FightMicro fighter;
    public Navigator nav;
    public EnemyMinerTracker tracker;
    public MapLocation target;
    public Direction[] dirs;

    public Unit()
    {
        // default constructor
    }

    public Unit(RobotController rc)
    {
        this.rc = rc;
        us = rc.getTeam();
        opponent = us.opponent();
        range = rc.getType().attackRadiusSquared;
        nav = new Navigator(rc, true, true, true, false, false);
        fighter = new FightMicro(rc);
        tracker = new EnemyMinerTracker(rc);
        ourHQ = rc.senseHQLocation();
        enemyHQ = rc.senseEnemyHQLocation();
        dirs = Direction.values();
        sightRange = rc.getType().sensorRadiusSquared;
    }

    public void collectData() throws GameActionException
    {
        nearByEnemies = rc.senseNearbyRobots(range, opponent);
        nearByAllies = rc.senseNearbyRobots(range, us);
        if(nearByEnemies != null && nearByEnemies.length > 0)
        {
            tracker.record(nearByEnemies);
        }
    }

    public void handleMessages() throws GameActionException
    {
        // if we are getting low on supply and are near other robots send out request
        if (rc.getSupplyLevel() < 40 && nearByAllies.length > 1)
        {
            if (rc.readBroadcast(Messaging.FirstNeedSupplyX.ordinal()) == 0)
            {
                MapLocation mySpot = rc.getLocation();
                rc.broadcast(Messaging.FirstNeedSupplyX.ordinal(), mySpot.x);
                rc.broadcast(Messaging.FirstNeedSupplyY.ordinal(), mySpot.y);
            }
            else if (rc.readBroadcast(Messaging.SecondNeedSupplyX.ordinal()) == 0)
            {
                MapLocation mySpot = rc.getLocation();
                rc.broadcast(Messaging.SecondNeedSupplyX.ordinal(), mySpot.x);
                rc.broadcast(Messaging.SecondNeedSupplyY.ordinal(), mySpot.y);
            }
        }
    }

    public boolean takeNextStep() throws GameActionException
    {
        return false;
    }

    public boolean fight() throws GameActionException
    {
        return false;
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    public void distributeSupply() throws GameActionException
    {
        Utilities.shareSupplies(rc);
    }
}
