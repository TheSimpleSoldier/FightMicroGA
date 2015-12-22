package team044.Units.harrassers;

import battlecode.common.*;
import team044.Messaging;
import team044.Unit;
import team044.Units.HarrasserUnit;
import team044.Units.Rushers.BasherRusher;
import team044.Units.Rushers.SoldierRusher;
import team044.Utilities;

public class BasherHarrass extends HarrasserUnit
{
    public BasherHarrass(RobotController rc)
    {
        super(rc);
        nav.setAvoidHQ(true);
        nav.setAvoidTowers(true);
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        Utilities.handleMessageCounter(rc, Messaging.NumbOfBashersOdd.ordinal(), Messaging.NumbOfBashersEven.ordinal());
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if (rc.readBroadcast(Messaging.Attack.ordinal()) == 1)
        {
            return new BasherRusher(rc);
        }
        return current;
    }

    public boolean fight() throws GameActionException
    {
        return fighter.basherFightMicro();
    }
}
