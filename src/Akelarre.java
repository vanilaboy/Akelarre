/**
 * Created by root on 21.11.17 with love.
 */
public class Akelarre {

    public int num_rounds;

    private static final int last7Bits = 0x0000007F;

    private KeyExtension scheduler;

    public boolean doRotate = true;

    private int[] Z;

    private int[] D;

    public Akelarre(int num_rounds,int key_size,int[] key){
        this.num_rounds = num_rounds;
        scheduler = new KeyExtension(num_rounds,key_size);
        Z = scheduler.schedule(key);
        D = scheduler.createDecryptionSubkeys(Z);
    }

    public int[] encrypt( int[] plaintext ){
        if( plaintext.length != 4 ){
            throw new IllegalArgumentException();
        }
        return encrypt(plaintext.clone(),Z);
    }

    public int[] decrypt( int[] cipherText ){
        return encrypt(cipherText.clone(),D);
    }

    public int[] encrypt( int[] a, int[] k){


        //int[] a = plaintext;

        //Initialization
        a[0] += k[0];
        a[1] ^= k[1];
        a[2] ^= k[2];
        a[3] += k[3];

        int P1, P2, t0, t1;
        //Begin Rounds
        for( int r=0; r < num_rounds; r++){

            if( doRotate )
                rotl128(a,  k[13*r+4] & last7Bits);

            P1 = a[0] ^ a[2]; //W0
            P2 = a[1] ^ a[3]; //W1

            t1 = rotl31(P1,P2&0x1f);
            t1+= k[13*r+5];
            t1 = rotl1(t1,(P1>>>5)&0x1f);
            t1+= k[13*r+6];
            t1 = rotl31(t1,(P1>>>10) &0x1f);
            t1+= k[13*r+7];
            t1 = rotl1(t1,(P1>>>15) &0x1f);
            t1+= k[13*r+8];
            t1 = rotl31(t1,(P1>>>20)&0xf);
            t1+= k[13*r+9];
            t1 = rotl1(t1,(P1>>>24)&0xf);
            t1+= k[13*r+10];

            t0 = rotl1(t1,P1&0x1f);
            t0+= k[13*r+11];
            t0 = rotl31(t0,(P1>>>5)&0x1f);
            t0+= k[13*r+12];
            t0 = rotl1(t0,(P1>>>10)&0x1f);
            t0+= k[13*r+13];
            t0 = rotl31(t0,(P1>>>15)&0x1f);
            t0+= k[13*r+14];
            t0 = rotl1(t0,(P1>>>20)&0xf);
            t0+= k[13*r+15];
            t0 = rotl31(t0,(P1>>>24)&0xf);
            t0+= k[13*r+16];


            a[0] ^=t1;
            a[2] ^=t1;
            a[1] ^=t0;
            a[3] ^=t0;

        }

        if( doRotate )
            rotl128(a,  k[13*num_rounds+4] & last7Bits);

        a[0] = a[0]+k[13*num_rounds+5];
        a[1] = a[1]^k[13*num_rounds+6];
        a[2] = a[2]^k[13*num_rounds+7];
        a[3] = a[3]+k[13*num_rounds+8];

        return a;
    }

    public static int rotl31( int x, int y )
    {
        int bit = x & 0x1;
        x &= 0xfffffffe;
        return ((x<<y) | (x>>>(31-y)))|bit;
    }

    public static int rotl1( int x, int y )
    {
        int bit = x & 0x80000000;
        x &= 0x7fffffff;
        return ((x<<y) | (x>>>(31-y)))|bit;
    }

    public static void rotl128(int[] input, int amount){
        int shiftAmount = amount % 128;
        int overflow = 0;

        while( amount > 0 ){
            shiftAmount = amount;
            if( shiftAmount > 31 ){
                shiftAmount = 31;
            }

            overflow = (input[0] >>> (32-shiftAmount));
            for( int i=0; i < input.length-1; i++){
                input[i] = (input[i]<<shiftAmount) | (input[i+1] >>> (32-shiftAmount));
            }
            input[input.length-1] = (input[input.length-1]<<shiftAmount) | overflow;

            amount -= shiftAmount;
        }
    }
}
