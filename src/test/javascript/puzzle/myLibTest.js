const fs = require("fs");
const {substring, readCSV, TrieEncoder} = require("./myLib");

const ENC = "Shift_JIS";
const trie = new TrieEncoder();
let count = 0;
let encodeCount = 0;

/**
 * 傷病名が１つだけ含まれる組み合わせを返すフィルタ。 
 * @param [{start:int,end:int,data:V}] sequence 単語の並び
 * @returns Bool
 */
function byomeiFilter(sequence) {
    return sequence
        .filter(entry => entry.data.length == 7)
        .length == 1;
}

readCSV('data/レセ電/b_20200601.txt', ENC,
    (items) => {
        if (items[2] == "0000999") return;
        trie.put(items[5], items[2]);
        ++count;
    },
    () => {
        readCSV('data/レセ電/z_20200601.txt', ENC,
            (items) => {
                trie.put(items[6], items[2]);
                ++count;
            },
            () => {
                readCSV('data/レセ電/micode.txt', "UTF-8",
                    (items) => {
                        console.log(`傷病名: ${items[1]}`);
                        for (const line of trie.encode(items[1], byomeiFilter)) {
                            console.log(" " + line
                                .map(e => e.data + ":" + substring(items[1], e.start, e.end))
                                .join(" "));
                            ++encodeCount;
                        }
                    },
                    () => {
                        fs.writeFileSync('src/test/javascript/puzzle/encoder.js',
                            "const { TrieEncoder } = require('./myLib');"
                            + `const ENCODER = new TrieEncoder(${JSON.stringify(trie.root)});`
                            + "module.exports = ENCODER;");
                        console.log(`登録件数=${count}`);
                        console.log(`エンコード件数=${encodeCount}`);
                    }
                );
            }
        )
    }
);
