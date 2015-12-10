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
        //this.target = rc.senseEnemyHQLocation();
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

    public abstract void run();
}
