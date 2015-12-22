package team044.Units;

import battlecode.common.*;
import team044.Messaging;
import team044.Unit;

public abstract class DefensiveUnits extends Unit
{
    public DefensiveUnits(RobotController rc)
    {
        super(rc);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        int x = rc.readBroadcast(Messaging.BuildingInDistressX.ordinal());
        int y = rc.readBroadcast(Messaging.BuildingInDistressY.ordinal());

        boolean defend = false;
        if (x != 0 && y != 0)
        {
            MapLocation building = new MapLocation(x,y);

            if (building.distanceSquaredTo(rc.getLocation()) < 200)
            {
                target = building;
                defend = true;
            }
        }

        int index = rc.readBroadcast(Messaging.TowerUnderAttack.ordinal()) - 1;
        if (index >= 0)
        {
            MapLocation[] towers = rc.senseTowerLocations();
            if (index < towers.length)
            {
                MapLocation tower = towers[index];

                // go farther for towers
                if (rc.getLocation().distanceSquaredTo(tower) < 400)
                {
                    target = tower;
                    defend = true;
                }
            }
        }

        x = rc.readBroadcast(Messaging.MinerUnderAttackX.ordinal());
        y = rc.readBroadcast(Messaging.MinerUnderAttackY.ordinal());

        if (x != 0 && y != 0)
        {
            MapLocation miner = new MapLocation(x, y);

            if (rc.getLocation().distanceSquaredTo(miner) <= 400)
            {
                target = miner;
                defend = true;
                if (rc.getType() == RobotType.LAUNCHER)
                {
                    rc.broadcast(Messaging.MinerUnderAttackX.ordinal(), 0);
                    rc.broadcast(Messaging.MinerUnderAttackY.ordinal(), 0);
                }
            }
        }

        x = rc.readBroadcast(Messaging.LauncherAttackX.ordinal());
        y = rc.readBroadcast(Messaging.LauncherAttackY.ordinal());

        if (x != 0 && y != 0)
        {
            MapLocation goal = new MapLocation(x, y);

            if (rc.getLocation().distanceSquaredTo(goal) < 400)
            {
                target = goal;
                defend = true;
            }
        }


        // if we are not defending a building run unit specified code
        if (!defend)
        {
            collectData2();
        }
    }

    // method for children to call to determine movement if they don't rush to help
    public abstract void collectData2() throws GameActionException;

    public boolean fight() throws GameActionException
    {
        rc.setIndicatorString(1, "We are in fightMicro");
        return fighter.advancedFightMicro(nearByEnemies);
    }

    public boolean takeNextStep() throws GameActionException
    {
        rc.setIndicatorString(1, "We are in navigator");
        if (target == null)
        {
            return false;
        }
        return nav.takeNextStep(target);
    }
}
