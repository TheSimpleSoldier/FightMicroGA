package team044;

import battlecode.common.*;

import java.util.Random;

public class Utilities
{
    public static Random random = new Random();
    // location for methods that can be used across multiple domains

    private static int startChannelMineSpots = 100;

    public static MapLocation getBestSpotSimple(RobotController rc) throws GameActionException
    {
        Random rand = new Random(rc.getID() * Clock.getRoundNum());
        MapLocation location = rc.getLocation();
        int dist = (int) Math.sqrt((double) rc.senseEnemyHQLocation().distanceSquaredTo(rc.senseHQLocation()));
        dist = dist / 3;
        dist = dist * dist;
        int k = 10;
        for(; --k >= 0;)
        {
            MapLocation nextLocation = location.add(Direction.values()[rand.nextInt(8)]);

            if(rc.canSenseLocation(location))
            {
                if(spotBetter(rc, location, nextLocation, dist))
                {
                    location = nextLocation;
                }
            }
        }

        Direction[] dirs = Direction.values();
        //MapLocation location = rc.getLocation();

        if(rc.senseOre(location) >= 20 && farFromHome(rc, dist, rc.getLocation()))
        {
            return location;
        }

        for(k = 0; k < 8; k++)
        {
            if(rc.senseOre(location.add(dirs[k])) >= 20 && farFromHome(rc, dist, rc.getLocation()))
            {
                return location.add(dirs[k]);
            }
        }

        return rc.getLocation();
    }

    public static MapLocation newOreSpot(RobotController rc) throws GameActionException
    {
        MapLocation current = rc.getLocation();
        MapLocation best = new MapLocation(rc.readBroadcast(Messaging.OreX.ordinal()),rc.readBroadcast(Messaging.OreY.ordinal()));
        MapLocation best2 = new MapLocation(rc.readBroadcast(Messaging.OreX2.ordinal()),rc.readBroadcast(Messaging.OreY2.ordinal()));
        int bestSpotMiners = rc.readBroadcast(Messaging.BestSpotMiners.ordinal());
        if (bestSpotMiners < 7 && best.distanceSquaredTo(current) < best2.distanceSquaredTo(current))
        {
            bestSpotMiners++;
            rc.broadcast(Messaging.BestSpotMiners.ordinal(),bestSpotMiners);
            return best;
        }
        bestSpotMiners = rc.readBroadcast(Messaging.BestSpot2Miners.ordinal());
        if (bestSpotMiners < 7)
        {
            bestSpotMiners++;
            rc.broadcast(Messaging.BestSpot2Miners.ordinal(),bestSpotMiners);
            return best2;
        }
        return greedyBestMiningSpot(rc);
    }

    public static MapLocation getBestSpot(RobotController rc, boolean lightWeight) throws GameActionException
    {
        int numberMineSpots = 3;

        MapLocation location = rc.senseHQLocation();
        Random rand = new Random();

        //set to 20 or 50 depending on how many bytecodes you want to use
        //20 will use about 1700 and 50 will use about 4500
        int k = 50;
        if(lightWeight)
        {
            k = 20;
        }

        //run hill climbing for how many iterations specified earlier.
        for(; --k >= 0;)
        {
            MapLocation nextLocation = location.add(Direction.values()[rand.nextInt(8)]);

            if(rc.senseOre(nextLocation) >= rc.senseOre(location))
            {
                location = nextLocation;
            }
        }

        if(!minesInitialized(rc, numberMineSpots))
        {
            initializeMines(rc, numberMineSpots);
        }

        int startChannel = -1;


/*
        //if far from us, but also not near enemy
        if(farFromHome(rc, rc.getTeam(), location) && farFromHome(rc, rc.getTeam().opponent(), location))
        {
            startChannel = startChannelMineSpots + numberMineSpots * 3;
        }
        //if near us
        else if(!farFromHome(rc, rc.getTeam(), location))
        {
            startChannel = startChannelMineSpots;
        }*/

        //this looks at the current top spots and inserts the current spot if it is larger big enough
        if(startChannel != -1)
        {
            for(int a = 0; a < numberMineSpots; a++)
            {
                if(rc.readBroadcast(startChannel + a * 3) == location.x &&
                   rc.readBroadcast(startChannel + a * 3 + 1) == location.y &&
                   rc.readBroadcast(startChannel + a * 3 + 2) == rc.senseOre(location))
                {
                    return location;
                }
            }
            int lastBiggest = -1;
            for(int a = 0; a < numberMineSpots; a++)
            {
                if(rc.readBroadcast(startChannel + a * 3 + 2) < rc.senseOre(location))
                {
                    lastBiggest = a;
                    //shuffle order of locations to make room for new one
                    if(a > 0)
                    {
                        rc.broadcast(startChannel + (a - 1) * 3, rc.readBroadcast(startChannel + a * 3));
                        rc.broadcast(startChannel + (a - 1) * 3 + 1, rc.readBroadcast(startChannel + a * 3 + 1));
                        rc.broadcast(startChannel + (a - 1) * 3 + 2, rc.readBroadcast(startChannel + a * 3 + 2));
                    }
                }
            }
            if(lastBiggest > -1)
            {
                rc.broadcast(startChannel + lastBiggest * 3, location.x);
                rc.broadcast(startChannel + lastBiggest * 3 + 1, location.y);
                rc.broadcast(startChannel + lastBiggest * 3 + 2, (int)Math.round(rc.senseOre(location)));
            }
        }

        //this is mainly for debug purposes and should be removed when it no longer needs to be checked
        return location;
    }

