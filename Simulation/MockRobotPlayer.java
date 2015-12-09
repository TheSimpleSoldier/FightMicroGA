package Simulation;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

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

    public abstract void run();
}
