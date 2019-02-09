import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MatrixUtils {

    public static int N = 9;

    public static int getBlock(int row, int col){ return row/3*3 + col/3; }

    public static Map<Integer, List<Couple>> blocks = new HashMap<>();

    public static List<Cell> queue = new ArrayList<>();

    public static Cell[][] readFile(String path) throws IOException {
        int lines = 0;
        Cell[][] matrix = new Cell[N][N];
        BufferedReader br = new BufferedReader(new FileReader(path));
        while (br.ready()) {
            String line = br.readLine();
            for (int i = 0; i < N; i++) {
                int n = Character.getNumericValue(line.charAt(i));
                Cell cell = n == -1 ? new Cell(lines,i,new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9))) : new Cell(lines,i, Collections.singleton(n));
                matrix[lines][i] = cell;
                if (cell.isFixed()) {
                    queue.add(cell);
                }
                Couple couple = new Couple(lines,i);
                List<Couple> set = blocks.get(cell.block);
                if (set == null) blocks.put(cell.block, new ArrayList<>(Collections.singletonList(couple)));
                else set.add(couple);
            } lines++;
        }
        return matrix;
    }

    public static String getTime(long millis) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static boolean isFixLegal(Cell cell, Cell[][] matrix) {
        for (int i = 0; i < MatrixUtils.N; i++) {
            Cell target = matrix[i][cell.col];
            if (target.getSingleton() == cell.getSingleton() && !target.equals(cell)) {
                return false;
            }

            target = matrix[cell.row][i];
            if (target.getSingleton() == cell.getSingleton() && !target.equals(cell)) {
                return false;
            }
        }

        for (Couple c : MatrixUtils.blocks.get(cell.block)) {
            Cell target = matrix[c.row][c.col];
            if (target.getSingleton() == cell.getSingleton() && !target.equals(cell)) {
                return false;
            }
        }

        return true;
    }

    public static void updateMap(Cell cell, Map<Integer, List<Cell>> map) {
        for (int value : cell.value) {
            List<Cell> set = map.get(value);
            if (set == null) map.put(value, new ArrayList<>() {{add(cell);}});
            else set.add(cell);
        }
    }

    public static Cell findChoice(Cell[][] matrix) {
        for (Cell[] row : matrix) {
            for (Cell cell : row) {
                if (!cell.isFixed()) return cell;
            }
        }
        return null;
    }

    public static Cell[][] clone(Cell[][] matrix) {
        Cell[][] clone = new Cell[N][N];
        for (Cell[] row : matrix) {
            for (Cell cell : row) {
                clone[cell.row][cell.col] = new Cell(cell.row, cell.col, cell.value);
            }
        }
        return clone;
    }
}
