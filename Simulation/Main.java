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

        //runFightSimulation(inputs, inputs);

        double[][] idealWeights = getIdealWeights(10, 10, mutationRate, crossOverRate, mutationAmount);

        System.out.println();

        for (int i = 0; i < idealWeights.length; i++)
        {
            for (int j = 0; j < idealWeights[i].length; j++)
            {
                System.out.print(idealWeights[i][j] + ", ");
            }
            System.out.println();
        }

        verbose = true;

        for (int i = 0; i < 10; i++)
        {
            System.out.println("Net is Red:");
            runFightSimulation(idealWeights, idealWeights, 0, 1, verbose);
            System.out.println("Net is blue");
            runFightSimulation(idealWeights, idealWeights, 1, 0, verbose);
        }
    }

    public static double[][] getIdealWeights(int popSize, int rounds, double mutationRate, double crossOverRate, double mutationAmount)
    {
        FeedForwardNeuralNetwork net = new FeedForwardNeuralNetwork(1, new int[]{18, 25, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);

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
                        double[][] results = runFightSimulation(population[j], population[k], 0, 0, false);
                        totalFitness[j] += results[0][0];
                        totalFitness[k] += results[1][0];
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
            for (int j = i; j < fitness.length; j++)
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
    public static double[][] runFightSimulation(double[][] team1Inputs, double[][] team2Inputs, int teamA, int teamB, boolean verbose)
    {
        if (verbose)
        {
            System.out.println("Simulating a match");
        }

//        System.out.println("team1Inputs.length: " + team1Inputs.length);
//        System.out.println("team2Inputs.length: " + team2Inputs.length);

        Game game = new Game(team1Inputs, team2Inputs, verbose);

        game.runMatch("FightMicroGA/Simulation/simulationMaps/onetower.xml", teamA, teamB);

        double[][] results = new double[2][];

        results[0] = game.getTeamResults(0);
        results[1] = game.getTeamResults(1);

        return results;
    }
}
