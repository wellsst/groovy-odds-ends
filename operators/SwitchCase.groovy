package operators

int switchVar  = 10

switch (switchVar) {
    case 10..20:
        println "Its 10..20"
    case 10:
        println "Falls also to its 10"
    default:
        "Fallen to default?? No"
}
