package team044;

import battlecode.common.*;

public class HQ extends Structure
{
    RobotInfo[] enemies;
    RobotInfo[] allies;
    MapLocation[] badTowers;
    int[] badTowerHP;
    RobotInfo[] nearByAllies;
    int numberOfMinerFactories = -1;
    Direction[] dirs;
    FightMicro fighter;
    Messenger messenger;
    BuildOrderMessaging[] strat;
    int currentUnit = 0;
    int numbOfBuildings = 0;
    int lastNumbOfBuildings = 0;

    int numbOfBashers   = 0;
    int numbOfBeavers   = 0;
    int numbOfComps     = 0;
    int numbOfDrones    = 0;
    int numbOfLaunchers = 0;
    int numbOfMiners    = 0;
    int numbOfSoldiers  = 0;
    int numbOfTanks     = 0;

    int lastGameEnemy = 0;
    int towersUp;

    public HQ(RobotController rc) throws GameActionException
    {
        super(rc);
        fighter = new FightMicro(rc);
        messenger = new Messenger(rc);
        lastGameEnemy = (int) rc.getTeamMemory()[TeamMemory.EnemyUnitBuild.ordinal()];
        strat = Strategy.initialStrategy(rc, messenger);
        towersUp = rc.senseTowerLocations().length;
        badTowers = rc.senseEnemyTowerLocations();
        badTowerHP = new int[badTowers.length];
        for (int i = 0; i < badTowers.length; i++) {
            badTowerHP[i] = 1000;
        }
        //rc.setIndicatorString(2, "HQ: " + rc.getType().attackRadiusSquared + ", sight Range : " + rc.getType().sensorRadiusSquared);
    }

