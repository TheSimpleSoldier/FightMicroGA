package team044;

import battlecode.common.*;

public class Messenger
{
    RobotController rc;
    private int numbOfBashers = 0;
    private int numbOfComputers = 0;
    private int numbOfLaunchers = 0;
    private int numbOfMiners = 0;
    private int numbOfSoldiers = 0;
    private int numbOfTanks = 0;
    private int numbOfDrones = 0;

    // these variables are for our groups
    // group 1
    int group1Launchers = 0;
    int group1Tanks = 0;
    int group1Soldiers = 0;
    int group1Bashers = 0;
    boolean group1Launched = false;
    boolean group1LauncherGroup = false;
    boolean group1Offensive = true;
    int group1LauncherCount = 0;
    int group1TankCount = 0;
    int group1SoldierCount = 0;
    int group1BasherCount = 0;
    MapLocation group1InitialSpot;
    MapLocation group1CurrentSpot;
    MapLocation group1Goal;

    // group 2
    int group2Launchers = 0;
    int group2Tanks = 0;
    int group2Soldiers = 0;
    int group2Bashers = 0;
    boolean group2Launched = false;
    boolean group2LauncherGroup = false;
    boolean group2Offensive = true;
    int group2LauncherCount = 0;
    int group2TankCount = 0;
    int group2SoldierCount = 0;
    int group2BasherCount = 0;
    MapLocation group2InitialSpot;
    MapLocation group2CurrentSpot;
    MapLocation group2Goal;
    int group2RoundFinished = 0;

    // group 3
    int group3Launchers = 0;
    int group3Tanks = 0;
    int group3Soldiers = 0;
    int group3Bashers = 0;
    boolean group3Launched = false;
    boolean group3LauncherGroup = false;
    boolean group3Offensive = true;
    int group3LauncherCount = 0;
    int group3TankCount = 0;
    int group3SoldierCount = 0;
    int group3BasherCount = 0;
    MapLocation group3InitialSpot;
    MapLocation group3CurrentSpot;
    MapLocation group3Goal;
    int group3RoundFinished = 0;

    private BuildOrderMessaging[] basherStrat;
    private BuildOrderMessaging[] computerStrat;
    private BuildOrderMessaging[] launcherStrat;
    private BuildOrderMessaging[] minerStrat;
    private BuildOrderMessaging[] soldierStrat;
    private BuildOrderMessaging[] tankStrat;
    private BuildOrderMessaging[] droneStrat;

