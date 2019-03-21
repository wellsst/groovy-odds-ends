package misc

int envContentDailyCount = 7400

Date startDate = new Date()
Date endDate = new Date()
endDate.hours = endDate.hours - 5000

long diffHours
int nrAlreadyFetched
int lastSearchHrs
int i

testDocStatusHoursBack(endDate, startDate)
//testEnvContentHoursBack(endDate, startDate)

private List testDocStatusHoursBack(Date endDate, Date startDate) {
    def diffHours = timeDiffHours(endDate, startDate)
//println diffHours

    int nrAlreadyFetched = 0
    def docNumber = null
    def status = null

    int lastSearchHrs = 0
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getDocStatusHoursBackToSearch(diffHours, nrAlreadyFetched, i, docNumber, status)

        println " --- gives $lastSearchHrs hours "
    }

    println "**** Some records back this time"
    lastSearchHrs = 0
    diffHours = timeDiffHours(endDate, startDate)
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getDocStatusHoursBackToSearch(diffHours, nrAlreadyFetched, i, docNumber, status)
        nrAlreadyFetched += 50

        println " --- gives $lastSearchHrs hours "
    }

    println "**** Status no records back"
    lastSearchHrs = 0
    diffHours = timeDiffHours(endDate, startDate)
    status = [1, 2]
    nrAlreadyFetched = 0
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getDocStatusHoursBackToSearch(diffHours, nrAlreadyFetched, i, docNumber, status)

        println " --- gives $lastSearchHrs hours "
    }

    println "**** Status with records back"
    lastSearchHrs = 0
    diffHours = timeDiffHours(endDate, startDate)
    status = [1, 2]
    nrAlreadyFetched = 0
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getDocStatusHoursBackToSearch(diffHours, nrAlreadyFetched, i, docNumber, status)
        nrAlreadyFetched += 50
        println " --- gives $lastSearchHrs hours "
    }

    println "**** Just with docunumber"
    nrAlreadyFetched = 0
    docNumber = "23456"
    status = null
    diffHours = timeDiffHours(endDate, startDate)
    println getDocStatusHoursBackToSearch(diffHours, nrAlreadyFetched, 1, docNumber, status)
}

private List testEnvContentHoursBack(Date endDate, Date startDate) {
    def diffHours = timeDiffHours(endDate, startDate)
//println diffHours

    int nrAlreadyFetched = 0
    def correlId = null
    def docTypeList = null
    def buyerId = null
    def sellerId = null

    int lastSearchHrs = 0
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getEnvContentHoursBackToSearch(diffHours, nrAlreadyFetched, i, correlId, docTypeList, buyerId, sellerId)

        println " --- gives $lastSearchHrs hours "
    }

    println "**** Some records back this time"
    lastSearchHrs = 0
    diffHours = timeDiffHours(endDate, startDate)
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getEnvContentHoursBackToSearch(diffHours, nrAlreadyFetched, i, correlId, docTypeList, buyerId, sellerId)
        nrAlreadyFetched += 50

        println " --- gives $lastSearchHrs hours "
    }

    println "**** docTypeList no records back"
    lastSearchHrs = 0
    diffHours = timeDiffHours(endDate, startDate)
    docTypeList = ['1', '2']
    nrAlreadyFetched = 0
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getEnvContentHoursBackToSearch(diffHours, nrAlreadyFetched, i, correlId, docTypeList, buyerId, sellerId)

        println " --- gives $lastSearchHrs hours "
    }

    println "**** Status with records back"
    lastSearchHrs = 0
    diffHours = timeDiffHours(endDate, startDate)
    docTypeList = ['1', '2']
    nrAlreadyFetched = 0
    for (int i = 1; i <= 5; i++) {
        diffHours = diffHours - lastSearchHrs
        if (diffHours <= 0) {
            break
        }
        if (diffHours < lastSearchHrs) {
            diffHours = lastSearchHrs
        }
        lastSearchHrs = getEnvContentHoursBackToSearch(diffHours, nrAlreadyFetched, i, correlId, docTypeList, buyerId, sellerId)
        nrAlreadyFetched += 50
        println " --- gives $lastSearchHrs hours "
    }

    println "**** Just with correlId"
    nrAlreadyFetched = 0
    correlId = "23456"
    diffHours = timeDiffHours(endDate, startDate)
    println getEnvContentHoursBackToSearch(diffHours, nrAlreadyFetched, 1, correlId, docTypeList, buyerId, sellerId)
}

