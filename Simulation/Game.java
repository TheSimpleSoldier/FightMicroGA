package Simulation;

public class Game
{
    private double[][] team1Inputs;
    private double[][] team2Inputs;
    private Map map;

    public Game(double[][] team1Inputs, double[][] team2Inputs)
    {
        this.team1Inputs = team1Inputs;
        this.team2Inputs = team2Inputs;
        map = new Map(team1Inputs, team2Inputs);

    }

    public void runMatch(String MapName)
    {
        map.readInMap(MapName, map);

        MockRobotPlayer[] robotPlayers;

        map.print();

        for (int i = 0; i < 200; i++)
        {
            robotPlayers = map.getRobotPlayers();

            for (int j = 0; j < robotPlayers.length; j++)
            {
                robotPlayers[j].run();
                robotPlayers[j].runTurnEnd();
            }

//            map.print();
//            System.out.println("There are: " + robotPlayers.length + " number of robots");
        }

        System.out.println("Total Red Damage Dealt: " + map.getRedSoldierDamageDealt());
        System.out.println("Total End Red Health: " + map.getRedSoldierTotalHealth());
        System.out.println("Total Blue Damage Dealt: " + map.getBlueSoldierDamageDealt());
        System.out.println("Total End Blue Health: " + map.getBlueSoldierTotalHealth());
        map.print();
    }

    public double[][] getTeamResults(int team)
    {
        double[][] results = null;

        return results;
    }
}
