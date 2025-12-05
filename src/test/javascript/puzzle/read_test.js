const fs = require('fs');
const readline = require('readline');
const iconv = require("iconv-lite");
const TrieEncoder = require("./TrieEncoder");

function remq(s) {
    return s.substring(1, s.length - 1);
}

const trie = new TrieEncoder();
let count = 0;

const stream = fs.createReadStream('data/レセ電/b_20200601.txt')
    .pipe(iconv.decodeStream("Shift_JIS"));
const reader = readline.createInterface({ input: stream });
reader.on('line', (line) => {
    const items = line.split(",");
    const code = remq(items[2]);
    const name = remq(items[5]);
    if (code != "0000999") {
        // console.log(`${count}:${code}:${name}`)
        trie.put(name, code);
    }
    ++count;
});
reader.on('close', () => {
    const stream = fs.createReadStream('data/レセ電/z_20200601.txt')
        .pipe(iconv.decodeStream("Shift_JIS"));
    const reader = readline.createInterface({ input: stream });
    reader.on('line', (line) => {
        const items = line.split(",");
        const code = remq(items[2]);
        const name = remq(items[6]);
        trie.put(name, code);
        ++count;
    });
    reader.on('close', () => {
        console.log(`count=${count}`);
        // console.log(trie.toString());
        const byomei = "急性潰瘍性大腸炎";
        console.log(JSON.stringify(trie.encode(byomei), null, 2));
    });
});
