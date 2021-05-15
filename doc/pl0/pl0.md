# PL/0コンパイラ
## 参照

[PL/0 – Pascal for small machines](http://pascal.hansotten.com/niklaus-wirth/pl0/)

## 文法

ただしstatementの内、`'?' ident`および`'!' expression`は実装していない。

```
program   = block '.'
block     = [ 'const' ident '=' number {, ident = number} ';' ]
            [ 'var' ident { ',' ident } ';' ]
            { 'procedure' ident ';' block ';' } statement
statement = [ ident ':=' expression
              | 'call' ident
              | '?' ident
              | '!' expression
              | 'begin' statement { ';' statement } 'end'
              | 'if' condition 'then' statement
              | 'while' condition 'do' statement ]
condition  = 'odd' expression
              | expression ( '==' | '!=' | '<' | '<=' | '>' | '>=' ) expression
expression = [ '+' | '-' ] term { ( '+' | '-' ) term }
term       = factor { ( '*' | '/' ) factor }
factor     = ident | number | '(' expression ')'
```

## スタックの使い方

### cal実行後のスタック

`pc`、`bp`およびリンクポインタをスタックに積む。
ただし`sp`自体は更新しない点に注意する。

```
            |                       | スタックは上に向かって成長する。
            |  old pc               |
            |  old bp               |
      bp -> |  base(l)              |
      sp -> |                       |
            |                       |
```

### コード

呼び出し先コードの先頭には`inc`命令（元の資料では`int`命令）があり、
ローカル変数を確保する。
`procedure`は`int`命令で始まり、`opr 0, 0`で終わる。

```
            |                       | 
      a  -> |  inc 0, n             | nは確保すべきローカル変数の数
            |                       |
            |  opr 0, 0             | return命令
            |                       |
      pc -> |  cal 0, a             | aは呼び出し先のアドレス
            |                       |
```

