program pl0(input,output);
const
type
var
procedure error(n: integer);
procedure getsym;
   procedure getch;
procedure gen(x: fct; y,z: integer);
procedure test(s1,s2: symset; n: integer);
procedure block(lev,tx: integer; fsys: symset);
   procedure enter(k: object);
   function position(id: alfa): integer;
   procedure constdeclaration;
   procedure vardeclaration;
   procedure listcode;
   procedure statement(fsys: symset);
      procedure expression(fsys: symset);
         procedure term(fsys: symset);
            procedure factor(fsys: symset);
      procedure condition(fsys: symset);
procedure interpret;