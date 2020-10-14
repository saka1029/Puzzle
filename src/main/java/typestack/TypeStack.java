package typestack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeStack {

    final Map<String, List<Definition>> definitions = new HashMap<>();

    public void add(String name, Definition definition) {
        definitions.computeIfAbsent(name, k -> new ArrayList<>())
            .add(definition);
    }

    public Iterable<Definition> get(String name) {
        List<Definition> list = definitions.get(name);
        return list != null ? list : Collections.emptyList();
    }

}
