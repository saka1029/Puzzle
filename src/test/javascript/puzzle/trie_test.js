const Trie = require("./trie");

const trie = new Trie();
// trie.insert("apple", { type: "fruit", color: "red" });
// trie.insert("app", { type: "abbreviation", meaning: "application" });
// trie.insert("banana", { type: "fruit", color: "yellow" });
trie.insert("A","A");
trie.insert("to","to");
trie.insert("tea","tea");
trie.insert("ted","ted");
trie.insert("ten","ten");
trie.insert("i","i");
trie.insert("in","in");
trie.insert("inn","inn");
console.log(trie.toString());