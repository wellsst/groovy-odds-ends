#!/usr/bin/env groovy
package html

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.concurrent.*
import java.util.logging.LogManager
import java.util.logging.Logger

// General TODO
// Add scheduler...could be done via OS but a good example

@Grab(group = 'org.jsoup', module = 'jsoup', version = '1.8.2')
@Grab('log4j:log4j:1.2.17')

String logPropsFile = "log4j.properties"
final InputStream inputStream = new File("./${logPropsFile}").newInputStream()
try {
    LogManager.getLogManager().readConfiguration(inputStream)
}
catch (final IOException e) {
    Logger.getAnonymousLogger().severe("Could not load default ${logPropsFile} file");
    //Logger.getAnonymousLogger().error(e.getMessage());
}

RunOpts runOpts = new RunOpts(args)

// TODO: Total amount of pages processed size
// TODO: Display help and then allow interactive ask for required and then if user wants to enter other options or accept defaults etc
if (!runOpts.argsAreValid) {
    return
}

if (!runOpts.nikeIt) {
    String runIt = Utils.input("> This will run with options ${runOpts} [Y/n]?") ?: "y"
    if (runIt.toLowerCase() != 'y') {
        return
    }
}
DysonGrabber dysonGrabber = new DysonGrabber(runOpts: runOpts)
dysonGrabber.cyclonizer()

@Log4j
class DysonGrabber {

    RunOpts runOpts

    // Stats tracking
    int totalBytesGrabbed = 0
    long totalGrabTime = 0

    // Caching of links already grabbed so we dont grab duplicates
    Set<String> linksVisited = []

    // Tracking matches found
    int matchCount = 0

    // Tracking of estimated total downloads
    int downloadSize = 0

    // Some runopts that limit the grabbing will need checking on this
    boolean stopFiltering = false

/*

 Can we display the current amount downloaded and update on screen?  Yes but not with the very simple JSoup, have to impl own approach just for this like:
 http://stackoverflow.com/questions/22708911/download-a-large-pdf-with-jsoup...maybe better for memory management too
*/

    def cyclonizer() {
        println "Logging (${log.name}: ${log.class})"
        log.level = Level.DEBUG

        log.debug "Options selected: ${runOpts}"

        // Ref: http://www.googleguide.com/advanced_operators_reference.html#intitle
        Set<String> searchResultPages = searchGoogle("?inanchor:index.of?${runOpts.searchFileType} ${runOpts.searchFor}", runOpts)

        log.debug " Google results:  ${searchResultPages}"

        if (searchResultPages) {
            int filesGrabbed = 0

            def duration = withTiming {->
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(runOpts.nrDownloadThreads, new ThreadFactory() {
                    @Override
                    Thread newThread(Runnable r) {
                        return new Thread(r, "Dyson")
                    }
                })
                Set<DownloadTask> downloadTasks = new HashSet<>()
                searchResultPages.each { searchPage ->
                    log.info "Grabbing links under: ${searchPage}"

                    if (searchPage) {
                        Set<String> links = linksFromURL(searchPage, runOpts)
                        for (String linkOnPage : links) {
                            if (!stopFiltering) {
                                downloadTasks.addAll filterAndCollectLinks(executor, searchPage, linkOnPage)
                            }
                        }
                    }
                }

                if (downloadTasks.size() > 0) {
                    String runIt = 'y'
                    if (!runOpts.nikeIt) {
                        runIt = Utils.input("> Ready to download ${downloadTasks.size()} files: ${downloadTasks*.link} [Y/n/(s)elective]?").toLowerCase() ?: "y"
                        if (!['y', 's'].contains(runIt)) {
                            return
                        }
                    }

                    List<Future> futures = []
                    switch (runIt) {
                        case 'y':
                            futures = executor.invokeAll(downloadTasks)
                            break
                        case 's':
                            for (DownloadTask downloadTask : downloadTasks) {
                                String grabIt = Utils.input("> Do you want to download: ${downloadTask.link} [Y/n]?").toLowerCase() ?: "y"
                                if (grabIt == 'y') {
                                    futures = executor.invokeAll([downloadTask])
                                }
                            }
                            break
                        default:
                            log.warn "Invalid selection ${runIt}"
                            return
                    }

                    filesGrabbed = futures.size()
                    for (Future<String> future : futures) {
                        if (totalBytesGrabbed >= runOpts.totalDownloadSize) {
                            log.info "Stopping scan, max estimated download size of ${humanReadableByteCount(totalBytesGrabbed, true)} reached"
                            break
                        } else {
                            DownloadTask.TaskStats stats = future.get() as DownloadTask.TaskStats
                            totalBytesGrabbed += stats.bytesGrabbed
                            totalGrabTime += stats.timeTaken
                        }
                    }
                } else {
                    log.info "Nothing to Download!"
                }
                executor.shutdown()
            }
            def now = new Date()
            TimeDuration processingDuration = TimeCategory.minus(new Date(now.time + duration), now)
            TimeDuration downloadDuration = TimeCategory.minus(new Date(now.time + totalGrabTime), now)
            log.info "TASK COMPLETE. Total downloaded, ${filesGrabbed} files in ${humanReadableByteCount(totalBytesGrabbed, false)} took ${downloadDuration}.  Processing of script: ${processingDuration}"
        } else {
            log.info "No results found for search..."
        }
    }

