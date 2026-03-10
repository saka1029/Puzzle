const zero = 0, one = 1, two = 2, three = 3;
var x, y, z, ok;
procedure addxandy;
    var x, y;
begin
    z := x + y
end;
begin
    ok := 0;
    x := one;
    y := two;
    call addxandy;
    if z = three then
        ok := -1
end.