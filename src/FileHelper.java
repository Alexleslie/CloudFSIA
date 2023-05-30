import java.io.*;

public class FileHelper {
    public static byte[] getContentFromFile(String filename) throws IOException {
        File file = new File(filename);
        long fileLength = file.length();
        byte[] content = new byte[(int)fileLength];

        FileInputStream fileInputStream = new FileInputStream(filename);
        fileInputStream.read(content);
        return content;
    }
}

