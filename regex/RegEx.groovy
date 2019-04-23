package regex

println "test[123][sdfsdf][]".replaceAll(/\[(.+?)\]/, {".$it"})
println "test[123][sdfsdf][]".replaceAll(/\[(.+?)\]/, /.$1/).replaceAll(/\[\]/, "")



"612-555-1212".replaceAll(/(\d{3})-(\d{3})-(\d{4})/) { fullMatch, areaCode, exchange, stationNumber ->
    assert fullMatch == "612-555-1212"
    assert areaCode == "612"
    assert exchange == "555"
    assert stationNumber == "1212"
    return "$areaCode-###-####"
}

String path = "/pidx:Invoice/pidx:InvPath2/pidx:InvPath333"

String ns = path.replaceAll(/\/*(.*):(.*)/) { fullMatch, ns, xpath ->
    //assert ns == "pidx"
    //assert xpath == "Invoice"

    return "$ns - $xpath"
}

println "ns: ${ns}"
 path.replaceAll(/(?=\:)(?<=\/)/) { l ->
    println "l: $l"
}
println "PIDX 2: " + path.replaceAll(/\/pidx:(.*)\//, { ".$it" })

println ""

path.split("(?=:)(.*)(?=/)").each {
    println "Split: $it"
}


//println path - /\/pidx:/

println "test[123][sdfsdf][]".replaceAll(/\[(.+?)\]/, {".$it"})
println "test[123][sdfsdf][]".replaceAll(/\[(.+?)\]/, /.$1/).replaceAll(/\[\]/, "")


def nsList = path.findAll(/\/(.*?):/) { match -> "${match[1]}"}.unique()
path.replaceAll("${nsList[0]}:", "")