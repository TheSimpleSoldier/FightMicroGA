package Simulation;

import battlecode.common.*;

public class Game
{
    private double[][] team1Inputs;
    private double[][] team2Inputs;
    private Map map;

    public Game(double[][] team1Inputs, double[][] team2Inputs)
    {
        this.team1Inputs = team1Inputs;
        this.team2Inputs = team2Inputs;

    }

    public void runMatch(String MapName)
    {
        map.readInMap(MapName);

        MockRobotPlayer[] robotPlayers;

        for (int i = 0; i < 2000; i++)
        {
            robotPlayers = map.getRobotPlayers(MapName);

            for (int j = 0; j < robotPlayers.length; j++)
            {
                robotPlayers[j].run();
            }
        }
    }

    public double[][] getTeamResults(int team)
    {
        double[][] results = null;

        return results;
    }
}