    //sets all ore values to -1
    private static void initializeMines(RobotController rc, int numberMineSpots) throws GameActionException
    {
        for(int k = startChannelMineSpots + 2; k < startChannelMineSpots + numberMineSpots * 2 * 3; k += 3)
        {
            rc.broadcast(k, -1);
        }
    }

    private static boolean spotBetter(RobotController rc, MapLocation oldSpot, MapLocation newSpot, int dist)
    {
        if (rc.senseOre(newSpot) < rc.senseOre(oldSpot))
        {
            return false;
        }

        int score = 0;

        int oldDistToHQ = oldSpot.distanceSquaredTo(rc.senseHQLocation());
        if (oldDistToHQ < dist)
        {
            if (newSpot.distanceSquaredTo(rc.senseHQLocation()) >= oldDistToHQ)
            {
                score++;
            }
            else
            {
                score--;
            }
        }

        int oldDistToEnemyHQ = oldSpot.distanceSquaredTo(rc.senseEnemyHQLocation());
        if (oldDistToEnemyHQ <= dist)
        {
            if (newSpot.distanceSquaredTo(rc.senseEnemyHQLocation()) >= oldDistToEnemyHQ)
            {
                score++;
            }
            else
            {
                score--;
            }
        }

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        for (int i = enemyTowers.length; --i>=0; )
        {
            int oldDist = oldSpot.distanceSquaredTo(enemyTowers[i]);
            if (oldDist <= dist)
            {
                if (newSpot.distanceSquaredTo(enemyTowers[i]) >= oldDist)
                {
                    score++;
                }
                else
                {
                    score--;
                }
            }
        }

        if (score >= 0)
        {
            return true;
        }
        return false;
    }

    //this will check if a spot is near the towers or hq of a particular team.
    private static boolean farFromHome(RobotController rc, int dist, MapLocation location)
    {
        int close = dist;

        if(location.distanceSquaredTo(rc.senseEnemyHQLocation()) < close)
        {
            return false;
        }

        // building close to our HQ is pointless and gets in the way
        if(location.distanceSquaredTo(rc.senseHQLocation()) < (close * 2))
        {
            return false;
        }


        MapLocation[] towers;

        // we are only concerned about being far away from enemy towers
        towers = rc.senseEnemyTowerLocations();

        for(int k = towers.length; --k >= 0;)
        {
            if(location.distanceSquaredTo(towers[k]) < close)
            {
                return false;
            }
        }

        return true;
    }

    //checks if the mines channels have been initialized yet
    private static boolean minesInitialized(RobotController rc, int numberMineSpots) throws GameActionException
    {
        for(int k = startChannelMineSpots; k < startChannelMineSpots + numberMineSpots * 2 * 3; k++)
        {
            if(rc.readBroadcast(k) != 0)
            {
                return true;
            }
        }

        return false;
    }

    public static MapLocation greedyBestMiningSpot(RobotController rc) throws GameActionException
    {
        MapLocation best = rc.getLocation();
        int bestOre = 2;
        MapLocation[] availableSpots = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 24);

        for (int i = availableSpots.length; --i>=0; )
        {
            int ore = (int) rc.senseOre(availableSpots[i]);
            if (ore > bestOre)
            {
                bestOre = ore;
                best = availableSpots[i];
            }
        }

