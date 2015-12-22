package team044.Structures;

import battlecode.common.*;
import team044.Messaging;
import team044.Structure;
import team044.Utilities;

public class TechnologyInstitute extends Structure {
    int numbComputers;
    public TechnologyInstitute(RobotController rc)
    {
        super(rc);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();
        rc.broadcast(Messaging.ComputerOnline.ordinal(), 0);
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (numbComputers == 0 && !Utilities.cutProd(rc) && Utilities.spawnUnit(RobotType.COMPUTER, rc))
        {
            numbComputers = 1;
            return true;
        }

        return false;
    }
}
