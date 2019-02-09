import java.util.*;
import java.util.concurrent.RecursiveTask;

public class Solver extends RecursiveTask<Integer> {

    private Cell[][] matrix;
    private List<Cell> queue;
    private enum Type { BLOCK, ROW, COLUMN }

    public Solver(Cell[][] matrix, List<Cell> queue) {
        this.matrix = matrix;
        this.queue = queue;
    }

    @Override
    public Integer compute() {
        while(!this.queue.isEmpty()) {
            int start = queue.size();
            boolean valid = removeFrom(Type.BLOCK,new ArrayDeque<>(this.queue));
            if (!valid) return 0;

            valid = removeFrom(Type.ROW,new ArrayDeque<>(this.queue));
            if (!valid) return 0;

            valid = removeFrom(Type.COLUMN,new ArrayDeque<>(this.queue));
            if (!valid) return 0;
            this.queue = queue.subList(start, queue.size());
        }
        Cell choice = MatrixUtils.findChoice(this.matrix);
        if (choice == null) {
            return 1;
        }
        List<Solver> solvers = new ArrayList<>();
        for (Integer i : choice.value) {
            Cell[][] clone = MatrixUtils.clone(this.matrix);
            Cell cell = clone[choice.row][choice.col];
            cell.fix(i);
            List<Cell> queue = new ArrayList<>() {{add(cell);}};
            solvers.add(new Solver(clone, queue));
        }
        return start(solvers);
    }

    private boolean removeFrom(Type type, Queue<Cell> queue) {
        Queue<Cell> fixed = new ArrayDeque<>();
        boolean[] toBeChecked = new boolean[9];
        while (!queue.isEmpty()) {
            Cell polled = queue.poll();
            switch (type) {
                case BLOCK:
                    for (Couple c : MatrixUtils.blocks.get(polled.block)) {
                        Cell cell = matrix[c.row][c.col];
                        boolean success = reduce(cell,polled,toBeChecked,queue,fixed);
                        if (!success) return false;
                    } break;
                case ROW:
                    for (int i = 0; i < MatrixUtils.N; i++) {
                        Cell cell = matrix[polled.row][i];
                        boolean success = reduce(cell,polled,toBeChecked,queue,fixed);
                        if (!success) return false;
                    } break;
                case COLUMN:
                    for (int i = 0; i < MatrixUtils.N; i++) {
                        Cell cell = matrix[i][polled.col];
                        boolean success = reduce(cell,polled,toBeChecked,queue,fixed);
                        if (!success) return false;
                    } break;
            }
        }
        boolean success = findFixes(type, toBeChecked,fixed);
        if (!success) return false;
        this.queue.addAll(fixed);
        return true;
    }

    private boolean findFixes(Type type, boolean[] toBeChecked, Queue<Cell> fixed) {
        for (int i = 0; i < toBeChecked.length; i++) {
            if (toBeChecked[i]) {
                Map<Integer, List<Cell>> map = new HashMap<>();
                switch (type) {
                    case BLOCK:
                        for (Couple c : MatrixUtils.blocks.get(i)) {
                            Cell cell = matrix[c.row][c.col];
                            if (!cell.isFixed()) MatrixUtils.updateMap(cell, map);
                        } break;
                    case ROW:
                        for (Cell cell : matrix[i]) {
                            if (!cell.isFixed()) MatrixUtils.updateMap(cell, map);
                        } break;
                    case COLUMN:
                        for (int j = 0; j < MatrixUtils.N; j++) {
                            Cell cell = matrix[j][i];
                            if (!cell.isFixed()) MatrixUtils.updateMap(cell, map);
                        } break;
                }
                if (!fixEntry(map,fixed)) return false;
            }
        }
        return true;
    }

    private boolean reduce(Cell cell, Cell polled, boolean[] toBeChecked, Queue<Cell> queue, Queue<Cell> fixed) {
        if (!cell.isFixed()) {
            boolean changes = cell.reduce(polled.getSingleton());
            if (changes) {
                if (cell.isEmpty()) {
                    return false;
                }
                if (cell.isFixed()) {
                    if (MatrixUtils.isFixLegal(cell, matrix)) {
                        queue.add(cell);
                        fixed.add(cell);
                        toBeChecked[cell.block] = true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int start(List<Solver> threads) {
        Solver last = threads.get(threads.size()-1);
        if(last != null) {
            for (int i = 0; i < threads.size()-1; i++) threads.get(i).fork();
            int instances = last.compute();
            for (int i = 0; i < threads.size()-1; i++) {
                instances += threads.get(i).join();
            }
            return instances;
        }
        return 0;
    }

    private boolean fixEntry(Map<Integer, List<Cell>> map, Queue<Cell> fixed) {
        for (Map.Entry<Integer, List<Cell>> entry : map.entrySet()) {
            List<Cell> list = entry.getValue();
            if (list.size() == 1) {
                Cell c = list.get(0);
                c.fix(entry.getKey());
                if (MatrixUtils.isFixLegal(c, matrix)) {
                    fixed.add(c);
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
