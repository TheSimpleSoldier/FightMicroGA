package Simulation;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Random;

/**
 * Created by joshua on 9/22/15.
 * This class creates a fully connected feedforward neural network
 * with a variable number of hidden layers.
 * It can be imported and exported through the JSON format to make
 * saving the net much easier. There is also a regular constructor
 * which will create the net with random weights.
 * The net can currently use 2 different activation functions,
 * a linear function with a slope of 1 and the logistic function.
 * Each node is connected to a bias node which has a value of 1.
 */
public class FeedForwardNeuralNetwork
{
    //properties of the net
    private double[] weights;
    private int hiddenLayers;
    private int[] sizes;
    private int biggestSize;
    private ActivationFunction hiddenActivationFunction, outputActivationFunction;
    private double biasNum = 1.;
    private double linearSlope = 1.;

    /**
     * Creates the net from a JSON file
     * @param file The file containing the JSON
     * @throws IOException
     */
    public FeedForwardNeuralNetwork(File file) throws IOException
    {
        this(getJSONFromFile(file));
    }

    /**
     * Creates net from string containing JSON
     * @param net the string containing the JSON
     */
    public FeedForwardNeuralNetwork(JSONObject net)
    {
        hiddenLayers = net.getInt("hiddenLayers");

        String activation = net.getString("hiddenActivationFunction");
        if(activation.equals("LINEAR"))
        {
            hiddenActivationFunction = ActivationFunction.LINEAR;
        }
        else if(activation.equals("LOGISTIC"))
        {
            hiddenActivationFunction = ActivationFunction.LOGISTIC;
        }
        else
        {
            System.out.println("Illegal activation function.");
        }

        activation = net.getString("outputActivationFunction");
        if(activation.equals("LINEAR"))
        {
            outputActivationFunction = ActivationFunction.LINEAR;
        }
        else if(activation.equals("LOGISTIC"))
        {
            outputActivationFunction = ActivationFunction.LOGISTIC;
        }
        else
        {
            System.out.println("Illegal activation function.");
        }

        JSONArray sizeArray = net.getJSONArray("sizes");
        if(sizeArray.length() == hiddenLayers + 2)
        {
            sizes = new int[hiddenLayers + 2];
            biggestSize = 0;
            for(int k = 0; k < hiddenLayers + 2; k++)
            {
                int size = sizeArray.getInt(k);
                sizes[k] = size;
                if(size > biggestSize)
                {
                    biggestSize = size;
                }
            }
        }
        else
        {
            System.out.println("Illegal number of layer sizes.");
        }

        if(net.has("weights"))
        {
            int totalWeights = 0;
            for(int k = 0; k < sizes.length - 1; k++)
            {
                totalWeights += sizes[k] * sizes[k + 1];
                totalWeights += sizes[k];
            }
            totalWeights += sizes[sizes.length - 1];
            JSONArray weightArray = net.getJSONArray("weights");
            if(weightArray.length() == totalWeights)
            {
                weights = new double[totalWeights];
                for(int k = 0; k < totalWeights; k++)
                {
                    weights[k] = weightArray.getDouble(k);
                }
            }
            else
            {
                System.out.println("Illegal number of weights");
            }
        }
        else
        {
            generateRandomWeights();
        }

        biasNum = net.getDouble("biasNum");
        linearSlope = net.getDouble("linearSlope");
    }

    /**
     * Initialize a new net with the following parameters and random weights
     * @param hiddenLayers Number of hidden layers
     * @param sizes Size of each layer, starting with the input and ending with the output
     * @param hiddenActivationFunction Activation function to use for hidden nodes
     * @param outputActivationFunction Activation function to use for the ouput layer
     */
    public FeedForwardNeuralNetwork(int hiddenLayers, int[] sizes, ActivationFunction hiddenActivationFunction, ActivationFunction outputActivationFunction)
    {
        this.hiddenLayers = hiddenLayers;
        this.sizes = sizes;
        this.hiddenActivationFunction = hiddenActivationFunction;
        this.outputActivationFunction = outputActivationFunction;

        biggestSize = 0;
        for(int k = 0; k < sizes.length; k++)
        {
            if(sizes[k] > biggestSize)
            {
                biggestSize = sizes[k];
            }
        }

        generateRandomWeights();
    }

