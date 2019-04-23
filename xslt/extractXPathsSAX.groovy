package xslt

import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

String xml = new File("InvoiceXCBLTest.xml").text
    // new FileInputStream("InvoiceXCBLTest.xml")

SAXParserFactory spf = SAXParserFactory.newInstance();
SAXParser sp = spf.newSAXParser();
XMLReader xr = sp.getXMLReader();

xr.setContentHandler(new FragmentContentHandler(xr));
xr.parse(new InputSource(new StringReader(xml)));