package text

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.template.PebbleTemplate
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import java.text.DecimalFormat

/// @Grab(group='io.pebbletemplates', module='pebble', version='3.1.2')

String localFileName = "LocalDXYArea.json"

File local = new File(localFileName)

String statsText
// if file is old
boolean isLocalOld = false
use(TimeCategory) {
    isLocalOld = new Date(local.lastModified()) > new Date() - 1.hour //seems ot update every hour'ish
}
if (local.exists() && isLocalOld) {
    println "Using local file for data ..."
    statsText = local.text
} else {
    println "Grabbing updated data from git ..."
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

new File("covd_stats_${new Date().format("dd-MM-yy")}.html").write(contentFromTemplate(stats))

stats.sort().each { stat ->
    println stat
}

String contentFromTemplate(List<StatHolder> stats) {
    PebbleEngine engine = new PebbleEngine.Builder().build();
    PebbleTemplate compiledTemplate = engine.getTemplate("pctCalc2_tmpl.html");

    Map<String, Object> context = new HashMap<>();
    context.put("stats", stats);

    Writer writer = new StringWriter();
    compiledTemplate.evaluate(writer, context);

    writer.toString()
}

trait IgnoreUnknownProperties {
    def propertyMissing(String name, value) {
        // do nothing
    }
}

class JSONStatHolder implements IgnoreUnknownProperties, Comparable {
    DecimalFormat df = new DecimalFormat("0.00%")
    String countryEnglishName
    String provinceEnglishName
    String continentEnglishName

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
        return "${countryEnglishName} - ${provinceEnglishName} (${continentEnglishName}): \t\t\t\t\t ${deadCount} / ${confirmedCount} (current ${currentConfirmedCount})= \t\t ${getPct()}, suspect: ${suspectedCount}, cured: ${curedCount}"
    }

    @Override
    int compareTo(final Object o) {
        this.calcPct() <=> o.calcPct()
    }

    def propertyMissing(name, value) {
        // nothing
    }
}




















