import static groovy.io.FileType.FILES
import com.drew.metadata.*
import com.drew.imaging.*

@Grapes(
@Grab(group = 'com.drewnoakes', module = 'metadata-extractor', version = '2.6.2')
)

def cli = new CliBuilder(usage: 'findInArchives')
cli.from('Starting folder location', args: 1, required: true)
//cli.type('Type of archive to look in', args: 1, required: true)
//cli.template('Template', args: 1, required: true)
//cli.outfile('Output filename', args: 1, required: true)
def options = cli.parse(args)

if (!options) {
    return
}

if (options.h) {
    cli.usage()
    return
}

// Yep should use a templating engine
def head = """\
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title></title>
</head>
<body>
"""

def tail = """\

</body>
</html>
"""

println "${options.from} for ${options.what}"

def output = head

new File(options.from).eachFileRecurse(FILES) { file ->

    println "Inspecting $file"

    try {
        Metadata metadata = ImageMetadataReader.readMetadata(file);

        output += "<br/><b>${file.absolutePath}</b>"
        output += "<br/><img src='${file.absolutePath}'/>"
        output += "<table>"
        for (Directory directory : metadata.getDirectories()) {

            for (Tag tag : directory.getTags()) {
                output += "<tr>"
                output += "<td>${tag.getTagName()}</td>"
                output += "<td>${tag.getDescription()}</td>"
                output += "</tr>"
            }

        }
        output += "</table>"
    } catch (all) {
        println "Error: ${all.message}"
    }

}

output += tail

File out = new File(options.from + "/index.html")
out.write output
println "Wrote to: ${options.from + "/index.html"}"