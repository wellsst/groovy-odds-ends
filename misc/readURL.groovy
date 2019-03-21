package misc
// groovy.grape.Grape.grab([group:'org.springframework', module:'spring', version:'2.5.6'])

@Grapes(
	@Grab(group='org.jsoup', module='jsoup', version='1.6.3')
)
    
def url = "http://mrhaki.blogspot.com.au/2009/10/groovy-goodness-reading-url-content.html".toURL()

println org.jsoup.Jsoup.parse(url.text).text()

String[] words = org.jsoup.Jsoup.parse(url.text).text().split
