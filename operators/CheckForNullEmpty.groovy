package operators

/**
 * Created by Ariba
 * User: i079413
 * Date: 25/11/2014
 * Time: 11:29 AM
 */

String s
println s ? "nada" : "has val ${s}"
s = ""
println s ? "nada" : "has val ${s}"
s= "123"
println s ? "nada" : "has val ${s}"

s = null
if (s == null) {
    println "its null"
}
s= ""
if (s.empty) {
    println "Its empty"
}

if (s == null || s.empty) {
    println "Its null or empty"
}