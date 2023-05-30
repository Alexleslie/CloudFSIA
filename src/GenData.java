import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class GenData {
    public GenData() {

    }

    public static void main(String[] args) throws IOException {
        GenData instance = new GenData();

        int totalNum = 824*6;// 824为50MB
        //double[] proraList = {0.1,0.1,0.1,0.1,0.1,0.1,0.4};

        double[] proraList = {0.1, 0.1, 0.4,0.15,0.1,0.07,0.05,0.015,0.015};
        double[] sizeList  = {  1, 2, 4, 8, 16, 32, 64,128,4096};
        FileOutputStream fileOutputStream=  null;

        for (int i = 0; i<sizeList.length;i++){
            int partNum = (int) (proraList[i] * totalNum);
            byte[] data = new byte[(int) (sizeList[i]*1024)];
            for (int j = 0; j <data.length; j++) {
                data[i]  = (byte) (Math.random()* 10);
            }

            for (int j = 0; j<partNum;j++) {
                int randomN = instance.genRandomNum();
                String filename = "Data/"+String.valueOf(randomN);

                File file = new File(filename);
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.flush();
                fileOutputStream.close();

                System.out.format(" [%d] Finishing !!! \n", i);
            }
        }
    }

    public int genRandomNum(){
        int num  = (int) (Math.random() * 7 * 1e8 );
        return num;
    }



}
