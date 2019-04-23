package text

def xhtml = """
<body>
<h1> At: 9 &#8230; h1. level 1 heading</h1>
<h2>level 2 heading</h2>
<h3>level 3 heading</h3>
<h4>level 4 heading</h4>
<blockquote>
<p>this is blockquoted text</p>
</blockquote>
</body>
"""

def records = new XmlSlurper().parseText(xhtml)
def allNodes = records.depthFirst().collect{ it }
def list = []
allNodes.each {
    it.text().tokenize().each {
        list << it
    }
}
println list.size()

list.each {
println it
}

// Good candidate for a closure
int count = 0
allNodes.each {
    it.text().tokenize().each {
        count++
    }
}

println count
