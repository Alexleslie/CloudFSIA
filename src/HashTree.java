import DataStructure.HashTreeD;
import DataStructure.MetaTagD;

import java.security.MessageDigest;
import java.util.Arrays;

public class HashTree {
    public static byte[][] buildBottomHash(int treeHeight, MetaTagD[] leafs){
        int treeSize = 2 * (int) Math.pow(2, treeHeight) - 1 ;
        byte[][] ht = new byte[treeSize][];  // ht = [index][hash]
        int first = (int) Math.pow(2, treeHeight) - 1;  // the first leaf index in hash tree

        for(int i = first; i < treeSize; i++){
            ht[i] = HashLib.h1(leafs[i-first]);
        }
        return ht;
    }

    public static MessageDigest generateMD(){
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            System.out.println("get SHA-256 instance error");
            System.out.println(e);
        }
        return md;
    }

    public static boolean verify(byte[][] authPath, byte[] root){
        MessageDigest md = generateMD();
        byte[] tempHash = null;
        int height = (authPath.length - 1) / 2;

        for (int i = 0; i < height  - 1 ; i++) {  // from bottom to (top-1) like 0-1>2 2-3>4
            md = generateMD();
            md.update(authPath[2 * i]);
            tempHash = md.digest(authPath[2 * i + 1]);

            if(!Arrays.equals(tempHash, authPath[2 * (i + 1)])
                    && !Arrays.equals(tempHash, authPath[2 * (i + 1) + 1])){
                System.out.format("AuthPath validate error %d \n", i);
                return false;
            }
        }

        md.update(authPath[2 * height - 2]); // from 4-5>6

        tempHash = md.digest(authPath[2 * height - 1]);

        if (!Arrays.equals(tempHash, authPath[2 * height])){
            System.out.println("AuthPath error");
            return false;
        }

        if (!Arrays.equals(root, authPath[2 * height])){
            System.out.println("Auth Root Error");
            return false;
        }
        return true;
    }

    public static byte[][] buildFromLeaf(int treeHeight, MetaTagD[] metaTagDS){
        MessageDigest md = generateMD();
        byte[][] ht = buildBottomHash(treeHeight, metaTagDS);

        for(int height = treeHeight - 1; height >= 0; height--){ // Root is 0,
            int first = (int) Math.pow(2, height) - 1;
            int end = first + (int) Math.pow(2, height);

            for (int i = first; i < end; i++) {
                md.update(ht[2 * i + 1]);
                ht[i] = md.digest(ht[2 * i + 2]);
            }
        }
        return ht;
    }

    public static void update(HashTreeD hashTreeD, int position, MetaTagD tag) {

        hashTreeD.setLeafSingle(position, tag); // a bug found later

        int first = (int) Math.pow(2, hashTreeD.getTreeHeight()) - 1;
        position = position + first; // find the node position of modified tag
        hashTreeD.getHt()[position] = HashLib.h1(tag);

        for (int height = hashTreeD.getTreeHeight(); height >= 1; height--) {
            if (position % 2 == 0)
                position = position -1;
            MessageDigest md = generateMD();
            md.update(hashTreeD.getHt()[position]);
            hashTreeD.setHtSingle((position - 1)/2, md.digest(hashTreeD.getHt()[position + 1]));
            position = (position - 1)/2;
        }
    }

    public static byte[][] getAuthPath(int index, HashTreeD hashTreeD){  
        int treeHeight = hashTreeD.getTreeHeight();

        byte[][] ht = hashTreeD.getHt();
        byte[][] result = new byte[2 * treeHeight + 1][];

        if (index % 2 == 0)
            index = (int) Math.pow(2, treeHeight) - 1 + index;
        else
            index = (int) Math.pow(2, treeHeight) - 1 + index - 1;

        for (int i = 0; i <= 2 * treeHeight - 2; i = i + 2) // note that i = i + 2
        {
            result[i] = ht[index];
            result[i + 1] = ht[index + 1];
            index = (index - 1) / 2;

            if (index % 2 == 0)
                index = index - 1;// pay attention; this bug is discovered later
        }

        result[2 * treeHeight] = ht[0]; // pay attention; this bug is discovered later
        return result;

    }




}
