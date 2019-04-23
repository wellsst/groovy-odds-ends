@Grapes(
        @Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='0.8.0')
)

package webservice

import wslite.soap.*

def client = new SOAPClient('http://ws.test.com:8080/Engine/AdminService/Admin')

response = client.send(SOAPAction: "insertUpdateRule") {
//response = client.send  {
    body {
            arg0() {
                ruleID('123456')
                documentType('Order')
                //ruleXML {mkp.yieldUnescaped "<Rule></Rule"}
                name(' WS Test 1')
                description('Testing WS call')
            }
            arg1 {
                userName('esportal')
                ipAddress('100.1.1.1')
                sessionId ('xyz')
                requestedAction('insertUpdateRule')
            }
        }
}
println "WS response: ${response.text}"

def soapResponse = new XmlSlurper().parseText(response.text)
println soapResponse