    public FeedForwardNeuralNetwork(int hiddenLayers, int[] sizes, ActivationFunction hiddenActivationFunction, ActivationFunction outputActivationFunction, double biasNum, double linearSlope)
    {
        this(hiddenLayers, sizes, hiddenActivationFunction, outputActivationFunction);

        this.biasNum = biasNum;
        this.linearSlope = linearSlope;
    }

    /**
     * Fills all the weights with random numbers between -1 and 1
     */
    public void generateRandomWeights()
    {
        int lowest = -1;
        int highest = 1;

        int totalWeights = 0;
        for(int k = 0; k < sizes.length - 1; k++)
        {
            totalWeights += sizes[k] * sizes[k + 1];
            totalWeights += sizes[k];
        }
        totalWeights += sizes[sizes.length - 1];

        Random rand = new Random();
        weights = new double[totalWeights];

        for(int k = 0; k < totalWeights; k++)
        {
            weights[k] = lowest + ((highest - lowest) * rand.nextDouble());
        }
    }

    /**
     * Puts JSON file contents into a string
     * @param file File containing JSON
     * @return String containing JSON
     * @throws IOException
     */
    private static JSONObject getJSONFromFile(File file) throws IOException
    {
        String input = FileUtils.readFileToString(file);
        return new JSONObject(input);
    }

    /**
     * Turns the net into a JSON string
     * @return The JSON string
     */
    public JSONObject export()
    {
        JSONObject net = new JSONObject();
        net.put("hiddenLayers", hiddenLayers);
        net.put("hiddenActivationFunction", hiddenActivationFunction.name());
        net.put("outputActivationFunction", outputActivationFunction.name());
        net.put("sizes", new JSONArray(sizes));
        net.put("weights", new JSONArray(weights));
        net.put("biasNum", biasNum);
        net.put("linearSlope", linearSlope);

        return net;
    }

    /**
     * Turns the net into a JSON string and saves it to a file
     * @param file File to save JSON to
     * @throws IOException
     */
    public void export(File file) throws IOException
    {
        JSONObject net = export();

        BufferedWriter out = new BufferedWriter(new PrintWriter(new FileWriter(file)));
        out.write(net.toString(2));
        out.close();
    }

    /**
     * Compute output nodes based on input
     * @param inputs Values for input layer
     * @return Values of the output layer
     */
    public double[] compute(double[] inputs)
    {
        //if input wrong size, return
        if(inputs.length != sizes[0])
        {
            System.out.println("Invalid number of inputs");
            return null;
        }

        //fill out first layer to temp output
        int lastLayer = sizes[0];
        double[] layerOut = new double[biggestSize];
        for(int k = 0; k < lastLayer; k++)
        {
            layerOut[k] = inputs[k];
        }

        //for each layer after first
        for(int k = 1; k < hiddenLayers + 2; k++)
        {
            //for each node in that layer
            double[] tempOut = new double[sizes[k]];
            for(int a = 0; a < sizes[k]; a++)
            {
                //get sum and apply activation function
                double sum = 0;
                for(int t = 0; t < lastLayer; t++)
                {
                    sum += layerOut[t] * getWeight(k - 1, t, k, a);
                }
                sum += biasNum * getWeight(-1, 0, k, a);
                if(k != hiddenLayers + 1)
                {
                    tempOut[a] = applyActivationFunction(sum, hiddenActivationFunction);
                }
                else
                {
                    tempOut[a] = applyActivationFunction(sum, outputActivationFunction);
                }
            }
            lastLayer = sizes[k];
            //fill out return
            for(int a = 0; a < lastLayer; a++)
            {
                layerOut[a] = tempOut[a];
            }
        }

        double[] toReturn = new double[sizes[sizes.length - 1]];
        for(int k = 0; k < toReturn.length; k++)
        {
            toReturn[k] = layerOut[k];
        }

        return toReturn;
    }

