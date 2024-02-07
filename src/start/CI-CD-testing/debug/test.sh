#!/bin/bash

# Script to process a list of files using a Java JAR file



# List of files to process

files_to_process="addition arr-append arr-arrIndex arr-concat arr-length arr-remove comp division for-loop if-statement modulus multiplication not-paren powers print remove-all string-dex subtraction var-assign while-loop"



# Loop through each file in the list

for file_name in $files_to_process; do

    given_file_name=$file_name



    # Run Java JAR file with input files

    java -jar start-CI-debug.jar ../../../../res/START-test-files/${given_file_name}.st ${given_file_name}.txt

done



