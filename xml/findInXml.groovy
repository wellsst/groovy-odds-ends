package xml


String xmlString = """<?xml version="1.0" encoding="UTF-8"?>

<Values version="2.0">
  <value name="enabled">yes</value>
  <value name="system_package">no</value>
  <value name="version">1.0</value>
  <null name="startup_services"/>
  <null name="shutdown_services"/>
  <null name="replication_services"/>
  <null name="requires"/>
  <null name="listACL"/>
  <value name="webappLoad">yes</value>
  <value name="name">ESPortal_Services</value>
  <value name="build"></value>
  <null name="description"/>
  <value name="time">2012-10-08 20:40:27 CDT</value>
  <value name="jvm_version">1.6.0_27</value>
  <value name="publisher">drouter</value>
  <value name="patch_nums"></value>
</Values>

"""

def manifestXml = new XmlParser(false, false).parseText(xmlString)

packageName =  manifestXml.'*'.find { node ->
    node.@name == "name"
}.text()

println packageName