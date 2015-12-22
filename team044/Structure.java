package team044;

import battlecode.common.*;

/**
 * This class is for common behavior
 */
public abstract class Structure extends Unit
{
    public Structure()
    {
        //do nothing
    }

    public Structure(RobotController rc)
    {
        this.rc = rc;
        us = rc.getTeam();
        opponent = us.opponent();
        range = rc.getType().attackRadiusSquared;
        sightRange = rc.getType().sensorRadiusSquared;
        tracker = new EnemyMinerTracker(rc);
        ourHQ = rc.senseHQLocation();
        enemyHQ = rc.senseEnemyHQLocation();
    }

    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();

        enemies = rc.senseNearbyRobots(sightRange, opponent);
    }

    public void handleMessages() throws GameActionException
    {
        rc.setIndicatorString(1, "Handle Messages");
        if (enemies.length > 0)
        {
            rc.setIndicatorString(2, "Enemies spoted");
            rc.broadcast(Messaging.BuildingInDistressX.ordinal(), rc.getLocation().x);
            rc.broadcast(Messaging.BuildingInDistressY.ordinal(), rc.getLocation().y);
        }
    }

    // structures can't move!
    public boolean takeNextStep() throws GameActionException
    {
        return false;
    }

    // most structures can't fight will override for towers and HQ
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

    // for structures even distribute supplies among all allies
    public void distributeSupply() throws  GameActionException
    {
        Utilities.shareSupplies(rc);
    }
}
