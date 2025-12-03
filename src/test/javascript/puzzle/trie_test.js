const Trie = require("./trie");
const assert = require("assert");

(function () {
    console.log("*** of String ****");
    const testString = "abc😀de";
    let i = 0;
    for (const c of testString)
        switch (i++) {
        case 0: assert.equal(c, 'a'); break;
        case 1: assert.equal(c, 'b'); break;
        case 2: assert.equal(c, 'c'); break;
        case 3: assert.equal(c, '😀'); break;
        case 4: assert.equal(c, 'd'); break;
        case 5: assert.equal(c, 'e'); break;
        }
})();

(function () {
    console.log("*** Array.from ****");
    const testString = "abc😀de";
    const testArray = Array.from(testString);
    assert.equal(testArray.toString(),
        ['a', 'b', 'c', '😀', 'd', 'e'].toString());
})();

const trie = new Trie();
trie.put("A","A");
trie.put("to","to");
trie.put("tea","tea");
trie.put("ted","ted");
trie.put("ten","ten");
trie.put("tennis","tennis");
trie.put("i","i");
trie.put("in","in");
trie.put("inn","inn");

(function () {
    console.log("*** Trie.put ****");
    assert.equal(trie.root.A.data, "A");
    assert.equal(trie.root.t.o.data, "to");
    assert.equal(trie.root.t.e.n.data, "ten");
    assert.equal(trie.root.t.e.n.n.i.s.data, "tennis");
    assert.equal(trie.root.i.n.n.data, "inn");
    console.log(trie.toString());
})();

(function () {
    console.log("*** Trie.find ****");
    assert.equal(trie.find("A"), "A");
    assert.equal(trie.find("ten"), "ten");
    assert.equal(trie.find("nine"), null);
})();

(function () {
    console.log("*** Trie.findFrom ****");
    assert.equal(trie.findFrom("A", 0).toString(), ["A"].toString());
    assert.equal(trie.findFrom("xA", 1).toString(), ["A"].toString());
    assert.equal(trie.findFrom("xtennis", 1).toString(), ["ten", "tennis"].toString());
})();