import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) throws IOException {
        Cell[][] matrix = MatrixUtils.readFile(args[0]);
        Solver ss = new Solver(matrix,MatrixUtils.queue);
        long start = System.currentTimeMillis();
        System.out.println("Found " + new ForkJoinPool().invoke(ss) + " solutions.");
        long finish = System.currentTimeMillis();
        System.out.println("Running time on " + args[0] + ": " + MatrixUtils.getTime(finish-start) + ".");
    }
}
