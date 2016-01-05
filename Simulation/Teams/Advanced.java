package Simulation.Teams;

import Simulation.ActivationFunction;
import Simulation.FeedForwardNeuralNetwork;
import Simulation.MockRobotPlayer;
import battlecode.common.*;

/**
 * Created by fred on 12/29/15.
 */
public class Advanced extends MockRobotPlayer {

    public Advanced(RobotController rc, double[][] weights) {
        super(rc, weights);
    }

    public void run() {
        if (target == null) {
            target = rc.senseEnemyHQLocation();
        }

        if (rc.getType() == RobotType.SOLDIER) {
            // run soldier code
            RobotInfo[] nearByEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
            RobotInfo[] allBots = rc.senseNearbyRobots(rc.getType().attackRadiusSquared);

            //System.out.println(rc.isCoreReady());
            if (nearByEnemies.length == 0 && rc.isCoreReady()) {
                // move towards target
                move(target);
            } else if (nearByEnemies.length > 0) {
                // fight
                try { runFightMicro(nearByEnemies); } catch (Exception e) { e.printStackTrace(); }
            } else {
//                System.out.println("Waiting");
            }
        } else {
            // do nothing
        }
    }

    /**
     * This function causes the unit to move
     *
     * @param target
     */
    public void move(MapLocation target) {
        Direction dir = getDir(target);
        try
        {
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
            else if (rc.canMove(dir.rotateRight()))
            {
                rc.move(dir.rotateRight());
            }
            else if (rc.canMove(dir.rotateLeft()))
            {
                rc.move(dir.rotateLeft());
            }
            else if (rc.canMove(dir.rotateLeft().rotateLeft()))
            {
                rc.move(dir.rotateLeft().rotateLeft());
            }
            else if (rc.canMove(dir.rotateRight().rotateRight()))
            {
                rc.move(dir.rotateRight().rotateRight());
            }
            else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft()))
            {
                rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
            }
            else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight()))
            {
                rc.move(dir.rotateRight().rotateRight().rotateRight());
            }
            else if (rc.canMove(dir.opposite()))
            {
                rc.move(dir.opposite());
            }
        }
        catch (Exception e) {
            System.out.println("Failed to move");
            e.printStackTrace();
        }
    }

    private Direction getDir(MapLocation target) {
        return rc.getLocation().directionTo(target);
    }

    /**
     * This method prioritizes towers, then launchers, then the weakest enemy
     */
    public static RobotInfo prioritizeTargets(RobotInfo[] nearByEnemies)
    {
        RobotInfo weakestTower = null;
        RobotInfo weakestLauncher = null;
        RobotInfo enemyHQ = null;
        RobotInfo weakest = nearByEnemies[0];

        for (int i = 0; i < nearByEnemies.length; i++)
        {
            // commander gets highest priority to eliminate him and his leadership bonus
            if (nearByEnemies[i].type == RobotType.COMMANDER)
            {
                return nearByEnemies[i];
            }
            else if (nearByEnemies[i].type == RobotType.TOWER)
            {
                if (weakestTower == null || nearByEnemies[i].health < weakestTower.health)
                {
                    weakestTower = nearByEnemies[i];
                }
            }
            else if (nearByEnemies[i].type == RobotType.LAUNCHER)
            {
                if (weakestLauncher == null || nearByEnemies[i].health < weakestLauncher.health)
                {
                    weakestLauncher = nearByEnemies[i];
                }
            }
            else if (nearByEnemies[i].type == RobotType.HQ)
            {
                enemyHQ = nearByEnemies[i];
            }
            else if (weakest.health > nearByEnemies[i].health)
            {
                weakest = nearByEnemies[i];
            }
        }

        if (weakestTower != null)
        {
            return weakestTower;
        }
        else if (weakestLauncher != null)
        {
            return weakestLauncher;
        }
        else if (enemyHQ != null)
        {
            return enemyHQ;
        }
        return weakest;
    }

    /**
     * This method determines if the enemy is more powerful than us
     */
    public static int balanceOfPower(RobotInfo[] enemies, RobotInfo[] allies)
    {
        int alliedHealth = 0;
        int enemyHealth = 0;
        int attack;

        for (int i = allies.length; --i>=0; )
        {
            if (allies[i].type == RobotType.LAUNCHER)
            {
                attack = 60;
            }
            else
            {
                attack = (int) allies[i].type.attackPower;
            }
            alliedHealth += allies[i].health * attack;
        }

        for (int j = enemies.length; --j>=0; )
        {
            if (enemies[j].type == RobotType.LAUNCHER)
            {
                attack = 60;
            }
            else
            {
                attack = (int) enemies[j].type.attackPower;
            }
            enemyHealth += enemies[j].health * attack;
        }

        return alliedHealth - enemyHealth;
    }

    /**
     * This method determines if any of our allies have engaged
     */
    public static boolean alliesEngaged(RobotInfo[] allies, RobotInfo[] enemies, MapLocation[] enemyTowers)
    {
        for (int i = allies.length; --i>=0; )
        {
            int range = allies[i].type.attackRadiusSquared;
            MapLocation ally = allies[i].location;

            for (int j = enemies.length; --j>=0; )
            {
                MapLocation enemy = enemies[j].location;

                if (ally.distanceSquaredTo(enemy) <= range)
                {
                    return true;
                }
            }

//            for (int j = enemyTowers.length; --j>=0; )
//            {
//                if (ally.distanceSquaredTo(enemyTowers[j]) <= 24)
//                {
//                    return true;
//                }
//            }
        }

        return false;
    }

    /**
     * This method looks to see if there is a location we can move to
     * that will allow us to kite the enemy
     */
    public static Direction safeAttack(RobotController rc, MapLocation us, RobotInfo[] enemies, MapLocation enemyHQ, MapLocation[] towers)
    {
        int range = rc.getType().attackRadiusSquared;
        Direction[] dirs = Direction.values();


        for (int i = 8; --i>=0; )
        {
            MapLocation next = us.add(dirs[i]);

            boolean good = false;

            for (int j = enemies.length; --j>=0; )
            {
                if (enemies[j].type == RobotType.MISSILE)
                {
                    continue;
                }

                int dist = next.distanceSquaredTo(enemies[j].location);
                int theirRange = enemies[j].type.attackRadiusSquared;

                // bashers have a longer range in reality than specified in the specs
                if (enemies[j].type == RobotType.BASHER)
                {
                    dist += 6;
                }

                if (dist > theirRange && dist <= range)
                {
                    good = true;
                }
                else if (dist <= theirRange)
                {
                    good = false;
                }
            }

            if (good)
            {
                return dirs[i];
            }
        }

        return null;
    }

    /**
     * this method advances us towards the enemy
     */
    public static void attack(RobotController rc, RobotInfo[] enemies) throws GameActionException
    {
        Direction dir;
        MapLocation us = rc.getLocation();

        for (int i = enemies.length; --i>=0; )
        {
            dir = us.directionTo(enemies[i].location);
            if (rc.canMove(dir))
            {
                rc.move(dir);
                break;
            }
        }
    }

    /**
     * This method determines if we are being strafed by an enemy
     */
    public static boolean enemyKitingUs(RobotController rc, RobotInfo[] enemies)
    {
        MapLocation us = rc.getLocation();
        int range = rc.getType().attackRadiusSquared;

        for (int i = enemies.length; --i>=0; )
        {
            int dist = enemies[i].location.distanceSquaredTo(us);

            if (dist > range)
            {
                int theirRange = enemies[i].type.attackRadiusSquared;
                if (theirRange >= dist)
                {
                    rc.setIndicatorString(2, "Enemy" + enemies[i].location);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This function runs the fight micro
     */
    public boolean runFightMicro(RobotInfo[] nearByEnemies) throws GameActionException
    {
        // if we don't have weapon delay and are in range Attack!!!
        if (rc.isWeaponReady() && nearByEnemies.length > 0)
        {
            RobotInfo enemyToAttack = prioritizeTargets(nearByEnemies);

            // if it is a missile we may want to charge through instead of trying to shoot it down
            if (enemyToAttack.type == RobotType.MISSILE && rc.getType() == RobotType.TANK)
            {
                // don't shoot missiles as a tank
            }
            else
            {
                MapLocation target = enemyToAttack.location;

                if (rc.canAttackLocation(target))
                {
                    rc.attackLocation(target);
                }
                return true;
            }
        }
        // if we can move
        if (rc.isCoreReady())
        {
            RobotInfo[] enemies = rc.senseNearbyRobots(35, rc.getTeam().opponent());
            MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

            // search for allies in sight range
            RobotInfo[] allies = rc.senseNearbyRobots(24, rc.getTeam());
            Direction dir = null;

            int balance = balanceOfPower(enemies, allies);
            balance += rc.getHealth() * rc.getType().attackPower;

            MapLocation closestTower = null; //team044.Utilities.closestTower(rc, enemyTowers);
            MapLocation enemyHQ = rc.senseEnemyHQLocation();
            MapLocation us = rc.getLocation();
            int dist = 9999999;


            if (closestTower != null)
            {
                dist = us.distanceSquaredTo(closestTower);
            }

            if (enemies.length == 0)
            {
                if (us.distanceSquaredTo(enemyHQ) <= 60)
                {
                    // when balance of power is in our favor Attack!!
                    if (enemyTowers.length < 3)
                    {
                        if (balance > 10000)
                        {
                            dir = us.directionTo(enemyHQ);
                        }
                        // stand ground and wait for support to arrive
                        else
                        {
                            return true;
                        }
                    }
                    // don't attack enemy HQ while lots of towers are up
                    // use nav to go to next enemy tower
                    else
                    {
                        return false;
                    }
                }
                else if (closestTower != null && dist <= 49)
                {
                    if (dist > 24 && balance > 10000)
                    {
                        dir = us.directionTo(closestTower);
                    }
                    else if (dist <= 24)
                    {
                        dir = us.directionTo(closestTower);
                    }
                    else if (alliesEngaged(allies, enemies, enemyTowers))
                    {
                        dir = us.directionTo(closestTower);
                    }
                    else
                    {
                        // sit and wait for reinforcements
                        return true;
                    }
                }
                // no enemies and far away from enemy HQ and tower so don't run fight Micro
                else
                {
                    return false;
                }
            }
            else
            {
                Direction safeAdvance = safeAttack(rc, us, enemies, enemyHQ, enemyTowers);

                if (safeAdvance == null)
                {
                    if (dist < 49)
                    {
                        balance -= 8000;
                    }

                    if (us.distanceSquaredTo(enemyHQ) < 60)
                    {
                        balance -= 15000;
                    }

                    // if in range of enemies no need to advance
                    if (nearByEnemies.length > 0)
                    {
                        // we will stand our ground!
                        if (nearByEnemies.length == 1 && (nearByEnemies[0].type == RobotType.TOWER || nearByEnemies[0].type == RobotType.HQ) && rc.getLocation().distanceSquaredTo(nearByEnemies[0].location) > 2)
                        {
                            dir = us.directionTo(nearByEnemies[0].location);
                        }
                        else if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) <= 100)
                        {
                            dir = us.directionTo(rc.senseEnemyHQLocation());
                        }
                        else
                        {
                            return true;
                        }
                    }
                    else if (dist <= 24)
                    {
                        dir = us.directionTo(closestTower);
                    }
                    else if (alliesEngaged(allies, enemies, enemyTowers))
                    {
                        rc.setIndicatorString(1, "Allies Engaged");
                        attack(rc, enemies);
                        return true;
                    }
//                    else if (team044.FightMicroUtilities.enemyHasLaunchers(enemies))
//                    {
//                        team044.FightMicroUtilities.lockOntoLauncher(rc, enemies);
//                        return true;
//                    }
                    else if (balance > 500)
                    {
                        attack(rc, enemies);
                        return true;
                    }
                    else if (enemyKitingUs(rc, enemies))
                    {
                        rc.setIndicatorString(1, "enemyKiting");
                        attack(rc, enemies);
                        return true;
                    }
                    // if we have no advantage then stand your ground!
                    else
                    {
                        return true;
                    }
                }
                // if we can advance to gain tactical advantage then do so!
                else
                {
                    dir = safeAdvance;
                }
            }

            if (dir != null)
            {
                if (rc.canMove(dir))
                {
                    rc.move(dir);
                }
                else if (rc.canMove(dir.rotateLeft()))
                {
                    rc.move(dir.rotateLeft());
                }
                else if (rc.canMove(dir.rotateRight()))
                {
                    rc.move(dir.rotateRight());
                }
            }
        }
        return false;
    }

    /**
     * This method returns the RobotInfo for the Robot with the lowest health
     */
    public static RobotInfo findWeakestEnemy(RobotInfo[] nearByEnemies)
    {
        RobotInfo weakest = nearByEnemies[nearByEnemies.length - 1];

        for (int i = nearByEnemies.length-1; --i > 0; )
        {
            if (nearByEnemies[i] == null)
            {
                System.out.println("Enemy is null");
            }
            else if (nearByEnemies[i].health < weakest.health)
            {
                weakest = nearByEnemies[i];
            }
        }

        return weakest;
    }
}