    public Messenger(RobotController rc) throws GameActionException
    {
        this.rc = rc;
        // initialize everything to -1
        rc.broadcast(Messaging.TankType.ordinal(), -1);
        rc.broadcast(Messaging.SoldierType.ordinal(), -1);
        rc.broadcast(Messaging.ComputerType.ordinal(), -1);
        rc.broadcast(Messaging.LauncherType.ordinal(), -1);
        rc.broadcast(Messaging.MinerType.ordinal(), -1);
        rc.broadcast(Messaging.BasherType.ordinal(), -1);
        rc.broadcast(Messaging.DroneType.ordinal(), -1);

        // initialize strategies
        basherStrat = new BuildOrderMessaging[1];
        basherStrat[0] = BuildOrderMessaging.BuildSquadBasher;

        computerStrat = new BuildOrderMessaging[1];
        computerStrat[0] = BuildOrderMessaging.BuildComputer;

        launcherStrat = new BuildOrderMessaging[1];
        launcherStrat[0] = BuildOrderMessaging.BuildSquadLauncher;

        minerStrat = new BuildOrderMessaging[1];
        minerStrat[0] = BuildOrderMessaging.BuildMiner;

        soldierStrat = new BuildOrderMessaging[1];
        soldierStrat[0] = BuildOrderMessaging.BuildSupportingSoldier;

        tankStrat = new BuildOrderMessaging[1];
        tankStrat[0] = BuildOrderMessaging.BuildSquadTank;

        droneStrat = new BuildOrderMessaging[1];
        droneStrat[0] = BuildOrderMessaging.BuildSupplyDrone;

        MapLocation[] towers = rc.senseTowerLocations();
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        group1InitialSpot = Utilities.getCentralTower(rc, towers);
        group1Goal = Utilities.closestTower(rc, enemyTowers);
        group2InitialSpot = Utilities.getRightFlank(rc, towers);
        group2Goal = Utilities.enemyTowerOnRightFlank(rc, enemyTowers);
        int goGoal = Strategy.loneTowers(rc);
        int x,y;
        group3InitialSpot = Utilities.getLeftFlank(rc, towers);
        group3Goal = Utilities.enemyTowerOnLeftFlank(rc, enemyTowers);
        if (group2Goal != null && (goGoal == 1 || goGoal == 3))
        {
            rc.setIndicatorString(2, "goGoal: " + goGoal + ", x: " + group2Goal.x + ", y: " + group2Goal.y);
            x = (group2InitialSpot.x + group2Goal.x) / 2;
            y = (group2InitialSpot.y + group2Goal.y) / 2;
            Direction dir = group2Goal.directionTo(group2InitialSpot);
            group2InitialSpot = new MapLocation(x,y);
            group2InitialSpot = group2InitialSpot.add(dir, 5);
        }
        if (group3Goal != null && goGoal > 1)
        {
            rc.setIndicatorString(2, "goGoal: " + goGoal + ", x: " + group3Goal.x + ", y: " + group3Goal.y);
            x = (group3InitialSpot.x + group3Goal.x) / 2;
            y = (group3InitialSpot.y + group3Goal.y) / 2;
            Direction dir = group3Goal.directionTo(group3InitialSpot);
            group3InitialSpot = new MapLocation(x,y);
            group3InitialSpot = group3InitialSpot.add(dir, 5);
        }
    }

    /**
     * When a Unit gets its orders it changes that channel to -1
     * so that the HQ knows to issue orders for the next unit
     */
    public void giveUnitOrders() throws GameActionException
    {
        // we want to give a little time before we start managing supply distribution
        if (rc.readBroadcast(Messaging.NumbOfDrones.ordinal()) < 65)
        {
            droneStrat[0] = BuildOrderMessaging.BuildSupplyDrone;
        }
        else
        {
            droneStrat[0] = BuildOrderMessaging.BuildSupplyDrone;
            //droneStrat[0] = BuildOrderMessaging.BuildScoutingDrone;
        }

        int message;
        if (rc.readBroadcast(Messaging.BasherType.ordinal()) == -1)
        {
            message = basherStrat[numbOfBashers].ordinal();
            rc.broadcast(Messaging.BasherType.ordinal(), message);
            numbOfBashers = (numbOfBashers + 1) % basherStrat.length;
        }

        if (rc.readBroadcast(Messaging.ComputerType.ordinal()) == -1)
        {
            message = computerStrat[numbOfComputers].ordinal();
            rc.broadcast(Messaging.ComputerType.ordinal(), message);
            numbOfComputers = (numbOfComputers + 1) % computerStrat.length;
        }

        if (rc.readBroadcast(Messaging.LauncherType.ordinal()) == -1)
        {
            message = launcherStrat[numbOfLaunchers].ordinal();
            rc.broadcast(Messaging.LauncherType.ordinal(), message);
            numbOfLaunchers = (numbOfLaunchers + 1) % launcherStrat.length;
        }

        if (rc.readBroadcast(Messaging.MinerType.ordinal()) == -1)
        {
            message = minerStrat[numbOfMiners].ordinal();
            rc.broadcast(Messaging.MinerType.ordinal(), message);
            numbOfMiners = (numbOfMiners + 1) % minerStrat.length;
        }

        if (rc.readBroadcast(Messaging.SoldierType.ordinal()) == -1)
        {
            message = soldierStrat[numbOfSoldiers].ordinal();
            rc.broadcast(Messaging.SoldierType.ordinal(), message);
            numbOfSoldiers = (numbOfSoldiers + 1) % soldierStrat.length;
        }

        if (rc.readBroadcast(Messaging.TankType.ordinal()) == -1)
        {
            message = tankStrat[numbOfTanks].ordinal();
            rc.broadcast(Messaging.TankType.ordinal(), message);
            numbOfTanks = (numbOfTanks + 1) % tankStrat.length;
        }

        if (rc.readBroadcast(Messaging.DroneType.ordinal()) == -1)
        {
            message = droneStrat[numbOfDrones].ordinal();
            rc.broadcast(Messaging.DroneType.ordinal(), message);
            numbOfDrones = (numbOfDrones + 1) % droneStrat.length;
        }
    }

