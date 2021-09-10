package puzzle.indexable;

public class SimpleIndexable implements Indexable {

    public int begin, end;
    public IndexComparator comparator;
    public IndexSwapper swapper;

    public SimpleIndexable(IndexComparator comparator, IndexSwapper swapper, int begin, int end) {
        this.comparator = comparator;
        this.swapper = swapper;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public int begin() {
        return begin;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public int compare(int leftIndex, int rightIndex) {
        return comparator.compare(leftIndex, rightIndex);
    }

    @Override
    public void swap(int leftIndex, int rightIndex) {
        swapper.swap(leftIndex, rightIndex);
    }
}
