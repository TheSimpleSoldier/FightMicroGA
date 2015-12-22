package team044.Units;

import team044.*;
import battlecode.common.*;

public class Computer extends Unit
{
    boolean scanning;
    MapDiscovery map;
    int job;
    public Computer(RobotController rc) throws GameActionException
    {
        super(rc);
        map = new MapDiscovery();
        scanning = true;
        rc.broadcast(Messaging.NumbOfComps.ordinal(), 1);
        job = 0;

        MapLocation[] towers = rc.senseTowerLocations();
        target = ourHQ;
        MapLocation myLoc = rc.getLocation();
        target = myLoc.add(myLoc.directionTo(ourHQ).opposite());
        target = target.add(myLoc.directionTo(ourHQ).opposite());
        target = target.add(myLoc.directionTo(ourHQ).opposite());
        target = target.add(myLoc.directionTo(ourHQ).opposite());
        target = target.add(myLoc.directionTo(ourHQ).opposite());
    }

    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();
        rc.broadcast(Messaging.ComputerOnline.ordinal(), 1);
        int broadcast = rc.readBroadcast(Messaging.ComputerOnline.ordinal());
        switch (broadcast)
        {
            case 0:
                rc.broadcast(Messaging.ComputerOnline.ordinal(), 1);
                map.checkMap(rc);
                break;
            case 1:
                rc.broadcast(Messaging.ComputerOnline.ordinal(), 2);
                job = 1;
                break;

        }
        Utilities.handleMessageCounter(rc, Messaging.NumbOfCompsOdd.ordinal(), Messaging.NumbOfCompsEven.ordinal());
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (nearByEnemies.length > 0) {
            MapLocation current = rc.getLocation();
            if (rc.canMove(current.directionTo(ourHQ))) {
                return nav.takeNextStep(current.add(current.directionTo(ourHQ)));
            }
            if (rc.canMove(current.directionTo(ourHQ).rotateRight())) {
                return nav.takeNextStep(current.add(current.directionTo(ourHQ).rotateRight()));
            }
            if (rc.canMove(current.directionTo(ourHQ).rotateLeft())) {
                return nav.takeNextStep(current.add(current.directionTo(ourHQ).rotateLeft()));
            }
        }
        if (target == null || target.equals(rc.getLocation()))
        {
            return false;
        }
        return nav.takeNextStep(target);
    }

    public boolean fight() throws GameActionException
    {
        return false;
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        map.checkMap(rc);
        return false;
    }
}
