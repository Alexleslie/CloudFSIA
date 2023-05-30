package DataStructure;

import java.io.Serializable;
import java.util.Arrays;

public class MetaTagD implements Serializable {
    private static final long serialVersionUID = -6772426518216338367L;

    private int index = -1; // index of the leafnode
    private byte[] filename = null; // filename of the leafnode
    private int state = 0; // state = 1 if this slot stored some data that were deleted; otherwise state = 0
    private byte[][] authenticationPath = null;
    // authentication path from the bottom to the root; the
    // bottom node lies at the beginning of the array

    public MetaTagD(int index,  byte[] filename, int state, byte[][] authenticationPath) {
        this.index = index;
        this.filename = filename;
        this.state = state;
        this.authenticationPath = authenticationPath;
    }

    public MetaTagD(int index, byte[] filename, int state) {
        this.index = index;
        this.filename = filename;
        this.state = state;
        this.authenticationPath = null;
    }


    @Override
    public String toString() {
        return "MetaProof in hashtable as following {" +
                "\n index: " + index +
                "\n fileName: " + Arrays.toString(filename)+
                "\n state: " + state +
                "\n authenticationPath" + Arrays.toString(authenticationPath) +
                "}";
    }

    public byte[] getFilename() {
        return filename;
    }

    public int getIndex() {
        return index;
    }

    public int getState() {
        return state;
    }

    public void setFilename(byte[] filename) {
        this.filename = filename;
    }

    public void setAuthenticationPath(byte[][] authenticationPath) {
        this.authenticationPath = authenticationPath;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setState(int state) {
        this.state = state;
    }

    public byte[][] getAuthenticationPath() {
        return authenticationPath;
    }

}
