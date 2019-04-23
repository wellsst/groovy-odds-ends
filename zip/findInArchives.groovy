package zip

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import static groovy.io.FileType.FILES

def cli = new CliBuilder(usage:'findInArchives')
cli.from('Starting folder location', args: 1, required: true)
//cli.type('Type of archive to look in', args: 1, required: true)
cli.what('Search string', args: 1, required: true)
def options = cli.parse(args)

if (!options) {
    return
}

if (options.h) {
    cli.usage()
    return
}

println "${options.from} for ${options.what}"

new File(options.from).eachFileRecurse(FILES) { file ->
    if(file.name.endsWith('.jar')) {
        println "Inspecting $file"
        ZipFile zipFile = new ZipFile(file)

        for(Enumeration e = zipFile.entries();e.hasMoreElements();){
            ZipEntry ze = (ZipEntry)e.nextElement()
            // println "\t- entry name:"+ze.getName()+"\t  uncompressed size:"+ze.getSize() +" bytes \tcompressed size:"+ze.getCompressedSize() +" bytes"
            if (ze.name.contains(options.what)) {
                println "*** Found ${options.what} in ${file} in entry: ${ze.name}"
            }
        }
    }
}