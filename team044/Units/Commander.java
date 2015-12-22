package team044.Units;

import battlecode.common.*;
import team044.Messaging;
import team044.Unit;
import team044.Utilities;

import java.util.Random;

public class Commander extends Unit
{
    boolean regenerating = false;
    boolean rushing = false;
    boolean avoidStructures = true;
    RobotInfo[] enemies;
    Random random;
    int choice;
    int round;

    public Commander(RobotController rc)
    {
        super(rc);
        target = enemyHQ;
        random = new Random(rc.getID());
        nav.setCircle(true);
        nav.setAvoidTowers(false);
        nav.setAvoidHQ(false);

        rc.setIndicatorString(0, "I am Achilles");
        rc.setIndicatorString(1, "Demigod of Greece");
        rc.setIndicatorString(2, "Prepare to Die!!");

        avoidStructures = true;
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        MapLocation mySpot = rc.getLocation();

        mySpot = mySpot.add(mySpot.directionTo(enemyHQ), 5);

        rc.broadcast(Messaging.CommanderLocX.ordinal(), mySpot.x);
        rc.broadcast(Messaging.CommanderLocY.ordinal(), mySpot.y);
    }

    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();

        if (rc.readBroadcast(Messaging.Attack.ordinal()) == 1)
        {
            rushing = true;
        }
        else
        {
            RobotInfo[] allies = rc.senseNearbyRobots(100, us);
            if (allies.length == 0)
            {
                rushing = false;
            }
        }

        enemies = rc.senseNearbyRobots(35, opponent);
        // when we hit 100 health we head back to the battlefield
        if (regenerating && rc.getHealth() >= 150)
        {
            regenerating = false;
        }

        // when our health gets too low we head away from the battlefield
        if (!regenerating && (rc.getHealth() <= 75))
        {
            regenerating = true;
        }

        if (rushing)
        {
            target = Utilities.getRushLocation(rc);
        }
        else
        {
            target = Utilities.getTowerClosestToEnemyHQ(rc);
        }

    }

    public boolean takeNextStep() throws GameActionException
    {
        if (target == null)
        {
            return false;
        }
        return nav.takeNextStep(target);

    }

    public boolean fight() throws GameActionException
    {
    //    rc.setIndicatorString(1, "Fight Micro");
        return fighter.commanderMicro(nearByEnemies, regenerating, enemies, avoidStructures);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    public void distributeSupply() throws GameActionException
    {
    }
}
