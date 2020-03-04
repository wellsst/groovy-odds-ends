package text

import groovy.json.JsonSlurper
import org.codehaus.groovy.ast.tools.BeanUtils
import org.codehaus.groovy.runtime.InvokerHelper

import java.text.DecimalFormat

String localFileName = "LocalDXYArea.json"

File local = new File(localFileName)

String statsText
// if file is old
if (local.exists() && new Date(local.lastModified()) > new Date()-1) {
    statsText = local.text
} else {
    statsText = new URL("https://raw.githubusercontent.com/BlankerL/DXY-COVID-19-Data/master/json/DXYArea.json").text
    local.write(statsText)
}

def rawStats = new JsonSlurper().parseText(statsText)

List<StatHolder> stats = []
rawStats.results.each { city ->
    try {
        JSONStatHolder stat = new JSONStatHolder(city)
        stats << stat
    } catch (all) {
        // println "Error ${all.message} on: '${city}'"
    }
}

stats.sort().each { stat ->
    println stat
}

trait IgnoreUnknownProperties {
    def propertyMissing(String name, value){
        // do nothing
    }
}

class JSONStatHolder implements IgnoreUnknownProperties, Comparable {
    DecimalFormat df = new DecimalFormat("0.00%")
    String countryEnglishName
    String provinceEnglishName

    Integer currentConfirmedCount
    Integer confirmedCount
    Integer deadCount
    Integer suspectedCount
    Integer curedCount

    String getPct() {
        df.format(calcPct())
    }

    float calcPct() {
        deadCount / confirmedCount
    }

    @Override
    public String toString() {
        return "${countryEnglishName} - ${provinceEnglishName}: \t\t\t ${deadCount} / ${confirmedCount} (current ${currentConfirmedCount})= \t ${getPct()}, suspect: ${suspectedCount}, cured: ${curedCount}"
    }

    @Override
    int compareTo(final Object o) {
        this.calcPct() <=> o.calcPct()
    }

    def propertyMissing(name, value) {
        // nothing
    }
}




















