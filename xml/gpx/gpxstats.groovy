package xml.gpx

import groovy.io.FileType
import groovy.time.TimeCategory
import groovy.time.TimeDuration

import java.text.SimpleDateFormat

// Map hrZones = ['Warm up': 102, 'Easy': 111, 'Aerobic': 129, 'Threshold': 164, 'Maximum': 185]


Date now = new Date()

new File('.').eachFileMatch(FileType.ANY, ~/.*gpx/) { gpxFile ->
    // println gpxFile.text

    List<HRZone> hrZones =
        [new HRZone(lowerBoundary: 0, upperBoundary: 100, name: 'Warm up'),
                new HRZone(lowerBoundary: 101, upperBoundary: 112, name: 'Easy'),
                new HRZone(lowerBoundary: 113, upperBoundary: 130, name: 'Aerobic'),
                new HRZone(lowerBoundary: 131, upperBoundary: 175, name: 'Threshold'),
                new HRZone(lowerBoundary: 176, upperBoundary: 999, name: 'Maximum')]


    def gpxXml = new XmlParser(false, false).parseText(gpxFile.text)

    List trackPointNodes = gpxXml.trk.trkseg

    // Keep track of the "last" zone info since we get the date/time diff from the next node to calc what the first one should be
    // Could interpolate the heart rates between the time samples but for simplicity lets just say that the first range "owns" all the ranges in between...close to accurate?  better than guess in-between values?

    Date activityStartDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(gpxXml.metadata.time.text()) // 2015-11-14T23:15:21.000Z
    println "******************  ${gpxXml.trk.name.text()} on ${activityStartDate} ******************"
    int lastGoodHR = 0
    trackPointNodes[0].eachWithIndex { trackPointNode, idx ->
        Date trkptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(trackPointNode.time.text())
        def hrNode = trackPointNode.depthFirst().find { it.name() == "gpxtpx:hr" }?.value() // the nodes in a Garmin gpx for heartrates, use the last known HR for times when it skips
        int hr
        if (hrNode) {
            hr = hrNode[0]?.toInteger() ?: lastGoodHR
        }
        BigDecimal lon = trackPointNode.@lon.toBigDecimal()
        BigDecimal lat = trackPointNode.@lat.toBigDecimal()

        // TODO calculate the distance between 2 points


        BigDecimal ele = new BigDecimal(trackPointNode.ele[0].text())
        Date time = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", trackPointNode.time.text())

        // get the next node so we can get the date
        int nextIdx = idx + 1
        int durationInSecs = 1
        if (idx + 1 == trackPointNodes[0].trkpt.size()) {
            nextIdx = idx // this must be the last node then
        }
        def nextTrkPtNode = trackPointNodes[0].trkpt[nextIdx]

        //if (nextTrkPtNode) {
        Date nextTrkptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(nextTrkPtNode.time.text())
        TimeDuration duration = TimeCategory.minus(nextTrkptDate, trkptDate)
        durationInSecs = duration.toMilliseconds() / 1000
        if (durationInSecs == 0) {   // mostly covers the last node in the data
            durationInSecs = 1
        }

        HRZone hrZone = hrZones.find { hrZone -> hr >= hrZone.lowerBoundary && hr <= hrZone.upperBoundary }

        // TODO: What to do when this is null?....eg go back to using lastKnownGood
        if (hrZone) {
            hrZone.timeInZoneInSecs += durationInSecs
        }

        // String targetZone = hrZones.find {k,v-> hr >= v}
        // println "trk point: ${trkptDate}-${nextTrkptDate}=${durationInSecs} @ ${hr} bpm in zone ${hrZone.name} "
        lastGoodHR = hr
    }

    println hrZones

    /*List heartRates = gpxXml.depthFirst().findAll { node ->
        node.name() == "gpxtpx:hr" // the nodes in a Garmin gpx for heartrates
    }

    List allHR = heartRates*.value().flatten()
    //println allHR
    Date activityStartDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(gpxXml.metadata.time.text()) // 2015-11-14T23:15:21.000Z

    // TODO: Could add in how long ago this was.


    //println allHR.findAll { (it as int) in (0..80)}

    int priorThreshold = 0
    hrZones.each { zoneName, threshold ->
        List zoneHR = allHR.findAll { (it as int) in (priorThreshold..threshold + 1) }
        println "${zoneName}: ${zoneHR.size()} ${zoneHR}"

        // TODO: Since HR samples are taken at random times we need to do a bit of work with dates and the HR for each to calc the time in zone


        priorThreshold = threshold
    }*/
    println "        -------------------"
}

class HRZone {
    int lowerBoundary
    int upperBoundary
    String name

    int timeInZoneInSecs = 0


    @Override
    public java.lang.String toString() {
        int hours = timeInZoneInSecs / 60 / 60
        int mins = (timeInZoneInSecs / 60 as int) % 60
        "In zone ${name} for ${new TimeDuration(hours, mins, timeInZoneInSecs % 60, 0).toString() - ".000"}"

        // todo: check the total time
        // eg for wello pt kayak start end in gpx is:
        /*
        <time>2015-12-22T21:21:04</time>
        <time>2015-12-22T22:32:31.000Z</time>*/
    }
}