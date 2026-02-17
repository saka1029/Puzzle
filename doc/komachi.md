# Komachi

# RPNによる表現

4つの数値A, B, C, D
と3つの演算子x, y, z
の組み合わせで考えてみる。

```
A B x C y D z  ->  ((A x B) y C) z D
A B x C D y z  ->  (A x B) z (C y D)
A B C x y D z  ->  (A y (B x C)) z D
A B C x D y z  ->   A z ((B x C) y D)
A B C D x y z  ->   A z (B y (C x D))
```