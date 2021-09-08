package puzzle.indexable;

import java.util.Objects;

public class IndexableString implements Indexable {

    public final StringBuilder array;
    public final int begin, end;
    public final IndexComparator indexComparator;
    public final IndexSwapper indexSwapper;

    private IndexableString(StringBuilder array, int begin, int end,
        IndexComparator indexComparator, IndexSwapper indexSwapper) {
        this.array = array;
        this.begin = begin;
        this.end = end;
        this.indexComparator = indexComparator;
        this.indexSwapper = indexSwapper;
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
        return indexComparator.compare(leftIndex, rightIndex);
    }

    @Override
    public void swap(int leftIndex, int rightIndex) {
        indexSwapper.swap(leftIndex, rightIndex);
    }

    public static Builder builder(StringBuilder array) {
        return new Builder(array);
    }

    public static class Builder {
        private final StringBuilder array;
        private int begin = -1, end = -1;
        private IndexComparator indexComparator = null;
        private IndexSwapper indexSwapper = null;

        Builder(StringBuilder array) {
            Objects.requireNonNull(array, "array");
            this.array = array;
        }

        public Builder range(int begin, int end) {
            if (begin < 0)
                throw new IllegalArgumentException("begin must be >= 0");
            if (end < 0)
                throw new IllegalArgumentException("end must be >= 0");
            if (begin > end)
                throw new IllegalArgumentException("begin must be <= end");
            if (this.begin != -1)
                throw new IllegalStateException("already range/size specified");
            this.begin = begin;
            this.end = end;
            return this;
        }

        public Builder size(int size) {
            if (size < 0)
                throw new IllegalArgumentException("size must be >= 0");
            if (this.begin != -1)
                throw new IllegalStateException("already range/size specified");
            this.begin = 0;
            this.end = size;
            return this;
        }

        public Builder indexComparator(IndexComparator indexComparator) {
            Objects.requireNonNull(indexComparator, "indexComparator");
            if (this.indexComparator != null)
                throw new IllegalStateException(
                    "already indexComparator specified");
            this.indexComparator = indexComparator;
            return this;
        }

        public Builder indexSwapper(IndexSwapper indexSwapper) {
            Objects.requireNonNull(indexSwapper, "indexSwapper");
            if (this.indexSwapper != null)
                throw new IllegalStateException(
                    "already indexSwapper specified");
            this.indexSwapper = indexSwapper;
            return this;
        }

        public IndexableString build() {
            if (begin == -1) {
                begin = 0;
                end = array.length();
            }
            if (indexComparator == null)
                indexComparator = (leftIndex, rightIndex) ->
                    Character.compare(array.charAt(leftIndex), array.charAt(rightIndex));
            if (indexSwapper == null)
                indexSwapper = (leftIndex, rightIndex) -> {
                    char temp = array.charAt(leftIndex);
                    array.setCharAt(leftIndex, array.charAt(rightIndex));
                    array.setCharAt(rightIndex, temp);
                };
            return new IndexableString(array, begin, end, indexComparator, indexSwapper);
        }
    }
}
