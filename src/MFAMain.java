import DataStructure.*;

import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.io.*;

public class MFAMain {
    private String rootDirectory = null;
    private String[] filesList = null;
    private static final int hashSize = 32;
    private static final byte[] defaultFile = new byte[hashSize];

    private byte[] seed = null;
    private SecureRandom sr = null;
    private KeyGenerator kg = null;
    private SecretKey sk = null;
    private Mac mac =  null;

    private MetaTagD[] leafFiles = null;
    private HashTreeD hTree = null;
    private HashMap macTable;

    private int currentSize = 0;
    private int leafSize = 0;
    private int treeHeight = 0;
    private int treeSize = 2 * this.leafSize - 1;
    private byte[] visited = null;

    public MFAMain(String rootDirectory) {
        super();
        this.rootDirectory = rootDirectory;
        this.keyGen();

        try {
            this.sr = new SecureRandom(this.seed);
            this.kg = KeyGenerator.getInstance("HmacSHA256");
            this.kg.init(this.sr);
            this.sk = kg.generateKey();
            this.mac = Mac.getInstance("HmacSHA256");
            this.mac.init(sk);
        } catch (Exception e) {
            System.out.println("Error occured when initializing HmacSHA256.");
        }
    }

    public void keyGen() {
        this.seed = new byte[32];
        Arrays.fill(this.seed, (byte) 0xf2);
    }

    public MetaTagD[] prepareOutsource() throws IOException{
        this.filesList = getFileListBing(this.rootDirectory);

        assert this.filesList != null;
        this.currentSize = this.filesList.length;
        System.out.format("Numbers of files : %d \n", this.currentSize);
        this.treeHeight = (int) Math.ceil(Math.log(this.currentSize) /Math.log(2));
        if(this.treeHeight == 0) this.treeHeight = 1;

        this.leafSize = (int) Math.pow(2, this.treeHeight);
        this.treeSize = 2 * this.leafSize - 1;
        this.visited = new byte[this.leafSize];  // the distribution of hashtable. 0 for empty 1 for existing

        for (int i = 0; i < this.leafSize; i++)
            this.visited[i] = 0;

        this.leafFiles = new MetaTagD[this.leafSize];
        for (int i=0;i<this.leafSize;i++){
            this.leafFiles[i] = new MetaTagD(i, defaultFile, 0);
        }

        this.macTable = new HashMap<Integer, byte[]>();

        for (String file : filesList) {
            byte[] fileMac = this.mac.doFinal(file.getBytes());

            int index = mapFunction(fileMac, this.leafSize, visited);
            this.leafFiles[index].setFilename(fileMac);
            this.leafFiles[index].setState(1);

            byte[] data;
            data = FileHelper.getContentFromFile(this.rootDirectory + "\\" + file);

            MessageDigest messageDigest = HashTree.generateMD();
            this.macTable.put(Arrays.hashCode(fileMac), messageDigest.digest(data));
            visited[index] = 1;
        }
        return leafFiles;
    }

    public HashTreeD outsource() {
        this.hTree = new HashTreeD(this.treeSize, this.treeHeight, this.leafFiles);
        this.hTree.setHt(HashTree.buildFromLeaf(this.treeHeight, this.leafFiles));
        return this.hTree;
    }



    public byte[] query(String file) {
        return this.mac.doFinal(file.getBytes());
    }

    public static ResProof proofGen(byte[] queryFile, HashTreeD hashTreeD) {
        ResProof proof = new ResProof(queryFile);

        int index = 0;
        int leafSize = hashTreeD.getLeafSize();
        index = hashFunction1(queryFile);
        index = Math.abs(index) % leafSize;

        int flag = 0;
        MetaTagD tagD = null;

        while (flag == 0 ) {
            tagD = (hashTreeD.getLeaf())[index];
            tagD.setAuthenticationPath(HashTree.getAuthPath(index, hashTreeD));
            proof.addMapItems(tagD);

            byte[] filenameMask = tagD.getFilename();
            int state = tagD.getState();

            if(Arrays.equals(filenameMask, queryFile)) {
                proof.setExistingFlag(1);
                flag = 1; // file exist
            } else if (Arrays.equals(filenameMask, defaultFile) && state == 0) {
                proof.setExistingFlag(2); // find a empty slot
                flag = 2;
            } else {
                index = hashFunction2(index, leafSize);
            }
        }
        return proof;
    }

    public boolean verify(String filePath, byte[] queryFile,
                                 ResProof proof, HashTreeD hashTreeD) throws IOException {
        int cheatFlag = 0;
        if(proof.getExistingFlag() == 1) {
            if(!Arrays.equals(proof.getQueryFile(), queryFile)
                    || validateMapQueue(hashTreeD, proof)) {
                cheatFlag +=1;
            }
        }else{
            System.out.println("Challenge a non-existing file !!!");
            cheatFlag += 1;
        }

        if(!verifyMac_offline(this.macTable, filePath, queryFile)) {
            cheatFlag += 1;
        }
        return cheatFlag == 0;



    }

