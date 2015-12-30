package Simulation.Teams;

import Simulation.ActivationFunction;
import Simulation.FeedForwardNeuralNetwork;
import Simulation.MockRobotPlayer;
import battlecode.common.*;

/**
 * Created by fred on 12/29/15.
 */
public class team044 extends MockRobotPlayer {

    public team044(RobotController rc, double[][] weights) {
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
                runFightMicro(nearByEnemies, target);
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
        //System.out.println(dir);

        if (rc.canMove(dir)) {
            try {
                //System.out.println("Moving: " + dir + " On team: " + rc.getTeam());
                //System.out.println(rc.getLocation());
                rc.move(dir);
                //System.out.println(rc.getLocation());
                //System.out.println();
            } catch (Exception e) {
                System.out.println("Failed to move");
                e.printStackTrace();
            }
        }

        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i] != Direction.OMNI && dirs[i] != Direction.NONE && rc.canMove(dirs[i])) {
                try {
                    rc.move(dirs[i]);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private Direction getDir(MapLocation target) {
        return rc.getLocation().directionTo(target);
    }

    /**
     * This function runs the fight micro
     */
    public void runFightMicro(RobotInfo[] nearByEnemies, MapLocation target)
    {
        try
        {
            if (!rc.isWeaponReady())
            {
                return;
            }

            if (nearByEnemies == null || nearByEnemies.length < 1)
            {
                return;
            }

            RobotInfo enemyToAttack = findWeakestEnemy(nearByEnemies);



            if (enemyToAttack != null)
            {
                MapLocation enemy = enemyToAttack.location;

                if (enemy != null && rc.canAttackLocation(enemy))
                {
                    rc.attackLocation(enemy);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Houston we have a problem as team044 has errored out!!!?!?!?!?!?!?!?!?!");
        }
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