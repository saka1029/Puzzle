package test.puzzle.core;

import static puzzle.core.Comparators.asc;
import static puzzle.core.Comparators.orderBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestComparators {

    /**
     * java - comparing and thenComparing gives compile error - Stack Overflow
     * https://stackoverflow.com/questions/40500280/comparing-and-thencomparing-gives-compile-error
     */
    @Test
    public void testOrigin() {
        String[] arr = {"alan 25", "mario 30", "alan 19", "mario 25"};
        Comparator<String> c = Comparator.<String, String> comparing(s -> s.split("\\s+")[0])
            .thenComparingInt(s -> Integer.parseInt(s.split("\\s+")[1]));
        List<String> sorted = Arrays.stream(arr).sorted(c).collect(Collectors.toList());
        System.out.println(sorted);
    }

    @Test
    public void testNewObject() {
        String[] arr = {"alan 25", "mario 30", "alan 19", "mario 25"};
        List<String> sorted = Arrays.stream(arr)
            .map(s -> new Object() {
                String[] keys = s.split("\\s");
                String name = keys[0];
                int age = Integer.parseInt(keys[1]);
                String origin = s;
            })
            .sorted(orderBy(asc(obj -> obj.name), asc(obj -> obj.age)))
            .map(obj -> obj.origin)
            .collect(Collectors.toList());
        System.out.println(sorted);
    }

    @Test
    public void testThenComparing() {
        record Song(String title, int duration, String artist) {
            String getTitle() {
                return title;
            }

            int getDuration() {
                return duration;
            }

            String getArtist() {
                return artist;
            }
        }
        ArrayList<Song> playlist1 = new ArrayList<Song>();

        // add some new Song objects
        playlist1.add(new Song("Only Girl (In The World)", 235, "Rhianna"));
        playlist1.add(new Song("Thinking of Me", 206, "Olly Murs"));
        playlist1.add(new Song("Raise Your Glass", 202, "P!nk"));

        // Here is a call to both types of sort method that works, no problem:

        Collections.sort(playlist1,
            Comparator.comparing(p1 -> p1.getTitle()));

        playlist1.sort(
            Comparator.comparing(p1 -> p1.getTitle()));
        // As soon as I start to chain thenComparing, the following happens:

        Collections.sort(playlist1,
            orderBy(asc(p1 -> p1.getTitle()),
                asc(p1 -> p1.getDuration()),
                asc(p1 -> p1.getArtist())));

        playlist1.sort(
            orderBy(asc(Song::getTitle),
                asc(Song::getDuration),
                asc(Song::getArtist)));

        playlist1.sort(
            Comparator.comparing(Song::getTitle)
                .thenComparing(Song::getDuration)
                .thenComparing(Song::getArtist));

        playlist1.sort(
            orderBy(asc(p1 -> p1.getTitle()),
                asc(p1 -> p1.getDuration()),
                asc(p1 -> p1.getArtist())));

//        playlist1.sort(
//            Comparator.comparing(p1 -> p1.getTitle())
//                .thenComparing(p1 -> p1.getDuration())
//                .thenComparing(p1 -> p1.getArtist()));

        //// i.e. syntax errors because it does not know the type of p1 anymore.
        // So to fix this I add the type Song to the first parameter (of
        // comparing):
        //
        Collections.sort(playlist1,
            Comparator.comparing((Song p1) -> p1.getTitle())
                .thenComparing(p1 -> p1.getDuration())
                .thenComparing(p1 -> p1.getArtist()));
        //
        playlist1.sort(
            Comparator.comparing((Song p1) -> p1.getTitle())
                .thenComparing(p1 -> p1.getDuration())
                .thenComparing(p1 -> p1.getArtist()));
    }

}