    public static boolean verifyMac_offline(HashMap hashMap, String filePath, byte[] query) throws IOException {
        MessageDigest md = HashTree.generateMD();
        byte[] data = FileHelper.getContentFromFile(filePath);
        byte[] hmac = md.digest(data);
        byte[] hamc_ = (byte[]) (hashMap.get(Arrays.hashCode(query)));
        if (!Arrays.equals(hmac, hamc_)){
            System.out.println("Hamc Validate Error");
            return false;
        }
        else {
            return true;
        }
    }

    public static int hashFunction1(byte[] fileMac) {
        return ((int) fileMac[0]) + (((int) fileMac[1]) << 8) + (((int) fileMac[2]) << 16)
                + (((int) fileMac[3]) << 24);
    }

    public HashTreeD delete(byte[] queryFile, HashTreeD hashTreeD) {
        ResProof proof = proofGen(queryFile, hashTreeD);
        if(proof.getExistingFlag() == 0 ) return null;

        int temp = proof.getTotalItems();
        MetaTagD deletedFile = proof.deQueueMapPath(temp - 1);
        deletedFile.setState(0);
        deletedFile.setFilename(defaultFile);

        HashTree.update(hashTreeD, deletedFile.getIndex(), deletedFile);
        return hashTreeD;
    }

    public void add(byte[] queryFile, HashTreeD hashTreeD, String filePath) throws IOException {
        ResProof proof = proofGen(queryFile, hashTreeD);

        if(proof.getExistingFlag() == 1) return ;
        MessageDigest md = HashTree.generateMD();
        byte[] data = FileHelper.getContentFromFile(filePath);
        byte[] hmac = md.digest(data);

        int position = proof.deQueueMapPath(proof.getTotalItems() - 1).getIndex();
        
        MetaTagD addedFile = new MetaTagD(position, queryFile, 1);
        HashTree.update(hashTreeD, position, addedFile);
    }


    public static int hashFunction2(int index, int base) {
        return (index + 6567) % base;
    }

    public static int mapFunction(byte[] filenameMask, int tableSize, byte[] visited) {
        int index = 0;
        index  = hashFunction1(filenameMask);
        index = Math.abs(index) % tableSize;

        while (visited[index] == 1) {
            index = hashFunction2(index, tableSize);
        }
        return index;
    }

    public static boolean validateMapQueue(HashTreeD hashTreeD, ResProof resProof) {
        byte[] filenameMask = resProof.getQueryFile();
        int leafSize = hashTreeD.getLeafSize();
        byte[] root = hashTreeD.getRoot();

        int cheatFlag = 0;
        int indexExpected = -1;

        indexExpected = hashFunction1(filenameMask);
        indexExpected = Math.abs(indexExpected) % leafSize;

        MetaTagD metaTag = resProof.getMapPath().get(0);
        if(!metaTagValidate(metaTag, indexExpected, root)) cheatFlag +=1;
        if (resProof.getTotalItems() ==1 && cheatFlag == 0) return true;

        for (int i = 2; i < resProof.getTotalItems(); i++) {
            indexExpected = hashFunction2(indexExpected, leafSize);
            metaTag  = resProof.getMapPath().get(i);

            if (!metaTagValidate(metaTag, indexExpected, root)) cheatFlag += 1;
        }
        if (cheatFlag == 0) return true;
        return false;
    }

    public static boolean metaTagValidate(MetaTagD metaTagD, int index, byte[] root) {
        return (metaTagD.getIndex() == index && HashTable.validateTag(root, metaTagD));
    }

    public String getRandomChallenge() {
        int r = (int) Math.floor(Math.random() * this.filesList.length);
        return this.filesList[r];
    }

    public String[] getFileList(String rootDirectory) {
        File directory = new File(rootDirectory);
        String[] files = directory.list();
        ArrayList<String> filesList = new ArrayList<>();

        for (String f: files) {
            File filePath = new File(rootDirectory + "\\" + f);
            if(filePath.isDirectory()) {
                System.out.println("This is a Directory");
            }
            else {
                filesList.add(f);
            }
        }
        return filesList.toArray(new String[filesList.size()]);
    }

    public static String[] getFileListBing(String rootDirectory) {
        List<String> filesList = new ArrayList<>();
        File directory = new File(rootDirectory);

        String[] files = directory.list();
        if (files == null) {
            return new String[0];
        }
        for (String file : files) {
            File filePath = new File(rootDirectory + "\\" + file);
            if (filePath.isDirectory()) {
                Collections.addAll(filesList, getFileListBing(filePath.toString()));
            } else {
                filesList.add(filePath.toString().substring(5));
            }
        }
        return filesList.toArray(new String[filesList.size()]);
    }

    public HashTreeD gethTree() {
        return hTree;
    }
}
