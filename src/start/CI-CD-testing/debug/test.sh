#!/bin/bash

# List of files to process
#add comments and functions later
files_to_process="addition arr-append arr-arrIndex arr-concat arr-length arr-remove comp division for-loop if-statement modulus multiplication not-paren powers print remove-all string-dex subtraction var-assign while-loop"

for file_name in $files_to_process; do
    given_file_name=$file_name

    java -jar start-CI-debug.jar ../../../../res/START-test-files/${given_file_name}.st addition.txt
done

