package zip

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


String replaceLast(String string, String substring, String replacement)
{
    int index = string.lastIndexOf(substring)
    if (index == -1)
        return string
    return string.substring(0, index) + replacement + string.substring(index+substring.length())
}

private String zipFileNameToServiceName(String zipFileName) {
    zipFileName = zipFileName.replaceFirst("ns/", "")
    zipFileName = zipFileName - "/flow.xml"
    zipFileName = zipFileName.tr("/", ".")
    zipFileName = replaceLast(zipFileName, ".", ":")
    zipFileName
}

//ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(Base64.decodeBase64(fileContents)))
ZipInputStream zis = new ZipInputStream(new FileInputStream("Services.zip"))
ZipEntry zipEntry = null
while ((zipEntry = zis.getNextEntry()) != null) {
    String fileEntryName = zipEntry.name
    if (fileEntryName.endsWith("flow.xml")) {
        // Get the zip entries to check for being locked
        //println fileEntryName // todo convert to proper service name

        println zipFileNameToServiceName(fileEntryName)
    } else if (fileEntryName.endsWith("manifest.v3")) {
        // Check the manifest file for the package name
        /*
        Looks like
        <?xml version="1.0" encoding="UTF-8"?>
            <Values version="2.0">
              <value name="name">ESPortal_Services</value>
              ...
         */

        BufferedReader reader = new BufferedReader(new InputStreamReader(zis))
        String xmlString = ""
        String line = ""
        while ((line = reader.readLine()) != null) {
            xmlString += line
        }
        println xmlString

        def manifestXml = new XmlParser(false, false).parseText(xmlString)

        packageName =  manifestXml.'*'.find { node ->
            node.@name == "name"
        }.text()

        println packageName
    }
}