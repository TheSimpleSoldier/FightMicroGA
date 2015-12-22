package Simulation;

public class Main
{
    /**
     * This is the main function which triggers simulations
     * @param args
     */
    public static void main(String[] args)
    {
        double[][] inputs = new double[][]{
                {
                        0.0,
                        1.1
                },
                {
                        2.0,
                        3.0
                }
        };

        runFightSimulation(inputs, inputs);
    }

    /**
     * This function returns an array with the total fitness values of all units
     * for both teams in the form
     *
     * [
     *      Team 1: [
     *                  Soldiers: [
     *
     *                             ]
     *                   Tanks: [
     *
     *                              ]
     *                        etc...
     *              ]
     *
     *      Team 2: [
     *
     *              ]
     * ]
     *
     *
     * @param team1Inputs
     * @param team2Inputs
     * @return
     */
    public static double[][][] runFightSimulation(double[][] team1Inputs, double[][] team2Inputs)
    {
        Game game = new Game(team1Inputs, team2Inputs);

        game.runMatch("Simulation/simulationMaps/onetower.xml");

        double[][][] results = new double[2][][];

        results[0] = game.getTeamResults(0);
        results[1] = game.getTeamResults(1);

        return results;
    }
}
