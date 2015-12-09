package Simulation.Teams;

import Simulation.MockRobotPlayer;
import battlecode.common.RobotController;
import battlecode.common.*;

public class Soldier extends MockRobotPlayer
{
    public void run()
    {
        if (rc.getType() == RobotType.SOLDIER)
        {
            // run soldier code
            RobotInfo[] nearByEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());

            if (nearByEnemies.length == 0 && rc.isWeaponReady())
            {
                // move towards target
                move(target);
            }
            else if (rc.isCoreReady())
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
