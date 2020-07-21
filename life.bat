setlocal
set JAVA_TOOL_OPTIONS=
set CP=target\classes;target\test-classes
set SYSOPT=--enable-preview
set CLASS=test.puzzle.TestLifeGame
java -cp %CP% %SYSOPT% %CLASS%
endlocal
