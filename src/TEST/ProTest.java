package TEST;

public class ProTest {
    public static void main(String[] args) {
        int size = 100000;
        int[] SampleSpace = new int[size];
        double rate = 0.01;

        for (int i = 0; i < size; i++)  SampleSpace[0] = 0;

        for (int i = 0; i < size*rate; i++) {
            int r = (int) (Math.random()*size);
            SampleSpace[r] = 1;
        }

        int challangeSize = 460;
        int batch = 1000000;
        int match = 0;
        for (int i = 0; i < batch; i++) {
            for (int j = 0; j < challangeSize; j++) {
                int r = (int) (Math.random()*size);
                if(SampleSpace[r] == 1){
                    match ++;
                    break;
                }

            }
        }
        System.out.format("Match Number: %d  \n", match);

        double matchRate = (double) match/batch;

        System.out.format("Audit match effective Rate: %f", matchRate);






    }
}
