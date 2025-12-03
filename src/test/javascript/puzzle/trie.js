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

    findFrom(word, start = 0) {
        const chars = Array.from(word);
        const length = chars.length;
        const result = [];
        let node = this.root;
        for (let i = start; i < length; i++) {
            if ((node = node[chars[i]]) === undefined)
                break;
            const data = node.data;
            if (data !== undefined)
                result.push(data);
        }
        return result;
    }

    toString() {
        return JSON.stringify(this.root, null, 2);
    }
}

module.exports = Trie;
