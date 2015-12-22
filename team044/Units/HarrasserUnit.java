package team044.Units;

import battlecode.common.*;
import team044.Unit;

import java.util.Random;

public class HarrasserUnit extends Unit
{
    int choice;
    Random random;
    MapLocation one, two, three, four, five;

    public HarrasserUnit(RobotController rc)
    {
        super(rc);

        random = new Random(rc.getID());

        target = enemyHQ;

        Direction dir = enemyHQ.directionTo(ourHQ);
        three = enemyHQ.add(dir, 7);
        two = enemyHQ.add(dir.rotateLeft(), 7);
        one = enemyHQ.add(dir.rotateLeft().rotateLeft(), 7);
        four = enemyHQ.add(dir.rotateRight(), 7);
        five = enemyHQ.add(dir.rotateRight().rotateRight(), 7);
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        if (Clock.getRoundNum() % 25 == 0 || rc.getLocation().isAdjacentTo(target) || rc.getLocation() == target)
        {
            choice = random.nextInt(5) + 1;

            switch (choice)
            {
                case 1:
                    target = one;
                    break;
                case 2:
                    target = two;
                    break;
                case 3:
                    target = three;
                    break;
                case 4:
                    target = four;
                    break;
                case 5:
                    target = five;
                    break;
                default:
                    target = three;
            }
        }
    }

    public boolean fight() throws GameActionException
    {
        return fighter.harrassMicro(nearByEnemies);
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (target == null)
        {
            return false;
        }
        return nav.takeNextStep(target);
    }
}
