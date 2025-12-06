const {readCSV, TrieEncoder} = require('./myLib');

const ENC = "Shift_JIS";
const trie = new TrieEncoder();
let count = 0;

function main() {
    console.log(`count=${count}`);
    // console.log(trie.toString());
    const byomei = "急性潰瘍性大腸炎";
    console.log(`傷病名: ${byomei}`);
    const enc = trie.encode(byomei);
    for (const line of enc) {
        let s = "";
        for (const e of line) {
            s += ` ${e.data}:${byomei.substring(e.start, e.end)}`;
        }
        console.log(s);
    }
}

function byomeiFilter(sequence) {
    let byomeiCount = 0;
    for (const entry of sequence) {
        if (entry.data.length == 7)
            ++byomeiCount;
    }
    return byomeiCount == 1;
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
                console.log(`登録件数=${count}`);
                readCSV('data/レセ電/micode.txt', "UTF-8",
                    (items) => {
                        console.log(`傷病名: ${items[1]}`);
                        for (const line of trie.encode(items[1], byomeiFilter))
                            console.log(" " + line
                                .map(e => e.data + ":" + items[1].substring(e.start, e.end))
                                .join(" "));
                    },
                    () => {}
                );
            }
        )
    }
);
