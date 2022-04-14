package test.puzzle.dikstra;

import org.junit.Test;

import puzzle.dikstra.Graph;


public class TestGraph {

    @Test
    public void test() {
        Graph g = new Graph();
        g.add("1", "2", 7);
        g.add("1", "3", 9);
        g.add("1", "6", 14);
        g.add("2", "3", 10);
        g.add("2", "4", 15);
        g.add("3", "4", 11);
        g.add("3", "6", 2);
        g.add("4", "5", 6);
        g.add("5", "6", 9);
        g.solve("1");
        System.out.println(g);
    }

}
