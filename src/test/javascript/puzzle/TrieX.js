class TrieX {
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

    get(arrayChar) {
        let node = this.root;
        for (const char of arrayChar) {
            if ((node = node[char]) === undefined)
                return null;
        }
        return node.data || null;
    }

    getFrom(arrayChar, start = 0) {
        const length = arrayChar.length;
        const result = [];
        let node = this.root;
        for (let i = start; i < length; i++) {
            if ((node = node[arrayChar[i]]) === undefined)
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

module.exports = TrieX;
