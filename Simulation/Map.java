package Simulation;

import Simulation.Teams.Soldier;
import battlecode.common.*;


public class Map
{
    double[][] weights1;
    double[][] weights2;

    public Map(double[][] weights1, double[][] weights2)
    {
        this.weights1 = weights1;
        this.weights2 = weights2;
    }

    public MockMapLocation[][] mapLayout;

    /**
     * This method will create MockMapLocations for a read in Map
     *
     * @return
     */
    public void readInMap(String mapName)
    {
        int[] mapDimensions = getMapWidthHeight(mapName);

        mapLayout = getInitialMap(mapName, mapDimensions);
    }

    /**
     * This method will return MockRobotPlayers for the start of a Match
     *
     * @return
     */
    public MockRobotPlayer[] getRobotPlayers(String mapName)
    {
        MockRobotPlayer[] mockRobotPlayers = null;


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

        // TODO: grab the width and height from the file

        return new int[] {width, height};
    }

    /**
     * This method will return a two d array of the map terrain
     *
     * @param mapName
     * @return
     */
    private MockMapLocation[][] getInitialMap(String mapName, int[] mapDimensions)
    {
        MockMapLocation[][] initialMap = new MockMapLocation[mapDimensions[0]][mapDimensions[1]];


        for (int i = 0; i < initialMap.length; i++)
        {
            for (int j = 0; j < initialMap[i].length; j++)
            {
                // TODO: assign terrainTile to the proper terrain for the space (i,j)
                TerrainTile terrainTile = null;

                // TODO: assign team to the proper team (Team.A or Team.B)
                Team team = null;

                // TODO: assign robotType to the proper value
                RobotType robotType = null;

                if (team != null && robotType != null)
                {
                    MockRobotController robotController = new MockRobotController(team, robotType);

                    // This is the team that we are training
                    MockRobotPlayer robotPlayer;

                    if (team == Team.A)
                    {
                        robotPlayer = new Soldier(robotController, weights1);
                    }
                    else
                    {
                        robotPlayer = new Soldier(robotController, weights2);
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
}