    Set<DownloadTask> filterAndCollectLinks(ThreadPoolExecutor executor, String searchPage, String linkOnPage, int searchDepth = 0) {
        String padding = "   ".padLeft(searchDepth * 3)
        log.info " + Processing url: ${linkOnPage}"
        int nrGrabs = 0

        Set<DownloadTask> downloadTasks = new HashSet<>()

        if (stopFiltering) {
            return downloadTasks
        } else if (nrGrabs++ >= runOpts.grabLimitPerSite) {
            log.info " ---- STOP: grab limit of ${nrGrabs - 1} reached on ${linkOnPage}"
            // break
        } else if (linksVisited.contains(linkOnPage)) {
            log.debug "  - Ignoring ${linkOnPage}: already visited"
        } else {
            linksVisited << linkOnPage
            if ((!runOpts.grabAnyType && linkOnPage.endsWith(runOpts.searchFileType)) || runOpts.grabAnyType) {  // This we might want to download...
                String absoluteFileName = "${runOpts.outputTo}/${runOpts.searchFileType}/${localFileNameFromUrl(linkOnPage)}"

                boolean fileNameExactMatch = absoluteFileName.toLowerCase().endsWith("/${runOpts.searchFor.toLowerCase()}.${runOpts.searchFileType.toLowerCase()}")

                log.debug "${padding}* link to save: ${linkOnPage} to local file: ${absoluteFileName}"
                File file = new File(absoluteFileName)

                if (file.exists() && runOpts.overWrite) {
                    log.debug "${padding}- Skipping ${absoluteFileName} as we already have it"
                } else {
                    try {
                        //def size = getFileSize(new URL(linkOnPage))   // This may give -1 if hte server doesnt like the request...should we include, it makes tihngs slower....
                        //downloadSize += size

                        //if ((runOpts.minFileSize == 0 || (runOpts.minFileSize > 0 && size > runOpts.minFileSize)) && (runOpts.grabOnlyExactMatches && fileNameExactMatch)) {   // max filesize is handed by JSoup inside the DLtask
                        //if ((runOpts.grabOnlyExactMatches && fileNameExactMatch)) {   // max filesize is handed by JSoup inside the DLtask
                        boolean grabIt = true
                        if (runOpts.grabOnlyExactMatches && !fileNameExactMatch) {
                            log.info "${padding} - Not an exact match, excluding"
                            grabIt = false
                        }
                        if (runOpts.grabOnlyKeywords) {
                            String[] keywords = runOpts.searchFor.split(" ")
                            keywords.each { keyword ->
                                if (!linkOnPage.toLowerCase().contains(keyword.toLowerCase())) {
                                    grabIt = false
                                }
                            }
                        }

                        if (grabIt) {
                            if (runOpts.stopOnExactMatches > 0) {
                                matchCount++
                                log.debug "${padding}* Found exact match #${matchCount}, stopping at ${runOpts.stopOnExactMatches}"
                                if (matchCount > runOpts.stopOnExactMatches) {
                                    log.info "${padding}--- Exact match count of ${runOpts.stopOnExactMatches} reached, not processing more files."
                                    stopFiltering = true
                                    return downloadTasks
                                }
                            }
                            if (runOpts.stopOnAnyMatches > 0) {
                                matchCount++
                                log.debug "${padding}* Found match #${matchCount}, stopping at ${runOpts.stopOnExactMatches}"
                                if (matchCount > runOpts.stopOnAnyMatches) {
                                    log.info "${padding}--- Any match count of ${runOpts.stopOnAnyMatches} reached, not processing more files."
                                    stopFiltering = true
                                    return downloadTasks
                                }
                            }
                            if (runOpts.testIt) {
                                log.info "${padding}~ testing link to grab: ${linkOnPage} to local file: ${absoluteFileName}" // size: ${humanReadableByteCount(size)}"
                            } else {
                                log.debug "${padding} + queueing download task ${linkOnPage} to local file: ${absoluteFileName}"
                                DownloadTask downloadTask = new DownloadTask(file: file, link: linkOnPage, runOpts: runOpts)
                                downloadTasks.add downloadTask
                            }
                        }

                        /*Future future = executor.submit(downloadTask)
                        DownloadTask.TaskStats stats = future.get() as DownloadTask.TaskStats
                        totalBytesGrabbed += stats.bytesGrabbed
                        totalGrabTime += stats.timeTaken*/
                        //    }
                        /*else {
                            //log.debug "${padding}- Excluding ${linkOnPage}; reported size ${size} < the min size asked for ${runOpts.minFileSize}"
                            log.debug "${padding}- Excluding ${linkOnPage}"
                        }*/
                    } catch (all) {
                        log.error "${padding}! Error grabbing: ${all.message}"
                    }
                }
                // If we have not hit the bottom of the search tree asked for and the link is not itself and the link is drillable/followable then drill down...
            } else if (linkOnPage != searchPage && linkIsDrillable(linkOnPage)) {
                searchDepth++
                log.debug "${padding}>> drilling down on link ${linkOnPage}, depth is ${searchDepth}"

                if (searchDepth < runOpts.maxSearchDepth) {
                    Set<String> links = linksFromURL(searchPage, runOpts)
                    for (String linkOnSubPage : links) {
                        if (!stopFiltering) {
                            downloadTasks.addAll(filterAndCollectLinks(executor, linkOnPage, linkOnSubPage, searchDepth))
                        }
                    }
                } else {
                    log.debug "${padding}- Search depth reached"
                }
            } else {
                log.debug "${padding}- filtering out ${linkOnPage}, linkIsDrillable: ${linkIsDrillable(linkOnPage)}, grabAnyType: ${runOpts.grabAnyType}, type required: ${runOpts.searchFileType}"
            }
        }
        return downloadTasks

    }

