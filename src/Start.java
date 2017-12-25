import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by root on 21.11.17 with love.
 */
public class Start {
    public static void main(String[] args) {
        /*args = new String[4];
        args[0] = "/root/IdeaProjects/crypto_lab2/src/foodEncrypt";
        args[1] = "/root/IdeaProjects/crypto_lab2/src/foodDecrypt";
        args[2] = "decrypt";
        args[3] = "aaaabbbbccccdddd";*/

        if (args.length < 3) {
            System.out.println("Akelarre inputFile outputFile <encrypt|decrypt> [key]");
        }

        if (args.length == 3) {
            String inputFile = args[0];
            String outputFile = args[1];
            String whatDo = args[2];
            Random random = new Random(System.currentTimeMillis());
            int[] key = new int[4];
            try {
                for (int i = 0; i < 4; i++) {
                    key[i] = random.nextInt();
                }
                int[] storeKey = key;
                int[] tmpInt = new int[4];
                FileOutputStream out = new FileOutputStream("/root/IdeaProjects/crypto_lab2/out/production/crypto_lab2/keyH");
                for (int i = 0; i < 4; i++) {
                    System.out.print(key[i] + "\n");
                    for (int j = 0; j < 4; j++) {
                        byte tmp = (byte) key[i];
                        tmpInt[j] = tmp & 0xFF;
                        key[i] = key[i] >> 8;
                    }
                    for (int j = 0; j < 4; j++) {
                        out.write(tmpInt[j]);
                        System.out.print(tmpInt[j] + " ");
                    }
                }
                key = storeKey;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (whatDo.equals("encrypt")) {
                try {
                    encrypt(key, inputFile, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (whatDo.equals("decrypt")) {
                    try {
                        decrypt(key, inputFile, outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Error action. Need encrypt or decrypt");
                    return;
                }
            }
        }

        try {
            if (args.length == 4) {
                String inputFile = args[0];
                String outputFile = args[1];
                String whatDo = args[2];
                String k = args[3];
                int[] key = new int[4];
                if (k.contains("root")) {

                    FileInputStream in = new FileInputStream(k);
                    key = new int[4];
                    for (int g = 0; g < 4; g++) {
                        key[g] = 0;
                        int[] tmpMas = new int[4];
                        for (int j = 0; j < 4; j++) {
                            byte fgh = (byte) in.read();
                            tmpMas[j] = fgh & 0xFF;
                            System.out.println(tmpMas[j] + " ");
                        }
                        for(int j = 3 ; j > -1; j--) {
                            key[g] = key[g] << 8;
                            key[g] += tmpMas[j];
                        }
                        System.out.print(key[g] + "\n");
                    }

                } else {
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
                }
                if (whatDo.equals("encrypt")) {
                    try {
                        encrypt(key, inputFile, outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (whatDo.equals("decrypt")) {
                        try {
                            decrypt(key, inputFile, outputFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Error action. Need encrypt or decrypt");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encrypt(int[] key, String inputFile, String outputFile) throws IOException {
        Akelarre akelarre = new Akelarre(4, key.length * 4, key);
        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);
        int addBits = 0;
        for (int i = 0; i < in.available(); i++) {
            i = 0;
            int available = in.available();
            int[] text = new int[4];
            for (int g = 0; g < 4; g++) {
                text[g] = 0;
                for (int j = 0; j < 4; j++) {
                    text[g] = text[g] << 8;
                    int fgh = in.read();
                    if(fgh == -1) {
                        if(addBits == 0) {
                            fgh = 254;
                            addBits++;
                        } else {
                            fgh = 0;
                            addBits++;
                        }
                    }
                    text[g] += fgh;
                }
            }
            int[] result = akelarre.encrypt(text);
            boolean end = false;
            boolean skipLast = false;
            if(i >= in.available()) {
                end = true;
            }
            if(end && addBits == 1) {
                skipLast = true;
            }
            for (int j = 0; j < 4; j++) {
                for (int h = 0; h < 4; h++) {
                    byte tmp;
                    tmp = (byte) result[j];
                    int tmpInt = tmp & 0xFF;
                    result[j] = result[j] >> 8;
                    if(j == 3 && h == 3) {
                        if(!skipLast) {
                            out.write(tmpInt);
                        }
                    } else {
                        out.write(tmpInt);
                    }
                }

            }
        }
    }

    private static void decrypt(int[] key, String inputFile, String outputFile) throws IOException {
        Akelarre akelarre = new Akelarre(4, key.length * 4, key);
        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);
        int value = in.available();
        int counter = 0;
        boolean addLastBit = false;
        for (int i = 0; i < in.available(); i++) {
            i = 1;
            int[] text = new int[4];
            for (int g = 0; g < 4; g++) {
                text[g] = 0;
                int[] fgh = new int[4];
                for (int j = 0; j < 4; j++) {
                    fgh[j] = in.read();
                }
                for (int j = 3; j > -1; j--) {
                    text[g] = text[g] << 8;
//                    System.out.print(fgh[j] + " ");
                    text[g] += fgh[j];
                }
            }
            boolean end = false;
            if(i >= in.available()) {
                end = true;
            }
            int[] result = akelarre.decrypt(text);
            int[] forWrite = new int[16];
            for (int j = 0; j < 4; j++) {
                int[] mas = new int[4];
                for (int r = 3; r > -1; r--) {
                    byte tmp;
                    tmp = (byte) result[j];
                    int tmpInt = tmp & 0xFF;
                    result[j] = result[j] >> 8;
                    mas[r] = tmpInt;
                }
                for(int r = 0 ; r < 4; r++) {
                    forWrite[j * 4 + r] = mas[r];
                }
            }
            boolean doNotSkip = false;
            for(int j = 0; j < 16; j++) {
                int tmpInt = forWrite[j];
                if(end && tmpInt == 254) {
                    if(j == 15) {
                        tmpInt = 0;
                    } else {
                        for(int h = j + 1; h < 16; h++) {
                            if(forWrite[h] != 0) {
                                doNotSkip = true;
                            }
                        }
                        if (tmpInt == 254 && !doNotSkip) {
                            break;
                        } else {
                            out.write(tmpInt);
                        }
                    }
                } else {
                    out.write(tmpInt);
                }
            }
        }

    }
}
