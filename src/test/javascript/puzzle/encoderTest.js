const { TrieEncoder } = require("./myLib");
const ENCODER = require("./encoder");

const name = "急性潰瘍性大腸炎";
const encs = ENCODER.encode(name);
for (const line of encs) {
    let s = "";
    for (const e of line)
        s += ` ${e.data}:${name.substring(e.start, e.end)}`;
    console.log(s);
}
