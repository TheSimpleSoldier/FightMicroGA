package team044.Units;


import battlecode.common.*;
import team044.Constants;
import team044.Messaging;
import team044.Utilities;

import java.util.Random;

public class SearchAndDestroyDrone extends Drone
{
    MapLocation target;
    Random rand;
    MapLocation nearestDrone;
    int roundNumb;
    boolean foundMiners;
    int seenMiners;
    int minerChannel;

    public SearchAndDestroyDrone(RobotController rc)
    {
        super(rc);
        target = rc.getLocation();
        rand = new Random(rc.getID());
        rc.setIndicatorString(0, "Search and Destroy drone");
        roundNumb = Clock.getRoundNum();
        foundMiners = false;
        seenMiners = 0;
        minerChannel = 0;
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        if (enemies != null) {
            for (int k = 0; k < enemies.length; k++) {
                if (enemies[k].type == RobotType.MINER || enemies[k].type == RobotType.MINERFACTORY) {
                    foundMiners = true;
                }
            }
            seenMiners++;

            if (minerChannel == 0) {
                for (minerChannel = Constants.startMinerSeenChannel; rc.readBroadcast(minerChannel) == 0; minerChannel++) {
                }
            } else {
                rc.broadcast(minerChannel, seenMiners);
            }
        }
    }

    public void collectData2() throws GameActionException
    {
        int closest = -1;
        double nearestDist = Math.sqrt(Math.pow(GameConstants.MAP_MAX_HEIGHT, 2) +
                                       Math.pow(GameConstants.MAP_MAX_WIDTH, 2));
        for(int k = 0; k < nearByEnemies.length; k++)
        {
            if(nearByEnemies[k].type == RobotType.MINER ||
               nearByEnemies[k].type == RobotType.MINERFACTORY &&
               nearByEnemies[k].location.distanceSquaredTo(rc.getLocation()) < nearestDist)
            {
                nearestDist = nearByEnemies[k].location.distanceSquaredTo(rc.getLocation());
                closest = k;
            }
        }

        if(closest != -1)
        {
            nearestDrone = nearByEnemies[closest].location;
        }
        else
        {
            nearestDrone = rc.getLocation();
        }

        rc.setIndicatorString(1, "Target" + target);
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        Utilities.handleMessageCounter(rc, Messaging.NumbOfDronesOdd.ordinal(), Messaging.NumbOfDronesEven.ordinal());
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (rc.getLocation().distanceSquaredTo(target) <= 35 || (roundNumb + 50) < Clock.getRoundNum())
        {
            target = findNextTarget();
            roundNumb = Clock.getRoundNum();
        }
        if (nearestDrone != null && !nearestDrone.equals(rc.getLocation()))
        {
            target = nearestDrone;
            roundNumb = Clock.getRoundNum();
        }

        rc.setIndicatorString(1, "In Navigator");
        return nav.takeNextStep(target);
    }

    private MapLocation findNextTarget() throws GameActionException
    {
        int choice = rand.nextInt(10);

        MapLocation toReturn = rc.getLocation();
        //70% chance of looking for mine factory
        if(choice < 7)
        {
            toReturn = tracker.getRandomMinerFactory();
        }
        else if (choice < 8)
        {
            toReturn = Utilities.getRandomLocation(rc);
        }
        //30% chance or no factories found for looking for miner
        if(toReturn.equals(rc.getLocation()))
        {
            toReturn = tracker.getRandomMiner();
        }
        if(toReturn.equals(rc.getLocation()))
        {
            MapLocation[] towers = rc.senseEnemyTowerLocations();
            // go the enemy HQ most of the time
            int decision = rand.nextInt(towers.length + 10);
            if (decision >= towers.length)
            {
                toReturn = rc.senseEnemyHQLocation();
            }
            else
            {
                toReturn = towers[decision];
            }
        }

        return toReturn;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
