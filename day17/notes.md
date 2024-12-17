Example program:

```
p = (adv 3) (out A) (jnz 0)
#00:  117440  0  0  @0  (adv  3)      //  A = A/8
#01:  14680   0  0  @2  (out  A)      //  print A/8 %8
#02:  14680   0  0  @4  (jnz  0)      //  goto 0
#03:  14680   0  0  @0  (adv  3)      //  A = A/8/8
#04:  1835    0  0  @2  (out  A)      //  print A/8/8 %8
#05:  1835    0  0  @4  (jnz  0)      //  goto 0
#06:  1835    0  0  @0  (adv  3)      //  A = A/8/8/8
#07:  229     0  0  @2  (out  A)      //  print A/8/8/8 %8
#08:  229     0  0  @4  (jnz  0)      //  A = A/8/8/8/8
#09:  229     0  0  @0  (adv  3)      //  goto 0
#10:  28      0  0  @2  (out  A)      //  print A/8/8/8/8 %8
#11:  28      0  0  @4  (jnz  0)      //  A = A/8/8/8/8/8
#12:  28      0  0  @0  (adv  3)      //  goto 0
#13:  3       0  0  @2  (out  A)      //  print A/8/8/8/8/8 %8
#14:  3       0  0  @4  (jnz  0)      //  A = A/8/8/8/8/8/8
#15:  3       0  0  @0  (adv  3)      //  goto 0
#16:  0       0  0  @2  (out  A)      //  print A/8/8/8/8/8/8 %8
#17:  0       0  0  @4  (jnz  0)      //  (not  goto  0)
```

Input program:

```
Program: 2,4,1,2,7,5,1,7,4,4,0,3,5,5,3,0

p= (bst A) (bxl 2) (cdv B) (bxl 7) (bxc _) (adv 3) (out B) (jnz 0)

00: bst A -> B = A % 8          // (i.e. RHS 3 bits)
02: bxl 2 -> B = B xor 2        // i.e. toggle second bit (10)
04: cdv B -> C = A shr B        // i.e. div by 2^B
06: bxl 7 -> B = B xor 7        // i.e. toggle bottom 3 bits (111)
08: bxc _ -> B = B xor C        // i.e. toggle bits from C
10: adv 3 -> A = A shr 3        // i.e. div by 8
12: out B -> output(B % 8)      // output bottom 3 bits
14: jnz 0 -> goto 0 if A != 0   // exit or restart
```

```rust
A = a

loop {
  b1 = A & 111
  b2 = b1 ^ 010

  c = A >> b2

  b3 = b2 ^ 111
  b4 = b3 ^ c

  A = A / 8

  print (b4 & 111)

  if (A == 0) break
}
```

```rust
A = a
loop {
  print( (A&111 ^ 010 ^ 111 ^ ( A >> (A&111 ^ 010) )) &111 )
  A = A / 8
  if (A == 0) break
}
```

```rust
A = a
loop {
  print( (A&111 ^ 101 ^ ( A >> (A&111 ^ 010) )) &111 )
  A = A >> 3
  if (A == 0) break
}
```

## Observation 1: Need to make 16 loop iterations

Therefore: `8^15 < A < 8^16` (so we exit)
which is `A in 35,184,372,088,832..281,474,976,710,656`

## Observation 2: First number is 2

Therefore:
```
2
= ((((A % 8) ^ 101) ^ 111) ^ (A >> ((A % 8) ^ 101))) % 8
= (((A % 8) ^ 101 ^ 111) ^ (A >> ((A % 8) ^ 101))) % 8
= (((A % 8) ^ 010) ^ (A >> ((A % 8) ^ 101))) % 8
= ((0-7) ^ (A >> (0-7))) % 8
= ((0-7) ^ (A >> (0-7))) % 8
= 0-7
```

Not sure there's anything we can use here?

## Observation 3: Each output is based on 3 fewer bits of A

Output = `2,4,1,2,7,5,1,7,4,4,0,3,5,5,3,0`

```rust
2 = (A>>0   &111 ^ 101 ^ ( A>>0  >> (A>>0  &111 ^ 010) )) &111 ) // EQ00
4 = (A>>3   &111 ^ 101 ^ ( A>>3  >> (A>>3  &111 ^ 010) )) &111 ) // EQ01
1 = (A>>6   &111 ^ 101 ^ ( A>>6  >> (A>>6  &111 ^ 010) )) &111 ) // EQ02
//...
3 = (A>>42  &111 ^ 101 ^ ( A>>42 >> (A>>42 &111 ^ 010) )) &111 ) // EQ14
0 = (A>>45  &111 ^ 101 ^ ( A>>45 >> (A>>45 &111 ^ 010) )) &111 ) // EQ15
```

And on last iteration `A < 8` because otherwise we'd get another iteration.

So if we find `A>>45` then we'd know the first 3 bits of A, which would massively reduce the search space.

We are looking for a number, `0 <= x <= 7` that satisfies EQ15:
`0 = (x  &111 ^ 101 ^ ( x >> (x &111 ^ 010) )) &111 )`

Only 7 so let's brute force it:
- Given 0: 101
- Given 1: 100
- Given 2: 101
- Given 3: 111
- Given 4: 001
- Given 5: 000 // ← Winner
- Given 6: 011
- Given 7: 010

So `A` starts with the bits `101` (5), followed by `42` other bits; the search space is now 2^42 rather than 2^45.

Repeating that for EQ14:

We are looking for a number, `0 <= x <= 7` that satisfies EQ15:
`3 = (x  &111 ^ 101 ^ ( x >> (x &111 ^ 010) )) &111 )`

Only 7 so let's brute force it:
- Given 0: 111
- Given 1: 001
- Given 2: 101
- Given 3: 011 // ← Winner
- Given 4: 001
- Given 5: 000
- Given 6: 001
- Given 7: 011 // ← Winner

So `A` either starts with `101011....` or `101111.....` followed by `39` other bits.
