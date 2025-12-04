class TrieX {
    constructor() {
        this.root = {};
    }

    // (String, V) => void
    // Vは単語と関連付ける任意のデータ型
    put(word, data) {
        let node = this.root;
        for (const char of word) {
            if (!node[char])
                node[char] = {};
            node = node[char];
        }
        node.data = data;
    }

    // ([String]) => V|null
    get(arrayChar) {
        let node = this.root;
        for (const char of arrayChar) {
            if ((node = node[char]) === undefined)
                return null;
        }
        return node.data || null;
    }

    // ([String]) => [{start: int, length: int, data: V}]
    getFrom(arrayChar, start = 0) {
        const length = arrayChar.length;
        const result = [];
        let node = this.root;
        for (let i = start; i < length; i++) {
            if ((node = node[arrayChar[i]]) === undefined)
                break;
            const data = node.data;
            if (data !== undefined)
                result.push({start: start, length: i - start + 1, data: data});
        }
        return result;
    }

    // () => String
    toString() {
        return JSON.stringify(this.root, null, 2);
    }
}

module.exports = TrieX;