def getDocStatusHoursBackToSearch(long hoursAskedFor, int countAlreadyFetched, int searchCount, def docNumber, def status) {
    int docStatusDailyCount = 300000 // 200, 22000, 504000
    long maxHoursBack = 4382 // 6 months in hours, will get from config
    int maxDocSearchResults = 200 // max records to fetch, get from config
    int MIN_HOURLY_TXN_COUNT = 5 // Assumes on average there are up to 5 records related in DS
    int maxSearchExecutions = 5
    int subsequentRunPower = 5
    int minHourlyTxnCount = 15000 / 24

    println "Running for hoursAskedFor=$hoursAskedFor, countAlreadyFetched=$countAlreadyFetched, searchCount=$searchCount, docNumber=$docNumber, status=$status"
    println "      where docStatusDailyCount=$docStatusDailyCount"
    // The real method body starts here...

    int hoursBackToSearch = Math.min(maxHoursBack, hoursAskedFor)

    searchCount < 1 ? 1 : searchCount // Just in case

    // Doc Status search has params docNumber and status
    // docNumber should expand the hours to the max as this should be effiecent to call the SP once anyway
    if (docNumber) {
        return hoursBackToSearch
    }

    // Last allowed just return the remaining hours
    if (searchCount >= maxSearchExecutions) {
        return hoursBackToSearch
    }

    // How many can we expect in 1 hour...
    int recordsIn1Hr = docStatusDailyCount / 24  // eg Dev 10 ,UAT 200 , prod 4000

    // Under hourly estimated amount then just give back the asked for, good for Dev and most UAT
    if (recordsIn1Hr < minHourlyTxnCount) {
        return hoursBackToSearch
    }

    // Ask for a few more than the final result set required
    int maxDocStatus = maxDocSearchResults * MIN_HOURLY_TXN_COUNT // 1000

    /// The more records in 1 hours the less our initial search range should be...may get adjustedd a little by other calculations anyway
    int hoursChunk = 24*(1+((1/(Math.abs(recordsIn1Hr-maxDocStatus))*750)))

    // Assume now no docNumber but there could be a status
    // status is trickier, currently 82 statuses we use
    // anyone could get back a vastly diff nr results, and we can use 0...ALL
    // best way then?
    // Chop the askedFor hours up
    if (status) {
        hoursChunk = calcHoursChunkForNarrowedSearch(maxSearchExecutions, countAlreadyFetched, maxDocSearchResults, hoursBackToSearch)
    }
    hoursBackToSearch = Math.min(maxHoursBack, calcHoursBackToSearch(hoursChunk, searchCount, subsequentRunPower, recordsIn1Hr, maxDocStatus, hoursAskedFor))  // Dev 100, UAT 5, Prod 1

    hoursBackToSearch
}

private int calcHoursBackToSearch(int hoursChunk, int searchCount, int BATCH_POWER, int recordsIn1Hr, int maxDocStatus, long hoursAskedFor) {
    int hoursBackToSearch
// How many hrs back should we search to hopefully grab what we need
    def i = (hoursChunk * searchCount) as int  // Probably the first search
    def i1 = ((recordsIn1Hr / maxDocStatus) * (Math.pow(searchCount, BATCH_POWER))) as int // Will use this for later searches
    def max = Math.max(i, i1)
    hoursBackToSearch = Math.min(hoursAskedFor, max)
    hoursBackToSearch
}

private int calcHoursChunkForNarrowedSearch(int MAX_SEARCH_EXECUTIONS, int countAlreadyFetched, int MAX_TO_FETCH, int hoursBackToSearch) {
    float divFactor = 1 / MAX_SEARCH_EXECUTIONS

    // If we already ran a query then we can use that to see if we should expand again or not.
    if (countAlreadyFetched in 1..MAX_TO_FETCH) {
        divFactor = (MAX_TO_FETCH - countAlreadyFetched) / MAX_TO_FETCH / MAX_SEARCH_EXECUTIONS
    }
    (hoursBackToSearch * divFactor) as int
}

