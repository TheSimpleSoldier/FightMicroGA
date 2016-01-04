package Simulation;

import battlecode.common.Team;

public class Game
{
    private double[][] team1Inputs;
    private double[][] team2Inputs;
    private Map map;
    private boolean verbose;

    public Game(double[][] team1Inputs, double[][] team2Inputs, boolean verbose)
    {
        this.team1Inputs = team1Inputs;
        this.team2Inputs = team2Inputs;
        map = new Map(team1Inputs, team2Inputs, verbose);
        this.verbose = verbose;
    }

    public void println(String string)
    {
        if (this.verbose)
        {
            System.out.println(string);
        }
    }

    public void print(String string)
    {
        if (this.verbose)
        {
            System.out.print(string);
        }
    }

    public void runMatch(String MapName, int teamA, int teamB)
    {
        map.readInMap(MapName, map, teamA, teamB);

        MockRobotPlayer[] robotPlayers;

//        map.print();

        for (int i = 0; i < 200; i++)
        {
            robotPlayers = map.getRobotPlayers();

            for (int j = 0; j < robotPlayers.length; j++)
            {
                robotPlayers[j].run();
                robotPlayers[j].runTurnEnd();
            }

            if (robotPlayers.length == 0)
            {
                break;
            }

            if (i > 0 && i % 2 == 0)
            {
//                map.print();
            }

            if (i == 199)
            {
                for (int j = 0; j < robotPlayers.length; j++)
                {
                    if (robotPlayers[j].getRc().getTeam() == Team.A)
                    {
                        map.countRedRobot(robotPlayers[j]);
                    }
                    else
                    {
                        map.countBlueRobot(robotPlayers[j]);
                    }
                }

                break;
            }

//            map.print();
        }

        println("Total Red Damage Dealt: " + map.getRedSoldierDamageDealt());
        println("Total End Red Health: " + map.getRedSoldierTotalHealth());
        println("Total Blue Damage Dealt: " + map.getBlueSoldierDamageDealt());
        println("Total End Blue Health: " + map.getBlueSoldierTotalHealth());
//        map.print();
    }

    public double[] getTeamResults(int team)
    {
        double[] results = new double[1];

        if (team == 0)
        {
            results[0] = map.getRedSoldierDamageDealt() + 3 * map.getRedSoldierTotalHealth();
        }
        else
        {
            results[0] = map.getBlueSoldierDamageDealt() + 3 * map.getBlueSoldierTotalHealth();
        }

        return results;
    }
}
