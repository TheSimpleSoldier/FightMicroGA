package team044;

import battlecode.common.*;
import battlecode.world.Robot;

public class FightMicro
{
    RobotController rc;
    public MapLocation enemyHQ;
    private int HQRange = 24;
    public FightMicro(RobotController rc)
    {
        this.rc = rc;
        enemyHQ = rc.senseEnemyHQLocation();

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        if (enemyTowers.length >= 5)
        {
            HQRange = 52;
        }
        else if (enemyTowers.length >= 2)
        {
            HQRange = 35;
        }
        else
        {
            HQRange = 24;
        }
    }
    public static Direction[] dirs = Direction.values();

    public boolean basicFightMicro(RobotInfo[] nearByEnemies) throws GameActionException
    {
        if (!rc.isWeaponReady())
        {
            return false;
        }

        if (nearByEnemies == null || nearByEnemies.length < 1)
        {
            return false;
        }

        RobotInfo enemyToAttack = FightMicroUtilities.findWeakestEnemy(nearByEnemies);
        MapLocation target = enemyToAttack.location;

        if (rc.canAttackLocation(target))
        {
            rc.attackLocation(target);
            return true;
        }
        return false;
    }

    /**
     * This is a more sophisticated fight micro for tanks/soldiers/miners/beavers/commanders
     */
    public boolean advancedFightMicro(RobotInfo[] nearByEnemies) throws GameActionException
    {
        // if we don't have weapon delay and are in range Attack!!!
        if (rc.isWeaponReady() && nearByEnemies.length > 0)
        {
            RobotInfo enemyToAttack = FightMicroUtilities.prioritizeTargets(nearByEnemies);

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

            int balance = FightMicroUtilities.balanceOfPower(enemies, allies);
            balance += rc.getHealth() * rc.getType().attackPower;

            MapLocation closestTower = Utilities.closestTower(rc, enemyTowers);
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
                    else if (FightMicroUtilities.alliesEngaged(allies, enemies, enemyTowers))
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
                Direction safeAdvance = FightMicroUtilities.safeAttack(rc, us, enemies, enemyHQ, enemyTowers);

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
                    else if (FightMicroUtilities.alliesEngaged(allies, enemies, enemyTowers))
                    {
                        rc.setIndicatorString(1, "Allies Engaged");
                        FightMicroUtilities.attack(rc, enemies);
                        return true;
                    }
                    else if (FightMicroUtilities.enemyHasLaunchers(enemies))
                    {
                        FightMicroUtilities.lockOntoLauncher(rc, enemies);
                        return true;
                    }
                    else if (balance > 500)
                    {
                        FightMicroUtilities.attack(rc, enemies);
                        return true;
                    }
                    else if (FightMicroUtilities.enemyKitingUs(rc, enemies))
                    {
                        rc.setIndicatorString(1, "enemyKiting");
                        FightMicroUtilities.attack(rc, enemies);
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
     * This method will attack enemies around it
     */
    public boolean structureFightMicro(RobotInfo[] nearByEnemies) throws GameActionException
    {
        if (!rc.isWeaponReady())
        {
            return false;
        }
        if (nearByEnemies.length < 1 && HQRange > 35)
        {
            if (rc.getType() == RobotType.HQ)
            {
                RobotInfo[] enemies = rc.senseNearbyRobots(HQRange, rc.getTeam().opponent());

                if (enemies.length > 0)
                {
                    MapLocation enemy;
                    MapLocation us = rc.getLocation();
                    for (int i = enemies.length; --i>=0; )
                    {
                        enemy = enemies[i].location;
                        enemy = enemy.add(enemy.directionTo(us));

                        if (!rc.isLocationOccupied(enemy) && rc.canAttackLocation(enemy))
                        {
                            rc.attackLocation(enemy);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        else if (nearByEnemies.length > 0)
        {
            RobotInfo enemyToAttack = FightMicroUtilities.findWeakestEnemy(nearByEnemies);
            MapLocation target = enemyToAttack.location;

            if (rc.canAttackLocation(target))
            {
                rc.attackLocation(target);
                return true;
            }
        }
        return false;
    }

    public boolean launcherAttack(RobotInfo[] nearByEnemies) throws GameActionException
    {
        if (rc.getMissileCount() == 0)
        {
            MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
            boolean closeToTower = false;

            for (int i = 0; i < enemyTowers.length; i++)
            {
                if (rc.getLocation().distanceSquaredTo(enemyTowers[i]) < 35)
                {
                    closeToTower = true;
                }
            }

            if (closeToTower)
            {
                MapLocation us = rc.getLocation();
                if (nearByEnemies.length == 0)
                {
                    return true;
                }
                else
                {
                    int closestDist = 25;
                    MapLocation closestEnemy = null;

                    for (int i = nearByEnemies.length; --i>=0; )
                    {
                        int dist = nearByEnemies[i].location.distanceSquaredTo(us);
                        if (dist < closestDist)
                        {
                            closestDist = dist;
                            closestEnemy = nearByEnemies[i].location;
                        }
                    }

                    // if there are no enemies in range the fire away at the enemies tower
                    if (closestDist > 24)
                    {
                        return true;
                    }
                    else
                    {
                        Direction dir = us.directionTo(closestEnemy).opposite();
                        FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                        return true;
                    }
                }
            }

            // if we see enemies then either move back or charge!
            if (rc.isCoreReady() && nearByEnemies.length > 0) {
                MapLocation missile = null;
                MapLocation commander = null;
                MapLocation closestEnemy = null;
                int closest = 24;
                MapLocation us = rc.getLocation();
                MapLocation enemyHQ = rc.senseEnemyHQLocation();
                Direction dir;


                for (int i = nearByEnemies.length; --i >= 0; )
                {
                    if (nearByEnemies[i].type == RobotType.MISSILE)
                    {
                        missile = nearByEnemies[i].location;
                    }
                    else if (nearByEnemies[i].type == RobotType.COMMANDER)
                    {
                        commander = nearByEnemies[i].location;
                    }
                    else
                    {
                        MapLocation enemy = nearByEnemies[i].location;
                        int dist = us.distanceSquaredTo(enemy);
                        if (dist < closest)
                        {
                            closest = dist;
                            closestEnemy = enemy;
                        }
                    }
                }

                // if the enemy shot a missile pull back
                if (missile != null)
                {
                    dir = us.directionTo(missile).opposite();
                    FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                    return true;
                }
                // don't want to fight commander head on
                else if (commander != null)
                {
                    dir = us.directionTo(commander).opposite();
                    FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                    return true;
                }
                else if (closestEnemy != null)
                {
                    dir = us.directionTo(closestEnemy).opposite();
                    FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                    return true;
                }
            }
            return false;
        }

        if (nearByEnemies.length == 0)
        {
            MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
            MapLocation enemyHQ = rc.senseEnemyHQLocation();
            boolean shooting = false;
            MapLocation spot = null;
            MapLocation us = rc.getLocation();

            for (int i = 0; i < enemyTowers.length; i++)
            {
                if (rc.getLocation().distanceSquaredTo(enemyTowers[i]) < 49)
                {
                    Direction dir = FightMicroUtilities.dirToShoot(rc, null, enemyTowers[i]);
                    if (dir != null && rc.canLaunch(dir))
                    {
                        // broadcast location for missiles
                        rc.broadcast(Constants.towerX, enemyTowers[i].x);
                        rc.broadcast(Constants.towerY, enemyTowers[i].y);
                        rc.launchMissile(dir);
                        shooting = true;
                        spot = enemyTowers[i];
                        break;
                    }
                }
            }

            if (shooting)
            {
                Direction dir = rc.getLocation().directionTo(spot);
                FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                return true;
            }

            if (rc.getLocation().distanceSquaredTo(enemyHQ) < 49)
            {
                Direction dir = FightMicroUtilities.dirToShoot(rc, null, enemyHQ);
                if (dir != null && rc.canLaunch(dir))
                {
                    rc.broadcast(Constants.towerX, enemyHQ.x);
                    rc.broadcast(Constants.towerY, enemyHQ.y);
                    rc.launchMissile(dir);
                    shooting = true;
                }
            }

            if (shooting)
            {
                Direction dir = rc.getLocation().directionTo(enemyHQ);
                FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                return true;
            }

            return false;
        }

        Direction dir = FightMicroUtilities.dirToShoot(rc, nearByEnemies, null);

        rc.setIndicatorString(2, "Dir: " + dir);

        if (dir != null && rc.canLaunch(dir))
        {
            rc.launchMissile(dir);
            MapLocation rallyPoint = rc.getLocation().add(dir, 7);
            rc.broadcast(Messaging.LauncherAttackX.ordinal(), rallyPoint.x);
            rc.broadcast(Messaging.LauncherAttackY.ordinal(), rallyPoint.y);
        }

        int x = rc.readBroadcast(Messaging.CommanderLocX.ordinal());
        int y = rc.readBroadcast(Messaging.CommanderLocY.ordinal());

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        MapLocation us = rc.getLocation();


        if (rc.isCoreReady())
        {
            MapLocation missile = null;
            MapLocation commander = null;
            MapLocation closestEnemy = null;
            int closest = 24;

            for (int i = nearByEnemies.length; --i >= 0; )
            {
                if (nearByEnemies[i].type == RobotType.MISSILE)
                {
                    missile = nearByEnemies[i].location;
                }
                else if (nearByEnemies[i].type == RobotType.COMMANDER)
                {
                    commander = nearByEnemies[i].location;
                }
                else
                {
                    MapLocation enemy = nearByEnemies[i].location;
                    int dist = us.distanceSquaredTo(enemy);
                    if (dist < closest)
                    {
                        closest = dist;
                        closestEnemy = enemy;
                    }
                }
            }

            // if the enemy shot a missile pull back
            if (missile != null)
            {
                dir = us.directionTo(missile).opposite();
                FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                return true;
            }
            // don't want to fight commander head on
            else if (commander != null)
            {
                dir = us.directionTo(commander).opposite();
                FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                return true;
            }
            else if (closestEnemy != null)
            {
                dir = us.directionTo(closestEnemy).opposite();
                FightMicroUtilities.moveInDir(rc, enemyHQ, enemyTowers, dir, us);
                return true;
            }
        }

        return true;
    }

    /**
     * This micro is for drones
     */
    public boolean droneAttack(RobotInfo[] nearByEnemies) throws GameActionException
    {
        Direction direction;
        RobotInfo[] enemies = rc.senseNearbyRobots(35, rc.getTeam().opponent());
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        if (enemyHQ == null)
        {
            enemyHQ = rc.senseEnemyHQLocation();
        }

        if (nearByEnemies == null)
        {
            nearByEnemies = rc.senseNearbyRobots(5, rc.getTeam().opponent());
        }

        // if we can shoot
        if (rc.isWeaponReady())
        {
            // there is no one to shoot
            if (nearByEnemies.length == 0)
            {
                // if there is no one to shoot and we can't move the return false
                if (!rc.isCoreReady())
                {
                    return false;
                }
                if (enemies.length == 0)
                {
                    return false;
                }
                // there is an enemy outside of shooting range that we can see
                else
                {
                    // if the enemy outranges us run away
                    if (FightMicroUtilities.enemyKitingUs(rc, enemies))
                    {
                        direction = FightMicroUtilities.retreatDir(enemies, rc, enemyTowers, enemyHQ);
                    }
                    // otherwise stand your ground!
                    else
                    {
                       return true;
                    }
                }
            }
            // there are enemies in range of us
            else
            {
                // if there is an enemy that we can shoot
                RobotInfo enemy = FightMicroUtilities.prioritizeTargets(nearByEnemies);

                MapLocation enemySpot = enemy.location;

                if (rc.canAttackLocation(enemySpot))
                {
                    rc.attackLocation(enemySpot);
                }

                return true;
            }
        }
        // we can't shoot
        else
        {
            // if we don't see any enemies then no fight micro
            if (enemies.length == 0)
            {
                return false;
            }
            else
            {
                // if we are in range of an enemy retreat
                if (FightMicroUtilities.enemyInRange(rc, enemies))
                {
                    direction = FightMicroUtilities.retreatDir(enemies, rc, enemyTowers, enemyHQ);
                }
                // if there are no enemies in range of us
                else if (nearByEnemies.length == 0)
                {
                    // if we can advance to a location that is not in range of an enemy do so
                    direction = FightMicroUtilities.advanceDir(rc, enemies, enemyTowers, enemyHQ, true);
                }
                else
                {
                    return true;
                }
            }
        }

        // if we can move then do so
        if (rc.isCoreReady())
        {
            if (rc.canMove(direction))
            {
                if (!Utilities.locInRangeOfEnemyTower(rc.getLocation().add(direction), enemyTowers, enemyHQ))
                {
                    rc.move(direction);
                    return true;
                }
            }
            return true;
        }

        return true;
    }

    /**
     * In this implementation we are going to use bashers almost like suicide attackers
     * where they never retreat just advance to the location that they can do the most
     * damage
     */
    public boolean basherFightMicro() throws GameActionException
    {
        if (!rc.isCoreReady())
        {
            return false;
        }

        RobotInfo[] enemies = rc.senseNearbyRobots(35, rc.getTeam().opponent());
        RobotInfo[] nearByEnemies = rc.senseNearbyRobots(2, rc.getTeam().opponent());
        MapLocation[] towers = rc.senseEnemyTowerLocations();
        MapLocation closestTower = Utilities.closestTower(rc, towers);

        if (enemies.length > 0)
        {
            Direction dir;

            dir = FightMicroUtilities.dirToLauncher(rc, enemies);

            if (dir != null)
            {
                if (dir == Direction.OMNI)
                {
                    // we are next to a launcher
                    return true;
                }
                else if (rc.canMove(dir))
                {
                    rc.move(dir);
                    return true;
                }
                else if (rc.canMove(dir.rotateLeft()))
                {
                    rc.move(dir.rotateLeft());
                    return true;
                }
                else if (rc.canMove(dir.rotateRight()))
                {
                    rc.move(dir.rotateRight());
                    return true;
                }
            }


            dir = FightMicroUtilities.bestBasherDir(rc, enemies, nearByEnemies.length);
            rc.setIndicatorString(1, "Moving in basher dir: " + dir);

            if (dir != null && rc.canMove(dir))
            {
                rc.move(dir);
                return true;
            }
            else if (dir != null)
            {
                dir = FightMicroUtilities.basherDirSecond(rc, enemies);
                rc.setIndicatorString(1, "Second basher dir: " + dir);
                if (rc.canMove(dir))
                {
                    rc.move(dir);
                    return true;
                }
            }
            // if our current location is best then go here
            else if (nearByEnemies.length > 0 && dir == null)
            {
                return true;
            }

            return false;
        }
        /*else if (closestTower != null && rc.getLocation().distanceSquaredTo(closestTower) <= 49)
        {
            rc.setIndicatorString(1, "going towards enemy tower: " + closestTower);
            Direction dir = rc.getLocation().directionTo(closestTower);

            if (rc.canMove(dir))
            {
                rc.move(dir);
                return true;
            }
            else if (rc.canMove(dir.rotateLeft()))
            {
                rc.move(dir.rotateLeft());
                return true;
            }
            else if (rc.canMove(dir.rotateRight()))
            {
                rc.move(dir.rotateRight());
                return true;
            }

            return false;
        }*/
        else
        {
            return false;
        }
    }

    /**
     * This fight Micro is for Commanders
     */
    public boolean commanderMicro(RobotInfo[] nearByEnemies, boolean regenerating, RobotInfo[] enemies, boolean avoidStructures) throws GameActionException
    {
        MapLocation flashTo = null;
        MapLocation attack = null;
        Direction moveTo = null;
        MapLocation us = rc.getLocation();

        if (rc.getFlashCooldown() < 1)
        {
            if (enemies.length > 0 && regenerating)
            {
                flashTo = FightMicroUtilities.retreatFlashLoc(rc, nearByEnemies);
            //    rc.setIndicatorString(2, "Flashing to safety: " + flashTo);
            }
            else if (!regenerating)
            {
                boolean launcher = false;
                for (int i = enemies.length; --i>=0; )
                {
                    if (enemies[i].type == RobotType.LAUNCHER)
                    {
                        flashTo = enemies[i].location;
                        if (flashTo.distanceSquaredTo(us) > 10)
                        {
                            flashTo = FightMicroUtilities.flashToLoc(rc, flashTo);
                            launcher = true;
                        }
                        else
                        {
                            flashTo = null;
                        }
                    }
                }

                if (!launcher && enemies.length <= 3)
                {
                    for (int i = enemies.length; --i>=0; )
                    {
                        if (enemies[i].type == RobotType.MISSILE)
                        {
                            if (enemies[i].location.distanceSquaredTo(us) <= 2)
                            {
                                flashTo = enemies[i].location;
                                flashTo = FightMicroUtilities.flashOverMissile(rc, flashTo);
                            //    rc.setIndicatorString(2, "Flash over missile: " + flashTo);
                            }
                        }
                    }
                }

                if (flashTo == null && rc.getHealth() >= 150 && enemies.length > 0 && nearByEnemies.length == 0)
                {
                    boolean onlyWeak = true;
                    int x = 0;
                    int y = 0;

                    for (int i = enemies.length; --i>=0;)
                    {
                        if (!FightMicroUtilities.unitVulnerable(enemies[i]))
                        {
                            onlyWeak = false;
                            break;
                        }
                        else
                        {
                            x += enemies[i].location.x;
                            y += enemies[i].location.y;
                        }
                    }

                    if (onlyWeak)
                    {
                        MapLocation center = new MapLocation(x,y);
                        flashTo = FightMicroUtilities.flashToLoc(rc, center);
                    }
                }
            }
        }
        // if we aren't flashing then c if we should move
        if (rc.isCoreReady() && flashTo == null)
        {
            // run away from enemies
            if (regenerating)
            {
                if (nearByEnemies.length == 0)
                {
                    if (enemies.length == 0)
                    {
                        // sit tight
                        return true;
                    }
                    else
                    {
                        boolean onlyWeak = true;

                        for (int i = enemies.length; --i>=0; )
                        {
                            if (!FightMicroUtilities.unitVulnerable(enemies[i]))
                            {
                                onlyWeak = false;
                            }
                        }

                        if (!onlyWeak)
                        {
                            moveTo = FightMicroUtilities.awayFromOpponents(rc, enemies);
                        }
                        else
                        {
                            moveTo = FightMicroUtilities.toTheEnemy(rc, enemies);
                        }
                    }
                }
                else
                {
                    boolean onlyWeak = true;

                    for (int i = enemies.length; --i>=0; )
                    {
                        if (!FightMicroUtilities.unitVulnerable(enemies[i]))
                        {
                            onlyWeak = false;
                        }
                    }

                    if (!onlyWeak)
                    {
                        moveTo = FightMicroUtilities.awayFromOpponents(rc, enemies);
                    }
                }
            }
            // run towards enemies
            else
            {
                if (nearByEnemies.length > 0)
                {
                    boolean launcher = false;
                    int numbOfMissiles = 0;
                    for (int i = enemies.length; --i>=0; )
                    {
                        if (enemies[i].type == RobotType.MISSILE)
                        {
                            moveTo = rc.getLocation().directionTo(enemies[i].location).opposite();
                            numbOfMissiles++;
                        }
                        else if (enemies[i].type == RobotType.LAUNCHER)
                        {
                            launcher = true;
                        }
                    }

                    if (launcher && numbOfMissiles <= 2)
                    {
                        moveTo = null;
                    }
                }
                else
                {
                    if (enemies.length > 0)
                    {
                        boolean enemyMissile = false;
                        boolean launcher = false;
                        int numbOfMissiles = 0;

                        for (int i = enemies.length; --i>=0; )
                        {
                            if (enemies[i].type == RobotType.MISSILE)
                            {
                                enemyMissile = true;
                                numbOfMissiles++;
                                moveTo = rc.getLocation().directionTo(enemies[i].location).opposite();
                            }
                            else if (enemies[i].type == RobotType.LAUNCHER)
                            {
                                launcher = true;
                            }
                        }

                        if (!enemyMissile || (launcher && numbOfMissiles <= 2))
                        {
                            if (avoidStructures)
                            {
                                MapLocation enemy = FightMicroUtilities.getCommanderAttack(rc, enemies);
                                if (enemy != null)
                                {
                                    moveTo = rc.getLocation().directionTo(enemy);
                                }
                                else
                                {
                                    moveTo = null;
                                }
                            }
                            else
                            {
                                moveTo = FightMicroUtilities.toTheEnemy(rc, enemies);
                            }
                        }
                    }
                }
            }
        }


        RobotInfo target;
        boolean returnVal = false;

        if (rc.isWeaponReady() && nearByEnemies.length > 0)
        {
            if (!rc.hasLearnedSkill(CommanderSkillType.HEAVY_HANDS))
            {
                target = FightMicroUtilities.prioritizeTargets(nearByEnemies);
            }
            else
            {
                target = FightMicroUtilities.prioritizeHeavyHands(nearByEnemies);
            }

            if (target != null)
            {
                attack = target.location;
            }
        }
        else if (nearByEnemies.length > 0)
        {
            returnVal = true;
        }

        // if we picked a spot to flash to then flash!
        if (flashTo != null && flashTo.distanceSquaredTo(rc.getLocation()) <= 10 && rc.isPathable(rc.getType(), flashTo) && rc.isCoreReady())
        {
            //rc.setIndicatorString(1, "Flashing to: " + flashTo);
            rc.castFlash(flashTo);
            returnVal = true;
        }

        // if we can attack then by all means do so
        if (attack != null)
        {
            if (rc.canAttackLocation(attack))
            {
                returnVal = true;
                rc.attackLocation(attack);
            }
        }

        if (moveTo != null && rc.isCoreReady())
        {
            moveTo = FightMicroUtilities.moveCommander(rc, true, moveTo);
            if (moveTo != null)
            {
                //rc.setIndicatorString(1, "Movement: " + moveTo);
                returnVal = true;
                rc.move(moveTo);
            }
        }

        return returnVal;
    }

    /**
     * This method is for units that are harrassing, these units do not attack towers but avoid towers and the enemyHQ
     * trying to focus on killing the enemies miners and structures and if possible avoids enemy military units
     */
    public boolean harrassMicro(RobotInfo[] nearByEnemies) throws GameActionException {
        if (!rc.isCoreReady() && !rc.isWeaponReady()) {
            return false;
        }

        if (rc.isWeaponReady() && nearByEnemies.length > 0)
        {
            RobotInfo enemy = FightMicroUtilities.prioritizeTargets(nearByEnemies);
            MapLocation enemySpot = enemy.location;

            if (rc.canAttackLocation(enemySpot))
            {
                rc.attackLocation(enemySpot);
                return true;
            }
        }
        else if (rc.isCoreReady())
        {
            // if there are enemies in range but we can't shoot
            // stop so we don't incur more
            if (nearByEnemies.length > 0)
            {
                return true;
            }


            RobotInfo[] enemies = rc.senseNearbyRobots(24, rc.getTeam().opponent());

            if (enemies.length > 0)
            {
                MapLocation weakEnemy = null;
                MapLocation strongEnemy = null;
                Direction dir = null;

                for (int i = enemies.length; --i >= 0; )
                {
                    if (FightMicroUtilities.unitVulnerable(enemies[i]))
                    {
                        weakEnemy = enemies[i].location;
                    }
                    else
                    {
                        strongEnemy = enemies[i].location;
                    }
                }

                if (weakEnemy != null)
                {
                    dir = rc.getLocation().directionTo(weakEnemy);
                }
                else if (strongEnemy != null)
                {
                    return false;
                    //dir = rc.getLocation().directionTo(strongEnemy).opposite();
                }

                if (dir != null)
                {
                    dir = FightMicroUtilities.moveAwayFromTowers(rc, dir);

                    if (dir != null && rc.canMove(dir))
                    {
                        rc.move(dir);
                    }

                    return true;
                }
            }
            else
            {
                // let nav take care of avoiding towers and HQ
                return false;
            }
        }
        return false;
    }


   public boolean minerMicro(RobotInfo[] nearByEnemies) throws GameActionException
   {
        // if we can shoot and there is an enemy in sight range
        if (!rc.isCoreReady() && !rc.isWeaponReady())
        {
            return false;
        }
        else if (rc.isWeaponReady() && nearByEnemies.length > 0)
        {
            RobotInfo enemy = FightMicroUtilities.prioritizeTargets(nearByEnemies);
            if (enemy != null)
            {
                MapLocation enemySpot = enemy.location;
                if (rc.canAttackLocation(enemySpot))
                {
                    rc.attackLocation(enemySpot);
                    return true;
                }
            }
        }
        // if there are no enemies in shooting range
        else
        {
            RobotInfo[] enemies = rc.senseNearbyRobots(35, rc.getTeam().opponent());

            // if there are enemies in sight but not shooting range
            if (enemies.length > 0)
            {
                RobotInfo commander = null;

                for (int i = enemies.length; --i>=0; )
                {
                    if (enemies[i].type == RobotType.COMMANDER)
                    {
                        commander = enemies[i];
                    }
                }

                if (commander != null && rc.isCoreReady())
                {
                    Direction direction = rc.getLocation().directionTo(commander.location).opposite();
                    if (rc.canMove(direction))
                    {
                        rc.move(direction);
                    }
                    else if (rc.canMove(direction.rotateRight()))
                    {
                        rc.move(direction.rotateRight());
                    }
                    else if (rc.canMove(direction.rotateLeft()))
                    {
                        rc.move(direction.rotateLeft());
                    }
                    else if (rc.canMove(direction.rotateLeft().rotateLeft()))
                    {
                        rc.move(direction.rotateLeft().rotateLeft());
                    }
                    else if (rc.canMove(direction.rotateRight().rotateRight()))
                    {
                        rc.move(direction.rotateRight().rotateRight());
                    }
                    return true;
                }
                // if there are a bunch of enemies then don't move towards them
                else if (enemies.length > 2)
                {
                    Direction direction = rc.getLocation().directionTo(enemies[0].location).opposite();

                    if (rc.isCoreReady())
                    {
                        if (rc.canMove(direction))
                        {
                            rc.move(direction);
                        }
                        else if (rc.canMove(direction.rotateRight()))
                        {
                            rc.move(direction.rotateRight());
                        }
                        else if (rc.canMove(direction.rotateLeft()))
                        {
                            rc.move(direction.rotateLeft());
                        }
                    }
                    // we don't want nav to move us into a large group of enemies
                    return true;
                }
                // if there are only a few enemies then we are calling for help so keep going
                // we can take a few shots
                else
                {
                    return false;
                }
            }
            // if we can't see any enemies then let nav take care of avoiding towers and enemy HQ
            else
            {
                return false;
            }
        }
        return false;
    }
}
