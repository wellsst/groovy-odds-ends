package http

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.2')
import groovyx.net.http.*


def http = new HTTPBuilder('http://10.14.6.164/')

def (resp, responseStatus) = http.post(body: "Testing castor") { resp, reader ->
    [resp, resp.status]
}

// http://groovy.codehaus.org/modules/http-builder/apidocs/groovyx/net/http/HttpResponseDecorator.html
println resp.allHeaders
println responseStatus

/*

def urlConnect = new URL("http://10.14.6.164/")
def connection = urlConnect.openConnection()
//Set all of your needed headers
connection.setRequestProperty("User-Agent", "CASTor Client/0.1")
connection.setRequestProperty("Content-Type", "text/plain")
connection.setRequestProperty("Content-Length", "10")
connection.setRequestProperty("Host", "castor.caringo.com")

if(connection.responseCode == 200){
    responseText = connection.content.text
}
else{
    println "An error occurred:"
    println connection.responseCode
    println connection.responseMessage
}
*/
