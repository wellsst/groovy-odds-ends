package text
// http://stackoverflow.com/questions/11359333/string-concatenation-with-groovy

@Grab( 'org.gperfutils:gbench:0.4.2-groovy-2.1' )

def (foo,bar,baz) = [ 'foo' * 50, 'bar' * 50, 'baz' * 50 ]
benchmark {
    // Just add the strings
    'String adder' {
        foo + bar + baz
    }
    // Templating
    'GString template' {
        "$foo$bar$baz"
    }
    // I find this more readable
    'Readable GString template' {
        "${foo}${bar}${baz}"
    }
    'GString template toString' {
        "$foo$bar$baz".toString()
    }
    'Readable GString template toString' {
        "${foo}${bar}${baz}".toString()
    }
    // StringBuilder
    'StringBuilder' {
        new StringBuilder().append( foo )
                .append( bar )
                .append( baz )
                .toString()
    }
    'StringBuffer' {
        new StringBuffer().append( foo )
                .append( bar )
                .append( baz )
                .toString()
    }
    'StringBuffer with Allocation' {
        new StringBuffer( 512 ).append( foo )
                .append( bar )
                .append( baz )
                .toString()
    }
}.prettyPrint()