    public void handleMessages() throws GameActionException
    {
        //rc.setIndicatorString(0, "Messaging");
        messenger.giveUnitOrders();
        messenger.manageGroups();

        //rc.setIndicatorString(0, "after give unit orders");

        // reset tower under attack channel every round
        rc.broadcast(Messaging.TowerUnderAttack.ordinal(), 0);

        // reset building under attack channels every round
        rc.broadcast(Messaging.BuildingInDistressY.ordinal(), 0);
        rc.broadcast(Messaging.BuildingInDistressX.ordinal(), 0);

        // reset Launcher in need of help channel
        rc.broadcast(Messaging.LauncherAttackX.ordinal(), 0);
        rc.broadcast(Messaging.LauncherAttackY.ordinal(), 0);

        // reset Miner in need of help channel
        rc.broadcast(Messaging.MinerUnderAttackX.ordinal(), 0);
        rc.broadcast(Messaging.MinerUnderAttackY.ordinal(), 0);

        // reset commander position
        //rc.broadcast(Messaging.CommanderLocX.ordinal(), 0);
        //rc.broadcast(Messaging.CommanderLocY.ordinal(), 0);

        // at the end of the game rush all units to try and take down the enemy as mining will no longer help us
        if (rc.getRoundLimit() - Clock.getRoundNum() < 250)
        {
            rc.broadcast(Messaging.RushEnemyBase.ordinal(), 1);
            rc.broadcast(Messaging.Attack.ordinal(), 1);
        }
        // currently we attack when we reach round 1000
        // TODO: Smarter attack metrics
        else if (Clock.getRoundNum() > 400 && Clock.getRoundNum() % 200 < 6)
        {
            rc.broadcast(Messaging.Attack.ordinal(), 1);
        }
        else
        {
            rc.broadcast(Messaging.Attack.ordinal(), 0);
        }

        // even round so odd channel has data
        if (Clock.getRoundNum() % 2 == 0)
        {
            // read in the integer value and then reset channel to 0
            numbOfBashers = rc.readBroadcast(Messaging.NumbOfBashersOdd.ordinal());
            rc.broadcast(Messaging.NumbOfBashersOdd.ordinal(), 0);

            numbOfBeavers = rc.readBroadcast(Messaging.NumbOfBeaverOdd.ordinal());
            rc.broadcast(Messaging.NumbOfBeaverOdd.ordinal(), 0);

            numbOfComps = rc.readBroadcast(Messaging.NumbOfCompsOdd.ordinal());
            rc.broadcast(Messaging.NumbOfCompsOdd.ordinal(), 0);

            numbOfDrones = rc.readBroadcast(Messaging.NumbOfDronesOdd.ordinal());
            rc.broadcast(Messaging.NumbOfDronesOdd.ordinal(), 0);

            numbOfLaunchers = rc.readBroadcast(Messaging.NumbOfLaunchersOdd.ordinal());
            rc.broadcast(Messaging.NumbOfLaunchersOdd.ordinal(), 0);

            numbOfMiners = rc.readBroadcast(Messaging.NumbOfMinersOdd.ordinal());
            rc.broadcast(Messaging.NumbOfMinersOdd.ordinal(),  0);

            numbOfSoldiers = rc.readBroadcast(Messaging.NumbOfSoldiersOdd.ordinal());
            rc.broadcast(Messaging.NumbOfSoldiersOdd.ordinal(), 0);

            numbOfTanks = rc.readBroadcast(Messaging.NumbOfTanksOdd.ordinal());
            rc.broadcast(Messaging.NumbOfTanksOdd.ordinal(), 0);
        }
        // odd round so even channel has complete numb
        else
        {
            // read in the integer value and then reset channel to 0
            numbOfBashers = rc.readBroadcast(Messaging.NumbOfBashersEven.ordinal());
            rc.broadcast(Messaging.NumbOfBashersEven.ordinal(), 0);

            numbOfBeavers = rc.readBroadcast(Messaging.NumbOfBeaverEven.ordinal());
            rc.broadcast(Messaging.NumbOfBeaverEven.ordinal(), 0);

            numbOfComps = rc.readBroadcast(Messaging.NumbOfCompsEven.ordinal());
            rc.broadcast(Messaging.NumbOfCompsEven.ordinal(), 0);

            numbOfDrones = rc.readBroadcast(Messaging.NumbOfDronesEven.ordinal());
            rc.broadcast(Messaging.NumbOfDronesEven.ordinal(), 0);

            numbOfLaunchers = rc.readBroadcast(Messaging.NumbOfLaunchersEven.ordinal());
            rc.broadcast(Messaging.NumbOfLaunchersEven.ordinal(), 0);

            numbOfMiners = rc.readBroadcast(Messaging.NumbOfMinersEven.ordinal());
            rc.broadcast(Messaging.NumbOfMinersEven.ordinal(),  0);

            numbOfSoldiers = rc.readBroadcast(Messaging.NumbOfSoldiersEven.ordinal());
            rc.broadcast(Messaging.NumbOfSoldiersEven.ordinal(), 0);

            numbOfTanks = rc.readBroadcast(Messaging.NumbOfTanksEven.ordinal());
            rc.broadcast(Messaging.NumbOfTanksEven.ordinal(), 0);
        }

        // now broadcast the value for anyone else to use
        rc.broadcast(Messaging.NumbOfBashers.ordinal(), numbOfBashers);
        rc.broadcast(Messaging.NumbOfBeavers.ordinal(), numbOfBeavers);
        rc.broadcast(Messaging.NumbOfComps.ordinal(), numbOfComps);
        rc.broadcast(Messaging.NumbOfDrones.ordinal(), numbOfDrones);
        rc.broadcast(Messaging.NumbOfLaunchers.ordinal(), numbOfLaunchers);
        rc.broadcast(Messaging.NumbOfMiners.ordinal(), numbOfMiners);
        rc.broadcast(Messaging.NumbOfSoldiers.ordinal(), numbOfSoldiers);
        rc.broadcast(Messaging.NumbOfTanks.ordinal(), numbOfTanks);

        //rc.setIndicatorString(0, "Bashers: " + numbOfBashers + ", Beavers: " + numbOfBeavers + ", Comps: " + numbOfComps + ", Drones: " + numbOfDrones + ", Launchers: " + numbOfLaunchers + ", Miners: " + numbOfMiners + ", Soldiers: " + ", Tanks: " + numbOfTanks);
        //numbOfBuildings = Utilities.test(rc);

        if (currentUnit < strat.length)
        {
            rc.setIndicatorString(1, "Next Unit: "+strat[currentUnit]);
        }


        if (currentUnit >= strat.length)
        {
            // we are done excecuting build order
            //rc.setIndicatorString(1, "currentUnit >= strat.length ");
        }
        else if (strat[currentUnit] == BuildOrderMessaging.BuildBeaverBuilder)
        {
            rc.broadcast(Messaging.BeaverType.ordinal(), BuildOrderMessaging.BuildBeaverBuilder.ordinal());
        }
        else if (strat[currentUnit] == BuildOrderMessaging.BuildBeaverMiner)
        {
            rc.broadcast(Messaging.BeaverType.ordinal(), BuildOrderMessaging.BuildBeaverMiner.ordinal());
        }
        else
        {
            // if a beaver has taken up a job then we go ahead and post the next building
            if (rc.readBroadcast(Messaging.BuildOrder.ordinal()) == -1)
            {
                currentUnit++;
            }

            if (currentUnit >= strat.length)
            {
                return;
            }

            // something is messed up
            if (strat[currentUnit] == null)
            {
                return;
            }

            // state which building we want built next
            //rc.setIndicatorString(1, ""+strat[currentUnit]);
            rc.broadcast(Messaging.BuildOrder.ordinal(), strat[currentUnit].ordinal());
        }

        if (nearByEnemies.length > 0)
        {
            rc.broadcast(Messaging.HQUnderAttack.ordinal(), 1);
            if (rc.readBroadcast(Messaging.AttackOccurred.ordinal()) == 0 && rc.getHealth() < RobotType.HQ.maxHealth)
            {
               rc.setTeamMemory(TeamMemory.AttackTiming.ordinal(), Clock.getRoundNum());
               rc.broadcast(Messaging.AttackOccurred.ordinal(), 1);
                int[] enemyType = new int[5];
                int enemyCountMax = -1;
                int mostUnits = -1;
                int secondMost = 0;
                for (int j = 0; j < nearByEnemies.length; j++)
                {
                    RobotType typeCheck = nearByEnemies[j].type;
                    // Drone counter
                    if (typeCheck.equals(RobotType.DRONE))
                    {
                        enemyType[0]++;
                        if (enemyType[0] >= enemyCountMax)
                        {
                            enemyCountMax = enemyType[0];
                            secondMost = mostUnits;
                            mostUnits = 1;
                        }
                    }
                    // Missile/Launcher counter
                    else if (typeCheck.equals(RobotType.MISSILE) || typeCheck.equals(RobotType.LAUNCHER))
                    {
                        enemyType[1]++;
                        if (enemyType[1] >= enemyCountMax)
                        {
                            enemyCountMax = enemyType[1];
                            secondMost = mostUnits;
                            mostUnits = 2;
                        }
                    }
                    // Tank counter
                    else if (typeCheck.equals(RobotType.TANK))
                    {
                        enemyType[2]++;
                        if (enemyType[2] >= enemyCountMax)
                        {
                            enemyCountMax = enemyType[2];
                            secondMost = mostUnits;
                            mostUnits = 3;
                        }
                    }
                    // Basher counter
                    else if (typeCheck.equals(RobotType.BASHER))
                    {
                        enemyType[3]++;
                        if (enemyType[3] >= enemyCountMax)
                        {
                            enemyCountMax = enemyType[3];
                            secondMost = mostUnits;
                            mostUnits = 4;
                        }
                    }
                    // Soldier counter
                    else if (typeCheck.equals(RobotType.SOLDIER))
                    {
                        enemyType[4]++;
                        if (enemyType[4] >= enemyCountMax)
                        {
                            enemyCountMax = enemyType[4];
                            secondMost = mostUnits;
                            mostUnits = 5;
                        }
                    }
                }
                // At least one offensive unit attacked the structure
                if (mostUnits > 0)
                {
                    secondMost = secondMost << 4;   // Retrieve this with: long secondMost = memoryArray[AttackTiming.ordinal()] >>> 16;
                    mostUnits += secondMost;
                    mostUnits = mostUnits << 12;    // Retrieve this with: long mostUnits = (memoryArray[AttackTiming.ordinal()] >>> 12) & 15;
                    int timing = Clock.getRoundNum();
                    timing += mostUnits;            // Retrieve this with: long timing = memoryArray[AttackTiming.ordinal()] & 4095;
                    rc.setTeamMemory(TeamMemory.AttackTiming.ordinal(), timing);
                }
            }
        }
        else
        {
            rc.broadcast(Messaging.HQUnderAttack.ordinal(), 0);
        }
        int hp = 0;
        int up = badTowers.length;
        for (int i = 0; i < badTowers.length; i++) {
            if (badTowers[i] != null) {
                boolean canSenseTower = rc.canSenseLocation(badTowers[i]);
                if (canSenseTower) {
                    RobotInfo tower = rc.senseRobotAtLocation(badTowers[i]);
                    if (tower == null) {
                        badTowers[i] = null;
                        badTowerHP[i] = 0;
                        up--;
                    } else if (tower.type != RobotType.TOWER) {
                        badTowers[i] = null;
                        badTowerHP[i] = 0;
                        up--;
                    } else if (badTowerHP[i] > tower.health) {
                        badTowerHP[i] -= (badTowerHP[i] - (int) tower.health);
                    }
                }
            }
            else
            {
                up--;
            }
            hp += badTowerHP[i];
        }

        int towerCount = rc.readBroadcast(Messaging.TowersUp.ordinal());
        towerCount -= up;
        rc.setTeamMemory(TeamMemory.TowersUp.ordinal(),towerCount);

        hp = rc.readBroadcast(Messaging.TowerHP.ordinal()) - hp;
        rc.setTeamMemory(TeamMemory.TowerHP.ordinal(), hp);
        rc.setTeamMemory(TeamMemory.TimeLeft.ordinal(), rc.getRoundLimit() - Clock.getRoundNum());
        if (rc.canSenseLocation(enemyHQ))
            rc.setTeamMemory(TeamMemory.HQHP.ordinal(), (long) (rc.getHealth() - rc.senseRobotAtLocation(enemyHQ).health));
        rc.broadcast(Messaging.TowersUp.ordinal(), 0);
        rc.broadcast(Messaging.TowerHP.ordinal(), 0);
    }

