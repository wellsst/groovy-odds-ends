package datetimes

String dateString = "28/09/2010 16:02:43";
def newDate = Date.parse("d/M/yyyy H:m:s", dateString)
println newDate

dateString = "1912-07-21T23:42:51Z";
println Date.parse("yyyy-MM-dd'T'HH:mm:ss'Z'", dateString)

dateString = "Thu Aug 01 2013"
println Date.parse("EEE MMM dd yyyy", dateString)

dateString = "1912-07-21T23:42:51Z";
println Date.parse("d/M/yyyy H:m:s", dateString)


