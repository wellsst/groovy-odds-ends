package collections

def envelopeTypes = ["MIME", "QSOAP", "XCBL", "CXML", "OAGI", "RNIF", "XML"]

def listOfMaps = envelopeTypes.collect { ["name": it] }
println listOfMaps
println listOfMaps*.getAt("name")   // and back again

