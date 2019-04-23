package xslt

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

println new File(".").absolutePath

File iscResources = new File(".")

def factory = TransformerFactory.newInstance()
//transformer = factory.newTransformer(new StreamSource(new StringReader(new File(iscResources, "extractXPaths.xsl").text)))
transformer = factory.newTransformer(new StreamSource(new StringReader(new File(iscResources, "simpleXPaths.xsl").text)))

ByteArrayOutputStream transformedStream = new ByteArrayOutputStream()
transformer.transform(new StreamSource(new StringReader(new File("InvoiceXCBLTest.xml").text)), new StreamResult(transformedStream))

println transformedStream.toString()


