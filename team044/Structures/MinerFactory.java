package team044.Structures;

import battlecode.common.*;
import team044.Messaging;
import team044.Structure;
import team044.Utilities;

public class MinerFactory extends Structure
{
    private int numbOfMiners;
    private int actualMiners = 0;

    public MinerFactory(RobotController rc)
    {
        super(rc);
    }


    public void collectData() throws GameActionException
    {
        // collect our data
        super.collectData();
        actualMiners = rc.readBroadcast(Messaging.NumbOfMiners.ordinal());
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (Utilities.cutProd(rc))
        {
            return false;
        }
        if (Clock.getRoundNum() > rc.getRoundLimit() - 750){
            return false;
        }

        int mapSize = rc.readBroadcast(Messaging.MapSize.ordinal());
        if (mapSize == 0)
            mapSize = ourHQ.distanceSquaredTo(enemyHQ);

        if (mapSize < 2000 && Clock.getRoundNum() > rc.getRoundLimit() - 1000)
        {
            return false;
        }
        int bestOre = rc.readBroadcast(Messaging.BestOre.ordinal());
        switch (actualMiners/5)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                if (Utilities.spawnUnit(RobotType.MINER,rc)) {
                    return true;
                }
                break;
            case 5:
            case 6:
                if (mapSize > 1600 && bestOre > 9 && Utilities.spawnUnit(RobotType.MINER,rc)){
                    return true;
                }
                break;
            case 7:
            case 8:
                if (mapSize > 2500 && bestOre > 9 && Utilities.spawnUnit(RobotType.MINER,rc)){
                    return true;
                }
                break;
            case 9:
                if (mapSize > 3600 && bestOre > 9 && Utilities.spawnUnit(RobotType.MINER,rc)){
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }
}
