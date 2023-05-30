import java.io.IOException;

public class PerformaceEvaluate {
    public static void main(String[] args) throws IOException {
        Benchmark b = new Benchmark
                ("Data/", 50);
        b.run();
    }
}