    /**
     * This method is for handling group orders
     */
    public void manageGroups() throws GameActionException
    {
        // this code determines where the groups rally point should be \\
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        // attack with everything when time to rush enemy occurs
        if (rc.readBroadcast(Messaging.RushEnemyBase.ordinal()) == 1)
        {
            group1Offensive = true;
            group2Offensive = true;
            group3Offensive = true;
            group1Launched = true;
            group2Launched = true;
            group3Launched = true;
        }

        if (group1Launched && group1Offensive && group1CurrentSpot != null)
        {
            if (group1Goal == null || group1CurrentSpot.distanceSquaredTo(group1Goal) < 10)
            {
                group1Goal = Utilities.closestTowerToLoc(enemyTowers, group1CurrentSpot);
                if (group1Goal == null || enemyTowers.length <= 2)
                {
                    group1Goal = rc.senseEnemyHQLocation();
                }
            }
            group1CurrentSpot = setTarget(group1LauncherGroup, group1CurrentSpot, group1Goal);
            rc.broadcast(Messaging.FirstGroupX.ordinal(), group1CurrentSpot.x);
            rc.broadcast(Messaging.FirstGroupY.ordinal(), group1CurrentSpot.y);
        }
        else
        {
            if (group1LauncherGroup && group1LauncherCount >= group1Launchers)
            {
                group1CurrentSpot = group1InitialSpot;
                group1Launched = true;
            }
            else if (!group1LauncherGroup && group1TankCount >= group1Tanks)
            {
                group1CurrentSpot = group1InitialSpot;
                group1Launched = true;
            }

            int towerUnderAttack = rc.readBroadcast(Messaging.TowerUnderAttack.ordinal());
            rc.broadcast(Messaging.TowerUnderAttack.ordinal(), 0);

            if (towerUnderAttack > 0)
            {
                MapLocation[] ourTowers = rc.senseTowerLocations();
                if (ourTowers.length >= towerUnderAttack)
                {
                    MapLocation tower = ourTowers[towerUnderAttack - 1];
                    tower = tower.add(tower.directionTo(rc.senseEnemyHQLocation()), 5);

                    rc.broadcast(Messaging.FirstGroupX.ordinal(), tower.x);
                    rc.broadcast(Messaging.FirstGroupY.ordinal(), tower.y);
                }
            }
            else
            {
                rc.broadcast(Messaging.FirstGroupX.ordinal(), group1InitialSpot.x);
                rc.broadcast(Messaging.FirstGroupY.ordinal(), group1InitialSpot.y);
            }

            cutProd(rc, group1Tanks, group1TankCount, group1Soldiers, group1SoldierCount, group1Bashers, group1BasherCount);

        }


        if (group2Launched && group3Launched && group2Offensive && group2CurrentSpot != null && (Clock.getRoundNum() - group3RoundFinished) > 25)
        {
            if (group2Goal == null || group2CurrentSpot.distanceSquaredTo(group2Goal) < 10)
            {
                group2Goal = Utilities.closestTowerToLoc(enemyTowers, group2CurrentSpot);
                if (group2Goal == null || enemyTowers.length <= 2)
                {
                    group2Goal = rc.senseEnemyHQLocation();
                }
            }
            group2CurrentSpot = setTarget(group2LauncherGroup, group2CurrentSpot, group2Goal);
            rc.setIndicatorString(2, "Current spot group2: " + group2CurrentSpot);
            rc.broadcast(Messaging.SeconGroupX.ordinal(), group2CurrentSpot.x);
            rc.broadcast(Messaging.SecondGroupY.ordinal(), group2CurrentSpot.y);
        }
        else if (!group2Launched)
        {
            if (group2LauncherGroup && group2LauncherCount >= group2Launchers)
            {
                group2CurrentSpot = group2InitialSpot;
                group2Launched = true;
                group2RoundFinished = Clock.getRoundNum();
            }
            else if (!group2LauncherGroup && group2TankCount >= group2Tanks)
            {
                group2CurrentSpot = group2InitialSpot;
                group2Launched = true;
                group2RoundFinished = Clock.getRoundNum();
            }
            /*else if (!group2LauncherGroup && group2TankCount == 0 && group2BasherCount >= group2Bashers)
            {
                group2CurrentSpot = group2InitialSpot;
                group2Launched = true;
                group2RoundFinished = Clock.getRoundNum();
            }*/
            rc.broadcast(Messaging.SeconGroupX.ordinal(), group2InitialSpot.x);
            rc.broadcast(Messaging.SecondGroupY.ordinal(), group2InitialSpot.y);

            if (group1Launched)
            {
                cutProd(rc, group2Tanks, group2TankCount, group2Soldiers, group2SoldierCount, group2Bashers, group2BasherCount);
            }
        }

        if (group3Launched && group3Offensive && group3CurrentSpot != null && (Clock.getRoundNum() - group3RoundFinished) > 75)
        {
            if (group3Goal == null || group3CurrentSpot.distanceSquaredTo(group3Goal) < 10)
            {
                group3Goal = Utilities.closestTowerToLoc(enemyTowers, group3CurrentSpot);
                if (group3Goal == null || enemyTowers.length <= 2)
                {
                    group3Goal = rc.senseEnemyHQLocation();
                }
            }
            group3CurrentSpot = setTarget(group3LauncherGroup, group3CurrentSpot, group3Goal);
            rc.broadcast(Messaging.ThirdGroupX.ordinal(), group3CurrentSpot.x);
            rc.broadcast(Messaging.ThirdGroupY.ordinal(), group3CurrentSpot.y);
        }
        else if (!group3Launched)
        {
            if (group3LauncherGroup && group3LauncherCount >= group3Launchers)
            {
                group3CurrentSpot = group3InitialSpot;
                group3Launched = true;
                group3RoundFinished = Clock.getRoundNum();
            }
            else if (!group3LauncherGroup && group3TankCount >= group3Tanks)
            {
                group3CurrentSpot = group3InitialSpot;
                group3Launched = true;
                group3RoundFinished = Clock.getRoundNum();
            }

            if (group3InitialSpot == null)
            {
                group3InitialSpot = rc.senseEnemyHQLocation();
            }
            rc.broadcast(Messaging.ThirdGroupX.ordinal(), group3InitialSpot.x);
            rc.broadcast(Messaging.ThirdGroupY.ordinal(), group3InitialSpot.y);

            if (group2Launched)
            {
                cutProd(rc, group3Tanks, group3TankCount, group3Soldiers, group3SoldierCount, group3Bashers, group3BasherCount);
            }
        }

        // this code tells a unit which group it should be in
        int newLauncher = rc.readBroadcast(Messaging.LauncherGroup.ordinal());
        int newTank = rc.readBroadcast(Messaging.TankGroup.ordinal());
        int newSoldier = rc.readBroadcast(Messaging.SoldierGroup.ordinal());
        int newBasher = rc.readBroadcast(Messaging.BasherGroup.ordinal());

        if (newLauncher == -1)
        {
            if (group1LauncherCount < group1Launchers)
            {
                group1LauncherCount++;
                rc.broadcast(Messaging.LauncherGroup.ordinal(), 1);
            }
            else if (group2LauncherCount < group2Launchers)
            {
                group2LauncherCount++;
                rc.broadcast(Messaging.LauncherGroup.ordinal(), 2);
            }
            else if (group3LauncherCount < group3Launchers)
            {
                group3LauncherCount++;
                rc.broadcast(Messaging.LauncherGroup.ordinal(), 3);
            }
            else if (group1Launched)
            {
                // once we have filled all the categories rebuild units for group1
                group1Launchers = 100;
            }
        }

        if (newTank == -1)
        {
            if (group1TankCount < group1Tanks)
            {
                group1TankCount++;
                rc.broadcast(Messaging.TankGroup.ordinal(), 1);
            }
            else if (group2TankCount < group2Tanks)
            {
                group2TankCount++;
                rc.broadcast(Messaging.TankGroup.ordinal(), 2);
            }
            else if (group3TankCount < group3Tanks)
            {
                group3TankCount++;
                rc.broadcast(Messaging.TankGroup.ordinal(), 3);
            }
            else if (group2Tanks > 0 || group3Tanks > 0)
            {
                group1Tanks = 15;
                group1Offensive = true;
                group1LauncherGroup = false;
                if (group1TankCount == 0)
                {
                    group1Launched = false;
                }
                group1TankCount = 0;
                group1Soldiers = 20;
                group1Bashers = 20;
                rc.broadcast(Messaging.TankGroup.ordinal(), 1);
            }
        }

        if (newSoldier == -1)
        {
            if (group1SoldierCount < group1Soldiers)
            {
                group1SoldierCount++;
                rc.broadcast(Messaging.SoldierGroup.ordinal(), 1);
            }
            else if (group2SoldierCount < group2Soldiers)
            {
                group2LauncherCount++;
                rc.broadcast(Messaging.SoldierGroup.ordinal(), 2);
            }
            else if (group3SoldierCount < group3Soldiers)
            {
                group3SoldierCount++;
                rc.broadcast(Messaging.SoldierGroup.ordinal(), 3);
            }
            else if (group1Launched)
            {
                group1SoldierCount = 0;
            }
        }

        if (newBasher == -1)
        {
            if (group1BasherCount < group1Bashers)
            {
                group1BasherCount++;
                rc.broadcast(Messaging.BasherGroup.ordinal(), 1);
            }
            else if (group2BasherCount < group2Bashers)
            {
                group2BasherCount++;
                rc.broadcast(Messaging.BasherGroup.ordinal(), 2);
            }
            else if (group3BasherCount < group3Bashers)
            {
                group3BasherCount++;
                rc.broadcast(Messaging.BasherGroup.ordinal(), 3);
            }
            else if (group1Launched)
            {
                group1BasherCount = 0;
            }
        }
    }

