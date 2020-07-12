package test.qiita.newObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TestRecords {

    class Person {

        private final String name;
        private final String age;

        public Person(String name, String age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return this.name;
        }

        public String getAge() {
            return this.age;
        }
    }

    record PersonWithAge(Person p, int age) {
        PersonWithAge(Person p) {
            this(p, Integer.parseInt(p.getAge()));
        }
    }

List<Person> list = List.of(
    new Person("Yamada", "18"),
    new Person("Ichikawa", "72"),
    new Person("Sato", "39"),
    new Person("Tanaka", "9"));

@Test
void testDirect() {
    List<Person> result = list.stream()
        .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getAge())))
        .collect(Collectors.toList());
}

@Test
void testEntry() {
    List<Person> result = list.stream()
        .map(p -> Map.entry(Integer.parseInt(p.getAge()), p))
        .sorted(Comparator.comparingInt(Entry::getKey))
        .map(Entry::getValue)
        .collect(Collectors.toList());
}

@Test
void testRecords() {
    List<Person> result = list.stream()
        .map(PersonWithAge::new)
        .sorted(Comparator.comparingInt(PersonWithAge::age))
        .map(PersonWithAge::p)
        .collect(Collectors.toList());
}

@Test
void testObject() {
    List<Person> result = list.stream()
        .map(p -> new Object() {
            int intAge = Integer.parseInt(p.getAge());
            Person person = p;
        })
        .sorted(Comparator.comparingInt(obj -> obj.intAge))
        .map(obj -> obj.person)
        .collect(Collectors.toList());
}
}
