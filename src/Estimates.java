/**
 * Created by root on 26.12.17 with love.
 */
public class Estimates {


    private int AMOUNT_BITS = 8;


    public double countCorrelation(byte[] inputData, byte[] outputData) {
        if (inputData.length != outputData.length) {
            throw new IllegalArgumentException("inputData and outputData must have equals dimensions.");
        }

        int size = inputData.length;

        int amountInputOnes = countOneBits(inputData) / (size * 8);
        int amountOutputOnes = countOneBits(outputData) / (size * 8);

        double numerator = 0.;
        double[] denominator = {0., 0.};

        int inputByte = 0;
        int outputByte = 0;

        int tempInput = 0;
        int tempOutput = 0;

        for (int i = 0; i < size; i++) {
            inputByte = inputData[i] & 0xFF;
            outputByte = outputData[i] & 0xFF;

            for (int j = 0; j < AMOUNT_BITS; j++) {
                tempInput = (inputByte & 1) - amountInputOnes;
                tempOutput = (outputByte & 1) - amountOutputOnes;

                numerator += (tempInput * tempOutput);

                denominator[0] += (tempInput * tempInput);
                denominator[1] += (tempOutput * tempOutput);

                inputByte = inputByte >>> 1;
                outputByte = outputByte >>> 1;
            }
        }

        return numerator / Math.sqrt(denominator[0] * denominator[1]);
    }


    private int countOneBits(byte[] source) {
        int result = 0;
        int sourceByte = 0;
        int counter = 0;

        for (int i = 0; i < source.length; i++) {
            sourceByte = source[i] & 0xFF;
            counter = 0;

            for (; sourceByte != 0; sourceByte = (sourceByte >>> 1)) {
                if ((sourceByte & 1) == 1) {
                    counter++;
                }
            }

            result += counter;
        }

        return result;
    }


    public static void main(String[] args) {
        String k = "aaaagyfrccccdddd";
        int[] key;
        byte[] tmp = k.getBytes();
        if (tmp.length != 16) {
            System.out.println("error key length. need 16");
        }
        key = new int[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                key[i] = key[i] << 8;
                key[i] += tmp[i * 4 + j];
            }
        }
        Akelarre akelarre = new Akelarre(4, key.length * 4, key);

        byte[] in = "qw3hF8 u0-+asxp[".getBytes();
        int[] input = new int[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                input[i] = input[i] << 8;
                input[i] += in[i * 4 + j];
            }
        }
        int[] ou = akelarre.encrypt(input);
        byte[] output = new byte[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output[i * 4 + j] = (byte) ou[i];
                ou[i] = ou[i] >> 8;
            }
        }

        Estimates rate = new Estimates();
        System.out.println(rate.countCorrelation(in, output));
    }

}
