package Simulation;

/**
 * inputs: 6
 * middle: 10
 * output: 5
 */
public class MinFeedForwardNeuralNetwork
{
    private double[] weights;

    public MinFeedForwardNeuralNetwork(double[] weights)
    {
        this.weights = weights;
    }

    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }

    public double[] compute(double[] input)
    {
        double[] weights = this.weights;
        double[] middle = {0,0,0,0,0,0,0,0,0,0};
        double[] output = {0,0,0,0,0};

        if(input[0] * weights[0] + input[1] * weights[6] + input[2] * weights[12] + input[3] * weights[18] + input[4] * weights[24] + input[5] * weights[30] > .5)
        {
            middle[0] = 1;
        }

        if(input[0] * weights[1] + input[1] * weights[7] + input[2] * weights[13] + input[3] * weights[19] + input[4] * weights[25] + input[5] * weights[31] > .5)
        {
            middle[1] = 1;
        }

        if(input[0] * weights[2] + input[1] * weights[8] + input[2] * weights[14] + input[3] * weights[20] + input[4] * weights[26] + input[5] * weights[32] > .5)
        {
            middle[2] = 1;
        }

        if(input[0] * weights[3] + input[1] * weights[9] + input[2] * weights[15] + input[3] * weights[21] + input[4] * weights[27] + input[5] * weights[33] > .5)
        {
            middle[3] = 1;
        }

        if(input[0] * weights[4] + input[1] * weights[10] + input[2] * weights[16] + input[3] * weights[22] + input[4] * weights[28] + input[5] * weights[34] > .5)
        {
            middle[4] = 1;
        }

        if(input[0] * weights[5] + input[1] * weights[11] + input[2] * weights[17] + input[3] * weights[23] + input[4] * weights[29] + input[5] * weights[35] > .5)
        {
            middle[5] = 1;
        }

        if(input[0] * weights[6] + input[1] * weights[12] + input[2] * weights[18] + input[3] * weights[24] + input[4] * weights[30] + input[5] * weights[36] > .5)
        {
            middle[6] = 1;
        }

        if(input[0] * weights[7] + input[1] * weights[13] + input[2] * weights[19] + input[3] * weights[25] + input[4] * weights[31] + input[5] * weights[37] > .5)
        {
            middle[7] = 1;
        }

        if(input[0] * weights[8] + input[1] * weights[14] + input[2] * weights[20] + input[3] * weights[26] + input[4] * weights[32] + input[5] * weights[38] > .5)
        {
            middle[8] = 1;
        }

        if(input[0] * weights[9] + input[1] * weights[15] + input[2] * weights[21] + input[3] * weights[27] + input[4] * weights[33] + input[5] * weights[39] > .5)
        {
            middle[9] = 1;
        }

        if(middle[0] * weights[60] + middle[1] * weights[70] + middle[2] * weights[80] + middle[3] * weights[90] + middle[4] * weights[100] + middle[5] * weights[110] + middle[6] * weights[120] + middle[7] * weights[130] + middle[8] * weights[140] + middle[9] * weights[150] > .5)
        {
            output[0] = 1;
        }

        if(middle[0] * weights[61] + middle[1] * weights[71] + middle[2] * weights[81] + middle[3] * weights[91] + middle[4] * weights[101] + middle[5] * weights[111] + middle[6] * weights[121] + middle[7] * weights[131] + middle[8] * weights[141] + middle[9] * weights[151] > .5)
        {
            output[1] = 1;
        }

        if(middle[0] * weights[62] + middle[1] * weights[72] + middle[2] * weights[82] + middle[3] * weights[92] + middle[4] * weights[102] + middle[5] * weights[112] + middle[6] * weights[122] + middle[7] * weights[132] + middle[8] * weights[142] + middle[9] * weights[152] > .5)
        {
            output[2] = 1;
        }

        if(middle[0] * weights[63] + middle[1] * weights[73] + middle[2] * weights[83] + middle[3] * weights[93] + middle[4] * weights[103] + middle[5] * weights[113] + middle[6] * weights[123] + middle[7] * weights[133] + middle[8] * weights[143] + middle[9] * weights[153] > .5)
        {
            output[3] = 1;
        }

        if(middle[0] * weights[64] + middle[1] * weights[74] + middle[2] * weights[84] + middle[3] * weights[94] + middle[4] * weights[104] + middle[5] * weights[114] + middle[6] * weights[124] + middle[7] * weights[134] + middle[8] * weights[144] + middle[9] * weights[154] > .5)
        {
            output[4] = 1;
        }

        return output;
    }
}
