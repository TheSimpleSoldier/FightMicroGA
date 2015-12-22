package team044.Structures;

import battlecode.common.*;
import team044.Messaging;
import team044.Structure;
import team044.Utilities;

import java.util.Random;

public class Barracks extends Structure
{
    boolean basher = true;
    int numbOfSoldiers = 0;
    int counter = 0;
    Random random;
    public Barracks(RobotController rc)
    {
        super(rc);
        if (rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) > 35)
        {
            basher = true;
        }

        random = new Random(rc.getID());
        rc.setIndicatorString(0, "Barracks");
    }

    // overridden methods go here

    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();
        numbOfSoldiers = rc.readBroadcast(Messaging.NumbOfSoldiers.ordinal());
        if (random.nextInt(4) < rc.readBroadcast(Messaging.BasherRatio.ordinal()))
        {
            basher = false;
        }
        else
        {
            basher = true;
        }
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (rc.getRoundLimit() - Clock.getRoundNum() < 300 && rc.getTeamOre() < 300)
        {
            return false;
        }
        else if (rc.readBroadcast(Messaging.BasherRush.ordinal()) == 1)
        {
            if (basher && Utilities.spawnUnit(RobotType.BASHER, rc))
            {
                return true;
            }
            else if (!basher && Utilities.spawnUnit(RobotType.SOLDIER, rc))
            {
                return true;
            }
        }
        else if (rc.readBroadcast(Messaging.ShutOffBasherProd.ordinal()) == 0 && Clock.getRoundNum() > 300)
        {
            if (Utilities.spawnUnit(RobotType.BASHER, rc))
            {
                counter++;
                return true;
            }
        }
        else if (rc.readBroadcast(Messaging.ShutOffSoldierProd.ordinal()) == 0 && Clock.getRoundNum() > 300)
        {
            if (Utilities.spawnUnit(RobotType.SOLDIER, rc))
            {
                counter++;
                return true;
            }
        }

        return false;
    }
}
