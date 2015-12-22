package team044.Units;


import team044.*;

import battlecode.common.*;
import team044.Units.Rushers.MinerRusher;

import java.util.Random;

public class Miner extends Unit
{
    boolean mineToOurHQ = true;
    MapLocation lastSpot;
    double miningAmount = 5;
    int dir;
    Random rand;
    Direction[] dirs = Direction.values();
    int roundNum;
    boolean changeDir = false;

    public Miner(RobotController rc)
    {
        super(rc);

        nav.setCircle(true);
        rand = new Random(rc.getID() * Clock.getRoundNum());
        dir = rand.nextInt(8);
        lastSpot = rc.getLocation();
        roundNum = Clock.getRoundNum();
        if (rc.senseOre(lastSpot) > 12)
        {
            miningAmount = 12;
        }
    }

    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();

        if (target != null) {
            if (target.x == rc.readBroadcast(Messaging.OreX.ordinal()) && target.y == rc.readBroadcast(Messaging.OreY.ordinal()))
                rc.broadcast(Messaging.BestOre.ordinal(), 0);
            else if (target.x == rc.readBroadcast(Messaging.OreX2.ordinal()) && target.y == rc.readBroadcast(Messaging.OreY2.ordinal()))
                rc.broadcast(Messaging.BestOre2.ordinal(), 0);
        }

        rc.setIndicatorString(2, "Target: " + target);

        if (lastSpot != rc.getLocation())
        {
            lastSpot = rc.getLocation();
            double senseOre = rc.senseOre(lastSpot);
            // get new target every time we move
            //target = Utilities.greedyBestMiningSpot(rc);
            if (senseOre > 10)
            {
                miningAmount = 10;
            }
            else if (senseOre <= 5 && rc.senseOre(target) < 15)
            {
                miningAmount = 0.5;
            }
            else
            {
                miningAmount = 5;
            }
            roundNum = Clock.getRoundNum();
            changeDir = false;
        }

        // if we have been in the same location for more than 25 turns
        if (roundNum < (Clock.getRoundNum() - 15))
        {
            changeDir = true;
            //System.out.println("been at same location more than 25 turns");
            for (int i = 8; --i>=0; )
            {
                if (rc.canMove(dirs[i]))
                {
                    dir = i;
                }
            }
        }

        if (target == null || rc.getLocation() == target || (rc.canSenseLocation(target) && (rc.isLocationOccupied(target) || !rc.isPathable(rc.getType(), target))))
        {
            //rc.setIndicatorString(2, "get greedy spot: " + Clock.getRoundNum());
            target = MapDiscovery.lightOreSearch(rc);
        }

        if (changeDir)
        {
            target = rc.getLocation().add(dirs[dir], 2);
            rc.setIndicatorString(0, "ChangeDir: " + dirs[dir] + ", Location: " + target);
            changeDir = false;
        }
        if (rc.getLocation() == target || (rc.canSenseLocation(target) && !rc.isPathable(rc.getType(), target)))
        {
            rc.setIndicatorString(0, "Changing target because we can't go farther");
            target = rc.getLocation();
            while ((rc.canSenseLocation(target) && (!rc.isPathable(rc.getType(), target) || rc.getLocation().equals(target))))
            {
                if (rc.canSenseLocation(target) && rc.senseTerrainTile(target) == TerrainTile.OFF_MAP)
                {
                    dir = rand.nextInt(8);
                    target = rc.getLocation();
                }
                target = target.add(dirs[dir]);
            }
        }


        enemies = rc.senseNearbyRobots(24, opponent);

        if (enemies.length > 0 && Clock.getRoundNum() < rc.getRoundLimit() - 201)
        {
            if (Clock.getRoundNum() < 1000)
            {
                int numb = rc.readBroadcast(Messaging.NumbOfHarassers.ordinal());
                numb++;
                rc.broadcast(Messaging.NumbOfHarassers.ordinal(), numb);
                rc.setTeamMemory(TeamMemory.EnemyHarrass.ordinal(), numb);
            }
            rc.broadcast(Messaging.MinerUnderAttackX.ordinal(), enemies[0].location.x);
            rc.broadcast(Messaging.MinerUnderAttackY.ordinal(), enemies[0].location.y);
        }

    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        Utilities.handleMessageCounter(rc, Messaging.NumbOfMinersOdd.ordinal(), Messaging.NumbOfMinersEven.ordinal());
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (target == null)
        {
            target = enemyHQ;
        }
        // no need to move if our location is good enough
        else if (rc.senseOre(rc.getLocation()) >= miningAmount && rc.senseOre(target) < 10)
        {
            return false;
        }
        rc.setIndicatorString(0, "In nav moving to: " + target);
        return nav.takeNextStep(target);
        //return nav.badMovement(target);
    }

    public boolean fight() throws GameActionException
    {
        return fighter.minerMicro(nearByEnemies);
        //return fighter.basicFightMicro(nearByEnemies);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if (rc.readBroadcast(Messaging.RushEnemyBase.ordinal()) == 1)
        {
            return new MinerRusher(rc);
        }
        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (rc.isCoreReady() && rc.canMine() && rc.senseOre(rc.getLocation()) >= miningAmount)
        {
            rc.mine();
            return true;
        }

        return false;
    }
}
