package Simulation;

import battlecode.common.*;

/**
 *
 * This class is implemented for a team and is used to control the individual units for a team
 *
 * Note:  It is very important that any class that extends this class to NOT use static variables or functions
 * as this will ruin the simulation as all of the bots will be run together
 *
 */
public abstract class MockRobotPlayer
{
    public MapLocation target;
    public RobotController rc;
    public double[][] weights;
    public Direction[] dirs;

    public MockRobotPlayer()
    {
        throw new Error("Mock Robot Player was not initialized");
    }

    public MockRobotPlayer(RobotController robotController, double[][] weights)
    {
        this.rc = robotController;
        this.target = rc.senseEnemyHQLocation();
        this.weights = weights;
        dirs = Direction.values();
    }

    public char getTypeLetter()
    {
        if (rc.getType() == RobotType.SOLDIER)
        {
            return 's';
        }
        else if (rc.getType() == RobotType.BASHER)
        {
            return 'b';
        }
        else if (rc.getType() == RobotType.LAUNCHER)
        {
            return 'l';
        }
        else if (rc.getType() == RobotType.TANK)
        {
            return 't';
        }
        else if (rc.getType() == RobotType.BEAVER)
        {
            return 'e';
        }
        else if (rc.getType() == RobotType.DRONE)
        {
            return 'd';
        }
        else if (rc.getType() == RobotType.COMMANDER)
        {
            return 'c';
        }
        else if (rc.getType() == RobotType.COMPUTER)
        {
            return 'o';
        }
        else if (rc.getType() == RobotType.HQ)
        {
            return 'h';
        }
        else if (rc.getType() == RobotType.TOWER)
        {
            return 'w';
        }

        return ' ';
    }

    public char getTeamChar()
    {
        if (rc.getTeam() == Team.A)
        {
            return 'a';
        }
        return 'b';
    }

    public RobotInfo getBotInfo()
    {
        int ID = 0;
        Team team = rc.getTeam();
        RobotType type = rc.getType();
        MapLocation location = rc.getLocation();
        double coreDelay = rc.getCoreDelay();
        double weaponDelay = rc.getWeaponDelay();
        double health = rc.getHealth();
        double supplyLevel = rc.getSupplyLevel();
        int xp = rc.getXP();
        int missileCount = rc.getMissileCount();
        MapLocation builder = rc.getLocation(); // this is wrong
        MapLocation buildingLocation = rc.getLocation(); // this is wrong

        return new RobotInfo(ID, team, type, location, coreDelay, weaponDelay, health, supplyLevel, xp, missileCount, builder, buildingLocation);
    }

    public void runTurnEnd()
    {
        rc.yield();
    }

    public abstract void run();

    public void takeDamage(double damage)
    {
//        System.out.println("Taking damage");
        ((MockRobotController) rc).takeDamage(damage);
    }

    public double getHealth()
    {
        return this.rc.getHealth();
    }

    public boolean noCloseEnemies()
    {
        if (this.rc.senseNearbyRobots(rc.getLocation(), 49, rc.getTeam().opponent()).length == 0)
        {
            return true;
        }
        return false;
    }

    public boolean removeFromGame()
    {
        if (target != null)
        {
            if (rc.getLocation().isAdjacentTo(target) && noCloseEnemies())
            {
                return true;
            }
        }
        return false;
    }

    public RobotController getRc()
    {
        return this.rc;
    }
}
