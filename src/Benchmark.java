import DataStructure.HashTreeD;
import DataStructure.MetaTagD;
import DataStructure.ResProof;

import java.io.IOException;

public class Benchmark {
    private long storage = 0;
    private long proofSize[] = null;
    private long time[] = new long[8];
    private int collisionCount[][] = null;
    private String directory;

    public int LOOP_TIMES;

    public Benchmark(String directory, int LOOP_TIMES){
        super();
        this.directory = directory;
        this.LOOP_TIMES = LOOP_TIMES;

        for (int i = 0; i < this.time.length; i++) {
            this.time[i] = 0;
        }
    }

    public void run() throws IOException {
        long startTime = 0, endTime = 0, startMemory = 0, endMemory = 0;

        Runtime r = Runtime.getRuntime();
        r.gc();

        MFAMain instance = new MFAMain(this.directory);

        startTime = System.nanoTime();

        MetaTagD[] metaTagDs = instance.prepareOutsource();
        HashTreeD hashTreeD = instance.outsource();

        endTime = System.nanoTime();

        this.time[0] = endTime - startTime;

        for (int i = 0; i < LOOP_TIMES; i++) {
            System.out.format("---[%d]--- Start a challenge  \n", i);
            String challenge = instance.getRandomChallenge();

            byte[] query = instance.query(challenge);


            //System.out.format("---[%d]--- Start proofGen  \n", i);
            startTime = System.nanoTime();
            ResProof resProof = MFAMain.proofGen(query, instance.gethTree());
            endTime = System.nanoTime();

            this.time[1] += (endTime - startTime);


            //System.out.format("---[%d]--- Start Verify  \n", i);
            startTime = System.nanoTime();
            instance.verify(this.directory + "\\" + challenge, query, resProof, instance.gethTree());
            endTime = System.nanoTime();
            this.time[2] += endTime - startTime;


            //System.out.format("---[%d]--- Start deleting  \n", i);
            startTime = System.nanoTime();
            instance.delete(query, hashTreeD);
            endTime = System.nanoTime();
            this.time[3] += endTime - startTime;


           // System.out.format("---[%d]--- Start adding \n", i);
            startTime = System.nanoTime();
            instance.add(query, hashTreeD, this.directory + "\\" + challenge);
            endTime = System.nanoTime();
            this.time[4] += endTime - startTime;

            //System.out.format("---[%d]--- End \n", i);

        }

        this.time[1] = (long) (this.time[1] / LOOP_TIMES);
        this.time[2] = (long) (this.time[2] / LOOP_TIMES);
        this.time[3] = (long) (this.time[3] / LOOP_TIMES);
        this.time[4] = (long) (this.time[4] / LOOP_TIMES);

        System.out.println("TEST CASE: " + this.directory);
        System.out.println("Time is: (ns)");
        System.out.format("OutSource: %d  ||  ProofGen: %d  ||  Verify: %d  " +
                        "||  Delete: %d  ||  Add: %d ",
                this.time[0], this.time[1], this.time[2], this.time[3], this.time[4]);
    }

}