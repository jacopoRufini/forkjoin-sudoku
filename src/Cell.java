import java.util.*;
public class Cell {

    public int row;

    public int col;

    public Set<Integer> value;

    private int singleton;

    private boolean isFixed;

    public int block;

    public Cell(int row, int col, Set<Integer> value) {
        this.row = row;
        this.col = col;
        this.block = MatrixUtils.getBlock(row,col);
        this.value = new HashSet<>(value);
        checkFixness();
    }

    public int getSingleton() { return singleton; }

    public boolean isFixed() { return this.isFixed; }

    public boolean isEmpty() { return this.value.isEmpty(); }

    public boolean reduce(int number) {
        boolean success = this.value.remove(number);
        if(success) checkFixness();
        return success;
    }

    private void checkFixness() {
        int size = this.value.size();
        this.singleton = size == 1 ? this.value.iterator().next() : -1;
        this.isFixed = this.singleton != -1;
    }

    public void fix(Integer number) {
        this.value = Collections.singleton(number);
        this.singleton = this.value.iterator().next();
        this.isFixed = true;
    }

    @Override
    public int hashCode() { return row * 31 + col; }

    @Override
    public boolean equals(Object obj) {
        Cell c = (Cell)obj;
        return this.row == c.row && this.col == c.col;
    }

    @Override
    public String toString(){ return this.value.toString(); }
}

