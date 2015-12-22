package team044.Units;

import battlecode.common.*;

public class Missile
{
    public static void run(RobotController rc)
    {
        RobotInfo[] nearByEnemies;
        Direction dir;
        MapLocation target;
        boolean foundLauncher = false;
        MapLocation us;
        int ally_x, ally_y;
        int enemy_x, enemy_y;
        int count;
        RobotType enemy;
        RobotInfo[] nearByAllies;
        MapLocation ally;

        while (true)
        {
            try
            {
                nearByEnemies = rc.senseNearbyRobots(24, rc.getTeam().opponent());

                if (nearByEnemies.length == 0)
                {
                    int x = rc.readBroadcast(7893);
                    int y = rc.readBroadcast(7894);

                    MapLocation closest = new MapLocation(x,y);
                    us = rc.getLocation();

                    if (us.distanceSquaredTo(closest) < 64)
                    {
                        dir = us.directionTo(closest);

                        if (!rc.isCoreReady())
                        {
                            rc.yield();
                        }

                        if (rc.canMove(dir))
                        {
                            rc.move(dir);
                        }
                        else if (rc.canMove(dir.rotateRight()))
                        {
                            rc.move(dir.rotateRight());
                        }
                        else if (rc.canMove(dir.rotateLeft()))
                        {
                            rc.move(dir.rotateLeft());
                        }
                        else if (rc.canMove(dir.rotateLeft().rotateLeft()))
                        {
                            rc.move(dir.rotateLeft().rotateLeft());
                        }
                        else if (rc.canMove(dir.rotateRight().rotateRight()))
                        {
                            rc.move(dir.rotateRight().rotateRight());
                        }
                        //rc.setIndicatorString(1, "us.distanceSquaredTo(Closest) < 64");
                    }
                    else
                    {
                        nearByAllies = rc.senseNearbyRobots(8, rc.getTeam());
                        count = 0;
                        ally_x = 0;
                        ally_y = 0;

                        for (int i = nearByAllies.length; --i>=0; )
                        {
                            if (nearByAllies[i].type == RobotType.LAUNCHER)
                            {
                                count++;
                                ally = nearByAllies[i].location;
                                ally_x += ally.x;
                                ally_y += ally.y;
                            }
                        }

                        ally_x /= count;
                        ally_y /= count;
                        dir = rc.getLocation().directionTo(new MapLocation(ally_x, ally_y)).opposite();

                        if (!rc.isCoreReady())
                        {
                            rc.yield();
                        }

                        if (dir == null)
                        {
                            // don't see any enemies and aren't next to an ally
                        }
                        else if (rc.canMove(dir))
                        {
                            rc.move(dir);
                        }
                        else if (rc.canMove(dir.rotateRight()))
                        {
                            rc.move(dir.rotateRight());
                        }
                        else if (rc.canMove(dir.rotateLeft()))
                        {
                            rc.move(dir.rotateLeft());
                        }
                        //rc.setIndicatorString(1, "no enemies in sight");
                    }
                }
                else
                {
                    count = 0;
                    enemy_x = 0;
                    enemy_y = 0;
                    MapLocation enemyUnit = null;
                    for (int i = nearByEnemies.length; --i>=0;)
                    {
                        enemy = nearByEnemies[i].type;
                        if (enemy == RobotType.LAUNCHER)
                        {
                            if (!rc.isCoreReady())
                            {
                                rc.yield();
                            }
                            dir = rc.getLocation().directionTo(nearByEnemies[i].location);
                            foundLauncher = true;
                            //rc.setIndicatorString(1, "Found launcher");
                            if (rc.canMove(dir))
                            {
                                rc.move(dir);
                                break;
                            }

                            if (rc.canMove(dir.rotateRight()))
                            {
                                rc.move(dir.rotateRight());
                                break;
                            }

                            if (rc.canMove(dir.rotateLeft()))
                            {
                                rc.move(dir.rotateLeft());
                                break;
                            }
                        }
                        else if (enemy != RobotType.MISSILE)
                        {
                            count++;
                            MapLocation enemySpot = nearByEnemies[i].location;
                            enemy_x += enemySpot.x;
                            enemy_y += enemySpot.y;
                            enemyUnit = enemySpot;
                        }
                    }

                    if (!foundLauncher)
                    {
                        if (count == 0)
                        {
                            nearByAllies = rc.senseNearbyRobots(8, rc.getTeam());
                            count = 0;
                            ally_x = 0;
                            ally_y = 0;

                            for (int i = nearByAllies.length; --i>=0; )
                            {
                                if (nearByAllies[i].type == RobotType.LAUNCHER)
                                {
                                    count++;
                                    ally = nearByAllies[i].location;
                                    ally_x += ally.x;
                                    ally_y += ally.y;
                                }
                            }

                            ally_x /= count;
                            ally_y /= count;
                            dir = rc.getLocation().directionTo(new MapLocation(ally_x, ally_y)).opposite();


                            if (!rc.isCoreReady())
                            {
                                rc.yield();
                            }

                            if (dir == null)
                            {
                                // don't see any enemies and aren't next to an ally
                            }
                            else if (rc.canMove(dir))
                            {
                                rc.move(dir);
                            }
                            else if (rc.canMove(dir.rotateRight()))
                            {
                                rc.move(dir.rotateRight());
                            }
                            else if (rc.canMove(dir.rotateLeft()))
                            {
                                rc.move(dir.rotateLeft());
                            }
                            //rc.setIndicatorString(1, "Found no target");
                        }
                        else
                        {
                            if (count > 3)
                            {
                                enemy_x /= count;
                                enemy_y /= count;
                                target = new MapLocation(enemy_x, enemy_y);
                                dir = rc.getLocation().directionTo(target);
                            }
                            else
                            {
                                target = enemyUnit;
                                dir = rc.getLocation().directionTo(target);
                            }

                            if (!rc.isCoreReady())
                            {
                                rc.yield();
                            }

                            if (rc.canMove(dir))
                            {
                                rc.move(dir);
                            }

                            else if (rc.canMove(dir.rotateRight()))
                            {
                                rc.move(dir.rotateRight());
                            }

                            else if (rc.canMove(dir.rotateLeft()))
                            {
                                rc.move(dir.rotateLeft());
                            }

                            //rc.setIndicatorString(1, "Heading towards target");
                        }
                    }
                }
            }
            catch (Exception e)
            {
                rc.yield();
            }
        }
    }
}
