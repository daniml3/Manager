#!/bin/bash

if [[ $(which clang-format) == "" ]]; then
 echo "clang-format is not installed"
 exit 1
fi

OLDIFS=$IFS	
IFS=$'\n'
for file in $(find . -iname *.java); do
 echo "Formatting $file"
 echo $file | xargs clang-format -i
done

export IFS=$OLDIFS