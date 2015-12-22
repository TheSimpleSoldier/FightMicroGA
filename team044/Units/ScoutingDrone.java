package team044.Units;


import battlecode.common.*;
import team044.Messaging;
import team044.Unit;

import java.util.Random;

public class ScoutingDrone extends Drone
{
    MapLocation target;
    Random rand;
    public ScoutingDrone(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(0, "Scouting Drone");
        target = rc.getLocation();
        rand = new Random();
    }

    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();

    }

    public boolean takeNextStep() throws GameActionException
    {
        int extremes = rc.readBroadcast(Messaging.MapExtremes.ordinal());
        if(rc.getLocation().equals(target))
        {
            if (extremes == 0)
                target = exploreLocation(rc);
            else
                target = findFog(extremes);
        }
        if(rc.senseTerrainTile(target) == TerrainTile.OFF_MAP)
        {
            target = findNextTarget();
        }

        rc.setIndicatorString(1, "target: " + target.toString());

        return nav.takeNextStep(target);
    }

    private MapLocation findNextTarget() throws GameActionException
    {
        MapLocation[] tendrils = new MapLocation[8];
        for(int k = 0; k < tendrils.length; k++)
        {
            tendrils[k] = rc.getLocation();
        }

        Direction[] dirs = Direction.values();

        boolean done = false;
        while(!done)
        {
            done = true;

            for(int k = 0; k < 8; k++)
            {
                tendrils[k] = tendrils[k].add(dirs[k]);
                if(rc.senseTerrainTile(tendrils[k]) == TerrainTile.UNKNOWN)
                {
                    return tendrils[k];
                }
                if(rc.senseTerrainTile(tendrils[k]) != TerrainTile.OFF_MAP)
                {
                    done = false;
                }
                else
                {
                    tendrils[k] = tendrils[k].subtract(dirs[k]);
                }
            }
        }

        MapLocation next = rc.getLocation().add(dirs[rand.nextInt(8)]);
        while(!rc.canMove(rc.getLocation().directionTo(next)))
        {
            next = rc.getLocation().add(dirs[rand.nextInt(8)]);
        }

        return next;
    }

    private MapLocation exploreLocation(RobotController rc) throws GameActionException
    {
        int goNorth = rc.readBroadcast(Messaging.DroneMinY.ordinal());
        int goSouth = rc.readBroadcast(Messaging.DroneMaxY.ordinal());
        int goWest = rc.readBroadcast(Messaging.DroneMinX.ordinal());
        int goEast = rc.readBroadcast(Messaging.DroneMaxX.ordinal());
        if (goNorth != 0 && goWest != 0)
        {
            rc.broadcast(Messaging.DroneMinX.ordinal(), 0);
            Direction d = rc.getLocation().directionTo(enemyHQ);
            if (d.equals(Direction.NORTH_WEST))
                return rc.getLocation().add(Direction.WEST);
            rc.broadcast(Messaging.DroneMinY.ordinal(), 0);
            return rc.getLocation().add(Direction.NORTH_WEST);
        }
        if (goSouth != 0 && goEast != 0)
        {
            rc.broadcast(Messaging.DroneMaxX.ordinal(), 0);
            Direction d = rc.getLocation().directionTo(enemyHQ);
            if (d.equals(Direction.SOUTH_EAST))
                return rc.getLocation().add(Direction.EAST);
            rc.broadcast(Messaging.DroneMaxY.ordinal(), 0);
            return rc.getLocation().add(Direction.SOUTH_EAST);
        }
        if (goSouth != 0 && goWest != 0)
        {
            rc.broadcast(Messaging.DroneMaxY.ordinal(), 0);
            Direction d = rc.getLocation().directionTo(enemyHQ);
            if (d.equals(Direction.SOUTH_WEST))
                return rc.getLocation().add(Direction.NORTH);
            rc.broadcast(Messaging.DroneMinX.ordinal(), 0);
            return rc.getLocation().add(Direction.SOUTH_WEST);
        }
        if (goNorth != 0 && goEast != 0)
        {
            rc.broadcast(Messaging.DroneMinY.ordinal(), 0);
            Direction d = rc.getLocation().directionTo(enemyHQ);
            if (d.equals(Direction.SOUTH_EAST))
                return rc.getLocation().add(Direction.SOUTH);
            rc.broadcast(Messaging.DroneMaxX.ordinal(), 0);
            return rc.getLocation().add(Direction.NORTH_EAST);
        }
        if (goNorth != 0)
        {
            rc.broadcast(Messaging.DroneMinY.ordinal(), 0);
            if (rc.getLocation().directionTo(enemyHQ).equals(Direction.NORTH))
                return rc.getLocation().add(leftOrRight(Direction.NORTH));
            return rc.getLocation().add(Direction.NORTH);
        }
        if (goWest != 0)
        {
            rc.broadcast(Messaging.DroneMinX.ordinal(), 0);
            if (rc.getLocation().directionTo(enemyHQ).equals(Direction.WEST))
                return rc.getLocation().add(leftOrRight(Direction.WEST));
            return rc.getLocation().add(Direction.WEST);
        }
        if (goEast != 0)
        {
            rc.broadcast(Messaging.DroneMaxX.ordinal(), 0);
            if (rc.getLocation().directionTo(enemyHQ).equals(Direction.EAST))
                return rc.getLocation().add(leftOrRight(Direction.EAST));
            return rc.getLocation().add(Direction.EAST);
        }
        if (goSouth != 0)
        {
            rc.broadcast(Messaging.DroneMaxY.ordinal(), 0);
            if (rc.getLocation().directionTo(enemyHQ).equals(Direction.SOUTH))
                return rc.getLocation().add(leftOrRight(Direction.SOUTH));
            return rc.getLocation().add(Direction.SOUTH);
        }
        return findNextTarget();
    }

    private Direction leftOrRight(Direction goal) throws GameActionException
    {
        MapLocation here = rc.getLocation();
        if (goal.equals(Direction.NORTH) || goal.equals(Direction.SOUTH)) {

            int mean = (enemyHQ.x + here.x) / 2;
            int left = mean - rc.readBroadcast(Messaging.MapLimitWest.ordinal());
            int right = rc.readBroadcast(Messaging.MapLimitEast.ordinal()) - mean;
            if ((left > right && goal.equals(Direction.NORTH)) || (left < right && goal.equals(Direction.SOUTH)))
            {
                return goal.rotateLeft();
            }
            return goal.rotateRight();
        }
        int mean = (enemyHQ.y + here.y) / 2;
        int left = mean - rc.readBroadcast(Messaging.MapLimitNorth.ordinal());
        int right = rc.readBroadcast(Messaging.MapLimitSouth.ordinal()) - mean;
        if ((left < right && goal.equals(Direction.WEST)) || (left > right && goal.equals(Direction.EAST)))
        {
            return goal.rotateLeft();
        }
        return goal.rotateRight();
    }

    public MapLocation findFog(int extremes) throws GameActionException
    {
        MapLocation myLoc = rc.getLocation();
        int x1 = myLoc.x - rc.readBroadcast(Messaging.MapLimitWest.ordinal());
        int x2 = rc.readBroadcast(Messaging.MapLimitEast.ordinal()) - myLoc.x;
        int y1 = myLoc.y - rc.readBroadcast(Messaging.MapLimitNorth.ordinal());
        int y2 = rc.readBroadcast(Messaging.MapLimitSouth.ordinal()) - myLoc.y;
        MapLocation go;
        switch(extremes)
        {
            case 1:
                if (x1>x2&&y1<y2)
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitWest.ordinal()),myLoc.y);
                else if (x1<x2&&y1>y2)
                    go = new MapLocation(myLoc.x,rc.readBroadcast(Messaging.MapLimitNorth.ordinal()));
                else
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitWest.ordinal()),rc.readBroadcast(Messaging.MapLimitNorth.ordinal()));
                break;
            case 2:
                if (x1>x2&&y1<y2)
                    go = new MapLocation(myLoc.x,rc.readBroadcast(Messaging.MapLimitNorth.ordinal()));
                else if (x1<x2&&y1>y2)
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitEast.ordinal()),myLoc.y);
                else
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitEast.ordinal()),rc.readBroadcast(Messaging.MapLimitNorth.ordinal()));
                break;
            case 3:
                if (x1>x2&&y1<y2)
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitWest.ordinal()),myLoc.y);
                else if (x1<x2&&y1>y2)
                    go = new MapLocation(myLoc.x,rc.readBroadcast(Messaging.MapLimitSouth.ordinal()));
                else
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitWest.ordinal()),rc.readBroadcast(Messaging.MapLimitSouth.ordinal()));
                break;
            case 4:
                if (x1>x2&&y1<y2)
                    go = new MapLocation(myLoc.x,rc.readBroadcast(Messaging.MapLimitSouth.ordinal()));
                else if (x1<x2&&y1>y2)
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitEast.ordinal()),myLoc.y);
                else
                    go = new MapLocation(rc.readBroadcast(Messaging.MapLimitEast.ordinal()),rc.readBroadcast(Messaging.MapLimitSouth.ordinal()));
                break;
            default:
                return findNextTarget();
        }
        return go;
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if(!tracker.getNearestMiner().equals(rc.getLocation()) ||
           !tracker.getNearestMinerFactory().equals(rc.getLocation()))
        {
            return new SearchAndDestroyDrone(rc);
        }

        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