    /**
     * this method is for setting group1
     */
    public void setGroup1(int launchers, int tanks, int soldiers, int bashers, boolean offensive)
    {
        group1Launchers = launchers;
        group1Tanks = tanks;
        group1Soldiers = soldiers;
        group1Bashers = bashers;
        group1Offensive = offensive;

        if (launchers > 0)
        {
            group1LauncherGroup = true;
        }
        else
        {
            group1LauncherGroup = false;
        }
    }

    /**
     * This method is for setting group2
     */
    public void setGroup2(int launchers, int tanks, int soldiers, int bashers, boolean offensive)
    {
        group2Launchers = launchers;
        group2Tanks = tanks;
        group2Soldiers = soldiers;
        group2Bashers = bashers;
        group2Offensive = offensive;

        if (launchers > 0)
        {
            group2LauncherGroup = true;
        }
        else
        {
            group2LauncherGroup = false;
        }
    }

    /**
     * this method is for setting group3
     */
    public void setGroup3(int launchers, int tanks, int soldiers, int bashers, boolean offensive)
    {
        group3Launchers = launchers;
        group3Tanks = tanks;
        group3Soldiers = soldiers;
        group3Bashers = bashers;
        group3Offensive = offensive;

        if (launchers > 0)
        {
            group3LauncherGroup = true;
        }
        else
        {
            group3LauncherGroup = false;
        }
    }

