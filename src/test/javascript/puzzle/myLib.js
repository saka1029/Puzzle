const fs = require('fs');
const readline = require('readline');
const iconv = require("iconv-lite");

function remq(s) {
    if (s.length >= 2 && s[0] == '"' && s[s.length - 1] == '"')
        return s.substring(1, s.length - 1);
    else
        return s;
}

/**
 * カンマ区切りのファイルを読み込む。
 * 各項目は２重引用符で囲まれていてもよい。
 * 
 * @param String file 
 * @param String encoding 
 * @param ([String])=>Void onRead 
 * @param ()=>Void onClose 
 */
function readCSV(file, encoding, onRead, onClose) {
    const stream = fs.createReadStream(file)
        .pipe(iconv.decodeStream(encoding));
    const reader = readline.createInterface( { input: stream});
    reader.on("line", (line) => {
        const items = line.split(",").map((item) => remq(item));
        onRead(items);
    });
    reader.on("close", onClose);
}

class TrieEncoder {
    constructor() {
        this.root = {};
    }

    /**
     * 単語と関連付けられたデータを登録する。
     * @param {String} word 登録する単語
     * @param {V} data 単語と関連付ける値
     *          Vは単語と関連付ける任意のデータ型
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
     * @param {String} word 検索する単語
     * @returns {V|null}
     *          Vは単語と関連付けられた任意のデータ型
     */
    get(word) {
        let node = this.root;
        for (const char of word) {
            if ((node = node[char]) === undefined)
                return null;
        }
        return node.data || null;
    }

    /**
     * 

わせをすべて返す。
     * @param String word encodedする文字列
     * @param (([{start:int,end:int,data:V}])=>Bool) filter
     *      単語の組み合わせを受け取り、条件を満たす場合にtrueを返す関数。
     *      nullの場合はすべての組み合わせを返す。
     * @returns [[{start: int, end: int, data: V}]]
     *      start:   wordにおける単語の開始位置
     *      end:   wordにおける単語の終了位置
     *      data:  wordに関連付けられた値
     */
    encode(word, filter = null) {
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
            if (index >= length && (filter == null || filter(sequence)))
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

module.exports = {readCSV, TrieEncoder};