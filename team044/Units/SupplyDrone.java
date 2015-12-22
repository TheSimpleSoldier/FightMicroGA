package team044.Units;

import battlecode.common.*;
import team044.Messaging;
import team044.Utilities;

public class SupplyDrone extends Drone
{
    boolean firstLoc = false;
    boolean secondDrone = false;
    public SupplyDrone(RobotController rc) throws GameActionException
    {
        super(rc);
        if (rc.readBroadcast(Messaging.NumbOfDrones.ordinal()) > 0)
        {
            secondDrone = true;
        }

        rc.setIndicatorString(0, "Supply Drone");
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        Utilities.handleMessageCounter(rc, Messaging.NumbOfDronesOdd.ordinal(), Messaging.NumbOfDronesEven.ordinal());
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        if (target == null || rc.getLocation().isAdjacentTo(ourHQ))
        {
            int x = rc.readBroadcast(Messaging.FirstNeedSupplyX.ordinal());
            int y = rc.readBroadcast(Messaging.FirstNeedSupplyY.ordinal());

            // then go to first
            if (x != 0 || y != 0 && !secondDrone)
            {
                target = new MapLocation(x, y);
                firstLoc = true;

                rc.broadcast(Messaging.FirstNeedSupplyX.ordinal(), 0);
                rc.broadcast(Messaging.FirstNeedSupplyY.ordinal(), 0);
            }
            // then go to second
            else
            {
                x = rc.readBroadcast(Messaging.SecondNeedSupplyX.ordinal());
                y = rc.readBroadcast(Messaging.SecondNeedSupplyY.ordinal());

                rc.broadcast(Messaging.SecondNeedSupplyX.ordinal(), 0);
                rc.broadcast(Messaging.SecondNeedSupplyY.ordinal(), 0);

                if (x != 0 || y != 0)
                {
                    target = new MapLocation(x, y);
                    firstLoc = false;
                }
            }
        }

        if (target == null)
        {
            return;
        }

        if (rc.getLocation().isAdjacentTo(target))
        {
            target = ourHQ;

            // clear message board so that new units can post for supply
            if (firstLoc)
            {
                rc.broadcast(Messaging.FirstNeedSupplyX.ordinal(), 0);
                rc.broadcast(Messaging.FirstNeedSupplyY.ordinal(), 0);
            }
            else
            {
                rc.broadcast(Messaging.SecondNeedSupplyX.ordinal(), 0);
                rc.broadcast(Messaging.SecondNeedSupplyY.ordinal(), 0);
            }
        }


        if (rc.getSupplyLevel() < 1000)
        {
            target = ourHQ;
        }

        rc.setIndicatorString(1, "Target: " + target);

    }

    public void distributeSupply() throws GameActionException
    {
        if (target == null)
        {
            return;
        }
        if (target.equals(ourHQ) && rc.getLocation().distanceSquaredTo(ourHQ) > 100)
        {
            super.distributeSupply();
        }


        if (rc.getLocation().distanceSquaredTo(target) < 24)
        {
            if (Utilities.supplyArmy(rc))
            {
                // we have given away our supplies go to HQ
                target = ourHQ;
            }
            else
            {
                // we have supplies with no one to give them to so go to next request
                target = null;
            }
        }
    }
}
