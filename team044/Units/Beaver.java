package team044.Units;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import team044.Messaging;
import team044.Unit;
import team044.Units.Rushers.BeaverRusher;
import team044.Utilities;

public class Beaver extends Unit
{
    int buildingType;

    public Beaver()
    {
        // we are in trouble
    }

    public Beaver(RobotController rc)
    {
        super(rc);
        nav.setCircle(true);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();
    }

    public void handleMessages() throws GameActionException
    {
        Utilities.handleMessageCounter(rc, Messaging.NumbOfBeaverOdd.ordinal(), Messaging.NumbOfBeaverEven.ordinal());
    }

    public boolean takeNextStep() throws GameActionException
    {
        //rc.setIndicatorString(1, "Bytecodes: " + Clock.getBytecodeNum());
        if (target == null)
        {
            return false;
        }
        if (nav.takeNextStep(target))
        {
            rc.setIndicatorString(0, "In navigation: " + target + ", Round: " + Clock.getRoundNum());
            return true;
        }
        rc.setIndicatorString(0, "Failed navigation: " + target + ", Round: " + Clock.getRoundNum());
        return false;
    }

    public boolean fight() throws GameActionException
    {
        rc.setIndicatorString(1, "Bytecodes used before nav: " + Clock.getBytecodeNum());
        if (fighter.basicFightMicro(nearByEnemies))
        {
            return true;
        }
        return false;
        //return fighter.basicFightMicro(nearByEnemies);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if (rc.readBroadcast(Messaging.RushEnemyBase.ordinal()) == 1)
        {
            return new BeaverRusher(rc);
        }
        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (rc.isCoreReady() && rc.canMine() && rc.senseOre(rc.getLocation()) >= 2)
        {
            rc.mine();
            return true;
        }

        return false;
    }
}
