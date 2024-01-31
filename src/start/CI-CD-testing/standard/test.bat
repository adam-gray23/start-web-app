@echo off
setlocal enabledelayedexpansion

rem List of files to process
set "files_to_process=addition arr-append arr-arrIndex arr-concat arr-length arr-remove comments comp division for-loop functions if-statement modulus multiplication not-paren powers print remove-all string-dex subtraction var-assign while-loop"

for %%f in (%files_to_process%) do (
    set "given_file_name=%%f"

    java -jar .\start-complete.jar ..\..\..\..\res\START-test-files\!given_file_name!.st > current.txt

    @REM compare the output of current.txt to the !given_file_name!.txt
    fc current.txt !given_file_name!.txt > nul
    if errorlevel 1 (
        echo Error: Files are not the same for !given_file_name!.
        exit /b 1
    ) else (
        echo Success: Files are the same for !given_file_name!.
    )
)