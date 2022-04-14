package puzzle.core;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class BinaryTree {

    public static class Node<T extends Comparable<T>> {
        T value;
        Node<T> left, right;

        public void insertToTree(T v) {
            if (value == null) {
                value = v;
                return;
            }
            if (v.compareTo(value) < 0) {
                if (left == null)
                    left = new Node<T>();
                left.insertToTree(v);
            } else {
                if (right == null)
                    right = new Node<T>();
                right.insertToTree(v);
            }
        }

        static final char NL = '\n';

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            try {
                printTree(sw);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return sw.toString();
        }

        public void printTree(Writer out) throws IOException {
            if (right != null)
                right.printTree(out, true, "");
            printNodeValue(out);
            if (left != null)
                left.printTree(out, false, "");
        }

        private void printNodeValue(Writer out) throws IOException {
            if (value == null)
                out.write("<null>");
            else
                out.write(value.toString());
            out.write(NL);
        }

        private void printTree(Writer out, boolean isRight, String indent) throws IOException {
            if (right != null)
                right.printTree(out, true, indent + (isRight ? "  " : "│"));
            out.write(indent);
            if (isRight)
                out.write("┌");
            else
                out.write("└");
            out.write(""); // number head
            printNodeValue(out);
            if (left != null)
                left.printTree(out, false, indent + (isRight ? "│" : "  "));
        }

    }

    public static void main(String[] args) throws IOException {
        Node<Integer> root = new Node<>();
        Integer[] values = IntStream.range(0, 20).boxed().toArray(Integer[]::new);
        Collections.shuffle(Arrays.asList(values));
        for (int i : values)
            root.insertToTree(i);
        System.out.println(root);
    }

}
