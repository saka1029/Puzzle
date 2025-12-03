const Trie = require("./trie");
const assert = require("assert");

console.log("*** TEST for of String ****");
const testString = "abc😀de";
let i = 0;
for (const c of testString) {
    switch (i++) {
    case 0: assert.equal(c, 'a'); break;
    case 1: assert.equal(c, 'b'); break;
    case 2: assert.equal(c, 'c'); break;
    case 3: assert.equal(c, '😀'); break;
    case 4: assert.equal(c, 'd'); break;
    case 5: assert.equal(c, 'e'); break;
    }
}
console.log("*** TEST Array.from ****");
const testArray = Array.from(testString);
assert.equal(testArray.toString(),
    ['a', 'b', 'c', '😀', 'd', 'e'].toString());

console.log("*** TEST Trie.insert ****");
const trie = new Trie();
trie.insert("A","A");
trie.insert("to","to");
trie.insert("tea","tea");
trie.insert("ted","ted");
trie.insert("ten","ten");
trie.insert("i","i");
trie.insert("in","in");
trie.insert("inn","inn");
trie.root["A"]["data"] = "A";
trie.root["t"]["o"]["data"] = "to";
trie.root["t"]["e"]["a"]["data"] = "tea";
trie.root["i"]["n"]["n"]["data"] = "inn";
// console.log(trie.toString());

console.log("*** TEST Trie.get ****");
assert.equal(trie.get("A"), "A");
assert.equal(trie.get("ten"), "ten");
assert.equal(trie.get("nine"), null);