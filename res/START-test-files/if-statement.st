n is 15

if n mod 3 equals 0 {
    if n mod 5 equals 0 {
        write(n)
        write(" is a multiple of 3 and 5") nl
    }
    otherwise {
        write(n)
        write(" is a multiple of 3") nl
    }
}
otherwise{
    write(n)
    write(" is not a multiple of 3") nl
}

m is 11
if (n equals 15) and (m equals 20) {
    write("n is 15 and m is 11") nl
}
otherwise if (n equals 15) and (m equals 11) {
    write("n is 15 and m is 11") nl
}
otherwise{
    write("neither are correct") nl
}

a is 2
if a greater than 1 or a equals 0{
    write("a is greater than 1") nl
}