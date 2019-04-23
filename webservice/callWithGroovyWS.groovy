
@Grab(group='org.codehaus.groovy.modules', module='groovyws', version='0.5.2')

package webservice

import groovyx.net.ws.WSClient
println "1"
proxy = new WSClient("http://www.w3schools.com/webservices/tempconvert.asmx?WSDL", this.class.classLoader)
println "2"
proxy.initialize()
println "3"

result = proxy.CelsiusToFahrenheit(0)
println "You are probably freezing at ${result} degrees Farhenheit"