package text

String getExtension(String name) {
    name.lastIndexOf('.') >= 0 ?
        name[name.lastIndexOf('.')+1..- 1] :
        ""
}

String getFileName(String name) {
    name.lastIndexOf('.') >= 0 ?
        name[0..name.lastIndexOf('.')-1] :
        name
}

println getExtension("test")
println getExtension(".test")
println getExtension("test.com")