    public void collectData() throws GameActionException
    {
        int average = 0;
        int count = 0;

        while (Clock.getRoundNum() > 15 && Clock.getRoundNum() < 50 && rc.getTeamOre() > 300)
        {
            rc.yield();
        }


        for(int k = Constants.startMinerSeenChannel; rc.readBroadcast(k) != 0; k++)
        {
            average += rc.readBroadcast(k);
            count++;
        }
        if (count > 0)
        {
            average = average / count;
        }

        if(average > 250)
        {
            rc.setTeamMemory(TeamMemory.harassDrone.ordinal(), 0);
        }
        else
        {
            rc.setTeamMemory(TeamMemory.harassDrone.ordinal(), 1);
        }

        MapLocation[] towers = rc.senseTowerLocations();

        if (towers.length >= 2)
        {
            range = 35;
        }
        else
        {
            range = 24;
        }

        enemies = rc.senseNearbyRobots(99999, opponent);
        nearByEnemies = rc.senseNearbyRobots(35, opponent);
        allies = rc.senseNearbyRobots(99999, us);
        nearByAllies = rc.senseNearbyRobots(range, us);

        if (currentUnit == strat.length)
        {
            if (rc.getTeamOre() > 1000)
            {
                rc.setIndicatorString(1, "Adding AeroSpaceLab");
                currentUnit--;
                strat[currentUnit] = BuildOrderMessaging.BuildAerospaceLab;
            }
        }
        int[] enemyType = new int[3];
        int enemyCountMax = -1;
        int mostUnits = -1;
        int secondMost = 0;

        for (int i = 0; i < enemies.length; i++)
        {
            RobotType typeCheck = enemies[i].type;
            // Drone counter
            if (typeCheck.equals(RobotType.DRONE))
            {
                enemyType[0]++;
                if (enemyType[0] >= enemyCountMax)
                {
                    enemyCountMax = enemyType[0];
                    secondMost = mostUnits;
                    mostUnits = 1;
                }
            }
            // Missile/Launcher counter
            else if (typeCheck.equals(RobotType.MISSILE) || typeCheck.equals(RobotType.LAUNCHER))
            {
                enemyType[1]++;
                if (enemyType[1] >= enemyCountMax)
                {
                    enemyCountMax = enemyType[1];
                    secondMost = mostUnits;
                    mostUnits = 2;
                }
            }
            // Tank counter
            else if (typeCheck.equals(RobotType.TANK))
            {
                enemyType[2]++;
                if (enemyType[2] >= enemyCountMax)
                {
                    enemyCountMax = enemyType[2];
                    secondMost = mostUnits;
                    mostUnits = 3;
                }
            }
        }

        if (rc.readBroadcast(Messaging.StopCountingEnemy.ordinal()) == 0 && ((enemyCountMax > 20 && mostUnits == 1) || (enemyCountMax > 20 && mostUnits == 2)))
        {
            rc.broadcast(Messaging.StopCountingEnemy.ordinal(), 1);
            rc.setTeamMemory(TeamMemory.EnemyUnitBuild.ordinal(), mostUnits);
            rc.setTeamMemory(TeamMemory.HQHP.ordinal(), (long) rc.getHealth());
        }
        else if (rc.readBroadcast(Messaging.StopCountingEnemy.ordinal()) == 0)
        {
            if (mostUnits == -1)
            {
                rc.setTeamMemory(TeamMemory.EnemyUnitBuild.ordinal(), lastGameEnemy);
                rc.setTeamMemory(TeamMemory.HQHP.ordinal(), (long) rc.getHealth());
            }
            else
            {
                rc.setTeamMemory(TeamMemory.EnemyUnitBuild.ordinal(), mostUnits);
                rc.setTeamMemory(TeamMemory.HQHP.ordinal(), (long) rc.getHealth());
            }

        }
    }

