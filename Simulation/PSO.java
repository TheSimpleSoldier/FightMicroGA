package Simulation;

public class PSO
{
    private double globalScale;
    private double localScale;
    private double randomScale;
    private FeedForwardNeuralNetwork net;

    public PSO(double globalScale, double localScale, double randomScale)
    {
        this.globalScale = globalScale;
        this.localScale = localScale;
        this.randomScale = randomScale;

        net = new FeedForwardNeuralNetwork(1, new int[]{6, 10, 5}, ActivationFunction.LOGISTIC, ActivationFunction.LOGISTIC);
    }

    public double[][] getBestWeights(int rounds, int popSize)
    {
        double[][][] currentWeights = new double[popSize][][];
        double[][][] localBestWeights = new double[popSize][][];
        double[][] globalBest = null;
        double[] localBestScores = new double[popSize];
        double globalBestScore;
        double[] currentFitness = new double[popSize];

        // initialization
        for (int i = 0; i < popSize; i++)
        {
            currentWeights[i] = new double[1][];
            localBestWeights[i] = new double[1][];

            net.generateRandomWeights();
            currentWeights[i][0] = net.getWeights();
            localBestWeights[i][0] = net.getWeights();
            localBestScores[i] = 0;
        }

        globalBestScore = 0;
        globalBest = currentWeights[0];


        for (int i = 0; i < rounds; i++)
        {
            // first update all of the particles
            for (int j = 0; j < popSize; j++)
            {
                currentWeights[j] = updateParticle(currentWeights[j], localBestWeights[j], globalBest);
            }

            // update all of the scores for global and local best
            for (int j = 0; j < popSize; j++)
            {
                for (int k = 0; k < popSize; k++)
                {
                    // run match and record scores
                    if (j != k)
                    {
                        double[][] results = Main.runFightSimulation(currentWeights[j], currentWeights[k], 0, 0, false, i);
                        currentFitness[j] += results[0][0];
                        currentFitness[k] += results[1][0];
                    }
                }
            }

            // loop over scores and update local and global best as necessary
            for (int j = 0; j < popSize; j++)
            {
                if (currentFitness[j] > localBestScores[j])
                {
                    localBestScores[j] = currentFitness[j];
                    localBestWeights[j] = currentWeights[j];
                }
                if (currentFitness[j] > globalBestScore)
                {
                    globalBestScore = currentFitness[j];
                    globalBest = currentWeights[j];
                }
            }
            System.out.println("Finished round " + i + " of PSO");
        }

        return globalBest;
    }

    public double[][] updateParticle(double[][] current, double[][] localBest, double[][] globalBest)
    {
        double[][] newWeights = new double[current.length][current[0].length];

        for (int i = 0; i < newWeights.length; i++)
        {
            for (int j = 0; j < newWeights[i].length; j++)
            {
                newWeights[i][j] = current[i][j] + (((2 * Math.random() * this.randomScale) - this.randomScale) + this.localScale * localBest[i][j] + this.globalScale * globalBest[i][j]);
            }
        }

        return newWeights;
    }

}