//String correlationId,  List<FunctionalDocType> docTypeList, String buyerId, String sellerId
def getEnvContentHoursBackToSearch(long hoursAskedFor, int countAlreadyFetched, int searchCount, def correlationId, def docTypeList, def buyerId, def sellerId) {
    int envContentDailyCount = 180000 // 200, 22000, 504000
    long maxHoursBack = 4382 // 6 months in hours, wil get from config
    int MAX_TO_FETCH = 200 // max records to fetch, get from config
    int maxSearchExecutions = 5
    int subsequentRunPower = 3     // subsequentRunPower
    int minHourlyTxnCount = 30000 / 24

    println "Running for hoursAskedFor=$hoursAskedFor, countAlreadyFetched=$countAlreadyFetched, searchCount=$searchCount, correlationId=$correlationId, status=$docTypeList, buyerId=$buyerId, sellerId=$sellerId"
    println "      where envContentDailyCount=$envContentDailyCount"
    // The real method body starts here...

    int hoursBackToSearch = Math.min(maxHoursBack, hoursAskedFor)

    searchCount < 1 ? 1 : searchCount // Just in case

    // correlationId should expand the hours to the max as this should be effiecent to call the SP once anyway
    if (correlationId) {
        return hoursBackToSearch
    }

    // Last allowed just return the remaining hours
    if (searchCount >= maxSearchExecutions) {
        return hoursBackToSearch
    }

    // How many can we expect in 1 hour...
    int recordsIn1Hr = envContentDailyCount / 24  // eg Dev 10 ,UAT 200 , prod 4000

    // Under hourly estimated amount then just give back the asked for, good for Dev and most UAT
    if (recordsIn1Hr < minHourlyTxnCount) {
        return hoursBackToSearch
    }

    int maxRecords = MAX_TO_FETCH

    int hoursChunk = 12 //(hoursBackToSearch / maxSearchExecutions) as int // min hours to search by

    // Assume now no docNumber but there could be a status
    // status is trickier, currently 82 statuses we use
    // anyone could get back a vastly diff nr results, and we can use 0...ALL
    // best way then?
    // Chop the askedFor hours up
    if (docTypeList || buyerId || sellerId) {
        hoursChunk = calcHoursChunkForNarrowedSearch(maxSearchExecutions, countAlreadyFetched, MAX_TO_FETCH, hoursBackToSearch)
    }

    // How many hrs back should we search to hopefully grab what we need
    hoursBackToSearch = Math.min(maxHoursBack, calcHoursBackToSearch(hoursChunk, searchCount, subsequentRunPower, recordsIn1Hr, maxRecords, hoursAskedFor))

    hoursBackToSearch
}


def timeDiffHours(aStart, aStop) {
    def result = Math.round(((aStop.time - aStart.time) / 1000 / 60 / 60) as double);
    return result;
}

/**
 * Returns difference b/w new Date and old date as Map holding difference in years, weeks, days, hrs, mins & secs
 */
public static Map diffInDates(Date oldDate, Date newDate = new Date()) {
    Long difference = newDate.time - oldDate.time
    Map diffMap = [:]
    difference = difference / 1000
    diffMap.seconds = difference % 60
    difference = diffMap.seconds / 60
    diffMap.minutes = difference % 60
    difference = diffMap.minutes / 60
    diffMap.hours = difference % 24
    difference = diffMap.hours / 24
    diffMap.years = (difference / 365).toInteger()
    if (diffMap.years)
        difference = (difference) % 365
    diffMap.days = difference % 7
    diffMap.weeks = (difference - diffMap.days) / 7
    return diffMap
}

/**
 def timeDiffInDetail = {attrs ->
 Map diff = DateUtil.getDiffernceInDates(attrs.oldDate, attrs.newDate ?: new Date())
 String result =  diff.years ? diff.years + " years " : ""
 result += diff.weeks ? diff.weeks + " weeks " : ""
 result += diff.days ? diff.days + " days " : ""
 result += diff.hours ? diff.hours + " hours " : ""
 result += diff.minutes ? diff.minutes + " minutes ago" : ""
 if (result)
 out << result
 else
 out << " 0 minutes ago"}*/