
package html

import groovyx.net.http.ContentType
@Grapes(@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1'))
import groovyx.net.http.HTTPBuilder
@Grapes(@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' ))

import groovyx.net.http.HTTPBuilder

def http = new HTTPBuilder('http://www.google.com')
http.get( path : '/search',
        contentType : ContentType.TEXT,
        query : [q:'?intitle:index.of?mp3 jump'] ) { resp, reader ->
    println "response status: ${resp.statusLine}"
    println 'Headers: -----------'
    resp.headers.each { h ->
        println " ${h.name} : ${h.value}"
    }

    String response = reader.text

    println 'Response data: -----'
    println  response
    println '--------------------'

    response = response - "<!doctype html>"
    doc = new XmlSlurper(false, false).parseText(response)
    println "Links..... "
    doc.depthFirst().collect { it }.findAll { it.name() == "a" }.each {
        println "     ${it.text()}, ${it.@href.text()}"
    }

}

