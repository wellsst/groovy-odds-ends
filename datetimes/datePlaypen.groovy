package datetimes
// playing with hours...

def d = new Date()
d.hours = d.hours-4000  /// It rolls!!!
println "Today ${d}"
println "Today hours ${d.hours}"
println "Today hours HOUR_OF_DAY ${d.getAt(Calendar.HOUR_OF_DAY)}"
println "Today hours HOUR ${d.getAt(Calendar.HOUR)}"

d.hours = 20
println "Today hours manualy back ${d.hours}"

d.hours -= 5
println "Today hours less using -= ${d.hours}"

1.upto(6) {
    d.hours = d.hours - it
    println "date subtracted in loop: $d"
}

// Playing with Days
Date day1 = new Date()
Date day2 = new Date() - 4
println "Days diff: ${day1-day2}"

// Playing with Hours calcs
day1 = new Date()
day2 = new Date() - 4
println "Hours diff: ${24*(day1-day2)}"
