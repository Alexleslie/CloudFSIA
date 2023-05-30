package DataStructure;

import java.io.Serializable;
import java.util.ArrayList;

public class ResProof implements Serializable {
    private static final long serialVersionUID = 7756652328703039630L;

    private byte[] queryFile = null; // queried filename MAC
    private int existingFlag = -1; // 1: file exists; 0: file not exist

    // the number of total authentication paths which are introduced by collisions in the
    // process mapping a filename to an index
    private int totalItems = 0;
    private ArrayList<MetaTagD> mapPath = null; // authentication paths


    public ResProof(byte[] queryFile){
        this.queryFile = queryFile;
        this.totalItems = 0;
        this.mapPath = new ArrayList<MetaTagD>();
    }

    public void addMapItems(MetaTagD temp) {
        this.mapPath.add(temp);
        this.totalItems = this.totalItems + 1;
    }


    public ArrayList<MetaTagD> getMapPath() {
        return mapPath;
    }

    public MetaTagD deQueueMapPath(int index) {return mapPath.get(index);}

    public byte[] getQueryFile() {
        return queryFile;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getExistingFlag() {
        return existingFlag;
    }

    public void setMapPath(ArrayList<MetaTagD> mapPath) {
        this.mapPath = mapPath;
    }

    public void setExistingFlag(int existingFlag) {
        this.existingFlag = existingFlag;
    }

    public void setQueryFile(byte[] queryFile) {
        this.queryFile = queryFile;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
