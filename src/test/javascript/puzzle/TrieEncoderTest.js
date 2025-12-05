const TrieEncoder = require('./TrieEncoder');
const assert = require("assert");

const trie = new TrieEncoder();
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

// (function () {
//     console.log("*** getFrom ****");
//     const wc = a("abc");
//     assert.deepEqual(trie.getFrom(wc, 0),
//         [{start: 0, end: 1, data: "a"}, {start: 0, end: 2, data: "ab"}, {start: 0, end: 3, data: "abc"}]);
//     assert.deepEqual(trie.getFrom(wc, 1),
//         [{start: 1, end: 2, data: "b"}, {start: 1, end: 3, data: "bc"}]);
//     assert.deepEqual(trie.getFrom(wc, 2),
//         [{start: 2, end: 3, data: "c"}]);
//     assert.deepEqual(trie.getFrom(a("xyz"), 1), []);
// })();

// (function () {
//     assert.deepEqual(trie.encode0("abc"),
//         [
//             [{start: 0, end: 1, data: "a"}, {start: 1, end: 2, data: "b"}, {start: 2, end: 3, data: "c"}],
//             [{start: 0, end: 1, data: "a"}, {start: 1, end: 3, data: "bc"}],
//             [{start: 0, end: 2, data: "ab"}, {start: 2, end: 3, data: "c"}],
//             [{start: 0, end: 3, data: "abc"}]
//         ]);
// })();

(function () {
    console.log("*** encode ****");
    assert.deepEqual(trie.encode("abc"),
        [
            [{start: 0, end: 1, data: "a"}, {start: 1, end: 2, data: "b"}, {start: 2, end: 3, data: "c"}],
            [{start: 0, end: 1, data: "a"}, {start: 1, end: 3, data: "bc"}],
            [{start: 0, end: 2, data: "ab"}, {start: 2, end: 3, data: "c"}],
            [{start: 0, end: 3, data: "abc"}]
        ]);
})();