    class DownloadTask implements Callable {

        File file
        String link
        RunOpts runOpts

        class TaskStats {
            int bytesGrabbed = 0
            long timeTaken = 0
        }

        @Override
        public TaskStats call() {
            try {
                log.info "  ++ Download task START ${link} is running on " + Thread.currentThread().getName()
                def duration = withTiming {->
                    file.parentFile.mkdirs()
                    if (runOpts.overWrite && file.exists()) {
                        if (!file.delete()) {
                            log.warn "Tried to remove existing file ${file.absolutePath}, but could not."
                        }
                    }
                    file << Jsoup.connect(link).ignoreHttpErrors(true).validateTLSCertificates(false).
                            followRedirects(runOpts.followRedirects).
                            maxBodySize(runOpts.maxFileSize).
                            timeout(runOpts.timeoutSecs * 1000).
                            ignoreContentType(true).
                            execute().bodyAsBytes()
                }

                /*BufferedOutputStream outputStream = file.newOutputStream()
                URL url = new URL(link)
                outputStream << url.openStream()
                outputStream.close()*/
                def now = new Date()
                TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + duration), now)
                log.info "     -- Download task END ${link} size ${DysonGrabber.humanReadableByteCount(file.size(), false)} took ${timeDuration} secs, on thread:" + Thread.currentThread().getName()
                return new TaskStats(bytesGrabbed: file.size(), timeTaken: duration)
            } catch (InterruptedException e) {
                e.printStackTrace()
            } catch (all) {
                log.error all
            }
        }

        def withTiming = { closure ->
            long start = System.currentTimeMillis()
            closure.call()
            long now = System.currentTimeMillis()
            now - start
        }

    }
