package text

import java.text.DecimalFormat

String rawStats = """
Mainland China: 80151 cases, 2945 deaths
South Korea: 5186 cases, 28 deaths
Italy: 2036 cases, 79 deaths
Iran: 2336 cases, 77 deaths
Japan: 979 cases (705 from the Diamond Princess cruise ship), 12 deaths (6 from the Diamond Princess)
France: 191 cases, 3 deaths
Germany: 165 cases
Spain: 153 cases, 1 death
Singapore: 106 cases
United States: 105 cases, 6 deaths
Hong Kong: 100 cases, 2 deaths
Thailand: 43 cases, 1 death
Taiwan: 42 cases, 1 death
Australia: 38 cases, 1 death
"""

List<StatHolder> stats = []
rawStats.eachLine { line ->

    try {
        def (location, cases, ignore, kaput) = line.split(/(: | cases|,| death)/)
        StatHolder stat = new StatHolder()
        stat.setCases(cases)
        stat.setKaput(kaput)
        stat.location = location
        stats << stat
    } catch (all) {
        // println "Error ${all.message} on: '${line}'"
    }
}

stats.sort().each { stat ->
    println stat
}


class StatHolder implements Comparable {
    DecimalFormat df = new DecimalFormat("0.00%")
    String location
    int cases
    int kaput

    void setCases(String c) {
        cases = (c as String).toInteger()
    }

    void setKaput(String c) {
        kaput = (c as String).toInteger()
    }

    String getPct() {
        df.format(calcPct())
    }

    float calcPct() {
        kaput / cases
    }


    @Override
    public String toString() {
        return "${location}: \t\t\t ${kaput} / ${cases} = \t ${getPct()} "
    }

    @Override
    int compareTo(final Object o) {
        this.calcPct() <=> o.calcPct()
    }
}




















