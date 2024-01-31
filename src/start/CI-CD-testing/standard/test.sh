#!/bin/bash

# List of files to process
files_to_process="addition arr-append arr-arrIndex arr-concat arr-length arr-remove comments comp division for-loop functions if-statement modulus multiplication not-paren powers print remove-all string-dex subtraction var-assign while-loop"

for file_name in $files_to_process; do
    given_file_name=$file_name

    java -jar ./start-complete.jar ../../../../res/START-test-files/${given_file_name}.st > current.txt

    # compare the output of current.txt to ${given_file_name}.txt
    cmp -s current.txt ${given_file_name}.txt
    if [ $? -ne 0 ]; then
        echo "Error: Files are not the same for ${given_file_name}."
        exit 1
    else
        echo "Success: Files are the same for ${given_file_name}."
    fi
done
echo "All tests passed."