const fs = require('fs');
const readline = require('readline');

const stream = fs.createReadStream('data/b_20250601.txt', 'utf8');
const reader = readline.createInterface({ input: stream });
let count = 0;
function remq(s) {
    return s.substring(1, s.length - 2);
}
reader.on('line', (line) => {
    // console.log(line);
    if (count % 100 == 0) {
        const items = line.split(",");
        console.log(`${count}:${remq(items[2])}:${remq(items[5])}`)
    }
    ++count;
});
reader.on('close', () => {
    console.log(`count=${count}`);
});