    /**
     * This method determines the new location for an advancing group
     */
    public MapLocation setTarget(boolean launcher, MapLocation current, MapLocation goal)
    {
        RobotInfo[] allies = rc.senseNearbyRobots(current, 10, rc.getTeam());

        if (launcher)
        {
            int numbOfLaunchers = 0;

            for (int i = allies.length; --i>=0; )
            {
                if (allies[i].type == RobotType.LAUNCHER)
                {
                    numbOfLaunchers++;
                }
            }

            // if we have a group of launchers near current rally point
            if (numbOfLaunchers >= 3)
            {
                do
                {
                    current = current.add(current.directionTo(goal));

                } while (!rc.isPathable(RobotType.LAUNCHER, current) && rc.canSenseLocation(current) && !current.equals(goal));
            }
        }
        else
        {
            // if we have a group of launchers near current rally point
            if (allies.length >= 3 || (rc.canSenseLocation(current) && !rc.isPathable(RobotType.TANK, current)))
            {
                do
                {
                    current = current.add(current.directionTo(goal));

                } while (!rc.isPathable(RobotType.TANK, current) && rc.canSenseLocation(current) && !current.equals(goal));
            }
        }

        return current;
    }

    /**
     * This method cuts production except for the current group
     */
    public void cutProd(RobotController rc, int tanks, int tankCount, int soldiers, int soldierCount, int bashers, int basherCount) throws GameActionException
    {
        if (tankCount < tanks && (tankStrat.length == 1 && tankStrat[0] == BuildOrderMessaging.BuildSquadTank))
        {
            rc.broadcast(Messaging.ShutOffTankProd.ordinal(), 0);
        }
        else
        {
            rc.broadcast(Messaging.ShutOffTankProd.ordinal(), 1);
        }

        if (soldierCount < soldiers)
        {
            rc.broadcast(Messaging.ShutOffSoldierProd.ordinal(), 0);
        }
        else
        {
            rc.broadcast(Messaging.ShutOffSoldierProd.ordinal(), 1);
        }

        if (basherCount < bashers)
        {
            rc.broadcast(Messaging.ShutOffBasherProd.ordinal(), 0);
        }
        else
        {
            rc.broadcast(Messaging.ShutOffBasherProd.ordinal(), 1);
        }
    }

    /**
     * This functions allow the HQ to change the unit strategies
     * throughout the course of the game
     */
    public void changeBasherStrat(BuildOrderMessaging[] basherStrat)
    {
        this.basherStrat = basherStrat;
    }

    public void changeComputerStrat(BuildOrderMessaging[] computerStrat)
    {
        this.computerStrat = computerStrat;
    }

    public void changeLauncherStrat(BuildOrderMessaging[] launcherStrat)
    {
        this.launcherStrat = launcherStrat;
    }

    public void changeMinerStrat(BuildOrderMessaging[] minerStrat)
    {
        this.minerStrat = minerStrat;
    }

    public void changeSoldierStrat(BuildOrderMessaging[] soldierStrat)
    {
        this.soldierStrat = soldierStrat;
    }

    public void changeTankStrat(BuildOrderMessaging[] tankStrat)
    {
        this.tankStrat = tankStrat;
    }

    public void changeDroneStrat(BuildOrderMessaging[] droneStrat)
    {
        this.droneStrat = droneStrat;
    }
}