    /**
     * Gets weight between 2 nodes
     * @param layerStart Starting layer, -1 for bias node
     * @param start Node number in the starting layer
     * @param layerEnd Ending layer, should be one more than the start layer
     * @param end Node number in the ending layer
     * @return The value of the weight
     */
    public double getWeight(int layerStart, int start, int layerEnd, int end)
    {
        int index = getIndex(layerStart, start, layerEnd, end);
        return weights[index];
    }

    /**
     * Sets a new weight between 2 nodes
     * @param layerStart Starting layer, -1 for bias node
     * @param start Node number in the starting layer
     * @param layerEnd Ending layer, should be one more than the start layer
     * @param end Node number in the ending layer
     * @param newWeight New weight between the nodes
     */
    public void setWeight(int layerStart, int start, int layerEnd, int end, double newWeight)
    {
        int index = getIndex(layerStart, start, layerEnd, end);
        weights[index] = newWeight;
    }

    /**
     * Gets the index of the 1-d array that represents the weight between the 2 nodes
     * @param layerStart Starting layer, -1 for bias node
     * @param start Node number in the starting layer
     * @param layerEnd Ending layer, should be one more than the start layer
     * @param end Node number in the ending layer
     * @return The index where the weight is
     */
    public int getIndex(int layerStart, int start, int layerEnd, int end)
    {
        if(layerStart != -1)
        {
            int index = 0;
            for(int k = 0; k < layerStart; k++)
            {
                index += sizes[k] * sizes[k + 1];
            }

            index += start * sizes[layerEnd];
            index += end;

            return index;
        }
        else
        {
            int index = 0;
            for(int k = 0; k < hiddenLayers + 1; k++)
            {
                index += sizes[k] * sizes[k + 1];
            }

            for(int k = 0; k < layerEnd; k++)
            {
                index += sizes[k];
            }

            index += end;

            return index;
        }
    }

    /**
     * Applies the relevant activation function to the sum of a node
     * @param sum The value to plug into the function
     * @return The value returned by applying the function to the value
     */
    public double applyActivationFunction(double sum, ActivationFunction activationFunction)
    {
        switch(activationFunction)
        {
            case LINEAR:
                return linearSlope * sum;
            case LOGISTIC:
                return 1.0 / (1.0 + Math.pow(Math.E, sum * -1.0));
            case STEP:
                if(sum < .5)
                {
                    return 0;
                }
                return 1;
        }

        System.out.println("Failed to apply activation function");
        return -9999;
    }

    /**
     * similar to applyActivationFunction, but applies the derivative for training
     * @param sum the value to plug into the function
     * @return The value returned by applying the function to the value
     */
    public double applyActivationFunctionDerivative(double sum, ActivationFunction activationFunction)
    {
        switch(activationFunction)
        {
            case LINEAR:
                return linearSlope;
            case LOGISTIC:
                return (Math.pow(Math.E, sum) / Math.pow(Math.pow(Math.E, sum) + 1, 2));
            case STEP:
                if(sum < .5)
                {
                    return 0;
                }
                return 1;
        }

        System.out.println("Failed to apply activation function");
        return -9999;
    }

    /**
     * Gets an array of all the for the net
     * @return double array of weights
     */
    public double[] getWeights()
    {
        return weights;
    }

    /**
     * Gets the array of sizes of each layer
     * @return double array with layer sizes with input being at index 0
     */
    public int[] getSizes()
    {
        return sizes;
    }

    /**
     * Gets the magnitude of the bias value
     * @return a double that is the bias value
     */
    public double getBiasNum()
    {
        return biasNum;
    }

    /**
     * Gets the activation function used by hidden layers
     * @return an ActivationFunction enum
     */
    public ActivationFunction getHiddenActivationFunction()
    {
        return hiddenActivationFunction;
    }

    /**
     * Gets the activation function used by output layer
     * @return an ActivationFunction enum
     */
    public ActivationFunction getOutputActivationFunction()
    {
        return outputActivationFunction;
    }

    /**
     * sets the weights for the net
     * @param weights array of doubles that has weight values
     */
    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }
}
