package team044;

import team044.Units.*;
import team044.Structures.*;
import battlecode.common.*;
import team044.Units.Defenders.DefensiveBasher;
import team044.Units.Defenders.DefensiveSoldiers;
import team044.Units.Defenders.DefensiveTank;
import team044.Units.Defenders.DefensiveLauncher;
import team044.Units.Followers.DroneFollower;
import team044.Units.Followers.SoldierFollower;
import team044.Units.SquadUnits.BasherSquad;
import team044.Units.SquadUnits.LauncherSquad;
import team044.Units.SquadUnits.SoldierSquad;
import team044.Units.SquadUnits.TankSquad;
import team044.Units.SupportingUnits.SupportingSoldier;
import team044.Units.harrassers.BasherHarrass;
import team044.Units.harrassers.SoldierHarrasser;
import team044.Units.harrassers.TankHarrasser;

public class RobotPlayer
{
    private static Unit unit;
    public static void run(RobotController rc)
    {
        while (true)
        {
            try
            {
                // units
                if (rc.getType() == RobotType.MISSILE)
                {
                    // we are going to be as bytecode efficient as possible with missiles
                    Missile.run(rc);
                }
                else if (rc.getType() == RobotType.BEAVER)
                {
                    unit = getBeaver(rc);
                }
                else if (rc.getType() == RobotType.COMPUTER)
                {
                    unit = getComputer(rc);
                }
                else if (rc.getType() == RobotType.COMMANDER)
                {
                    unit = getCommander(rc);
                }
                else if (rc.getType() == RobotType.SOLDIER)
                {
                    unit = getSoldier(rc);
                }
                else if (rc.getType() == RobotType.BASHER)
                {
                    unit = getBasher(rc);
                }
                else if (rc.getType() == RobotType.TANK)
                {
                    unit = getTank(rc);
                }
                else if (rc.getType() == RobotType.DRONE)
                {
                    unit = getDrone(rc);
                }
                else if (rc.getType() == RobotType.LAUNCHER)
                {
                    unit = getLauncher(rc);
                }
                else if (rc.getType() == RobotType.MINER)
                {
                    unit = getMiner(rc);
                }
                // Structures
                else if (rc.getType() == RobotType.AEROSPACELAB)
                {
                    unit = getAerospaceLab(rc);
                }
                else if (rc.getType() == RobotType.BARRACKS)
                {
                    unit = getBarracks(rc);
                }
                else if (rc.getType() == RobotType.HELIPAD)
                {
                    unit = getHelipad(rc);
                }
                else if (rc.getType() == RobotType.MINERFACTORY)
                {
                    unit = getMinerFactory(rc);
                }
                else if (rc.getType() == RobotType.TANKFACTORY)
                {
                    unit = getTankFactory(rc);
                }
                else if (rc.getType() == RobotType.TECHNOLOGYINSTITUTE)
                {
                    unit = getTechnologyInstitute(rc);
                }
                else if (rc.getType() == RobotType.TRAININGFIELD)
                {
                    unit = getTrainingField(rc);
                }
                else if (rc.getType() == RobotType.HQ)
                {
                    unit = getHQ(rc);
                }
                else if (rc.getType() == RobotType.TOWER)
                {
                    unit = getTower(rc);
                    Utilities.getBestSpot(rc, false);
                }
                else if (rc.getType() == RobotType.SUPPLYDEPOT)
                {
                    unit = getSupplyDepot(rc);
                }
                else if (rc.getType() == RobotType.HANDWASHSTATION)
                {
                    while (true) { rc.yield(); }
                }
                else
                {
                    System.out.println("Houston we have a problem");
                }

                while (true)
                {
                    try
                    {
                        unit.collectData();
                        unit.handleMessages();
                        if (unit.fight())
                        {
                            // run fight micro
                        }
                        else if (unit.carryOutAbility())
                        {
                            // execute ability
                        }
                        else if (unit.takeNextStep())
                        {
                            // take one step forward
                        }

                        unit = unit.getNewStrategy(unit);

                        unit.distributeSupply();
                        rc.yield();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        rc.yield();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Failure setting unit type");
            }
        }
    }

    // the methods below determine the class we use for a particular unit

    private static Unit getBeaver(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.BeaverType.ordinal());
        rc.broadcast(Messaging.BeaverType.ordinal(), -1);

        if (type == BuildOrderMessaging.BuildBeaverBuilder.ordinal())
        {
            return new BuildingBeaver(rc);
        }
        else if (type == BuildOrderMessaging.BuildBeaverMiner.ordinal())
        {
            return new MinerBeaver(rc);
        }
        // default to a building beaver
        return new BuildingBeaver(rc);
    }

    private static Unit getComputer(RobotController rc) throws GameActionException
    {
        // type for specific types of computers
        int type = rc.readBroadcast(Messaging.ComputerType.ordinal());
        rc.broadcast(Messaging.ComputerType.ordinal(), -1);

        // default to base computer
        return new Computer(rc);
    }

    private static Unit getCommander(RobotController rc)
    {
        return new Commander(rc);
    }

    private static Unit getSoldier(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.SoldierType.ordinal());
        rc.broadcast(Messaging.SoldierType.ordinal(), -1);

        if (type == BuildOrderMessaging.BuildDefensiveSoldier.ordinal())
        {
            return new DefensiveSoldiers(rc);
        }
        else if (type == BuildOrderMessaging.BuildHarrassSoldier.ordinal())
        {
            return new SoldierHarrasser(rc);
        }
        else if (type == BuildOrderMessaging.BuildSquadSoldier.ordinal())
        {
            return new SoldierSquad(rc);
        }
        else if (type == BuildOrderMessaging.BuildSupportingSoldier.ordinal())
        {
            return new SupportingSoldier(rc);
        }
        else if (type == BuildOrderMessaging.BuildFollowerSoldier.ordinal())
        {
            return new SoldierFollower(rc);
        }
        // default to defensive soldier
        return new Soldier(rc);
    }

