@echo off
for /r %%t in (*.java) do echo Formatting %%t && clang-format -i -style=file "%%t"