package html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Based on :
 * http://www.mkyong.com/java/jsoup-send-search-query-to-google/
 * http://cs221-awesome-team.googlecode.com/svn/trunk/project3/Assignment3/src/ir/assignments/four/GoogleSearcher.java
 */
public class FunnyCrawler {

    private static Pattern patternDomainName;
    private Matcher matcher;
    private static final String DOMAIN_NAME_PATTERN = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
    static {
        patternDomainName = Pattern.compile(DOMAIN_NAME_PATTERN);
    }

    public static void main(String[] args) {

        FunnyCrawler obj = new FunnyCrawler();
        Set<String> result = obj.getDataFromGoogle("?intitle:index.of?mp3 jump");
        for (String temp : result) {
            System.out.println(temp);
        }
        System.out.println(result.size());
    }

    public String getDomainName(String url) {

        String domainName = "";
        matcher = patternDomainName.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;

    }

    private Set<String> getDataFromGoogle(String query, int num) {

        Set<String> linksFound = new HashSet<String>();
        String request = "https://www.google.com/search?q=" + query + "&num=${num}";
        System.out.println("Sending request..." + request);

        try {

            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request)
                    .userAgent(
                    "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(5000).get();

            // get all links
            /*Elements links = doc.select("a[href]");
            for (Element link : links) {

                String temp = link.attr("href");
                def googleUrlStart = "/url?q="
                if(temp.startsWith(googleUrlStart)){
                    //use regex to get domain name
                    //linksFound.add(getDomainName(temp));
                    linksFound.add(temp - googleUrlStart )
                }

            }*/

            // Get the URLs of the search results
            Elements searchResults = doc.select("li[class=g] a");
            for (Element result : searchResults) {
                if (result.text().equals("Cached") || result.text().equals("Similar")) // don't include cached or related links
                    continue;

                String href = result.attr("href");
                String extractedUrl = extractUrl(href);
                linksFound.add extractedUrl
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return linksFound;
    }

    private String extractUrl(String googleUrl) {
        // Google search result links are not the direct links, have to get it from the "q" parameter
        int start = googleUrl.indexOf("q=");
        if (start > -1) {
            start += "q=".length();

            int end = googleUrl.indexOf("&", start);
            if (end > start) {
                try {
                    return URLDecoder.decode(googleUrl.substring(start, end), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

}