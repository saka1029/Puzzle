const fs = require('fs');
const Trie = require("./trie");
const readline = require('readline');

const stream = fs.createReadStream('data/b_20250601.txt', 'utf8');
const reader = readline.createInterface({ input: stream });
const trie = new Trie();
let count = 0;
function remq(s) {
    return s.substring(1, s.length - 1);
}
reader.on('line', (line) => {
    // console.log(line);
    // if (count % 200 == 0) {
    if (true) {
        const items = line.split(",");
        const code = remq(items[2]);
        const name = remq(items[5]);
        if (code != "0000999") {
            // console.log(`${count}:${code}:${name}`)
            trie.insert(name, code);
        }
    }
    ++count;
});
reader.on('close', () => {
    console.log(`count=${count}`);
    // console.log(trie.toString());
});