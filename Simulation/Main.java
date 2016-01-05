package Simulation;

public class Main
{
    /**
     * This is the main function which triggers simulations
     * @param args
     */
    public static void main(String[] args)
    {
        double mutationRate = 0.3;
        double crossOverRate = 0.1;
        double mutationAmount = 0.1;
        boolean verbose = false;
        double globalScale = 0.1;
        double localScale = 0.2;
        double randomScale = 0.3;


        //runFightSimulation(inputs, inputs);

//        System.out.println("Basic vs. Advanced");
//        runFightSimulation(null, null, 1, 2, true, 2);
//        System.out.println("Advanced vs. Basic");
//        runFightSimulation(null, null, 2, 1, true, 2);

        double[][] idealWeights = getIdealWeights(10, 1, mutationRate, crossOverRate, mutationAmount);

        PSO pso = new PSO(globalScale, localScale, randomScale);
        double[][] idealWeights2 = pso.getBestWeights(200, 10);

        long startTime = System.currentTimeMillis();

        System.out.println();

        for (int i = 0; i < idealWeights2.length; i++)
        {
            for (int j = 0; j < idealWeights2[i].length; j++)
            {
                System.out.print(idealWeights2[i][j] + ", ");
            }
            System.out.println();
        }

        verbose = true;

        for (int i = 0; i < 2; i++)
        {
            System.out.println("Net is Red:");
            runFightSimulation(idealWeights, idealWeights2, 0, 0, verbose, 1);
            System.out.println("Net is blue");
            runFightSimulation(idealWeights2, idealWeights, 0, 0, verbose, 1);
            System.out.println("Net vs. basic");
            runFightSimulation(idealWeights, idealWeights, 0, 1, verbose, 1);
            System.out.println("basic vs. Net");
            runFightSimulation(idealWeights, idealWeights, 1, 0, verbose, 1);
            System.out.println("PSO vs. basic");
            runFightSimulation(idealWeights2, idealWeights2, 0, 1, verbose, 1);
            System.out.println("basic vs. PSO");
            runFightSimulation(idealWeights2, idealWeights2, 1, 0, verbose, 1);
        }

        System.out.println("Run Time: " + (System.currentTimeMillis() - startTime));
    }

    public static double[][] getIdealWeights(int popSize, int rounds, double mutationRate, double crossOverRate, double mutationAmount)
    {
        FeedForwardNeuralNetwork net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 4}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);

        double[][][] population = new double[popSize][][];

        for (int i = 0; i < popSize; i++)
        {
            net.generateRandomWeights();
            population[i] = new double[1][];
            population[i][0] = net.getWeights();
//            System.out.println("net length:" + net.getWeights().length);
        }


        for (int i = 0; i < rounds; i++)
        {
            population = runTheGA(population, mutationRate, crossOverRate, mutationAmount);

            double[] totalFitness = new double[popSize];

            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < popSize; k++)
                {
                    if (j != k)
                    {
                        double[][] results = runFightSimulation(population[j], population[k], 0, 0, false, i);
                        totalFitness[j] += results[0][0];
                        totalFitness[k] += results[1][0];

//                        System.out.println("Score for Red: " + results[0][0]);
//                        System.out.println("Score for Blue: " + results[1][0]);
                    }
                }
            }

            population = sortPopulation(population, totalFitness);


            for (int j = 0; j < totalFitness.length; j++)
            {
                totalFitness[j] = 0;
            }

            System.out.println("Finished round: " + i + " of the GA");
        }

        return population[0];
    }

    /**
     * This method sorts the population based on their fitness values
     *
     * @param initialPop
     * @param fitness
     * @return
     */
    public static double[][][] sortPopulation(double[][][] initialPop, double[] fitness)
    {
        double[][][] sortedPop = new double[initialPop.length][][];

        for (int i = 0; i < sortedPop.length; i++)
        {
            sortedPop[i] = initialPop[i];
        }

        // bubble sort FTW!!!!
        for (int i = 0; i < fitness.length; i++)
        {
            for (int j = i+1; j < fitness.length; j++)
            {
                if (fitness[j] > fitness[i])
                {
                    double temp = fitness[j];
                    fitness[j] = fitness[i];
                    fitness[i] = temp;
                    double[][] temp2 = sortedPop[j];
                    sortedPop[j] = sortedPop[i];
                    sortedPop[i] = temp2;
                }
            }
        }

        return sortedPop;
    }

    /**
     * This method takes an initial population and runs the GA to evolve a superior poputation
     *
     * Note: this method assumes that the initial population has been sorted from best to worst
     *
     * @param initialPop
     * @param mutationRate
     * @param crossOverRate
     * @param mutationAmount
     * @return
     */
    public static double[][][] runTheGA(double[][][] initialPop, double mutationRate, double crossOverRate, double mutationAmount)
    {
        double[][][] evolvedPop = new double[initialPop.length][][];
        int currentIndex = 0;
        int len = initialPop.length;

        for (int i = 0; i < len; i++)
        {
            double[][] selected = null;

            while (selected == null)
            {
                if (Math.random() < (((double) ((len + 1) - currentIndex) / (len + 2)) / 2))
                {
                    selected = initialPop[currentIndex];
                }

                currentIndex = (currentIndex + 1) % len;
            }

            // Cross Over
            if (Math.random() < crossOverRate)
            {
                double[][] selected2 = null;

                while (selected2 == null)
                {
                    if (Math.random() < (((double) ((len + 1) - currentIndex) / (len + 2)) / 2))
                    {
                        selected2 = initialPop[currentIndex];
                    }

                    currentIndex = (currentIndex + 1) % len;
                }

                for (int j = 0; j < selected.length; j++)
                {
                    int crossOverPoint = (int) (Math.random() * selected[j].length);
                    for (int k = crossOverPoint; k < selected[j].length; k++)
                    {
                        selected[j] = selected2[j];
                    }

                }
            }

            // Mutation
            for (int j = 0; j < selected.length; j++)
            {
                for (int k = 0; k < selected[j].length; k++)
                {
                    if (Math.random() < mutationRate)
                    {
                        selected[j][k] += (Math.random() * 2 * mutationAmount) - mutationAmount;
                    }
                }
            }

            evolvedPop[i] = selected;
        }

        return evolvedPop;
    }

    /**
     * This method returns an array with the total fitness values of all units
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
    public static double[][] runFightSimulation(double[][] team1Inputs, double[][] team2Inputs, int teamA, int teamB, boolean verbose, int round)
    {
        if (verbose)
        {
            System.out.println("Simulating a match");
        }

        Game game = new Game(team1Inputs, team2Inputs, verbose);

        String map = "FightMicroGA/Simulation/simulationMaps/onetower.xml";

        if (round % 4 == 1)
        {
            map = "FightMicroGA/Simulation/simulationMaps/barren.xml";
        }
        else if (round % 4 == 2)
        {
            map = "FightMicroGA/Simulation/simulationMaps/frontlines.xml";
        }
//        else if (round % 4 == 3)
//        {
//            map = "FightMicroGA/Simulation/simulationMaps/noeffort.xml";
//        }

        game.runMatch(map, teamA, teamB);

        double[][] results = new double[2][];

        results[0] = game.getTeamResults(0);
        results[1] = game.getTeamResults(1);

        return results;
    }
}
