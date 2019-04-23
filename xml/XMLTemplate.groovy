package xml

deliveryHeaderTemplate = '''\
                <DeliveryHeader><isSecureTransportRequired>
                        <AffirmationIndicator>Yes</AffirmationIndicator>
                    </isSecureTransportRequired>
                    <messageDateTime>
                        <DateTimeStamp>$currentdateTime</DateTimeStamp>
                    </messageDateTime>
                     <%
                        attachments.each { attachment ->
                            print "<title>${attachment}</title>"
                        }
                    %>
                </DeliveryHeader>
            '''

//def engine = new XmlTemplateEngine("", false)
def engine = new groovy.text.SimpleTemplateEngine()
def binding = [currentdateTime: new Date(), attachments: ["123", "asdasdas", "3456uytjgh"]]

println engine.createTemplate(deliveryHeaderTemplate).make(binding).toString()