/// ------------ Utils


    String localFileNameFromUrl(String url) {
        // remove the http(s)://, swap . with /, remove any non-filesystem chars
        URLDecoder.decode(url, "UTF-8").split("://")[1].replaceAll("[^A-Za-z0-9_/.]", "")  // replaceAll("\\.", "/")
    }

    Set<String> searchGoogle(String query, RunOpts runOpts) {
        Set<String> linksFound = new HashSet<String>()
        String request = "https://www.google.com/search?q=" + query + "&num=${runOpts.nrSitesToGrabFrom}"
        log.debug "Searching google..." + request

        try {

            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request).ignoreHttpErrors(true).validateTLSCertificates(false)
                    .userAgent(runOpts.userAgent)
                    .timeout(runOpts.timeoutSecs * 1000).get()

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


    Set<String> linksFromURL(String url, RunOpts runOpts) {

        Set<String> linksFound = new HashSet<String>()

        try {
            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(url).ignoreHttpErrors(true).validateTLSCertificates(false)
                    .userAgent(runOpts.userAgent)
                    .timeout(runOpts.timeoutSecs * 1000).get()

            // Get the URLs of the search results
            Elements searchResults = doc.select("a");
            for (Element result : searchResults) {
                log.debug "    ^ found page link: ${result}"

                String href = result.attr("href");
                String fullUrl = fullUrl(url, href)
                linksFound.add fullUrl
            }

        } catch (Exception e) {
            log.error e.message, e
        }

        linksFound
    }


    def withTiming = { closure ->
        long start = System.currentTimeMillis()
        closure.call()
        long now = System.currentTimeMillis()
        now - start
    }

    private long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLengthLong();
        } catch (all) {
            return -1;
        } finally {
            conn.disconnect();
        }
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

    Boolean linkIsDrillable(String link) {
        boolean isFollowable = false
        if (link.endsWith(".htm") || link.endsWith(".html") || link.endsWith("/")) {  // todo add if ends with like .com or .com.au etc
            isFollowable = true
        }
        isFollowable
    }

    String fullUrl(String url, String link) {
        String urlStart = url.tokenize("://")[0]
        if (urlStart && urlStart == link.startsWith(urlStart)) {
            return link
        } else {
            if (url.endsWith("/")) {
                return "${url}${link}"
            }
            return "${url}/${link}"
        }
    }

