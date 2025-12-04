const TrieX = require('./TrieX');
const assert = require("assert");

const trie = new TrieX();
trie.put("a", "a");
trie.put("ab", "ab");
trie.put("abc", "abc");
trie.put("b", "b");
trie.put("bc", "bc");
trie.put("c", "c");

function a(string) {
    return Array.from(string);
}

(function () {
    console.log("*** put ****");
    assert.strictEqual(trie.root.a.data, "a");
    assert.strictEqual(trie.root.a.b.data, "ab");
    assert.strictEqual(trie.root.a.b.c.data, "abc");
    assert.strictEqual(trie.root.b.data, "b");
    assert.strictEqual(trie.root.b.c.data, "bc");
    assert.strictEqual(trie.root.c.data, "c");
    // console.log(trie.toString());
})();

(function () {
    console.log("*** get ****");
    assert.strictEqual(trie.get(a("a")), "a");
    assert.strictEqual(trie.get(a("ab")), "ab");
    assert.strictEqual(trie.get(a("abc")), "abc");
    assert.strictEqual(trie.get(a("bc")), "bc");
    assert.strictEqual(trie.get(a("c")), "c");
    assert.strictEqual(trie.get(a("ca")), null);
})();

(function () {
    console.log("*** getFrom ****");
    const wc = a("abc");
    assert.deepEqual(trie.getFrom(wc, 0),
        [[1, "a"], [2, "ab"], [3, "abc"]]);
    assert.deepEqual(trie.getFrom(wc, 1),
        [[1, "b"], [2, "bc"]]);
    assert.deepEqual(trie.getFrom(wc, 2),
        [[1, "c"]]);
    assert.deepEqual(trie.getFrom(a("xyz"), 1), []);
})();