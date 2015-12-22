package team044;

import battlecode.common.*;

import java.util.Random;

/**
 * Created by joshua on 1/8/15.
 *
 * The goal of this class is to allow bots to search and destroy the enemies mining operations
 * The record methods should be used as part of the collectData methods of each unit
 *
 * The channels are used as follows:
 * first one is how many miner factories are recorded
 * second is how many miners are recorded
 * the next 30 store 10 copies of x,y,round seen in that order for miner factories
 * the remaining 168 store 42 copies of miner data where each one stores id,x,y,round seen in that order
 *
 * the reason for not saving the miner factory ids is they never move, and the enemy might rebuild it
 */
public class EnemyMinerTracker
{
    private int startChannel = 200;
    private int requiredFresh = 100;
    private int maxMinerFactories = 10;
    private int maxMiners = 42;
    private RobotController rc;
    private Random rand;

    public EnemyMinerTracker(RobotController rc)
    {
        this.rc = rc;
        rand = new Random(rc.getID());
    }

    //this method takes the full enemyBots array from a unit and extracts the miners and miner factories.
    public void record(RobotInfo[] enemyBots) throws GameActionException
    {
        if(enemyBots == null)
        {
            return;
        }

        for(int k = 0; k < enemyBots.length; k++)
        {
            if(enemyBots[k].type == RobotType.MINER)
            {
                recordMiner(enemyBots[k].ID, enemyBots[k].location.x, enemyBots[k].location.y);
            }
            else if(enemyBots[k].type == RobotType.MINERFACTORY)
            {
                recordMinerFactory(enemyBots[k].location.x, enemyBots[k].location.y);
            }
        }
    }

