package Simulation.Teams;

import Simulation.MockRobotPlayer;
import battlecode.common.RobotController;
import battlecode.common.*;

public class Soldier extends MockRobotPlayer
{
    public Soldier(RobotController rc, double[][] weights)
    {
        super(rc, weights);
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

            //System.out.println(rc.isCoreReady());
            if (nearByEnemies.length == 0 && rc.isCoreReady())
            {
                // move towards target
                move(target);
            }
            else if (rc.isWeaponReady())
            {
                // fight
                runFightMicro(nearByEnemies);
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
        Direction dir = rc.getLocation().directionTo(target);
        System.out.println(dir);

        if (rc.canMove(dir))
        {
            try
            {
                System.out.println("Moving: " + dir + " On team: " + rc.getTeam());
                rc.move(dir);
            }
            catch(Exception e)
            {
                System.out.println(e);
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

    /**
     * This function runs the fight micro
     */
    public void runFightMicro(RobotInfo[] nearByEnemies)
    {
        if (nearByEnemies.length > 0)
        {
            try
            {
                rc.attackLocation(nearByEnemies[0].location);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }
}
