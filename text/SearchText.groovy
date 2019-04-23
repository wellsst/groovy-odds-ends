package text

String text = """Message-ID: <1992969365.19.1413522585464.JavaMail.tomcat@dapp1gw>
MIME-Version: 1.0
Content-Type: multipart/related;
boundary="----=_Part_18_383501499.1413522585450"; type="application/xml"
------=_Part_18_383501499.1413522585450
Content-Type: application/xml
Content-Transfer-Encoding: 7bit
Content-ID: 5f2ef224-7bfd-1000-8ef6-ac1851130001
boundary="----=_Part_18_383501499.1413522585450"; type="application/xml"
"""

def matcher = (text =~ /boundary="(.*)";/)

String boundary = matcher[0][1]

println boundary