/**
 * Credit: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
 * @param bytes
 * @param si
 * @return
 */
    public static String humanReadableByteCount(long bytes, boolean si = true) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = new String((si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}




class Utils {
    static def input = { prompt ->
        print prompt
/*        Scanner scanner = new Scanner(System.in)
        String entry = scanner.next()
        scanner.close()
        entry*/
        System.in.newReader().readLine()
    }
}

class RunOpts {

    List<String> excludeTypes = ["bat", "exe"] // only effective if grabbing any type
    String searchFileType = ""    // todo: add groups eg music = mp3|ogg|etc...
    String searchFor = ""

    boolean nikeIt = false  // Just do it

    int maxSearchDepth = 1

    String outputTo = "." // TODO

    // Limiting options...
    boolean grabOnlyExactMatches = false
    boolean grabOnlyKeywords = false

    // Limit on counts

    int grabLimitPerSite = 50
    int nrSitesToGrabFrom = 5
    int stopOnExactMatches = 0 // eg looking for bridge.avi, we get it and then stop after we find this many
    int stopOnAnyMatches = 0 // eg looking for bridge.avi get xyz.avi, we get it and then stop after we find this many

    // Limit on sizes
    int maxFileSize = readableMemorySizeToByteSize "8g"  // blank no max, as per JVM patterns
    int minFileSize = 0  // blank grab any, as per JVM patterns
    int totalDownloadSize = readableMemorySizeToByteSize "8g"

    // Limit on types
    boolean grabAnyType = false
    String urlMatching = /./
    String urlNotMatching = /./

    // Other
    boolean testIt = false
    boolean overWrite = true
    boolean followRedirects = false

    int nrDownloadThreads = 0 // 0 = let Dyson decide
    int timeoutSecs = 10  // JSoup says its connect and read

    // todo allow pre-canned agents
    String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)" // "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36"

    boolean argsAreValid = false

    public RunOpts(args) {
        collectRunOpts(args)
    }

    /**
     * eg 2m is 2 megabytes
     * @param memSize
     * @return
     */
    static long readableMemorySizeToByteSize(String memSize) {
        try {
            Integer.parseInt(memSize)
        } catch (all) {
            Map memPow = [k: 1, m: 2, g: 3]
            def memTok = memSize =~ (/^(.*)([kmgt])$/)

            String number = memTok[0][1]
            String memId = memTok[0][2]

            new BigDecimal(number).multiply(new BigDecimal("1024").pow(memPow."${memId}"))
        }
    }


    void collectRunOpts(args) {
        def cli = new CliBuilder(width: 200, stopAtNonOption: false,
                usage: "Dyson.groovy -t 'file type' -s 'search term'",
                footer: """
            Examples:
            Dyson -t txt -s readme
            Dyson -t txt -s readme -test -gl 5 -stg 3 -to 10 -max 1m -min 10k -stop
            Dyson -type txt -search "once upon a time" -grabLimitPerSite 5 -stg 5 -timeoutSecs 10 -nrDownloadThreads 10 -maxSearchDepth 3 -totalDownloadSize 20m -stop 3

            Hint: To set a proxy use something like: -Dhttps.proxyHost=myproxy.com -Dhttps.proxyPort=8000, see: https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html
            Hint: Getting out of memory?  Increase by passing options like -Xmx768m or -Xmx2g and/or reduce the number of threads with -th
            """)
        //log.debug args
        cli.with {
            h(longOpt: 'help', 'usage information', required: false)
            t(longOpt: 'type', 'file type to search for, eg txt', required: true, args: 1)
            s(longOpt: 'search', 'search for, eg readme', required: true, args: 1)

            // Optionals
            // Ints
            msd(longOpt: 'maxSearchDepth', "maxSearchDepth (0 = unlimited, default=${maxSearchDepth}, eg 3", required: false, args: 1)
            gl(longOpt: 'grabLimitPerSite', "grabLimitPerSite, number of matching files to download per site, eg 10. (0=unlimited, default=${grabLimitPerSite})", required: false, args: 1)
            stg(longOpt: 'nrSitesToGrabFrom', "Number of sites to grab, number of matching sites to download from, eg 10. (0=all matched, default=${nrSitesToGrabFrom})", required: false, args: 1)
            th(longOpt: 'nrDownloadThreads', "nrDownloadThreads. (0=Let me decide for you, default=${nrDownloadThreads})", required: false, args: 1)
            to(longOpt: 'timeoutSecs', "timeoutSecs. (0=Infinite, default=${timeoutSecs})", required: false, args: 1)
            max(longOpt: 'maxFileSize', "maxFileSize as an integer or in human readable, eg 1k, 3m, 4g, 1t. (-1=Infinite, default=${maxFileSize})", required: false, args: 1)
            min(longOpt: 'minFileSize', "minFileSize as an integer or in human readable, eg 1k, 3m, 4g, 1t. (default=${maxFileSize})", required: false, args: 1)
            mds(longOpt: 'totalDownloadSize', "totalDownloadSize as an integer or in human readable, eg 1k, 3m, 4g, 1t. (default=${totalDownloadSize})", required: false, args: 1)
            stopExact(longOpt: 'stopOnExactMatches', "stopOnExactMatches (default=${stopOnExactMatches}), 0 for no stopping", required: false, args: 1)
            stopAny(longOpt: 'stopOnAnyMatches', "stopOnAnyMatches (default=${stopOnAnyMatches}), 0 for no stopping", required: false, args: 1)

            // Booleans
            nike(longOpt: 'nike', "Just do it. (default=${nikeIt})", required: false)
            test(longOpt: 'testIt', "Just do a test run to show what might be grabbed. (default=${testIt})", required: false)
            over(longOpt: 'overWrite', "Overwrite existing files. (default=${overWrite})", required: false)
            ga(longOpt: 'grabAnyType', "Grab any file type on the site not only for asked type (based on extension) (default=${grabAnyType})", required: false)
            fol(longOpt: 'followRedirects', "followRedirects (default=${followRedirects})", required: false)
            exact(longOpt: 'grabOnlyExactMatches', "grabOnlyExactMatches (default=${grabOnlyExactMatches})", required: false)
            key(longOpt: 'grabOnlyKeywords', "grabOnlyKeywords (default=${grabOnlyKeywords})", required: false)

            // Strings
            agent(longOpt: 'userAgent', "userAgent (default=${userAgent})", required: false, args: 1)
            out(longOpt: 'out', "output to folder (default=${outputTo})", required: false, args: 1)
        }

        OptionAccessor opt = cli.parse(args)
        if (!opt || opt.h) {
            cli.usage()
            return
        }
        if (opt.t) {
            searchFileType = opt.t
        }
        if (opt.s) {
            searchFor = opt.s
        }

        // Gets this far then the args supplied are ok
        argsAreValid = true

        if (opt.msd) {
            maxSearchDepth = Integer.parseInt opt.msd
        }
        if (opt.gl) {
            grabLimitPerSite = Integer.parseInt opt.gl
        }
        if (opt.stg) {
            nrSitesToGrabFrom = Integer.parseInt opt.stg
        }

        if (opt.th) {
            nrDownloadThreads = Integer.parseInt opt.th
        }
        if (nrDownloadThreads == 0) {
            nrDownloadThreads = decideNrDownloadThreads()
        }

        if (opt.to) {
            timeoutSecs = Integer.parseInt opt.to
        }
        if (opt.max) {
            maxFileSize = readableMemorySizeToByteSize opt.max
        }
        if (opt.min) {
            minFileSize = readableMemorySizeToByteSize opt.min
        }
        if (opt.mds) {
            totalDownloadSize = readableMemorySizeToByteSize opt.mds
        }

        if (opt.stopExact) {
            stopOnExactMatches = Integer.parseInt opt.stopExact
        }
        if (opt.stopAny) {
            stopOnAnyMatches = Integer.parseInt opt.stopAny
        }

        if (opt.nike) {
            nikeIt = true
        }
        if (opt.test) {
            testIt = true
        }
        if (opt.over) {
            overWrite = true
        }
        if (opt.ga) {
            grabAnyType = true
        }
        if (opt.fol) {
            followRedirects = true
        }
        if (opt.exact) {
            grabOnlyExactMatches = true
        }
        if (opt.key) {
            grabOnlyKeywords = true
        }
        if (opt.agent) {
            userAgent = opt.agent
        }
        if (opt.out) {
            outputTo = opt.out
        }

    }

    Integer decideNrDownloadThreads() {
        // possibly a better way to decide this, eg with available heap size and current file size, straming in, very difficult, maybe the JSoup method is not quite right as I've seen OOM exceptions.
        // Seems like a bit of black magic to get this right and for what purpose?
        // Have the user reduce this manually or increase Heap size
        Math.min nrSitesToGrabFrom * Math.min(grabLimitPerSite, 10), 50
    }


    @Override
    public java.lang.String toString() {
        return "RunOpts{" +
                "searchFileType='" + searchFileType + '\'' +
                ", searchFor='" + searchFor + '\'' +
                ", nikeIt=" + nikeIt +
                ", maxSearchDepth=" + maxSearchDepth +
                ", outputTo='" + outputTo + '\'' +
                ", grabOnlyExactMatches=" + grabOnlyExactMatches +
                ", grabOnlyKeywords=" + grabOnlyKeywords +
                ", grabLimitPerSite=" + grabLimitPerSite +
                ", nrSitesToGrabFrom=" + nrSitesToGrabFrom +
                ", stopOnExactMatches=" + stopOnExactMatches +
                ", stopOnAnyMatches=" + stopOnAnyMatches +
                ", maxFileSize=" + maxFileSize +
                ", minFileSize=" + minFileSize +
                ", totalDownloadSize=" + totalDownloadSize +
                ", grabAnyType=" + grabAnyType +
                ", urlMatching='" + urlMatching + '\'' +
                ", urlNotMatching='" + urlNotMatching + '\'' +
                ", testIt=" + testIt +
                ", overWrite=" + overWrite +
                ", followRedirects=" + followRedirects +
                ", nrDownloadThreads=" + nrDownloadThreads +
                ", timeoutSecs=" + timeoutSecs +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}

