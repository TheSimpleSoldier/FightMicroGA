package Simulation;

import Simulation.Teams.Advanced;
import Simulation.Teams.Soldier;
import Simulation.Teams.team044;
import battlecode.common.*;
import java.io.*;

public class Map
{
    double[][] weights1;
    double[][] weights2;
    public MockMapLocation[][] mapLayout;
    public MapLocation teamAHQ;
    public MapLocation teamBHQ;
    private boolean verbose;

    private double blueSoldierDamageDealt;
    private double blueSoldierTotalHealth;

    private double redSoldierDamageDealt;
    private double redSoldierTotalHealth;


    public Map(double[][] weights1, double[][] weights2, boolean verbose)
    {
        this.weights1 = weights1;
        this.weights2 = weights2;

        this.blueSoldierDamageDealt = 0;
        this.blueSoldierTotalHealth = 0;
        this.redSoldierDamageDealt = 0;
        this.redSoldierTotalHealth = 0;
        this.verbose = verbose;
    }

    /**
     * This method prints out the current map with all of the robots on it
     */
    public void print()
    {
        System.out.println();
        System.out.println("------------------------- Printing map ------------------------");

        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                String location = "";
                MockMapLocation current = mapLayout[i][j];

                if (current.getRobotPlayer() != null)
                {
                    location += current.getRobotPlayer().getTypeLetter();
                    location += current.getRobotPlayer().getTeamChar();
                }

                if (i == teamAHQ.x && j == teamAHQ.y)
                {
                    location += 'A';
                }
                else if (i == teamBHQ.x && j == teamBHQ.y)
                {
                    location += 'B';
                }
                else if (current.getTerrain() == TerrainTile.NORMAL)
                {
                    location += 'n';
                }
                else
                {
                    location += 'v';
                }

                location += ' ';

                System.out.print(location);
            }
            System.out.println();
        }
    }

    /**
     * This method will create MockMapLocations for a read in Map
     *
     * @return
     */
    public void readInMap(String mapName, Map map, int teamA, int teamB)
    {
        int[] mapDimensions = getMapWidthHeight(mapName);

        mapLayout = getInitialMap(mapName, mapDimensions, map, teamA, teamB);
    }

    /**
     * This method will return MockRobotPlayers for the start of a Match
     *
     * @return
     */
    public MockRobotPlayer[] getRobotPlayers()
    {
        MockRobotPlayer[] mockRobotPlayers;

        int robotCount = 0;

        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                if (mapLayout[i][j].getRobotPlayer() != null)
                {
                    robotCount++;
                }
            }
        }

        mockRobotPlayers = new MockRobotPlayer[robotCount];

        int index = 0;
        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                if (mapLayout[i][j].getRobotPlayer() != null)
                {
                    mockRobotPlayers[index] = mapLayout[i][j].getRobotPlayer();
                    index++;
                }
            }
        }

        return mockRobotPlayers;
    }

    /**
     * This method will find the width and height of the map
     *
     * @return
     */
    private int[] getMapWidthHeight(String mapName)
    {
        int height = 0;
        int width = 0;

//        System.out.println("got here");
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(new File(mapName)));

            for (String x = in.readLine(); x != null; x = in.readLine())
            {
                if (x.contains("height"))
                {
                    int index = x.indexOf("height");
                    index += 8;
                    char numb = x.charAt(index);

                    while (numb >= '0' && numb <= '9')
                    {
                        height *= 10;
                        height += Integer.parseInt("" + numb);
                        index++;
                        numb = x.charAt(index);
                    }
                }

                if (x.contains("width"))
                {
                    int index = x.indexOf("width");
                    index += 7;
                    char numb = x.charAt(index);

                    while (numb >= '0' && numb <= '9')
                    {
                        width *= 10;
                        width += Integer.parseInt("" + numb);
                        index++;
                        numb = x.charAt(index);
                    }
                }
                if (width != 0 && height != 0)
                {
                    break;
                }
            }

            in.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }


        return new int[] {height, width};
    }

    /**
     * This method will return a two d array of the map terrain
     *
     * @param mapName
     * @return
     */
    private MockMapLocation[][] getInitialMap(String mapName, int[] mapDimensions, Map map, int teamA, int teamB)
    {
        MockMapLocation[][] initialMap = new MockMapLocation[mapDimensions[0]][mapDimensions[1]];
        String[][] mapLocations = new String[mapDimensions[0]][mapDimensions[1]];
        int index = 0;
        boolean data = false;

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(new File(mapName)));

            for (String x = in.readLine(); x != null; x = in.readLine())
            {
                if (!data)
                {
                    if (x.contains("CDATA"))
                    {
                        data = true;
                    }
                }
                // we are in the data
                else
                {
                    // we have reached the end of the file
                    if (x.contains("]]>"))
                    {
                        break;
                    }
                    else
                    {
//                        System.out.println("we are gathering data");
                        String[] row = x.split(" ");
                        mapLocations[index] = row;
                        index++;
                    }
                }
            }

            in.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }

        for (int i = 0; i < initialMap.length; i++)
        {
            for (int j = 0; j < initialMap[i].length; j++)
            {
                TerrainTile terrainTile;

                //System.out.println(mapLocations[i][j]);
                if (mapLocations[i][j].contains("v"))
                {
                    terrainTile = TerrainTile.VOID;
                }
                else
                {
                    terrainTile = TerrainTile.NORMAL;
                }

                Team team = null;
                if (mapLocations[i][j].contains("a"))
                {
                    team = Team.A;
                }
                else if (mapLocations[i][j].contains("b"))
                {
                    team = Team.B;
                }


                RobotType robotType = null;

                if (mapLocations[i][j].contains("s"))
                {
                    robotType = RobotType.SOLDIER;
                }
                else if (mapLocations[i][j].contains("h"))
                {
                    if (team == Team.A)
                    {
                        teamAHQ = new MapLocation(i, j);
                    }
                    else
                    {
                        teamBHQ = new MapLocation(i, j);
                    }
                }

                if (team != null && robotType != null)
                {
                    MockRobotController robotController = new MockRobotController(team, robotType, new MapLocation(i, j), map);

                    // This is the team that we are training
                    MockRobotPlayer robotPlayer;

                    if (team == Team.A)
                    {
                        if (teamA == 1)
                        {
                            robotPlayer = new team044(robotController, weights1);
                        }
                        else if (teamA == 2)
                        {
                            robotPlayer = new Advanced(robotController, weights1);
                        }
                        else
                        {
                            robotPlayer = new Soldier(robotController, weights1);
                        }
                    }
                    else
                    {
                        if (teamB == 1)
                        {
                            robotPlayer = new team044(robotController, weights2);
                        }
                        else if (teamB == 2)
                        {
                            robotPlayer = new Advanced(robotController, weights2);
                        }
                        else
                        {
                            robotPlayer = new Soldier(robotController, weights2);
                        }
                    }

                    initialMap[i][j] = new MockMapLocation(i, j, terrainTile, robotPlayer);
                }
                else
                {
                    initialMap[i][j] = new MockMapLocation(i, j, terrainTile);
                }
            }
        }

        return initialMap;
    }

    public MapLocation getTeamAHQ()
    {
        return this.teamAHQ;
    }

    public MapLocation getTeamBHQ()
    {
        return this.teamBHQ;
    }

    /**
     * This method is used by SenseNearbyRobots
     *
     * @param center
     * @param distanceSquared
     * @return
     */
    public RobotInfo[] getAllRobotsInRange(MapLocation center, int distanceSquared)
    {
        int count = 0;
        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                MockMapLocation current = mapLayout[i][j];

                if (current.getRobotPlayer() != null && current.distanceSquaredTo(center) <= distanceSquared)
                {
                    count++;
                }
            }
        }

        RobotInfo[] robotInfos = new RobotInfo[count];
        count = 0;

        for (int i = 0; i < mapLayout.length; i++)
        {
            for (int j = 0; j < mapLayout[i].length; j++)
            {
                MockMapLocation current = mapLayout[i][j];

                if (current.getRobotPlayer() != null && current.distanceSquaredTo(center) <= distanceSquared)
                {
                    robotInfos[count] = current.getRobotPlayer().getBotInfo();
                    count++;
                }
            }
        }

        return robotInfos;
    }

    /**
     * This method returns true if a location is occupied and false otherwise
     *
     * @param location
     * @return
     */
    public boolean locationOccupied(MapLocation location)
    {
        if (location.x < 0 || location.x >= mapLayout.length)
        {
            return false;
        }
        if (location.y < 0 || location.y >= mapLayout[location.x].length)
        {
            return false;
        }

        if (mapLayout[location.x][location.y].getRobotPlayer() == null)
        {
            return false;
        }

        return true;
    }


    /**
     * This method takes in a robot type and map location and returns if a unit can traverse it
     *
     * @param location
     * @param robotType
     * @return
     */
    public boolean terranTraversalbe(MapLocation location, RobotType robotType)
    {
        // can't go out of bounds
        if (location.x >= mapLayout.length || location.x < 0)
        {
            return false;
        }

        if (location.y >= mapLayout[location.x].length || location.y < 0)
        {
            return false;
        }

        if (robotType == RobotType.DRONE)
        {
            return true;
        }

        if (mapLayout[location.x][location.y].getTerrain() == TerrainTile.VOID)
        {
            return false;
        }

        return true;
    }

    /**
     * This method moves a robot from one location to another
     *
     * @param startLoc
     * @param newLoc
     */
    public void moveRobot(MapLocation startLoc, MapLocation newLoc)
    {
        MockRobotPlayer robotPlayer = mapLayout[startLoc.x][startLoc.y].getRobotPlayer();

        if (robotPlayer == null)
        {
            return;
        }

        mapLayout[startLoc.x][startLoc.y].removeRobotPlayer();

        if (!robotPlayer.removeFromGame())
        {
            mapLayout[newLoc.x][newLoc.y].setRobotPlayer(robotPlayer);
        }
        else
        {
            // record stats for bot
            if (robotPlayer.getRc().getTeam() == Team.A)
            {
                this.redSoldierTotalHealth += robotPlayer.getHealth();
//                this.redSoldierDamageDealt += ((MockRobotController) robotPlayer.getRc()).getTotalDamageDealt();
            }
            else
            {
                this.blueSoldierTotalHealth += robotPlayer.getHealth();
//                this.blueSoldierDamageDealt += ((MockRobotController) robotPlayer.getRc()).getTotalDamageDealt();
            }

//            System.out.println("Player removed from game");
        }
    }

    public void countRedRobot(MockRobotPlayer robotPlayer)
    {
        this.redSoldierTotalHealth += robotPlayer.getHealth();
    }

    public void countBlueRobot(MockRobotPlayer robotPlayer)
    {
        this.blueSoldierTotalHealth += robotPlayer.getHealth();
    }


    public MapLocation getHQLocation(Team team)
    {
        if (team == Team.A)
        {
            return teamAHQ;
        }
        return teamBHQ;
    }

    public void attackLocation(MapLocation loc, double attackAmount)
    {
        MockRobotPlayer player = mapLayout[loc.x][loc.y].getRobotPlayer();

        if (player != null)
        {
            player.takeDamage(attackAmount);

            if (player.getRc().getTeam() == Team.A)
            {
                this.blueSoldierDamageDealt += attackAmount;
            }
            else
            {
                this.redSoldierDamageDealt += attackAmount;
            }

            if (player.getHealth() <= 0)
            {
                mapLayout[loc.x][loc.y].removeRobotPlayer();
            }
        }
    }

    public double getBlueSoldierDamageDealt()
    {
        return this.blueSoldierDamageDealt;
    }

    public double getBlueSoldierTotalHealth()
    {
        return this.blueSoldierTotalHealth;
    }

    public double getRedSoldierDamageDealt()
    {
        return this.redSoldierDamageDealt;
    }

    public double getRedSoldierTotalHealth()
    {
        return this.redSoldierTotalHealth;
    }
}
