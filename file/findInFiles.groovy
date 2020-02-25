package file

import groovy.io.FileType
import groovy.io.FileVisitResult
import util.GroovyUtils

import static groovy.io.FileType.*

String startFrom = "D:\\1dev"
String searchFor = "cpis-4358"
searchFor = "ac_batch_diaries.generate_batch_diaries"
searchFor = "AC0007.sql"

String oracleFileTypes = "pkb|pks|dat|vw|sql|trg|prc|ind"
String srcFiles = "java|groovy|rb|factories"
String webFiles = "rb|json|js|ts|css|html|jsp|less|scss|tag|tld"
String txtFiles = "xml|json|txt|properties"

String fileTypes =  oracleFileTypes

long filesSearched = 0
long kbSearched = 0
new File(startFrom).traverse(nameFilter: ~/.*\.(${fileTypes})/,
        type: FILES,
        maxDepth: -1,
        preDir: { if (it.name == '.svn') return FileVisitResult.SKIP_SUBTREE },) { file ->
    filesSearched++
    List<String> contents = file.readLines()
    kbSearched += file.size()
    boolean found = false
    String linesFound = ""

    contents.eachWithIndex { line, i ->
        if (line.toLowerCase().contains(searchFor.toLowerCase())) {
            found = true
            if (i > 0) linesFound += "${i-1}:  ${contents[i-1]}\n"
            linesFound +=  "${i}:  ${line}\n"
            if (i >= contents.size()) linesFound +=  "${i+1}:  ${contents[i+1]}"
        }
    }

    if (found) {
        println file
        println linesFound
        println "   -- files searched: ${filesSearched} : ${GroovyUtils.humanReadableByteCount(kbSearched)} "
    } else if (filesSearched % 1000 == 0) {
        println "   -- files searched: ${filesSearched} : ${GroovyUtils.humanReadableByteCount(kbSearched)} "
    }
}

println "   -- Total files searched: ${filesSearched} : ${GroovyUtils.humanReadableByteCount(kbSearched)} "


