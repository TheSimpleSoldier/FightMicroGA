package Simulation.Teams;

import Simulation.ActivationFunction;
import Simulation.FeedForwardNeuralNetwork;
import Simulation.MockRobotPlayer;
import battlecode.common.RobotController;
import battlecode.common.*;

public class Soldier extends MockRobotPlayer
{
    FeedForwardNeuralNetwork net;
    public Soldier(RobotController rc, double[][] weights)
    {
        super(rc, weights);
        net = new FeedForwardNeuralNetwork(1, new int[]{50, 50, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);
        //net.setWeights(weights[0]);
    }

    public void run()
    {
        if (target == null)
        {
            target = rc.senseEnemyHQLocation();
        }

        if (rc.getType() == RobotType.SOLDIER)
        {
            // run soldier code
            RobotInfo[] nearByEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
            RobotInfo[] allBots = rc.senseNearbyRobots(rc.getType().attackRadiusSquared);

            //System.out.println(rc.isCoreReady());
            if (nearByEnemies.length == 0 && rc.isCoreReady())
            {
                // move towards target
                move(target);
            }
            else if(nearByEnemies.length > 0)
            {
                // fight
                System.out.println("fighting");
                runFightMicro(allBots, target);
            }
        }
        else
        {
            // do nothing
        }
    }

    /**
     * This function causes the unit to move
     *
     * @param target
     */
    public void move(MapLocation target)
    {
        Direction dir = getDir(target);
        //System.out.println(dir);

        if (rc.canMove(dir))
        {
            try
            {
                System.out.println("Moving: " + dir + " On team: " + rc.getTeam());
                System.out.println(rc.getLocation());
                rc.move(dir);
                System.out.println(rc.getLocation());
                System.out.println();
            }
            catch(Exception e)
            {
                System.out.println("Failed to move");
                e.printStackTrace();
            }
        }

        for (int i = 0; i < dirs.length; i++)
        {
            if (rc.canMove(dirs[i]))
            {
                try
                {
                    rc.move(dirs[i]);
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
        }
    }

    private Direction getDir(MapLocation target)
    {
        return rc.getLocation().directionTo(target);
    }

    /**
     * This function runs the fight micro
     */
    public void runFightMicro(RobotInfo[] nearByBots, MapLocation target)
    {
        Direction dir = getDir(target);
        int forward = 0;
        int left = 0;
        boolean fight = false;

        double[] inputs = getInputs(nearByBots, dir);

        double[] output = net.compute(inputs);

        if(output[0] > .5)
        {
            forward++;
        }
        if(output[1] > .5)
        {
            forward--;
        }
        if(output[2] > .5)
        {
            left++;
        }
        if(output[3] > .5)
        {
            left--;
        }
        if(output[4] > .5)
        {
            fight = true;
        }

        if (fight && nearByBots.length > 0)
        {
            try
            {
                rc.attackLocation(nearByBots[0].location);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        else
        {
            switch(forward)
            {
                case -1:
                    switch(left)
                    {
                        case -1:
                            dir = dir.rotateRight().rotateRight().rotateRight();
                            break;
                        case 0:
                            dir = dir.opposite();
                            break;
                        case 1:
                            dir = dir.rotateLeft().rotateLeft().rotateLeft();
                            break;
                    }
                    break;
                case 0:
                    switch(left)
                    {
                        case -1:
                            dir = dir.rotateRight().rotateRight();
                            break;
                        case 0:
                            dir = Direction.NONE;
                            break;
                        case 1:
                            dir = dir.rotateLeft().rotateLeft();
                            break;
                    }
                    break;
                case 1:
                    switch(left)
                    {
                        case -1:
                            dir = dir.rotateRight();
                            break;
                        case 0:
                            break;
                        case 1:
                            dir = dir.rotateLeft();
                            break;
                    }
                    break;
            }

            try
            {
                if(rc.canMove(dir))
                {
                    rc.move(dir);
                }
            }
            catch(Exception e)
            {
                System.out.println("Failed to move while fighting");
                e.printStackTrace();
            }
        }
    }

    private double[] getInputs(RobotInfo[] nearByBots, Direction dir)
    {
        return new double[50];
    }
}
