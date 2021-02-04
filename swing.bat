@echo off
setlocal
set JAVA_TOOL_OPTIONS=
set CP=target\classes;target\test-classes
set SYSOPT=--enable-preview
java -cp %CP% %SYSOPT% %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
