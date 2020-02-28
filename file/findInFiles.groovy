package file

import groovy.io.FileVisitResult
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import util.GroovyUtils

import static groovy.io.FileType.*

String startFrom = "D:\\1dev"
String searchFor = "WICS"

String oracleFileTypes = "pkb|pks|dat|vw|sql|trg|prc|ind|fmb"
String srcFiles = "java|groovy|rb|factories|py|js"
String webFiles = "rb|json|js|ts|css|html|jsp|less|css|scss|tag|tld|rdf"
String txtFiles = "xml|json|txt|properties|md|yml|config"
String otherFiles = "rjl|jrxml"
String allFiles = ".*"

String ignoreFiles = "log|log2|old|tmp|temp|lock|bak"
String binFiles = "zip|gz|jar|svg|dll|out|class|war"
String officeFiles = "xls|doc|ppt|rtf|docx"
String mediaFiles = "jpg|png|ttf|pdf"
String dotFiles = /\..*/

String excludeFileTypes = binFiles + "|" + officeFiles + "|" + ignoreFiles + "|" + mediaFiles + "|" + dotFiles

String includeFileTypes = srcFiles + "|" + webFiles
includeFileTypes = allFiles
includeFileTypes = srcFiles  + "|" + webFiles + "|" + txtFiles
includeFileTypes = txtFiles  + "|" + officeFiles

long maxFileSize = 1024 * 1024 * 2
println "Max file size to search on: ${GroovyUtils.humanReadableByteCount(maxFileSize)}"

long maxFileSizeProcessed = 0
long filesSearched = 0
long kbSearched = 0
int matches = 0
Map extensionsFound = [:]

Date now = new Date()

def duration = GroovyUtils.withTiming { ->
    new File(startFrom).traverse(
            nameFilter: ~/(?i).*\.(${includeFileTypes})/,
            excludeNameFilter: ~/(?i).*\.(${excludeFileTypes})/,
            type: FILES,
            maxDepth: -1,
            preDir: { if (['.svn', 'Maven']*.matches(it.name).contains(true)) return FileVisitResult.SKIP_SUBTREE },) { file ->

        if (file.size() <= maxFileSize) {

            filesSearched++

            def extName = file.name[file.name.lastIndexOf(".")..-1]
            long extSize = extensionsFound."${extName}" ?: 0
            extensionsFound[extName] = extSize + file.size()

            kbSearched += file.size()
            if (file.size() > maxFileSizeProcessed) {
                maxFileSizeProcessed = file.size()
            }

            List<String> contents = file.readLines()
            boolean found = false
            String linesFound = ""

            contents.eachWithIndex { line, lineIndex ->
                // if (line.toLowerCase().contains(searchFor.toLowerCase())) {
                if (line ==~ /(?i).*\b${searchFor}\b.*/) {
                    found = true
                    matches++
                    if (lineIndex > 0) linesFound += "${lineIndex - 1}:  ${contents[lineIndex - 1]}\n"
                    linesFound += "${lineIndex}:  ${line}\n"
                    if (lineIndex < contents.size()) linesFound += "${lineIndex + 1}:  ${contents[lineIndex + 1]}\n"
                }
            }

            if (found) {
                println file
                println linesFound
                println "   -- files searched: ${filesSearched} : ${GroovyUtils.humanReadableByteCount(kbSearched)} "
                println()
            } else if (filesSearched % 1000 == 0) {
                println "   -- files searched: ${filesSearched} : ${GroovyUtils.humanReadableByteCount(kbSearched)}"
            }
        }
    }
}

TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + duration), now)
println "   -- Total files searched: ${filesSearched} : ${GroovyUtils.humanReadableByteCount(kbSearched)} , '${searchFor}' matched ${matches} times in ${timeDuration} secs"
String extText = ""
extensionsFound = extensionsFound.sort { -it.value }
extensionsFound.each { k, v ->
    extText += "${k} : ${GroovyUtils.humanReadableByteCount(v)}, "
}
println "      -- extensions searched ${extText} "
println "      -- max file size ${GroovyUtils.humanReadableByteCount(maxFileSizeProcessed)} ($maxFileSizeProcessed)"