    //put in a new value for miners
    public void recordMiner(int id, int x, int y) throws GameActionException
    {
        int miners = rc.readBroadcast(startChannel + 1);

        boolean found = false;
        int oldest = 0;
        int oldestValue = GameConstants.ROUND_MAX_LIMIT;
        //go through all current and update it if the bot is already there
        for(int k = 0; k < miners; k++)
        {
            //if you find it, update the info and exit the loop
            if(rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4) == id)
            {
                found = true;
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 1, x);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 2, y);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 3, Clock.getRoundNum());
                break;
            }
            //otherwise, work on an update for the oldest value. this will be used later possibly
            else
            {
                int temp = rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 3);
                if(temp < oldestValue)
                {
                    oldest = k;
                    oldestValue = temp;
                }
            }
        }

        //if it didn't find the miner
        if(!found)
        {
            //if we haven't filled all the spots for miners
            if(miners < maxMiners)
            {
                rc.broadcast(startChannel + 1, miners + 1);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + miners * 4, id);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + miners * 4 + 1, x);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + miners * 4 + 2, y);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + miners * 4 + 3, Clock.getRoundNum());
            }
            //otherwise, use the oldest(taken from before) and replace it
            else
            {
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + oldest * 4, id);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + oldest * 4 + 1, x);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + oldest * 4 + 2, y);
                rc.broadcast(startChannel + 2 + maxMinerFactories * 3 + oldest * 4 + 3, Clock.getRoundNum());
            }
        }
    }

    //put in a new value for miner factories
    public void recordMinerFactory(int x, int y) throws GameActionException
    {
        int minerFactories = rc.readBroadcast(startChannel);

        boolean found = false;
        int oldest = 0;
        int oldestValue = GameConstants.ROUND_MAX_LIMIT;
        //go through all current and update it if the factory is already there
        for(int k = 0; k < minerFactories; k++)
        {
            //if you find it, update the info and exit the loop
            if(rc.readBroadcast(startChannel + 2 + k * 3) == x &&
               rc.readBroadcast(startChannel + 2 + k * 3 + 1) == y)
            {
                found = true;
                rc.broadcast(startChannel + 2 + k * 3 + 2, Clock.getRoundNum());
                break;
            }
            //otherwise, work on an update for the oldest value. this will be used later possibly
            else
            {
                int temp = rc.readBroadcast(startChannel + 2 + k * 3 + 2);
                if(temp < oldestValue)
                {
                    oldest = k;
                    oldestValue = temp;
                }
            }
        }

        //if it didn't find the miner factory
        if(!found)
        {
            //if we haven't filled all the spots for miner factories
            if(minerFactories < maxMinerFactories)
            {
                rc.broadcast(startChannel, minerFactories + 1);
                rc.broadcast(startChannel + 2 + minerFactories * 3, x);
                rc.broadcast(startChannel + 2 + minerFactories * 3 + 1, y);
                rc.broadcast(startChannel + 2 + minerFactories * 3 + 2, Clock.getRoundNum());
            }
            //otherwise, use the oldest(taken from before) and replace it
            else
            {
                rc.broadcast(startChannel + 2 + oldest * 3, x);
                rc.broadcast(startChannel + 2 + oldest * 3 + 1, y);
                rc.broadcast(startChannel + 2 + oldest * 3 + 2, Clock.getRoundNum());
            }
        }
    }

    public MapLocation getNearestMiner() throws GameActionException
    {
        int miners = rc.readBroadcast(startChannel + 1);
        if(miners == 0)
        {
            return rc.getLocation();
        }

        int closest = 0;
        double closestValue = Math.sqrt(Math.pow(GameConstants.MAP_MAX_HEIGHT, 2) +
                                        Math.pow(GameConstants.MAP_MAX_WIDTH, 2));
        for(int k = 0; k < miners; k++)
        {
            MapLocation tempLoc = new MapLocation(
                    rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 1),
                    rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 2));
            double tempDist = tempLoc.distanceSquaredTo(rc.getLocation());

            //this checks if the spot is the closest that is not on the bot's spot and has been updated recently
            if(tempDist < closestValue && tempDist > .1 &&
               rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + k * 4 + 3) >
               Clock.getRoundNum() - requiredFresh)
            {
                closest = k;
                closestValue = tempDist;
            }
        }

        return new MapLocation(rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + closest * 4 + 1),
                               rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + closest * 4 + 2));
    }

    public MapLocation getNearestMinerFactory() throws GameActionException
    {
        int minerFactories = rc.readBroadcast(startChannel);

        if(minerFactories == 0)
        {
            return rc.getLocation();
        }

        int closest = 0;
        double closestValue = Math.sqrt(Math.pow(GameConstants.MAP_MAX_HEIGHT, 2) +
                                        Math.pow(GameConstants.MAP_MAX_WIDTH, 2));
        for(int k = 0; k < minerFactories; k++)
        {
            MapLocation tempLoc = new MapLocation(
                    rc.readBroadcast(startChannel + 2 + k * 3),
                    rc.readBroadcast(startChannel + 2 + k * 3 + 1));
            double tempDist = tempLoc.distanceSquaredTo(rc.getLocation());

            //this checks if the spot is the closest that is not on the bot's spot and has been updated recently
            if(tempDist < closestValue && tempDist > .1 &&
               rc.readBroadcast(startChannel + 2 + k * 3 + 2) >
               Clock.getRoundNum() - requiredFresh)
            {
                closest = k;
                closestValue = tempDist;
            }
        }

        return new MapLocation(rc.readBroadcast(startChannel + 2 + closest * 3),
                               rc.readBroadcast(startChannel + 2 + closest * 3 + 1));
    }

    public MapLocation getRandomMiner() throws GameActionException
    {
        int miners = rc.readBroadcast(startChannel + 1);
        if(miners == 0)
        {
            return rc.getLocation();
        }
        int randSpot = rand.nextInt(miners);
        int checked = 0;

        //it will keep trying new locations till one is fresh enough or it has tried 50 times
        //the try 50 times to prevent an infinite loo[
        while(rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + randSpot * 4 + 3) <
              Clock.getRoundNum() - requiredFresh && checked < 50)
        {
            randSpot = rand.nextInt(miners);
            checked++;
        }

        return new MapLocation(rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + randSpot * 4 + 1),
                               rc.readBroadcast(startChannel + 2 + maxMinerFactories * 3 + randSpot * 4 + 2));
    }

    public MapLocation getRandomMinerFactory() throws GameActionException
    {
        int minerFactories = rc.readBroadcast(startChannel);
        if(minerFactories == 0)
        {
            return rc.getLocation();
        }
        int randSpot = rand.nextInt(minerFactories);
        int checked = 0;

        //it will keep trying new locations till one is fresh enough or it has tried 50 times
        //the try 50 times to prevent an infinite loo[
        while(rc.readBroadcast(startChannel + 2 + randSpot * 3 + 2) <
              Clock.getRoundNum() - requiredFresh && checked < 50)
        {
            randSpot = rand.nextInt(minerFactories);
            checked++;
        }

        return new MapLocation(rc.readBroadcast(startChannel + 2 + randSpot * 3),
                               rc.readBroadcast(startChannel + 2 + randSpot * 3 + 1));
    }
}
