package file

import util.GroovyUtils

class DuplicateFileUtils {

    static {
        File.metaClass.md5 = { ->
            def digest = java.security.MessageDigest.getInstance("MD5")
            delegate.withInputStream() { is ->
                is.eachByte(8192) { buffer, bytesRead ->
                    digest.update(buffer, 0, bytesRead)
                }
            }
            new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
        }
    }

    static HashMap<String, List<String>> findDuplicates(String startFrom, boolean useChecksum = false, minSizeKB = 0, maxSizeKB = Long.MAX_VALUE, String filenameContains = "") {

        HashMap<String, List<String>> files = new HashMap<>()

        File startFolder = new File(startFrom)
        if (startFolder.exists()) {
            startFolder.eachFileRecurse { f ->
                // todo: impl the checksum in here iof you like
                if (f.isFile() && f.size() >= minSizeKB * 1024 && f.size() <= maxSizeKB) {
                    def fileNameKey = "${f.name}::${f.size()}" as String
                    List<String> fileList = new ArrayList<>()
                    if (files.containsKey(fileNameKey)) {
                        fileList = files.get(fileNameKey)
                    }
                    fileList.add(f.absolutePath)
                    files.put(fileNameKey, fileList)
                }
            }
            Long totalSave = 0
            files.each { key, fileList ->
                if (fileList.size() > 1) {
                    println "Duplicate: ${key}"
                    // Yes should really use a better key rather than doing this
                    Long size = new Long(key - ~/.*::/)
                    Long fileSize = size * (fileList.size() - 1)
                    totalSave += fileSize
                    println " would save: ${GroovyUtils.humanReadableByteCount(fileSize)}"
                    fileList.each { listItem ->
                        println "  ${listItem}"
                    }
                }
            }
            println " Total save: ${GroovyUtils.humanReadableByteCount(totalSave)}"
            return files
        } else {
            throw new Exception("Folder: ${startFrom} does not exist")
        }
    }

    /* Simply delete all files in the list for each key but retain the shortest path */

    static def deleteDuplicates(HashMap<String, List<String>> files) {
        files.each { key, fileList ->
            fileList = fileList.sort { it.size() }
            fileList.eachWithIndex { listItem, index ->
                if (index > 0) {
                    println "Deleting ${listItem}"
                    if (new File(listItem).delete()) {
                        println " ... deleted."
                    } else {
                        println "  ... could not delete"
                    }
                } else {
                    println "File retained: ${listItem}"
                }
            }
        }
    }

}
