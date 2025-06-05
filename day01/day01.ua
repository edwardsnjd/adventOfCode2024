#! /usr/bin/env -S uiua run

Input ← &rs ∞ 0

Lists ← °⊟⍉↯∞_2⋕⊜□¬∊" \n".
P₁    ← /+ ⌵- ⊙⍆⍆ Lists
P₂    ← /+ × ⟜(≡/+⊞⌕) Lists

⊃(P₂|P₁) Input