    private static Unit getBasher(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.BasherType.ordinal());
        rc.broadcast(Messaging.BasherType.ordinal(), -1);

        if (type == BuildOrderMessaging.BuildDefensiveBasher.ordinal())
        {
            return new DefensiveBasher(rc);
        }
        else if (type == BuildOrderMessaging.BuildHarrassBasher.ordinal())
        {
            return new BasherHarrass(rc);
        }
        else if (type == BuildOrderMessaging.BuildSquadBasher.ordinal())
        {
            return new BasherSquad(rc);
        }
        // default Basher
        return new Basher(rc);
    }

    private static Unit getTank(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.TankType.ordinal());
        rc.broadcast(Messaging.TankType.ordinal(), -1);

        if (type == BuildOrderMessaging.BuildDefensiveTank.ordinal())
        {
            return new DefensiveTank(rc);
        }
        else if (type == BuildOrderMessaging.BuildHarrassTank.ordinal())
        {
            return new TankHarrasser(rc);
        }
        else if (type == BuildOrderMessaging.BuildSquadTank.ordinal())
        {
            return new TankSquad(rc);
        }

        // default to defensive Tank
        return new Tank(rc);
    }

    private static Unit getDrone(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.DroneType.ordinal());
        rc.broadcast(Messaging.DroneType.ordinal(), -1);

        if (type == BuildOrderMessaging.BuildScoutingDrone.ordinal())
        {
            return new ScoutingDrone(rc);
        }
        else if (type == BuildOrderMessaging.BuildSupplyDrone.ordinal())
        {
            return  new SupplyDrone(rc);
        }
        else if (type == BuildOrderMessaging.BuildSearchAndDestroyDrone.ordinal())
        {
            return new SearchAndDestroyDrone(rc);
        }
        else if (type == BuildOrderMessaging.BuildFollowerDrone.ordinal())
        {
            return new DroneFollower(rc);
        }

        // default to Search and Destroy Drone
        return new SearchAndDestroyDrone(rc);
    }

    private static Unit getLauncher(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.LauncherType.ordinal());
        rc.broadcast(Messaging.LauncherType.ordinal(), -1);

        if (type == BuildOrderMessaging.BuildSquadLauncher.ordinal())
        {
            return new LauncherSquad(rc);
        }
        else if (type == BuildOrderMessaging.BuildDefensiveLauncher.ordinal())
        {
            return new DefensiveLauncher(rc);
        }

        // default Launcher
        return new Launcher(rc);
    }

    private static Unit getMiner(RobotController rc) throws GameActionException
    {
        int type = rc.readBroadcast(Messaging.MinerType.ordinal());
        rc.broadcast(Messaging.MinerType.ordinal(), -1);

        // default miner
        return new Miner(rc);
    }

    private static Unit getAerospaceLab(RobotController rc)
    {
        return new AerospaceLab(rc);
    }

    private static Unit getBarracks(RobotController rc)
    {
        return new Barracks(rc);
    }

    private static Unit getHelipad(RobotController rc)
    {
        return new Helipad(rc);
    }

    private static Unit getMinerFactory(RobotController rc)
    {
        return new MinerFactory(rc);
    }

    private static Unit getTankFactory(RobotController rc)
    {
        return new TankFactory(rc);
    }

    private static Unit getTechnologyInstitute(RobotController rc)
    {
        return new TechnologyInstitute(rc);
    }

    private static Unit getTrainingField(RobotController rc)
    {
        return new TrainingField(rc);
    }

    private static Unit getHQ(RobotController rc) throws GameActionException
    {
        return new HQ(rc);
    }

    private static Unit getTower(RobotController rc)
    {
        return new Tower(rc);
    }

    private static Unit getSupplyDepot(RobotController rc)
    {
        return new SupplyDepot(rc);
    }
}
