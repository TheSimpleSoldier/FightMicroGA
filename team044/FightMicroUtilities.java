package team044;

import battlecode.common.*;
import battlecode.world.Robot;
import battlecode.world.Util;
import team044.Units.Launcher;

import java.util.Map;

public class FightMicroUtilities
{

    //===================== Shooting methods ===========================\\

    /**
     * This method returns the RobotInfo for the Robot with the lowest health
     */
    public static RobotInfo findWeakestEnemy(RobotInfo[] nearByEnemies)
    {
        RobotInfo weakest = nearByEnemies[nearByEnemies.length - 1];

        for (int i = nearByEnemies.length-1; --i > 0; )
        {
            if (nearByEnemies[i].health < weakest.health)
            {
                weakest = nearByEnemies[i];
            }
        }

        return weakest;
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

    public static RobotInfo prioritizeHeavyHands(RobotInfo[] nearByEnemies)
    {
        RobotInfo toAttack = nearByEnemies[0];
        double attackDelay = toAttack.weaponDelay;
        double attackPower = toAttack.type.attackPower;

        for (int i = nearByEnemies.length; --i>=1;)
        {
            RobotInfo next = nearByEnemies[i];
            double currentAttackDelay = next.weaponDelay;
            double currentAttackPower = next.type.attackPower;
            if (currentAttackDelay < attackDelay)
            {
                toAttack = next;
                attackDelay = currentAttackDelay;
                attackPower = currentAttackPower;
            }
            else if (currentAttackDelay == attackDelay && currentAttackPower > attackPower)
            {
                toAttack = next;
                attackDelay = currentAttackDelay;
                attackPower = currentAttackPower;
            }
        }

        return toAttack;
    }


    //========================== Methods for standard units like tanks ==========================\\


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
     * This method causes the robot to retreat
     * basically we loop through all of the enemy robots and if we find a direction that we can move
     * in that is opposite of an enemy then we do so
     */
    public static void retreat(RobotController rc, RobotInfo[] enemies) throws GameActionException
    {
        Direction dir;
        MapLocation us = rc.getLocation();

        for (int i = enemies.length; --i>=0; )
        {
            dir = us.directionTo(enemies[i].location).opposite();
            if (rc.canMove(dir))
            {
                rc.move(dir);
                break;
            }
        }

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

            if (Utilities.locInRangeOfEnemyTower(next, towers, enemyHQ))
            {
                continue;
            }

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
     * This method determines if the enemy has launchers
     */
    public static boolean enemyHasLaunchers(RobotInfo[] enemies)
    {
        for (int i = enemies.length; --i>=0; )
        {
            if (enemies[i].type == RobotType.LAUNCHER || enemies[i].type == RobotType.MISSILE)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method locks onto a Launcher to kill it
     * or to die gloriously in the attempt
     */
    public static void lockOntoLauncher(RobotController rc, RobotInfo[] enemies) throws GameActionException
    {
        // if we can't move then don't waste bytecodes
        if (!rc.isCoreReady())
        {
            return;
        }
        RobotInfo launcher = null;
        boolean missile = false;
        int missile_x = 0;
        int missile_y = 0;
        int count = 0;
        int closestDist = 999;
        int dist = 0;
        MapLocation us = rc.getLocation();

        for (int i = enemies.length; --i>=0; )
        {
            if (enemies[i].type == RobotType.LAUNCHER)
            {
                dist = us.distanceSquaredTo(enemies[i].location);
                if (dist < closestDist)
                {
                    launcher = enemies[i];
                    closestDist = dist;
                }
            }
            else if (enemies[i].type == RobotType.MISSILE)
            {
                launcher = enemies[i];
                missile_x += launcher.location.x;
                missile_y += launcher.location.y;
                count++;
                missile = true;
            }
        }

        if (launcher != null)
        {
            missile = false;
        }

        // if we see a missile we can assume their is a launcher beyond
        if (missile)
        {
            missile_x /= count;
            missile_y /= count;

            MapLocation missile_center = new MapLocation(missile_x, missile_y);

            Direction dir = rc.getLocation().directionTo(missile_center);

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
        // lock on launcher
        else if (launcher != null && closestDist > 2)
        {
            Direction dir = rc.getLocation().directionTo(launcher.location);

            // if possible move towards the launcher
            if (rc.canMove(dir))
            {
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
        }
    }

    /**
     * We are one move away from enemyTower
     */
    public static boolean enemyTowerClose(RobotController rc, MapLocation[] enemyTowers)
    {
        for (int i = enemyTowers.length; --i>=0; )
        {
            if (rc.getLocation().distanceSquaredTo(enemyTowers[i]) < 37)
            {
                return true;
            }
        }
        return false;
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

            for (int j = enemyTowers.length; --j>=0; )
            {
                if (ally.distanceSquaredTo(enemyTowers[j]) <= 24)
                {
                    return true;
                }
            }
        }

        return false;
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

    //============================ Drone micro methods ==============================\\

    /**
     * This function returns the best direction to retreat in
     */
    public static Direction retreatDir(RobotInfo[] enemies, RobotController rc, MapLocation[] enemyTowers, MapLocation enemyHQ)
    {
        Direction[] dirs = Direction.values();
        Direction best = null;
        MapLocation us = rc.getLocation();
        int score = 0;

        for (int a = enemies.length; --a>=0; )
        {
            score += enemies[a].location.distanceSquaredTo(us);
        }

        for (int i = 0; i < 8; i++)
        {
            if (!rc.canMove(dirs[i]))
            {
                continue;
            }

            MapLocation next = us.add(dirs[i]);
            int dirScore = 0;

            for (int j = enemies.length; --j>=0; )
            {
                dirScore += enemies[j].location.distanceSquaredTo(next);
            }

            if (Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                // if nxt move is in range of tower don't do it
            }
            else if (dirScore > score)
            {
                score = dirScore;
                best = dirs[i];
            }
        }


        return best;
    }

    /**
     * This function finds the best direction to advance in
     */
    public static Direction advanceDir(RobotController rc, RobotInfo[] enemies, MapLocation[] enemyTowers, MapLocation enemyHQ, boolean safe)
    {
        Direction best = null;
        Direction[] dirs = Direction.values();
        MapLocation us = rc.getLocation();
        int bestScore = 0;
        boolean enemyDrone = false;

        for (int i = 0; i < 8; i++)
        {
            MapLocation next = us.add(dirs[i]);
            int score = 0;

            for (int j = enemies.length; --j>=0; )
            {
                int distToEnemy = enemies[j].location.distanceSquaredTo(next);
                if (score <= 0 && distToEnemy <= 10)
                {
                    score += 20;
                    j = 0;
                }
                if (enemies[j].type.attackRadiusSquared >= distToEnemy)
                {
                    if (!safe && enemies[j].type == RobotType.DRONE)
                    {
                        if (!attackDrone(rc, enemies, enemies[j]))
                        {
                            score -= 100000;
                            enemyDrone = true;
                        }
                    }
                    else if (safe)
                    {
                        score -= 100000;
                    }

                    if (enemies[j].type == RobotType.DRONE)
                    {
                        enemyDrone = true;
                    }
                    score--;
                }
            }

            if (Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                // if nxt move is in range of tower don't do it
            }
            else if (score > bestScore)
            {
                bestScore = score;
                best = dirs[i];
            }
            else if (!enemyDrone && score >= bestScore)
            {
                bestScore = score;
                best = dirs[i];
            }
        }

        return best;
    }

    /**
     * Should we attack enemy drone
     */
    public static boolean attackDrone(RobotController rc, RobotInfo[] enemies, RobotInfo drone)
    {
        boolean enemyAtRisk = false;
        RobotInfo[] allies = null;

        if (drone.supplyLevel == 0)
        {
            enemyAtRisk = true;
        }
        else
        {
            // first we look behind it to see if it is against a wall
            MapLocation behindDrone = drone.location.add(rc.getLocation().directionTo(drone.location), 6);

            allies = rc.senseNearbyRobots(behindDrone, 24, rc.getTeam());

            if (allies.length > 1)
            {
                enemyAtRisk = true;
            }
        }

        if (enemyAtRisk)
        {
            RobotInfo[] allies2 = rc.senseNearbyRobots(24, rc.getTeam());

            if (allies != null && allies.length > 1)
            {
                if (allies2.length + allies.length > (1 + enemies.length))
                {
                    rc.setIndicatorString(1, "Advance anyways");
                    return true;
                }
            }
            else
            {
                if (allies2.length > (3 + enemies.length))
                {
                    rc.setIndicatorString(1, "Advance anyways 2");
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if there is an enemy in range of us
     */
    public static boolean enemyInRange(RobotController rc, RobotInfo[] enemies)
    {
        MapLocation us = rc.getLocation();

        for (int i = enemies.length; --i>=0; )
        {
            int dist = us.distanceSquaredTo(enemies[i].location);
            // bashers and missiles move then attack
            RobotType enemy = enemies[i].type;
            if (enemy == RobotType.BASHER || enemy == RobotType.MISSILE)
            {
                dist -= 11;
            }
            if (dist <= enemies[i].type.attackRadiusSquared)
            {
                return true;
            }

        }
        return false;
    }



    //==================== Methods for Bashers ========================\\
    public static Direction bestBasherDir(RobotController rc, RobotInfo[] enemies, int currentScore)
    {
        Direction[] dirs = Direction.values();
        int score;
        int bestScore = currentScore;
        Direction best = null;
        MapLocation current;
        MapLocation us = rc.getLocation();

        for (int i = 0; i < 8; i++)
        {
            current = us.add(dirs[i]);
            score = 0;

            if (rc.canMove(dirs[i]))
            {
                for (int j = enemies.length; --j>=0; )
                {
                    if (current.isAdjacentTo(enemies[j].location))
                    {
                        score++;
                    }
                }

                if (score > bestScore)
                {
                    bestScore = score;
                    best = dirs[i];
                }
            }
        }

        MapLocation closestEnemy = null;
        if (best == null)
        {
            int bestDist = 9999;
            for (int i = enemies.length; --i>=0;)
            {
                int dist = us.distanceSquaredTo(enemies[i].location);
                if (dist <= 2)
                {
                    return null;
                }
                else if (dist < bestDist)
                {
                    bestDist = dist;
                    closestEnemy = enemies[i].location;
                }
            }

            if (closestEnemy != null)
            {
                return us.directionTo(closestEnemy);
            }
        }


        return best;
    }

    public static Direction basherDirSecond(RobotController rc, RobotInfo[] enemies)
    {
        Direction[] dirs = Direction.values();
        int score;
        int bestScore = -99999999;
        Direction best = null;
        MapLocation current;
        MapLocation us = rc.getLocation();

        for (int i = 0; i < 8; i++)
        {
            current = us.add(dirs[i]);
            score = 0;

            if (rc.canMove(dirs[i]))
            {
                for (int j = enemies.length; --j>=0; )
                {
                    score -= current.distanceSquaredTo(enemies[j].location);
                }

                if (score > bestScore)
                {
                    bestScore = score;
                    best = dirs[i];
                }
            }
        }
        return best;
    }

    public static Direction dirToLauncher(RobotController rc, RobotInfo[] enemies)
    {
        MapLocation closestLauncher = null;
        int closestDist = 9999;

        for (int i = enemies.length; --i>=0; )
        {
            if (enemies[i].type == RobotType.LAUNCHER)
            {
                MapLocation enemy = enemies[i].location;
                int dist = enemy.distanceSquaredTo(rc.getLocation());
                if (dist <= 2)
                {
                    return Direction.OMNI;
                }
                else if (dist < closestDist)
                {
                    closestDist = dist;
                    closestLauncher = enemy;
                }
            }
        }

        if (closestLauncher == null)
        {
            return null;
        }
        return rc.getLocation().directionTo(closestLauncher);
    }


    //======================== Methods for launchers ============================\\
    public static Direction dirToShoot(RobotController rc, RobotInfo[] nearByEnemies, MapLocation enemyStructure) throws GameActionException
    {
        Direction dir, toReturn = null;
        MapLocation us = rc.getLocation();
        if (enemyStructure != null)
        {
            int dist = us.distanceSquaredTo(enemyStructure);
            dir = us.directionTo(enemyStructure);

            if (!rc.canLaunch(dir))
            {

            }
            else if (dist <= 24)
            {
                return dir;
            }
            else if (!alliesInPath(rc.senseNearbyRobots(35, rc.getTeam()), dir, us) || (rc.canSenseLocation(enemyStructure) && rc.senseRobotAtLocation(enemyStructure).type == RobotType.HQ))
            {
                return dir;
            }
        }
        else
        {
            RobotInfo[] allies = rc.senseNearbyRobots(35, rc.getTeam());
            for (int i = nearByEnemies.length; --i>=0; )
            {
                dir = us.directionTo(nearByEnemies[i].location);
                if (!rc.canLaunch(dir))
                {
                }
                else if (!alliesInPath(allies, dir, us))
                {
                    // focus launchers then commanders then tanks and finally just any enemy we can shoot at
                    if (nearByEnemies[i].type == RobotType.LAUNCHER)
                    {
                        return dir;
                    }
                    else if (nearByEnemies[i].type == RobotType.COMMANDER)
                    {
                        return dir;
                    }
                    else if (nearByEnemies[i].type == RobotType.TANK)
                    {
                        toReturn = dir;
                    }
                    else if (toReturn == null)
                    {
                        toReturn = dir;
                    }
                }
            }
        }
        return toReturn;
    }

    public static boolean alliesInPath(RobotInfo[] nearByAllies, Direction dir, MapLocation startingSpot)
    {
        for (int i = nearByAllies.length; --i>=0; )
        {
            if (nearByAllies[i].type != RobotType.MISSILE)
            {
                // if an ally is in the way
                if (startingSpot.directionTo(nearByAllies[i].location) == dir)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * this method moves us in a direction that is safe from enemy towers
     */
    public static void moveInDir(RobotController rc, MapLocation enemyHQ, MapLocation[] enemyTowers, Direction dir, MapLocation us) throws GameActionException
    {
        if (!rc.isCoreReady())
        {

        }
        else if (rc.canMove(dir)  && !Utilities.locInRangeOfEnemyTower(us.add(dir), enemyTowers, enemyHQ))
        {
            rc.move(dir);
        }
        else if (rc.canMove(dir.rotateRight())  && !Utilities.locInRangeOfEnemyTower(us.add(dir.rotateRight()), enemyTowers, enemyHQ))
        {
            rc.move(dir.rotateRight());
        }
        else if (rc.canMove(dir.rotateLeft())  && !Utilities.locInRangeOfEnemyTower(us.add(dir.rotateLeft()), enemyTowers, enemyHQ))
        {
            rc.move(dir.rotateLeft());
        }
        else if (rc.canMove(dir.rotateLeft().rotateLeft())  && !Utilities.locInRangeOfEnemyTower(us.add(dir.rotateLeft().rotateLeft()), enemyTowers, enemyHQ))
        {
            rc.move(dir.rotateLeft().rotateLeft());
        }
        else if (rc.canMove(dir.rotateRight().rotateRight())  && !Utilities.locInRangeOfEnemyTower(us.add(dir.rotateRight().rotateRight()), enemyTowers, enemyHQ))
        {
            rc.move(dir.rotateRight().rotateRight());
        }
    }

    //======================= Commander Methods =========================\\

    /**
     * This method finds the best place for a commander to flash back to
     */
    public static MapLocation retreatFlashLoc(RobotController rc, RobotInfo[] enemies) throws GameActionException
    {
        MapLocation us = rc.getLocation();
        MapLocation flashTo = null;
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation closestTower = Utilities.closestTower(rc, enemyTowers);
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        // then we should flash away from enemyHQ
        if (us.distanceSquaredTo(enemyHQ) <= 52)
        {
            return flashAwayFrom(rc, enemyHQ);
        }
        else if (closestTower != null && us.distanceSquaredTo(closestTower) <= 24)
        {
            return flashAwayFrom(rc, closestTower);
        }
        else
        {
            int x = 0;
            int y = 0;

            for (int i = enemies.length; --i>=0; )
            {
                MapLocation enemy = enemies[i].location;
                x += enemy.x;
                y += enemy.y;
            }
            if (x != 0 && y != 0)
            {
                x /= enemies.length;
                y /= enemies.length;
                MapLocation center = new MapLocation(x,y);
                return flashAwayFrom(rc, center);
            }
        }

        return flashTo;
    }


    /**
     * This method finds the furthest location away from another location that the commander can flash to
     */
    public static MapLocation flashAwayFrom(RobotController rc, MapLocation bad)
    {
        MapLocation[] avaliableSpots = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 10);
        int bestDist = rc.getLocation().distanceSquaredTo(bad);
        MapLocation bestSpot = null;
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        for (int i = avaliableSpots.length; --i>=0; )
        {
            if (rc.isPathable(rc.getType(), avaliableSpots[i]) && !Utilities.locInRangeOfEnemyTower(avaliableSpots[i], enemyTowers, enemyHQ))
            {
                int dist = avaliableSpots[i].distanceSquaredTo(bad);
                if (dist > bestDist)
                {
                    bestDist = dist;
                    bestSpot = avaliableSpots[i];
                }
            }
        }

        return bestSpot;
    }

    /**
     * This method flashes to the closest location to another location
     */
    public static MapLocation flashToLoc(RobotController rc, MapLocation good)
    {
        MapLocation[] avaliableSpots = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 10);
        int bestDist = rc.getLocation().distanceSquaredTo(good);
        MapLocation bestSpot = null;
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        for (int i = avaliableSpots.length; --i>=0; )
        {
            if (rc.isPathable(rc.getType(), avaliableSpots[i]) && !Utilities.locInRangeOfEnemyTower(avaliableSpots[i], enemyTowers, enemyHQ))
            {
                int dist = avaliableSpots[i].distanceSquaredTo(good);
                if (dist < bestDist)
                {
                    bestDist = dist;
                    bestSpot = avaliableSpots[i];
                }
            }
        }

        return bestSpot;
    }

    /**
     * Flash over missile
     */
    public static MapLocation flashOverMissile(RobotController rc, MapLocation missile)
    {
        MapLocation[] avaliableSpots = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 10);
        int ourDist = rc.getLocation().distanceSquaredTo(missile);
        int bestDist = ourDist;
        MapLocation bestSpot = rc.getLocation();
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        for (int i = avaliableSpots.length; --i>=0; )
        {
            if (rc.isPathable(rc.getType(), avaliableSpots[i]) && !Utilities.locInRangeOfEnemyTower(avaliableSpots[i], enemyTowers, enemyHQ))
            {
                int dist = avaliableSpots[i].distanceSquaredTo(missile);
                int distToUs = avaliableSpots[i].distanceSquaredTo(rc.getLocation());
                if (dist > bestDist && dist < distToUs)
                {
                    bestDist = dist;
                    bestSpot = avaliableSpots[i];
                }
            }
        }

        return bestSpot;
    }

    /**
     * This method causes the commander to flash to a forward location that is in a certain distance
     * the inteded use of this method is for when the commander is trying to get over a void space region
     */
    public static MapLocation flashInDir(RobotController rc, Direction dir)
    {
        MapLocation[] avaliableSpots = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 10);
        MapLocation target = rc.getLocation().add(dir, 5);
        MapLocation us = rc.getLocation();
        int ourDist = us.distanceSquaredTo(target);
        int bestDist = 999;
        MapLocation jumpTo = null;

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        for (int i = avaliableSpots.length; --i>=0; )
        {
            if (rc.isPathable(rc.getType(), avaliableSpots[i]))
            {
                if (!Utilities.locInRangeOfEnemyTower(avaliableSpots[i], enemyTowers, enemyHQ))
                {
                    int dist = avaliableSpots[i].distanceSquaredTo(target);
                    int distToUs = avaliableSpots[i].distanceSquaredTo(us);
                    if (dist < bestDist && dist < ourDist && distToUs > 2)
                    {
                        bestDist = dist;
                        jumpTo = avaliableSpots[i];
                    }
                }
            }
        }

        return jumpTo;
    }

    /**
     * This method returns if the commander can't move forward
     */
    public static boolean commanderBlocked(RobotController rc, MapLocation target)
    {
        Direction dir = rc.getLocation().directionTo(target);
        MapLocation us = rc.getLocation();
        MapLocation next = us.add(dir);
        MapLocation right = us.add(dir.rotateRight());
        MapLocation left = us.add(dir.rotateLeft());

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        if (!rc.isPathable(rc.getType(), next) || Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
        {
            if (!rc.isPathable(rc.getType(), left) || Utilities.locInRangeOfEnemyTower(left, enemyTowers, enemyHQ))
            {
                if (!rc.isPathable(rc.getType(), right) || Utilities.locInRangeOfEnemyTower(right, enemyTowers, enemyHQ))
                {
                    return true;
                }
            }
        }


        return false;
    }

    /**
     * This method finds the best location to move the commander to
     */
    public static Direction moveCommander(RobotController rc, boolean avoidStructures, Direction dir)
    {
        MapLocation us = rc.getLocation();
        MapLocation next = us.add(dir);

        if (avoidStructures)
        {
            MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
            MapLocation enemyHQ = rc.senseEnemyHQLocation();
            if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                if (rc.canMove(dir))
                {
                    return dir;
                }
            }
            next = us.add(dir.rotateLeft());

            if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                if (rc.canMove(dir.rotateLeft()))
                {
                    return dir.rotateLeft();
                }
            }

            next = us.add(dir.rotateRight());

            if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                if (rc.canMove(dir.rotateRight()))
                {
                    return dir.rotateRight();
                }
            }

            next = us.add(dir.rotateLeft().rotateLeft());

            if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                if (rc.canMove(dir.rotateLeft().rotateLeft()))
                {
                    return dir.rotateLeft().rotateLeft();
                }
            }

            next = us.add(dir.rotateRight().rotateRight());

            if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
            {
                if (rc.canMove(dir.rotateRight().rotateRight()))
                {
                    return dir.rotateRight().rotateRight();
                }
            }
        }
        else
        {
            if (rc.canMove(dir))
            {
                return dir;
            }
            else if (rc.canMove(dir.rotateLeft()))
            {
                return dir.rotateLeft();
            }
            else if (rc.canMove(dir.rotateRight()))
            {
                return  dir.rotateRight();
            }
            else if (rc.canMove(dir.rotateLeft().rotateLeft()))
            {
                return dir.rotateLeft().rotateLeft();
            }
            else if (rc.canMove(dir.rotateRight().rotateRight()))
            {
                return dir.rotateRight().rotateRight();
            }
        }

        return null;
    }

    /**
     * This method determines if the commander should go after a particular unit
     */
    public static MapLocation getCommanderAttack(RobotController rc, RobotInfo[] enemies)
    {
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        MapLocation[] towers = rc.senseEnemyTowerLocations();
        boolean inRange;
        int distFromHQ;

        if (towers.length < 2)
        {
            distFromHQ = 2;
        }
        // 35 range no splash damage
        else if (towers.length < 5)
        {
            distFromHQ = 5;
        }
        // 35 range and splash damage
        else
        {
            distFromHQ = 10;
        }

        for (int i = enemies.length; --i >=0; )
        {
            if (enemies[i].type == RobotType.DRONE)
            {
                continue;
            }

            inRange = true;
            MapLocation enemy = enemies[i].location;

            if (enemy.distanceSquaredTo(enemyHQ) <= distFromHQ)
            {
                continue;
            }

            for (int j = towers.length; --j>=0; )
            {
                if (towers[j].distanceSquaredTo(enemy) <= 2)
                {
                    inRange = false;
                    j = 0;
                }
            }

            if (inRange)
            {
                return enemy;
            }
        }
        return null;
    }

    /**
     * This method determines if unit is type that is vulnerable to attack
     */
    public static boolean unitVulnerable(RobotInfo enemy)
    {
        switch(enemy.type)
        {
            case LAUNCHER:
                return true;
            case MISSILE:
                return false;
            case MINERFACTORY:
                return true;
            case MINER:
                return true;
            case COMMANDER:
                return false;
            case COMPUTER:
                return true;
            case SUPPLYDEPOT:
                return true;
            case SOLDIER:
                return false;
            case BASHER:
                return false;
            case DRONE:
                return false;
            case TANK:
                return false;
            case TOWER:
                return false;
            case TECHNOLOGYINSTITUTE:
                return true;
            case TRAININGFIELD:
                return true;
            case BARRACKS:
                return true;
            case TANKFACTORY:
                return true;
            case HANDWASHSTATION:
                return true;
            case HELIPAD:
                return true;
            case AEROSPACELAB:
                return true;
            case BEAVER:
                return true;
            case HQ:
                return false;
            default:
                return false;
        }
    }

    /**
     * This method moves a unit in a given direction that is not in range of enemy towers or HQ
     */
    public static Direction moveAwayFromTowers(RobotController rc, Direction dir) throws GameActionException
    {
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        MapLocation us = rc.getLocation();
        MapLocation next = us.add(dir);

        if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
        {
            if (rc.canMove(dir))
            {
                return dir;
            }
        }
        next = us.add(dir.rotateLeft());

        if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
        {
            if (rc.canMove(dir.rotateLeft()))
            {
                return dir.rotateLeft();
            }
        }

        next = us.add(dir.rotateRight());

        if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
        {
            if (rc.canMove(dir.rotateRight()))
            {
                return dir.rotateRight();
            }
        }

        next = us.add(dir.rotateLeft().rotateLeft());

        if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
        {
            if (rc.canMove(dir.rotateLeft().rotateLeft()))
            {
                return dir.rotateLeft().rotateLeft();
            }
        }

        next = us.add(dir.rotateRight().rotateRight());

        if (!Utilities.locInRangeOfEnemyTower(next, enemyTowers, enemyHQ))
        {
            if (rc.canMove(dir.rotateRight().rotateRight()))
            {
                return dir.rotateRight().rotateRight();
            }
        }
        return null;
    }

    /**
     * This method finds the direction in the opposite of the center of mass of the visible enemies
     */
    public static Direction awayFromOpponents(RobotController rc, RobotInfo[] enemies)
    {
        int x = 0;
        int y = 0;
        int count = 0;
        MapLocation enemy;

        for (int i = enemies.length; --i>=0; )
        {
            if (!unitVulnerable(enemies[i]))
            {
                enemy = enemies[i].location;
                x += enemy.x;
                y += enemy.y;
                count++;
            }
        }

        x /= count;
        y /= count;

        MapLocation center = new MapLocation(x,y);
        Direction dir = center.directionTo(rc.getLocation());

        return dir;
    }

    /**
     * This method will move us towards the center of mass of the enemy
     */
    public static Direction toTheEnemy(RobotController rc, RobotInfo[] enemies)
    {
        int x = 0;
        int y = 0;
        MapLocation enemy;
        for (int i = enemies.length; --i>=0; )
        {
            enemy = enemies[i].location;
            x += enemy.x;
            y += enemy.y;

        }

        x /= enemies.length;
        y /= enemies.length;

        MapLocation center = new MapLocation(x,y);
        Direction dir = rc.getLocation().directionTo(center);

        return dir;
    }
}