    public boolean fight() throws GameActionException
    {
        return fighter.structureFightMicro(nearByEnemies);
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (currentUnit >= strat.length)
        {
            return false;
        }
        // we only build a beaver if it is the next unit to be built
        if (strat[currentUnit] == BuildOrderMessaging.BuildBeaverBuilder || strat[currentUnit] == BuildOrderMessaging.BuildBeaverMiner)
        {
            if (Utilities.spawnUnit(RobotType.BEAVER, rc))
            {
                currentUnit++;
                if (currentUnit >= strat.length)
                {
                    return true;
                }

                // state which building we want built next
                if (strat[currentUnit] == BuildOrderMessaging.BuildMinerFactory)
                {
                    numberOfMinerFactories++;
                    rc.broadcast(Messaging.NumbOfBeavers.ordinal(), numberOfMinerFactories);
                }

                //rc.setIndicatorString(1, "" + strat[currentUnit]);
                if (strat[currentUnit] != null)
                {
                    rc.broadcast(Messaging.BuildOrder.ordinal(), strat[currentUnit].ordinal());
                }
                return true;
            }
        }
        // if we are trying to build a building but don't have any beavers then create a beaver
        else if ((numbOfBeavers < 1 && Clock.getRoundNum() > 500) || rc.getTeamOre() > 2000)
        {
            if (Utilities.spawnUnit(RobotType.BEAVER, rc))
            {
                rc.broadcast(Messaging.BuildOrder.ordinal(), BuildOrderMessaging.BuildBeaverBuilder.ordinal());
                return true;
            }
        }
        return false;
    }

    public void distributeSupply() throws GameActionException {
        Utilities.shareSupplies(rc);
    }
}
