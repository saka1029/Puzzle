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
    if (count % 200 == 0) {
//    if (true) {
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
    console.log(trie.toString());
});


//////////
//    const fs = require('fs');
//    const readline = require('readline');
//    // テキストファイル1行ごとの処理
//    async function processLineByLine() {
//        const fileStream = fs.createReadStream('input.txt');
//        // crlfDelay オプションを使用して、改行コードが'\r\n'の場合も1行として処理する
//        const rl = readline.createInterface({
//            input: fileStream,
//            crlfDelay: Infinity
//        });
//        // for awaitで1行ずつ処理
//        for await (const line of rl) {
//            console.log(`Line from file: ${line}`);
//            // ループ内でデータベース検索など、awaitな処理を実行できる
//            // await somePromise();
//        }
//    }
//    processLineByLine();

