#!/usr/bin/env groovy
package text

import org.apache.commons.lang.WordUtils

def cli = new CliBuilder(usage:'DicewarePhraseGen')

//cli.logfile(args:1, argName:'file', 'use given file for log')
cli.p(args: 1, 'nr of phrases')
cli.w(args: 1, 'words per phrase')
// cli.t('translate e to 3, t to 7')
// cli.c('capitalize subsequent words')

def options = cli.parse(args)

Map<String, String> wordMap = [:]
new File("diceware.wordlist.asc.txt").eachLine { line ->
    List fields = line.tokenize()
    wordMap.put(fields[0], fields[1])
}


int nrPhrases = Integer.parseInt(options.p)
int nrWords = Integer.parseInt(options.w)

(1..nrPhrases).each {
    String phrase = ""
    (1..nrWords).eachWithIndex { attempt, index ->
        String rollKey = ""
        (1..5).each { roll ->
            rollKey += new Random().nextInt(6) + 1
        }

        //println "   Rolled: ${rollKey}"
        String word = wordMap[rollKey]

        if (index >= 1) {
            word = WordUtils.capitalize(word)
        }

        //println "   Word: ${word}"
        phrase += word

    }

    println phrase
}
