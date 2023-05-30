package DataStructure;

import java.io.Serializable;
import java.util.Arrays;

public class HashTreeD implements Serializable {
    private static final long serialVersionUID = 6516202990485486673L;

    private byte[][] ht = null; // the entire node of this tree
    private int treeSize = 0; // the size of tree
    private int treeHeight = 0; // the height of tree
    private MetaTagD[] leaf = null;  // the leafnode of tree, which is the hashtable.
    private int leafSize = 0; // the number of leaf

    public HashTreeD(byte[][] ht, int treesize, int treeHeight, MetaTagD[] metaTagDS){
        this.ht = ht;
        this.treeSize = treesize;
        this.leafSize = (this.treeSize + 1) /2 ;
        this.treeHeight = treeHeight;
        this.leaf = metaTagDS;

    }

    public HashTreeD(int treeSize, int treeHeight, MetaTagD[] metaTagDS){
        this.treeSize = treeSize;
        this.treeHeight = treeHeight;
        this.leaf = metaTagDS;
        this.leafSize = (this.treeSize + 1) /2 ;

    }

    @Override
    public String toString() {
        return "HashTreeD{" +
                "ht=" + Arrays.toString(ht) +
                ", treeSize=" + treeSize +
                ", treeHeight=" + treeHeight +
                ", leaf=" + Arrays.toString(leaf) +
                ", leafSize=" + leafSize +
                '}';
    }

    public void setHt(byte[][] ht) {
        this.ht = ht;
    }

    public void setHtSingle(int position, byte[] leaf) {this.ht[position] = leaf;}

    public void setLeaf(MetaTagD[] leaf) {
        this.leaf = leaf;
    }

    public void setLeafSingle(int position, MetaTagD metaProof) { this.leaf[position] = metaProof; }

    public void setTreeHeight(int treeHeight) {
        this.treeHeight = treeHeight;
    }

    public void setTreeSize(int treeSize) {
        this.treeSize = treeSize;
    }

    public byte[][] getHt() {
        return ht;
    }

    public int getTreeHeight() {
        return treeHeight;
    }

    public int getTreeSize() {
        return treeSize;
    }

    public MetaTagD[] getLeaf() {
        return leaf;
    }

    public byte[] getRoot() {
        return ht[0];
    }

    public int getLeafSize() {
        return leafSize;
    }

    public void setRoot(byte[] root) {ht[0] = root;}


}

