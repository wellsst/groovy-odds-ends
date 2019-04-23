package xml

def getNodes = { doc, path ->
    def nodes = doc

    path.split("/").each {
        println it
        nodes = nodes."${it}"
        println nodes
    }
    return nodes
}

def root = new groovy.util.XmlSlurper(false, true).parseText(new File("pidx.xml").text)

def nodes = getNodes(root, "pidx:InvoiceProperties/pidx:InvoiceNumber")
nodes = getNodes(root, "InvoiceProperties")

String documentIdentifier = nodes[0]

println documentIdentifier