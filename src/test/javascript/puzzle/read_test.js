const fs = require('fs');
const readline = require('readline');

const stream = fs.createReadStream('data/b_20250601.txt', 'utf8');
const reader = readline.createInterface({ input: stream });
let count = 0;
reader.on('line', (line) => {
    // console.log(line);
    ++count;
});
reader.on('close', () => {
    console.log(`count=${count}`);
});