class Trie {
    constructor() {
        this.root = {};
    }

    put(word, data) {
        let node = this.root;
        for (const char of word) {
            if (!node[char])
                node[char] = {};
            node = node[char];
        }
        node.data = data;
    }

    find(word) {
        let node = this.root;
        for (const char of word) {
            if ((node = node[char]) === undefined)
                return null;
        }
        return node.data || null;
    }

    toString() {
        return JSON.stringify(this.root, null, 2);
    }
}

module.exports = Trie;
