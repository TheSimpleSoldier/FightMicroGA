package team044.Units.SquadUnits;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import team044.Messaging;
import team044.Unit;
import team044.Units.Rushers.LauncherRusher;
import team044.Units.Rushers.SoldierRusher;
import team044.Units.SquadUnit;

public class SoldierSquad extends SquadUnit
{
    public SoldierSquad(RobotController rc) throws GameActionException
    {
        super(rc);
        group = rc.readBroadcast(Messaging.SoldierGroup.ordinal());
        rc.broadcast(Messaging.SoldierGroup.ordinal(), -1);
        rc.setIndicatorString(0, "Squad Soldier group: " + group);
        nav.setAvoidTowers(false);
        //nav.setAvoidHQ(false);
    }
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (group < 1)
        {
            group = rc.readBroadcast(Messaging.SoldierGroup.ordinal());
            rc.broadcast(Messaging.SoldierGroup.ordinal(), -1);
        }
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        if (rc.readBroadcast(Messaging.RushEnemyBase.ordinal()) == 1)
        {
            return new SoldierRusher(rc);
        }
        return current;
    }
}