// extensions found [.h, .sql, .ctl, .ksh, .config, .xml, .md, .java, .txt, .properties, .js, .json, .html, .yml, .rb, .cmd,
// .license, .dtd, .pl, .xsl, .zip, .bat, .conf, .jar, .lock, .dat, .sh, .classpath, .project, .prefs, .css, .gif, .jpg, .psd,
// .MF, .mymetadata, .myumldata, .jsp, .tld, .vsd, .editorconfig, .csv, .pdf, .doc, .db, .bak, .docx, .xlsx, .htm, .stx, .STX,
// .xls, .msg, .log, .tab, .log2, .sql_old, .log3, .dotm, .tmpl, .cfg, .crontab, .pkb, .pks, .vw, .ind, .typ, .trg, .prc, .con,
// .sqs, .snp, .fnc, .iml, .fix, .pll, .olb, .pal, .mmb, .par, .rdf, .ora, .res, .fmb, .tif, .kl, .cgi, .udposdm, .exe, .cnt, .GID,
// .hlp, .template, .060313, .xsd, .superseded, .ddl, .pw, .out, .out1, .sts, .gz, .in, .exp, .xmlcatalog, .key, .jks, .jrtx, .png,
// .bmp, .less, .eot, .svg, .ttf, .woff, .woff2, .otf, .cur, .swf, .class, .kotlin_module, .tag, .exec, .lst, .jrxml, .jrctx,
// .factories, .feature, .yaml, .dmd, .map, .ppt, .0, .1, .jboss, .jspx, .browserslistrc, .eslintignore, .npmrc, .stylelintignore,
// .stylelintrc, .scss, .thtest, .war, .main, .tomee, .wls, .original, .component, .ico, .babelrc, .npmignore, .mdx, .MockMaker,
// .lic, .rtf, .gitignore, .htmlhintrc, .ts, .bowerrc, .jshintrc, .gzip, .jscsrc, .csslintrc, .nuspec, .ps1, .versions, .eps,
// .gitmodules, .markdown, .jsm, .patch, .gitattributes, .rst, .ension, .bin, .opts, .types, .jshintignore, .crt, .srl, .csr, .hidden,
// .tgz, .el, .old, .gnu, .c, .dll, .coffee, .hidden_file, .must+be-escaped, .gradle, .tern-port, .eslintrc, .jst, .def, .deps, .targ,
// .tm_properties, .test, .mk, .reviewboardrc, .ini, .m, .bplist, .mdown, .tern-project, .3, .5, .7, .ignore, .input, .gypi, .settings,
// .cc, .py, .gyp, .fontified, .pbfilespec, .xclangspec, .couch, .sublime-project, .sublime-workspace, .3ctype, .cnf, .crl, .gitkeep,
// .APACHE2, .BSD, .MIT, .pegjs, .completion, .git_ignore, .mustache, .swp, .name, .casey, .sample, .idx, .pack, .2, .data, .index,
// .npminclude, .jsbeautifyrc, .ejs, .source-map, .lint, .lintignore, .xhtml, .appcache, .aspx, .cs, .StyleCop, .Config, .info, .dmc,
// .node, .cpp, .hpp, .ac, .am, .jade, .orig, .dntrc, .ls, .Makefile, .d, .http, .upload, .pem, .11, .20, .4, .as, .org, .watchr, .TXT,
// .ftl, .diff, .xconf, .TTF, .XML, .loadpath, .erb, .rjs, .prawn, .rhtml, .textile, .rake, .gemspec, .rdoc, .generators, .rakeTasks,
// .ru, .autotest, .simplecov, .x, .rails-master, .sqlite3, .providers, .release, .59, .22, .13, .14, .12, .02, .03, .30, .31,
// .properties_noncmb, .rjl, .tmp, .policy, .renametojar, .jspf, .mdl, .unused]