class Trie {
    constructor() {
        this.root = {};
    }

    insert(word, data) {
        let node = this.root;
        for (const char of word) {
            if (!node[char]) {
                node[char] = {};
            }
            node = node[char];
        }
        node.data = data;
    }

    toString() {
        return JSON.stringify(this.root, null, 2);
    }
}

module.exports = Trie;
