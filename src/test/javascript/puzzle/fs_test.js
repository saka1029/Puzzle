const fs = require('fs');

fs.readFileSync('data/b_20250601.txt', "utf-8", (err, data) => {
//dataがファイルの中身、errは読み込み時のエラー
  if(data) {
    console.log(data);
  } else {
    console.log(err);
  }
});