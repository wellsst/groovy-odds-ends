package xml

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

//http://stackoverflow.com/questions/1307919/using-variables-in-xmlslurpers-gpath
def getNodes = { doc, path ->
    def nodes = doc
    path.split("/").each {
        nodes = nodes."${it}"
        //println "Nodes: ${nodes} : ${it}"
    }
    return nodes
}


def input = '''
<shopping>
    <category type="groceries">
        <item>Chocolate</item>
        <item>Coffee
        <thing>Things1</thing>
        </item>

    </category>
    <category type="supplies">
        <item quantity="4">Paper</item>
        <item quantity="4">Pens
        <thing>Things2</thing>
        </item>
    </category>
    <category type="present">
        <item when="Aug 10">
        <thing>Things3</thing>
        Kathryn's Birthday</item>
    </category>
</shopping>
'''
def root = new XmlSlurper().parseText(input)

root.@attributeName = 'attributeValue'

/*def g = root.category.find{ it.@type == 'groceries' }
println g
g = "Atrribure"
println g*/

def pens = root.category.find{ it.@type == 'supplies' }.item.findAll{ it.text() == 'Paper' }
pens.each { p ->
     println "Paper: ${p}"
     p.@quantity = (p.@quantity.toInteger() + 2).toString()
     p.@when = 'Urgent'
}

println XmlUtil.serialize(root)

/// Updating xpath
def trimXPath = "category/item/thing" //.replace("/", ".")
def nodes = getNodes(root, trimXPath)
println "Type: ${nodes[0].getClass()}"

nodes.each { println "PRE * $it" }

nodes[1] = "updated"

nodes.each { println "POST * $it" }

def outputBuilder = new StreamingMarkupBuilder()
String result = outputBuilder.bind{ mkp.yield root }

println result

// Updating an attribute/ cant update one directly eg /a/@b returns an attribute object which can't be updated, you have to get 'a' as a Node then do a.@b = 'abc'
trimXPath = "category/item/thing" //.replace("/", ".")
String attrRegex = /^(.*)\/@(.*)$/
matcher = (trimXPath =~ attrRegex)

nodes = getNodes(root, matcher[0][1])

nodes.each { println "ATTR PRE * ${it.text()}" }

println "Has attr: ${matcher[0][2]}"

if (matcher[0][2]) {
    nodes[0]."@${matcher[0][2]}" = "attr updated"
}
else {
    nodes[0] = "attr updated"
}

nodes.each { println "ATTR POST * $it" }

outputBuilder = new StreamingMarkupBuilder()
result = outputBuilder.bind{ mkp.yield root }

println result

println XmlUtil.serialize(root)