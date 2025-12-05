class TrieEncoder {
    constructor() {
        this.root = {};
    }

    /**
     * 単語と関連付けられたデータを登録する。
     * (String, V) => void
     * Vは単語と関連付ける任意のデータ型
     */
    put(word, data) {
        let node = this.root;
        for (const char of word) {
            if (!node[char])
                node[char] = {};
            node = node[char];
        }
        node.data = data;
    }

    /**
     * 与えられた単語が登録されていれば関連付けられたデータを返す。
     * (String) => V|null
     */
    get(arrayChar) {
        let node = this.root;
        for (const char of arrayChar) {
            if ((node = node[char]) === undefined)
                return null;
        }
        return node.data || null;
    }

    // // ([String], int) => [{start: int, end: int, data: V}]
    // getFrom(arrayChar, start) {
    //     const length = arrayChar.length;
    //     const result = [];
    //     let node = this.root;
    //     for (let i = start; i < length; i++) {
    //         if ((node = node[arrayChar[i]]) === undefined)
    //             break;
    //         const data = node.data;
    //         if (data !== undefined)
    //             result.push({start: start, end: i + 1, data: data});
    //     }
    //     return result;
    // }

    // // ([String], [[{start: int, end: int, data: V}]], [{start: int, end: int, data: V}], int) => void
    // search(arrayChar, result, sequence, index) {
    //     const length = arrayChar.length;
    //     if (index >= length)
    //         result.push(sequence.slice());
    //     else
    //         for (const entry of this.getFrom(arrayChar, index)) {
    //             sequence.push(entry);
    //             this.search(arrayChar, result, sequence, entry.end);
    //             sequence.pop();
    //         }
    // }

    // // (String) => [[{start: int, end: int, data: V}]]
    // encode0(word) {
    //     const arrayChar = Array.from(word);
    //     const result = [], sequence = [];
    //     this.search(arrayChar, result, sequence, 0);
    //     return result;
    // }

    /**
     * 与えられた文字列を構成する単語の組み合わせをすべて返す。
     * (String) => [[{start: int, end: int, data: V}]]
     */
    encode(word) {
        const arrayChar = Array.from(word);
        const length = arrayChar.length;
        const result = [], sequence = [];
        const root = this.root;
        function search(index) {
            function getFrom(start) {
                const result = [];
                let node = root;
                for (let i = start; i < length; i++) {
                    if ((node = node[arrayChar[i]]) === undefined)
                        break;
                    const data = node.data;
                    if (data !== undefined)
                        result.push({start: start, end: i + 1, data: data});
                }
                return result;
            }
            if (index >= length)
                result.push(sequence.slice());
            else
                for (const entry of getFrom(index)) {
                    sequence.push(entry);
                    search(entry.end);
                    sequence.pop();
                }
        }
        search(0);
        return result;
    }

    // () => String
    toString() {
        return JSON.stringify(this.root, null, 2);
    }
}

module.exports = TrieEncoder;
