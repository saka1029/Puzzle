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
              | expression ( '=' | '#' | '<' | '<=' | '>' | '>=' ) expression
expression = [ '+' | '-' ] term { ( '+' | '-' ) term }
term       = factor { ( '*' | '/' ) factor }
factor     = ident | number | '(' expression ')'
```

## コード生成

```

            0:jmp l=0 a=7                             goto #main#
            Symbol(#main#:proc, level=0, adr=7)
const ZERO = 0, ONE = 1, TWO = 2, THREE = 3;
            Symbol(ZERO:constant, level=0, adr=0)
            Symbol(ONE:constant, level=0, adr=1)
            Symbol(TWO:constant, level=0, adr=2)
            Symbol(THREE:constant, level=0, adr=3)
var x, y, z, ok;
            Symbol(x:variable, level=0, adr=3)
            Symbol(y:variable, level=0, adr=4)
            Symbol(z:variable, level=0, adr=5)
            Symbol(ok:variable, level=0, adr=6)
procedure addXandY;
            Symbol(addXandY:proc, level=0, adr=1)
      var x, y;
            Symbol(x:variable, level=1, adr=3)
            Symbol(y:variable, level=1, adr=4)
begin                                                 addXandY
            1:inc l=0 a=5
      z := x + y
            2:lod l=0 a=3 x
            3:lod l=0 a=4 y
            4:opr + l=0 a=2
            5:sto l=1 a=5 z
end;
            6:opr return l=0 a=0
begin                                                 #main#
            7:inc l=0 a=7
      ok := 0;
            8:lit l=0 a=0
            9:sto l=0 a=6
      x := ONE;
            10:lit l=0 a=1
            11:sto l=-1 a=3
      y := TWO;
            12:lit l=0 a=2
            13:sto l=-1 a=4
      call addXandY;
            14:cal l=0 a=1
      if z = THREE then
            15:lod l=0 a=5  z
            16:lit l=0 a=3  THREE
            17:opr = l=0 a=8
            18:jpc l=0 a=22
      ok := -1
            19:lit l=0 a=1
            20:opr negate l=0 a=1
            21:sto l=0 a=6  ok
end.
            22:opr return l=0 a=0
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

