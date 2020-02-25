package file

// Suppose we have a environment variable GROOVY_HOME pointing to the Groovy installation dir.
def groovyHome = System.getenv('GROOVY_HOME')

def txtFiles = new FileNameFinder().getFileNames(groovyHome, '**/*.txt' /* includes */, '**/*.doc **/*.pdf' /* excludes */)
assert new File(groovyHome, 'README.txt').absolutePath in txtFiles

def icoFiles = new FileNameByRegexFinder().getFileNames(groovyHome, /.*\.ico/)
assert new File(groovyHome, 'html/groovy-jdk/groovy.ico').absolutePath in icoFiles