        rc.setIndicatorString(1, "Best: "+best);
        return best;
    }

    /**
     * This function returns the Robot type for a given message
     */
    public static RobotType getRobotType(BuildOrderMessaging message)
    {
        switch(message)
        {
            case BuildBeaverMiner:
                return RobotType.BEAVER;
            case BuildBeaverBuilder:
                return RobotType.BEAVER;
            case BuildAerospaceLab:
                return RobotType.AEROSPACELAB;
            case BuildBaracks:
                return RobotType.BARRACKS;
            case BuildBasher:
                return RobotType.BASHER;
            case BuildCommander:
                return RobotType.COMMANDER;
            case BuildComputer:
                return RobotType.COMPUTER;
            case BuildDrone:
                return RobotType.DRONE;
            case BuildHelipad:
                return RobotType.HELIPAD;
            case BuildLauncher:
                return RobotType.LAUNCHER;
            case BuildMiner:
                return RobotType.MINER;
            case BuildMinerFactory:
                return RobotType.MINERFACTORY;
            case BuildSoldier:
                return RobotType.SOLDIER;
            case BuildTank:
                return RobotType.TANK;
            case BuildTankFactory:
                return RobotType.TANKFACTORY;
            case BuildTechnologyInstitute:
                return RobotType.TECHNOLOGYINSTITUTE;
            case BuildTrainingField:
                return RobotType.TRAININGFIELD;
            case BuildSupplyDepot:
                return RobotType.SUPPLYDEPOT;
        }

        return null;
    }

    /**
     * This method determines if a unit can build another robot
     */
    public static boolean canBuild(RobotType robot, RobotController rc)
    {
        Direction[] dirs = Direction.values();

        for (int i = 0; i < dirs.length; i++)
        {
            if (rc.canMove(dirs[i]))
            {
                if (rc.canBuild(dirs[i], robot))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * This method handles all of the spawning of units
     */
    public static boolean spawnUnit(RobotType type, RobotController rc) throws GameActionException
    {
        if (!rc.isCoreReady())
        {
            return false;
        }

        if (type.oreCost > rc.getTeamOre())
        {
            return false;
        }

        Direction[] dirs = Direction.values();
        for (int i = 0; i < 8; i++)
        {
            if (rc.canSpawn(dirs[i], type))
            {
                rc.spawn(dirs[i], type);
                return true;
            }
        }
        return false;
    }

    /**
     * This method is for a unit to distribute Supplies to allies who are further away from the HQ
     */
    public static void shareSupplies(RobotController rc) throws GameActionException
    {
        // no use transfering no supplies
        if (rc.getSupplyLevel() <= 100)
        {
            return;
        }

        int dist = GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED;

        RobotInfo[] nearByAllies = rc.senseNearbyRobots(dist, rc.getTeam());
        if (nearByAllies.length <= 0)
        {
            return;
        }

        int roundNumb = Clock.getRoundNum();

        if (shareAllSupplies(rc, nearByAllies))
        {
        }
        else
        {
            int ourSupply = (int) rc.getSupplyLevel();
            int totalSupply = ourSupply;
            int index = 1;
            RobotType ally;
            for (int i = nearByAllies.length; --i>=0; )
            {
                ally = nearByAllies[i].type;
                if (ally == RobotType.BEAVER || ally == RobotType.AEROSPACELAB || ally == RobotType.BARRACKS || ally == RobotType.HQ || ally == RobotType.MINERFACTORY || ally == RobotType.SUPPLYDEPOT || ally == RobotType.TANKFACTORY || ally == RobotType.TOWER)
                {
                    continue;
                }
                /*else if (ally == RobotType.COMMANDER)
                {
                    int supply = (int) rc.getSupplyLevel() - 1;
                    rc.transferSupplies(supply, nearByAllies[i].location);
                    return;
                }*/
                int allySupply = (int) nearByAllies[i].supplyLevel;
                if (allySupply < ourSupply)
                {
                    totalSupply += allySupply;
                    index++;
                }
            }

            int averageSupply = totalSupply / index;

            for (int i = 0; i < nearByAllies.length; i++)
            {
                ally = nearByAllies[i].type;
                if (Clock.getBytecodesLeft() < 1250)
                {
                    break;
                }
                if (roundNumb != Clock.getRoundNum())
                {
                    break;
                }
                if (ally == RobotType.BEAVER || ally == RobotType.AEROSPACELAB || ally == RobotType.BARRACKS || ally == RobotType.HQ || ally == RobotType.MINERFACTORY || ally == RobotType.SUPPLYDEPOT || ally == RobotType.TANKFACTORY || ally == RobotType.TOWER)
                {
                    continue;
                }
                int allySupply = (int) nearByAllies[i].supplyLevel;
                if (allySupply < ourSupply)
                {
                    int supplyAmount = averageSupply - allySupply;
                    if (supplyAmount > 0)
                    {
                        rc.transferSupplies(supplyAmount, nearByAllies[i].location);
                    }
                }
            }
        }
    }

    /**
     * This method is for transferring almost all supplies right before death
     */
    public static boolean shareAllSupplies(RobotController rc, RobotInfo[] nearByAllies) throws GameActionException
    {
        /* TODO: Look into making a better Implementation of this
        if (rc.getHealth() < 20)
        {
            int totalSupplies = (int) rc.getSupplyLevel();
            totalSupplies = totalSupplies - 50;

            if (totalSupplies > 0)
            {
                for (int i = 0; i < nearByAllies.length; i++)
                {
                    if (Clock.getBytecodeNum() > 4000)
                    {
                        break;
                    }
                    if (rc.isLocationOccupied(nearByAllies[i].location))
                    {
                        rc.transferSupplies((totalSupplies/nearByAllies.length), nearByAllies[i].location);
                    }
                }
            }

            return true;
        }*/
        return false;
    }

    /**
     * This method is for a supply drone to give away its supply
     */
    public static boolean supplyArmy(RobotController rc) throws GameActionException
    {
        int dist = GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED - 1;

        RobotInfo[] allies = rc.senseNearbyRobots(dist, rc.getTeam());

        for (int i = 0; i < allies.length; i++)
        {
            if (Clock.getBytecodeNum() > 4000)
            {
                break;
            }
            // if building don't give it supply
            if (!allies[i].type.needsSupply())
            {
                continue;
            }
            int supplyAmount = (int) rc.getSupplyLevel() - 100;
            if (supplyAmount < 0 )
            {
                supplyAmount = 0;
            }
            rc.transferSupplies(supplyAmount, allies[i].location);
            return true;
        }

        return false;
    }

    /**
     * This method is for creating a structure
     * currently it tries to build it in the target location
     * but will build it in any open location if it can't
     */
    public static boolean BuildStructure(RobotController rc, MapLocation target, RobotType type) throws GameActionException
    {
        if (!rc.isCoreReady())
        {
            return false;
        }

        Direction dir = rc.getLocation().directionTo(target);

        if (rc.canMove(dir))
        {
            if (rc.canBuild(dir, type))
            {
                rc.build(dir, type);
                return true;
            }
        }
        else
        {
            Direction[] dirs = Direction.values();
            for (int i = 0; i < 8; i++)
            {
                if (rc.canMove(dirs[i]))
                {
                    if (rc.canBuild(dirs[i], type))
                    {
                        rc.build(dirs[i], type);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * This method returns the type of Building for an int
     * or -1 if it is not on list
     */
    public static RobotType getTypeForInt(int type)
    {
        if (type == BuildOrderMessaging.BuildAerospaceLab.ordinal())
        {
            return RobotType.AEROSPACELAB;
        }
        else if (type == BuildOrderMessaging.BuildBaracks.ordinal())
        {
            return RobotType.BARRACKS;
        }
        else if (type == BuildOrderMessaging.BuildHelipad.ordinal())
        {
            return RobotType.HELIPAD;
        }
        else if (type == BuildOrderMessaging.BuildMinerFactory.ordinal())
        {
            return RobotType.MINERFACTORY;
        }
        else if (type == BuildOrderMessaging.BuildTankFactory.ordinal())
        {
            return RobotType.TANKFACTORY;
        }
        else if (type == BuildOrderMessaging.BuildSupplyDepot.ordinal())
        {
            return RobotType.SUPPLYDEPOT;
        }
        else if (type == BuildOrderMessaging.BuildTechnologyInstitute.ordinal())
        {
            return RobotType.TECHNOLOGYINSTITUTE;
        }
        else if (type == BuildOrderMessaging.BuildTrainingField.ordinal())
        {
            return RobotType.TRAININGFIELD;
        }
        else if (type == BuildOrderMessaging.BuildMiningBaracks.ordinal())
        {
            return RobotType.BARRACKS;
        }
        else if (type == BuildOrderMessaging.BuildMiningAeroSpaceLab.ordinal())
        {
            return RobotType.MINERFACTORY;
        }
        return null;
    }

    /**
     * This method returns a MapLocation to build a target structure at
     */
    public static MapLocation findLocationForBuilding(RobotController rc, int numb, RobotType robotType) throws GameActionException
    {
        MapLocation target = null;

        // supply depot
        /*if (robotType == RobotType.SUPPLYDEPOT)
        {
            target = buildSupplyDepot(rc);
        }
        // mining facility
        else if (robotType == RobotType.MINERFACTORY)
        {
            target = buildMiningCamp(rc, numb);
        }
        // otherwise troop building
        else
        {
            //target = buildTrainingFacility(rc);
            target = buildSupplyDepot(rc);
        }*/

        target = buildSupplyDepot(rc);

        return target;
    }

    /**
     * Currently and unimplemented method for determining where to put the next supply depot
     */
    public static MapLocation buildSupplyDepot(RobotController rc) throws GameActionException
    {
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation target = ourHQ;

        Random rand = new Random(rc.getID() * Clock.getRoundNum());

        int dirToTake = rand.nextInt(8);
        Direction[] dirs = Direction.values();
        Direction dir = target.directionTo(rc.senseEnemyHQLocation());
        MapLocation next;
        int numbOfTries = 0;

        while (true) //rc.canSenseLocation(target) && !rc.isPathable(RobotType.BEAVER, target))
        {
            for (int i = 1; i < 8; i+=2)
            {
                next = target.add(dirs[i]);
                if (rc.canSenseLocation(next) && rc.isPathable(RobotType.BEAVER, next) && locationNotBlocked(rc, next, 3))
                {
                    return next;
                }
                else if (!rc.canSenseLocation(next))
                {
                    MapLocation last = next.add(next.directionTo(ourHQ));
                    while (!rc.canSenseLocation(last))
                    {
                        last = last.add(last.directionTo(ourHQ));
                    }

                    if (rc.canSenseLocation(last) && rc.senseTerrainTile(last) != TerrainTile.OFF_MAP && numbOfTries > 6)
                    {
                        return next;
                    }
                    else
                    {
                        numbOfTries++;
                        dirToTake = rand.nextInt(8);
                        target = ourHQ;
                    }
                }
            }

            if (rc.canSenseLocation(target) && rc.senseTerrainTile(target) == TerrainTile.OFF_MAP)
            {
                target = ourHQ;
                dirToTake = rand.nextInt(8);
            }

            if (dirToTake == 0)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir);
                }
                else
                {
                    target = target.add(dir.rotateLeft());
                }
            }
            else if (dirToTake == 1)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.rotateRight(), 2);
                }
                else
                {
                    target = target.add(dir.rotateRight());
                }
            }
            else if (dirToTake == 2)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.rotateLeft(), 2);
                }
                else
                {
                    target = target.add(dir, 2);
                }
            }
            else if (dirToTake == 3)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.rotateRight().rotateRight());
                }
                else
                {
                    target = target.add(dir.rotateRight().rotateRight(), 2);
                }
            }
            else if (dirToTake == 4)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.rotateLeft().rotateLeft());
                }
                else
                {
                    target = target.add(dir.rotateLeft().rotateLeft(), 2);
                }
            }
            else if (dirToTake == 5)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.rotateRight().rotateRight().rotateRight(), 2);
                }
                else
                {
                    target = target.add(dir.rotateRight().rotateRight().rotateRight());
                }
            }
            else if (dirToTake == 6)
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.rotateLeft().rotateLeft().rotateLeft(), 2);
                }
                else
                {
                    target = target.add(dir.rotateLeft().rotateLeft().rotateLeft());
                }
            }
            else
            {
                if (dir.isDiagonal())
                {
                    target = target.add(dir.opposite());
                }
                else
                {
                    target = target.add(dir.opposite(), 2);
                }
            }
        }

        //System.out.println("Target returned");
        //return target;
    }

    public static boolean locationNotBlocked(RobotController rc, MapLocation spot, int openings) throws GameActionException
    {
        int bytecodes = Clock.getBytecodeNum();
        Direction[] dirs = Direction.values();

        int openSpots;
        int openSpots2 = 0;

        for (int i = 0; i < 8; i++)
        {
            openSpots = 0;
            MapLocation next = spot.add(dirs[i]);

            if (rc.isPathable(RobotType.BEAVER, spot))
            {
                openSpots2++;
            }

            for (int j = 0; j < 8; j++)
            {
                MapLocation checking = next.add(dirs[j]);
                if (rc.isPathable(RobotType.BEAVER, checking))
                {
                    openSpots++;
                }
            }

            if (openSpots <= openings)
            {
                rc.setIndicatorString(1, "Bytecodes used: " + (Clock.getBytecodeNum() - bytecodes) + ", Round: " + Clock.getRoundNum());
                return false;
            }

        }

        if (openSpots2 < openings)
        {
            return false;
        }

        rc.setIndicatorString(1, "Bytecodes used: " + (Clock.getBytecodeNum() - bytecodes) + ", Round: " + Clock.getRoundNum());
        return true;
    }

    /**
     * This method builds a mining camp at the tower that the beaver was assigned to
     */
    public static MapLocation buildMiningCamp(RobotController rc, int numb)
    {
        MapLocation target = null;

        MapLocation[] towers = rc.senseTowerLocations();

        if (numb == 0)//< towers.length)
        {
            target = rc.senseHQLocation();  //getTowerClosestToEnemyHQ(rc);
        }
        else
        {
            // first go to right
            if (numb == 1)//towers.length)
            {
                MapLocation ourHQ = rc.senseHQLocation();
                MapLocation enemyHQ = rc.senseEnemyHQLocation();
                int dist = ourHQ.distanceSquaredTo(enemyHQ);
                Direction dir = ourHQ.directionTo(enemyHQ);
                dir = dir.rotateRight();
                MapLocation current = rc.senseHQLocation().add(dir);
                int newDist = current.distanceSquaredTo(ourHQ);
                int counter = 0;
                while (newDist < (dist*2/5))
                {
                    current = current.add(dir);
                    newDist = current.distanceSquaredTo(ourHQ);
                    dir = current.directionTo(enemyHQ).rotateRight();
                    if (counter % 2 == 0)
                    {
                        dir = dir.rotateRight();
                    }
                    counter++;
                }
                target = current;
            }
            // next go to left
            else
            {
                MapLocation ourHQ = rc.senseHQLocation();
                MapLocation enemyHQ = rc.senseEnemyHQLocation();
                int dist = ourHQ.distanceSquaredTo(enemyHQ);
                Direction dir = ourHQ.directionTo(enemyHQ);
                dir = dir.rotateLeft();
                MapLocation current = rc.senseHQLocation().add(dir);
                int newDist = current.distanceSquaredTo(ourHQ);
                int counter = 0;
                while (newDist < dist*2/5)
                {
                    current = current.add(dir);
                    newDist = current.distanceSquaredTo(ourHQ);
                    dir = current.directionTo(enemyHQ).rotateLeft();
                    if (counter % 2 == 0)
                    {
                        dir = dir.rotateLeft();
                    }
                    counter++;
                }

                target = current;
            }
        }

        target = target.add(target.directionTo(rc.getLocation()));

        return target;
    }

    /**
     * This method builds a training facility near our HQ
     * so that the units it spawns can get supply
     */
    public static MapLocation buildTrainingFacility(RobotController rc) throws GameActionException
    {
        MapLocation target = rc.senseHQLocation();

        Direction[] dirs = Direction.values();

        random = new Random();

        int dir = random.nextInt(8);

        target = target.add(dirs[dir], 2);

        while (!rc.isPathable(rc.getType(), target))
        {
            dir = random.nextInt(8);

            target = target.add(dirs[dir]);

        }

        return target;
    }

    /**
     * This method determines if we are within firing distance
     * of an enemy tower or HQ
     */
    public static boolean nearEnemyTower(RobotController rc)
    {
        MapLocation[] towers = rc.senseEnemyTowerLocations();

        for (int i = 0; i < towers.length; i++)
        {
            if (rc.getLocation().distanceSquaredTo(towers[i]) < 34)
            {
                return true;
            }
        }

        if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 34)
        {
            return true;
        }
        return false;
    }

    /**
     * This method returns the Tower that is closest to the enemy's HQ
     * Which is generally the first tower to come under attack
     */
    public static MapLocation getTowerClosestToEnemyHQ(RobotController rc)
    {
        MapLocation[] towers = rc.senseTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        int bestDist = 9999999;
        MapLocation bestTower = null;

        for (int i = 0; i < towers.length; i++)
        {
            int dist = towers[i].distanceSquaredTo(enemyHQ);
            if (dist < bestDist)
            {
                bestDist = dist;
                bestTower = towers[i];
            }
        }

        // we want to rally in front of our tower so the enemy launchers
        // don't have the advantage of knowing where we are
        if (bestTower != null)
        {
        //    bestTower = bestTower.add(bestTower.directionTo(enemyHQ), 5);
        }
        else
        {
            bestTower = rc.senseHQLocation().add(rc.senseHQLocation().directionTo(enemyHQ), 10);
        }

        return bestTower;
    }


    /**
     * This method handles units incrementing the counter that tells us how many
     * units of each type we have
     */
    public static void handleMessageCounter(RobotController rc, int channelOdd, int channelEven) throws GameActionException
    {
        // even
        if (Clock.getRoundNum() % 2 == 0)
        {
            int numb = rc.readBroadcast(channelEven);
            numb++;
            rc.broadcast(channelEven, numb);
        }
        // odd
        else
        {
            int numb = rc.readBroadcast(channelOdd);
            numb++;
            rc.broadcast(channelOdd, numb);
        }
    }

    /**
     * This method will build the requirement for a building
     */
    public static void buildRequirement(RobotController rc, MapLocation spot, RobotType type) throws GameActionException
    {
        RobotType buildStruct = null;
        // need to build barracks
        if (type == RobotType.TANKFACTORY)
        {
            buildStruct = RobotType.BARRACKS;
        }
        // need to build a helipad
        else if (type == RobotType.AEROSPACELAB)
        {
            buildStruct = RobotType.HELIPAD;
        }
        // need to build a technology institue
        else if (type == RobotType.TRAININGFIELD && Clock.getRoundNum() > 500)
        {
            buildStruct = RobotType.TECHNOLOGYINSTITUTE;
        }
        else
        {
            System.out.println("Unknown building type");
        }

        RobotInfo[] allies = rc.senseNearbyRobots(99999, rc.getTeam());

        for (int i = allies.length; --i>=0;)
        {
            if (allies[i].type == buildStruct)
            {
                return;
            }
        }

        BuildStructure(rc, spot, buildStruct);
    }

    /**
     * This method returns the closest tower
     */
    public static MapLocation closestTower(RobotController rc, MapLocation[] towers)
    {
        int closestDist = 99999999;
        MapLocation closest = null;
        MapLocation us = rc.getLocation();

        for (int i = towers.length; --i>=0; )
        {
            int dist = towers[i].distanceSquaredTo(us);
            if (dist < closestDist)
            {
                closestDist = dist;
                closest = towers[i];
            }
        }

        return closest;
    }

    /**
     * This method finds the closest tower to a location
     */
    public static MapLocation closestTowerToLoc(MapLocation[] towers, MapLocation current)
    {
        int closestDist = 99999;
        MapLocation closest = null;

        for (int i = towers.length; --i>=0; )
        {
            int dist = towers[i].distanceSquaredTo(current);
            if (dist < closestDist)
            {
                closestDist = dist;
                closest = towers[i];
            }
        }

        return closest;
    }

    /**
     * This is a test
     */
    public static int test(RobotController rc) throws GameActionException
    {
        int numbOfMinerFactories = 0;
        int numbOfTankFactories = 0;
        int numbOfBarracks = 0;
        int numbOfHelipads = 0;
        int numbOfAerospacelab = 0;
        int numbOfSupplyDepots = 0;
        int numbOfTrainingfields = 0;
        int numbOfTechnologyInstitutes = 0;

        RobotInfo[] allies = rc.senseNearbyRobots(9999, rc.getTeam());

        for (int i = allies.length; --i>=0; )
        {
            if (allies[i].type == RobotType.BARRACKS)
            {
                numbOfBarracks++;
            }
            else if (allies[i].type == RobotType.MINERFACTORY)
            {
                numbOfMinerFactories++;
            }
            else if (allies[i].type == RobotType.TANKFACTORY)
            {
                numbOfTankFactories++;
            }
            else if (allies[i].type == RobotType.HELIPAD)
            {
                numbOfHelipads++;
            }
            else if (allies[i].type == RobotType.AEROSPACELAB)
            {
                numbOfAerospacelab++;
            }
            else if (allies[i].type == RobotType.SUPPLYDEPOT)
            {
                numbOfSupplyDepots++;
            }
            else if (allies[i].type == RobotType.TRAININGFIELD)
            {
                numbOfTrainingfields++;
            }
            else if (allies[i].type == RobotType.TECHNOLOGYINSTITUTE)
            {
                numbOfTechnologyInstitutes++;
            }

        }

        rc.setIndicatorString(0, "Barracks: " + numbOfBarracks + ", MinerFactory: " + numbOfMinerFactories + ", Tank Factory: " + ", Helipads: " + numbOfHelipads);
        return numbOfTankFactories + numbOfBarracks + numbOfHelipads + numbOfMinerFactories + numbOfAerospacelab + numbOfSupplyDepots + numbOfTechnologyInstitutes + numbOfTrainingfields;
    }

    /**
     * This method returns the rush location
     */
    public static MapLocation getRushLocation(RobotController rc)
    {
        MapLocation[] towers = rc.senseEnemyTowerLocations();

        if (towers.length == 0)
        {
            return rc.senseEnemyHQLocation();
        }

        int bestDist = 99999;
        MapLocation best = null;
        for (int i = towers.length; --i>=0; )
        {
            int dist = towers[i].distanceSquaredTo(rc.senseHQLocation());
            if (dist < bestDist)
            {
                bestDist = dist;
                best = towers[i];
            }
        }

        if (towers.length <= 2)
        {
            MapLocation enemyHQ = rc.senseEnemyHQLocation();
            if (rc.getLocation().distanceSquaredTo(enemyHQ) > rc.getLocation().distanceSquaredTo(best))
            {
                return best;
            }
            else
            {
                return rc.senseEnemyHQLocation();
            }
        }


        return best;
    }

    /**
     * This method returns the if a location is within firing distance of a tower or enemy HQ
     */
    public static boolean locInRangeOfEnemyTower(MapLocation spot, MapLocation[] towers, MapLocation enemyHQ)
    {
        int HQRange = 24;

        if (towers.length >= 5)
        {
            HQRange = 52;
        }
        else if (towers.length >= 2)
        {
            HQRange = 35;
        }

        for (int i = 0; i < towers.length; i++)
        {
            if (spot.distanceSquaredTo(towers[i]) <= 24)
            {
                return true;
            }
        }

        if (spot.distanceSquaredTo(enemyHQ) <= HQRange)
        {
            return true;
        }

        return false;
    }

    /**
     * This method determines if we are surrounded and need to cut all prod except for launchers
     */
    public static boolean cutProd(RobotController rc)
    {
        // this function doesn't apply in early game
        if (Clock.getRoundNum() > 500 && rc.getTeamOre() < 600)
        {
            RobotInfo[] allies = rc.senseNearbyRobots(99999, rc.getTeam());

            boolean haveAerospaceLab = false;
            int numbOfMiners = 0;
            RobotType ally;

            for (int i = allies.length; --i>=0; )
            {
                ally = allies[i].type;
                if (ally == RobotType.AEROSPACELAB)
                {
                    haveAerospaceLab = true;
                }
                else if (ally == RobotType.MINER)
                {
                    numbOfMiners++;
                }
            }

            // if we have an aerospace lab and not economy then just mass launchers
            if (haveAerospaceLab && numbOfMiners < 6)
            {
                return true;
            }
        }
        return false;
    }


    public static MapLocation getRandomLocation(RobotController rc)
    {
        MapLocation us = rc.getLocation();

        MapLocation next = us;
        MapLocation nextTry;
        Direction dir;
        Direction[] dirs = Direction.values();
        Random rand = new Random(rc.getID() * Clock.getRoundNum());

        while (us.distanceSquaredTo(next) < 100)
        {
            nextTry = next.add(dirs[rand.nextInt(8)], 5);
            if (rc.isPathable(rc.getType(), nextTry))
            {
                next = nextTry;
            }
        }

        return next;
    }


    /**
     * This method gets our tower closest to the center
     */
    public static MapLocation getCentralTower(RobotController rc, MapLocation[] towers)
    {
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        int x = (ourHQ.x + enemyHQ.x) / 2;
        int y = (ourHQ.y + enemyHQ.y) / 2;

        System.out.println("X: " + x + ", Y: "+ y);

        MapLocation center = new MapLocation(x,y);
        int closestDist = 99999999;
        MapLocation closest = null;

        for (int i = towers.length; --i>=0; )
        {
            int dist = towers[i].distanceSquaredTo(center);

            if (dist < closestDist)
            {
                closestDist = dist;
                closest = towers[i];
            }
        }

        if (closest == null)
        {
            closest = ourHQ.add(ourHQ.directionTo(enemyHQ), 15);
        }
        else
        {
            closest = closest.add(ourHQ.directionTo(enemyHQ), 5);
        }

        // group up on our side of center
        center = center.add(enemyHQ.directionTo(ourHQ), 5);

        return center;
        //return closest;
    }

    /**
     * This method gets the tower on our right flank
     */
    public static MapLocation getRightFlank(RobotController rc, MapLocation[] towers)
    {
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        Direction direction = ourHQ.directionTo(enemyHQ);
        int maxDist = -99999999;
        MapLocation location = null;

        for (int i = towers.length; --i>=0; )
        {
            Direction dir = ourHQ.directionTo(towers[i]);

            if (dir == direction.rotateRight() || dir == direction.rotateRight().rotateRight())
            {
                int dist = towers[i].distanceSquaredTo(ourHQ);
                if (dist > maxDist)
                {
                    maxDist = dist;
                    location = towers[i];
                }
            }
        }

        if (location == null)
        {
            location = ourHQ.add(direction.rotateRight(), 15);
        }

        return location;
    }

    /**
     * This method gets the tower on our left flank
     */
    public static MapLocation getLeftFlank(RobotController rc, MapLocation[] towers)
    {
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        Direction direction = ourHQ.directionTo(enemyHQ);
        int maxDist = -99999999;
        MapLocation location = null;

        for (int i = towers.length; --i>=0; )
        {
            Direction dir = ourHQ.directionTo(towers[i]);

            if (dir == direction.rotateLeft() || dir == direction.rotateLeft().rotateLeft())
            {
                int dist = towers[i].distanceSquaredTo(ourHQ);
                if (dist > maxDist)
                {
                    maxDist = dist;
                    location = towers[i];
                }
            }
        }

        if (location == null)
        {
            location = ourHQ.add(direction.rotateLeft(), 15);
        }

        return location;
    }

    /**
     * This method gets the enemies tower on the right Flank
     */
    public static MapLocation enemyTowerOnRightFlank(RobotController rc, MapLocation[] towers)
    {
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        Direction direction = enemyHQ.directionTo(ourHQ);
        int maxDist = -9999999;
        MapLocation location = null;

        for (int i = towers.length; --i>=0; )
        {
            Direction dir = enemyHQ.directionTo(towers[i]);

            if (dir == direction.rotateLeft() || dir == direction.rotateLeft().rotateLeft())
            {
                int dist = towers[i].distanceSquaredTo(enemyHQ);
                if (dist > maxDist)
                {
                    maxDist = dist;
                    location = towers[i];
                }
            }
        }

        return location;
    }

    /**
     * This method gets the enemies tower on the left flank
     */
    public static MapLocation enemyTowerOnLeftFlank(RobotController rc, MapLocation[] towers)
    {
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        Direction direction = enemyHQ.directionTo(ourHQ);
        int maxDist = -9999999;
        MapLocation location = null;

        for (int i = towers.length; --i>=0; )
        {
            Direction dir = enemyHQ.directionTo(towers[i]);

            if (dir == direction.rotateRight() || dir == direction.rotateRight().rotateRight())
            {
                int dist = towers[i].distanceSquaredTo(enemyHQ);
                if (dist > maxDist)
                {
                    maxDist = dist;
                    location = towers[i];
                }
            }
        }

        return location;
    }

    //ONLY TO BE USED FOR DRONE SURROUNDS!!!
    //does not factor in voids
    public static boolean towersBlocking(RobotController rc)
    {
        MapLocation[] towers = rc.senseTowerLocations();
        MapLocation edgeTower = null;

        for(int k = towers.length; --k >= 0;)
        {
            if(nextToEdge(rc, towers[k]))
            {
                edgeTower = towers[k];
                break;
            }
        }

        if(edgeTower == null)
        {
            System.out.println("halfway");
            return false;
        }

        MapLocation lastTower = edgeTower;
        MapLocation currentTower = edgeTower;

        for(int k = towers.length; --k >= 0;)
        {
            System.out.println("last: " + lastTower.toString());
            System.out.println("current: " + currentTower.toString());
            for(int a = towers.length; --a >= 0;)
            {
                System.out.println("try: " + towers[a].toString());
                MapLocation temp = currentTower.add(currentTower.directionTo(towers[a]));
                while(currentTower.distanceSquaredTo(temp) <= 24 ||
                        towers[a].distanceSquaredTo(temp) <= 24)
                {
                    temp = temp.add(temp.directionTo(towers[a]));
                    if(temp.equals(towers[a]))
                    {
                        break;
                    }
                }
                if(temp.equals(towers[a]) && !towers[a].equals(currentTower) &&
                   !towers[a].equals(lastTower))
                {
                    System.out.println("works");
                    lastTower = currentTower;
                    currentTower = towers[a];
                    break;
                }
            }

            if(nextToEdge(rc, currentTower) && currentTower.distanceSquaredTo(edgeTower) > 100)
            {
                return true;
            }
        }

        return false;
    }

    private static boolean nextToEdge(RobotController rc, MapLocation spot)
    {
        Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for(int a = 4; --a >= 0;)
        {
            if(rc.senseTerrainTile(spot.add(dirs[a], 5)) == TerrainTile.OFF_MAP)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean mobileUnit(RobotInfo unit)
    {
        if (unit == null)
        {
            return false;
        }

        switch(unit.type)
        {
            case LAUNCHER:
                return true;
            case MISSILE:
                return true;
            case BASHER:
                return true;
            case BEAVER:
                return true;
            case SOLDIER:
                return true;
            case COMPUTER:
                return true;
            case TANK:
                return true;
            case COMMANDER:
                return true;
            case DRONE:
                return true;
            default:
                return false;
        }
    }
}
