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
        net = new FeedForwardNeuralNetwork(1, new int[]{18, 25, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);
        net.setWeights(weights[0]);
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
                runFightMicro(allBots, target, nearByEnemies);
            }
            else
            {
//                System.out.println("Waiting");
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
                //System.out.println("Moving: " + dir + " On team: " + rc.getTeam());
                //System.out.println(rc.getLocation());
                rc.move(dir);
                //System.out.println(rc.getLocation());
                //System.out.println();
            }
            catch(Exception e)
            {
                System.out.println("Failed to move");
                e.printStackTrace();
            }
        }

        for (int i = 0; i < dirs.length; i++)
        {
            if (dirs[i] != Direction.OMNI && dirs[i] != Direction.NONE && rc.canMove(dirs[i]))
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

    private Direction getDir(MapLocation target)
    {
        return rc.getLocation().directionTo(target);
    }

    /**
     * This function runs the fight micro
     */
    public void runFightMicro(RobotInfo[] nearByBots, MapLocation target, RobotInfo[] nearByEnemies)
    {
        try
        {
            Direction dir = getDir(target);
            int forward = 0;
            int left = 0;
            boolean fight = false;

            double[] inputs = getInputs(nearByBots, dir);

            double[] output = net.compute(inputs);
            for(int k = 0; k < output.length; k++)
            {
//            System.out.print(output[k] + " ");
            }
//        System.out.println();

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
                    MapLocation attackSpot = findWeakestEnemy(nearByEnemies).location;
                    if (rc.canAttackLocation(attackSpot))
                    {
                        rc.attackLocation(attackSpot);
                    }
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
        catch(Exception e)
        {
            System.out.println("We failed in the fight Micro");
        }
    }

    /**
     * Creates inputs for network
     * 2 are for center of mass of enemy
     * 2 are for center of mass of allies
     * 1 is for standard deviation of enemy
     * 1 is for standard deviation of allies
     * 1 is for number of enemy
     * 1 is for number of allies
     * 1 is for core delay
     * 1 is for weapon delay
     * 8 are for terrain around bot
     * 21 for each type of bot and is count of that type of enemy
     * 21 for each type of bot and is count of that type of ally
     *
     * each count is divided by 10 to normalize
     * @param nearByBots all near by bots
     * @param dir direction we want to go
     * @return
     */
    private double[] getInputs(RobotInfo[] nearByBots, Direction dir) throws Exception
    {
        double enemyCount = 0;
        double allyCount = 0;
//        double[] enemyCounts = new double[21];
//        double[] allyCounts = new double[21];
        double averageAllyX = 0;
        double averageAllyY = 0;
        double averageEnemyX = 0;
        double averageEnemyY = 0;

//        for(int k = 0; k < enemyCounts.length; k++)
//        {
//            enemyCounts[k] = 0;
//            allyCounts[k] = 0;
//        }

        for(RobotInfo bot : nearByBots)
        {
            if(bot.team.equals(rc.getTeam()))
            {
                allyCount++;
                averageAllyX += bot.location.x - rc.getLocation().x;
                averageAllyY += bot.location.y - rc.getLocation().y;

//                switch(bot.type)
//                {
//                    case AEROSPACELAB:
//                        allyCounts[0]++;
//                        break;
//                    case BARRACKS:
//                        allyCounts[1]++;
//                        break;
//                    case BASHER:
//                        allyCounts[2]++;
//                        break;
//                    case BEAVER:
//                        allyCounts[3]++;
//                        break;
//                    case COMMANDER:
//                        allyCounts[4]++;
//                        break;
//                    case COMPUTER:
//                        allyCounts[5]++;
//                        break;
//                    case DRONE:
//                        allyCounts[6]++;
//                        break;
//                    case HANDWASHSTATION:
//                        allyCounts[7]++;
//                        break;
//                    case HELIPAD:
//                        allyCounts[8]++;
//                        break;
//                    case HQ:
//                        allyCounts[9]++;
//                        break;
//                    case LAUNCHER:
//                        allyCounts[10]++;
//                        break;
//                    case MINER:
//                        allyCounts[11]++;
//                        break;
//                    case MINERFACTORY:
//                        allyCounts[12]++;
//                        break;
//                    case MISSILE:
//                        allyCounts[13]++;
//                        break;
//                    case SOLDIER:
//                        allyCounts[14]++;
//                        break;
//                    case SUPPLYDEPOT:
//                        allyCounts[15]++;
//                        break;
//                    case TANK:
//                        allyCounts[16]++;
//                        break;
//                    case TANKFACTORY:
//                        allyCounts[17]++;
//                        break;
//                    case TECHNOLOGYINSTITUTE:
//                        allyCounts[18]++;
//                        break;
//                    case TOWER:
//                        allyCounts[19]++;
//                        break;
//                    case TRAININGFIELD:
//                        allyCounts[20]++;
//                        break;
//                }
            }
            else
            {
                enemyCount++;
                averageEnemyX += bot.location.x - rc.getLocation().x;
                averageEnemyY += bot.location.y - rc.getLocation().y;

//                switch(bot.type)
//                {
//                    case AEROSPACELAB:
//                        enemyCounts[0]++;
//                        break;
//                    case BARRACKS:
//                        enemyCounts[1]++;
//                        break;
//                    case BASHER:
//                        enemyCounts[2]++;
//                        break;
//                    case BEAVER:
//                        enemyCounts[3]++;
//                        break;
//                    case COMMANDER:
//                        enemyCounts[4]++;
//                        break;
//                    case COMPUTER:
//                        enemyCounts[5]++;
//                        break;
//                    case DRONE:
//                        enemyCounts[6]++;
//                        break;
//                    case HANDWASHSTATION:
//                        enemyCounts[7]++;
//                        break;
//                    case HELIPAD:
//                        enemyCounts[8]++;
//                        break;
//                    case HQ:
//                        enemyCounts[9]++;
//                        break;
//                    case LAUNCHER:
//                        enemyCounts[10]++;
//                        break;
//                    case MINER:
//                        enemyCounts[11]++;
//                        break;
//                    case MINERFACTORY:
//                        enemyCounts[12]++;
//                        break;
//                    case MISSILE:
//                        enemyCounts[13]++;
//                        break;
//                    case SOLDIER:
//                        enemyCounts[14]++;
//                        break;
//                    case SUPPLYDEPOT:
//                        enemyCounts[15]++;
//                        break;
//                    case TANK:
//                        enemyCounts[16]++;
//                        break;
//                    case TANKFACTORY:
//                        enemyCounts[17]++;
//                        break;
//                    case TECHNOLOGYINSTITUTE:
//                        enemyCounts[18]++;
//                        break;
//                    case TOWER:
//                        enemyCounts[19]++;
//                        break;
//                    case TRAININGFIELD:
//                        enemyCounts[20]++;
//                        break;
//                }
            }
        }

        double stdDevAllyX = 0;
        double stdDevAllyY = 0;
        double stdDevEnemyX = 0;
        double stdDevEnemyY = 0;
        for(RobotInfo bot : nearByBots)
        {
            if(bot.team.equals(rc.getTeam()))
            {
                stdDevAllyX += Math.abs(bot.location.x - ((averageAllyX / allyCount) + rc.getLocation().x));
                stdDevAllyY += Math.abs(bot.location.y - ((averageAllyX / allyCount) + rc.getLocation().y));
            }
            else
            {
                stdDevEnemyX += Math.abs(bot.location.x - ((averageEnemyX / enemyCount) + rc.getLocation().x));
                stdDevEnemyY += Math.abs(bot.location.y - ((averageEnemyX / enemyCount) + rc.getLocation().y));
            }
        }

        double[] toReturn = new double[18];

        toReturn[0] = averageEnemyX / enemyCount;
        toReturn[1] = averageEnemyY / enemyCount;
        toReturn[2] = averageAllyX / allyCount;
        toReturn[3] = averageAllyY / allyCount;
        toReturn[4] = (stdDevEnemyX + stdDevEnemyY) / enemyCount;
        toReturn[5] = (stdDevAllyX + stdDevAllyY) / allyCount;
        toReturn[6] = enemyCount / 10;
        toReturn[7] = allyCount / 10;
        toReturn[8] = rc.getCoreDelay();
        toReturn[9] = rc.getWeaponDelay();
        for(int k = 0; k < 8; k++)
        {
            TerrainTile tile = rc.senseTerrainTile(rc.getLocation().add(dir));//terrain

            if (tile != null)
            {
                switch(tile)
                {
                    case NORMAL:
                        toReturn[k + 10] = .25;
                        break;
                    case UNKNOWN:
                        toReturn[k + 10] = .5;
                        break;
                    case VOID:
                        toReturn[k + 10] = .75;
                        break;
                    case OFF_MAP:
                        toReturn[k + 10] = 1;
                        break;
                    default:
                        toReturn[k + 10] = 0;
                }
            }
            dir = dir.rotateRight();
        }
        for(int k = 0; k < 21; k++)
        {
//            toReturn[k + 18] = enemyCounts[k] / 10;
        }
        for(int k = 0; k < 21; k++)
        {
//            toReturn[k + 39] = allyCounts[k] / 10;
        }

//        for(int k = 0; k < toReturn.length; k++)
//        {
//            System.out.print(toReturn[k] + ", ");
//        }
//        System.out.println();

        return toReturn;
    }
}
