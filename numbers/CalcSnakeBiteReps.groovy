package numbers

(6..30).each { nrTimes ->
    int i = 0
    nrTimes.times {
        i += it
    }
    println "${nrTimes} reps you do ${i} of each"
}


/**
 6 reps you do 15 of each
 7 reps you do 21 of each
 8 reps you do 28 of each
 9 reps you do 36 of each
 10 reps you do 45 of each
 11 reps you do 55 of each
 12 reps you do 66 of each
 13 reps you do 78 of each
 14 reps you do 91 of each
 15 reps you do 105 of each
 16 reps you do 120 of each
 17 reps you do 136 of each
 18 reps you do 153 of each
 19 reps you do 171 of each
 20 reps you do 190 of each
 21 reps you do 210 of each
 22 reps you do 231 of each
 23 reps you do 253 of each
 24 reps you do 276 of each
 25 reps you do 300 of each
 26 reps you do 325 of each
 27 reps you do 351 of each
 28 reps you do 378 of each
 29 reps you do 406 of each
 30 reps you do 435 of each
 */