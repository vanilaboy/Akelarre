/**
 * Created by root on 21.11.17 with love.
 */
public class KeyExtension {
    private final int NUM_ROUNDS;
    public final int KEY_SIZE;
    private final int A0 = 0xA49ED284;
    private final int A1 = 0x735203DE;

    public KeyExtension (int num_rounds,int key_size){
        NUM_ROUNDS = num_rounds;
        KEY_SIZE = key_size;
    }

    public int[] schedule(int[] key){
        int[] K = new int[13*NUM_ROUNDS+9];

        if( key.length != KEY_SIZE/4){
            throw new IllegalArgumentException("Key provided of improper length.");
        }

        int[] s = new int[key.length*2];
        int[] u = new int[key.length*2];
        int[] v = new int[key.length*2];
        for(int i=0;i<s.length;i+=2){
            s[i]     = key[i/2] >>> 16;
            s[i+1]   = key[i/2] & 0xFFFF;

            u[i]     = (s[i]*s[i]+A0);
            v[i]     = (s[i]*s[i]+A1);
            u[i+1]   = (s[i+1]*s[i+1]+A0);
            v[i+1]   = (s[i+1]*s[i+1]+A1);
        }

        for(int i=0;i<13*NUM_ROUNDS+9;i++){
            int index = i % s.length;
            int um = (u[index] >> 8) & 0xFFFF;
            int vm = (v[index] >> 8) & 0xFFFF;
            K[i] = ((u[index] << 24) & 0xFF000000) | ((u[index] >> 8) & 0xFF0000)  | ((v[index] << 8) & 0xFF00) | ((v[index] >> 24) & 0xFF);
            u[index] = um*um+A0;
            v[index] = vm*vm+A1;
        }
        return K;
    }

    public static int neg(int A){
        int x = A&0x7f;
        int y = (-(x % 128));
        return (A & 0xFFFFFF80) | (y&0x7f);
    }

    public int[] createDecryptionSubkeys(int[] Z){
        int[] D = new int[13*NUM_ROUNDS+9];
        D[0] = -Z[13*NUM_ROUNDS+5];
        D[1] =  Z[13*NUM_ROUNDS+6];
        D[2] =  Z[13*NUM_ROUNDS+7];
        D[3] = -Z[13*NUM_ROUNDS+8];

        for(int r=0;r<=NUM_ROUNDS-1;r++){
            D[13*r+4] = neg(Z[13*(NUM_ROUNDS-r)+4]);
            for( int j=5;j<=16;j++ ){
                D[13*r+j] = Z[13*(NUM_ROUNDS-r-1)+j];
            }
        }

        D[13*NUM_ROUNDS+4] = neg(Z[4]);

        D[13*NUM_ROUNDS+5] = -Z[0];
        D[13*NUM_ROUNDS+6] =  Z[1];
        D[13*NUM_ROUNDS+7] =  Z[2];
        D[13*NUM_ROUNDS+8] = -Z[3];
        return D;
    }
}
