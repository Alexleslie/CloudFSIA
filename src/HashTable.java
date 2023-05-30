import DataStructure.MetaTagD;

import java.util.Arrays;

public class HashTable {
    public static boolean validateTag(byte[] root, MetaTagD metaTagD){
        byte[][] authPath = metaTagD.getAuthenticationPath();
        byte[] Hash = HashLib.h1(metaTagD);

        if(!Arrays.equals(Hash, authPath[0]) && !Arrays.equals(Hash, authPath[1])){
            System.out.println("The hash of Tag is wrong");
            return false;
        }

        if(HashTree.verify(authPath, root) == false){
            System.out.println("The auth hash path is wrong");
            return false;
        }
        return true;

    }

}
