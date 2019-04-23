package html

/**
 * Created by Ariba
 * User: stwells
 * Date: 20/09/13
 * Time: 1:43 PM
 */

String encodeMe = """
﻿"sans-serif";                                     	div.MsoNormal                       	--_000_05CFBF0717B7FB4F96DC1CFC5DDBBE130CA8D37D6Fqddalex03quad_--
"sans-serif";}                                    	div.MsoNormal                       	font-family:"Calibri"
li.MsoNormal                                      	div.MsoNormal                       	font-size:11.0pt;
null                                              	null                                	/* Style Definitions */
sans-serif" size="2">                             	null                                	<font face="Arial
sans-serif" size=3D"3">                           	null                                	--_000_05CFBF0717B7FB4F96DC1CFC5DDBBE130C9E1E3494qddalex03quad_--
span.MsoHyperlink                                 	div.MsoNormal                       	text-decoration:underline;}
span.MsoHyperlinkFollowed                         	div.MsoNormal                       	{mso-style-type:personal-compose;
"""

println encodeMe.encodeAsHTML()