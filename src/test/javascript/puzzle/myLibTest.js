const {readCSV, TrieEncoder} = require('./myLib');

const trie = new TrieEncoder();
let count = 0;

readCSV('data/レセ電/b_20200601.txt', "Shift_JIS",
    (items) => {
        if (items[2] == "0000999") return;
        trie.put(items[5], items[2]);
        ++count;
    },
    () => {
        readCSV('data/レセ電/z_20200601.txt', "Shift_JIS",
            (items) => {
                trie.put(items[6], items[2]);
                ++count;
            },
            () => {
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
        )
    }
);
