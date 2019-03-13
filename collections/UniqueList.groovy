package collections
/*

import org.apache.directory.groovyldap.LDAP
import org.apache.directory.groovyldap.Search
import org.apache.directory.groovyldap.SearchScope


Map devUsers = ['user1@oldplace.com': 'user1@newplace.com']
Map uatUsers = ['user1@oldplace.com': 'user1@newplace.com']
Map prodUsers = ['user1@oldplace.com': 'user1@newplace.com']

def getKeysWithRepeatedValue(map) {
    map.groupBy { it.value }.find { it.value.size() > 1 }?.value*.key
}
*/
/*
println getKeysWithRepeatedValue(devUsers)
println getKeysWithRepeatedValue(uatUsers)
println getKeysWithRepeatedValue(prodUsers)*//*


def findDupsKeysInLists(Map source, Map target) {
    List found = []
    source.keySet().each { key ->
        if (target.key) {
            found << target.key
        }
    }
}

println "In dev and uat"
def devUatDups = findDupsKeysInLists(devUsers, uatUsers).sort()
println devUatDups

println "In dev and prod"
def devProdDups = findDupsKeysInLists(devUsers, prodUsers).sort()
println devProdDups

println "In UAT and PRod"
def uatProdDups = findDupsKeysInLists(uatUsers, prodUsers).sort()
println uatProdDups

def allDups = (devUatDups + uatProdDups + devProdDups)
println "in all env: "
println allDups.findAll { allDups.count(it) > 1 }.unique()

//LDAP ldapConnection = LDAP.newInstance('ldap://ds1pal0000.global.corp.sap:389') //389


LDAP ldapConnection = LDAP.newInstance('ldap://ldap1.global.com:389', 'CN=my_user_id,OU=I,OU=Identities,DC=global,DC=corp,DC=myco', 'password_here')

Search search = new Search()
//search.base = "DC=global,DC=corp,DC=sap"
search.base = "OU=I,OU=Identities,DC=global,DC=corp,DC=myco"
search.scope = SearchScope.ONE

println " -------- Checking DEV users: "
devUsers.each { k, v ->
    search.filter = "mail=${v}"
    int size = ldapConnection.search(search).size()
    String alert = ""
    if (size == 0) alert = " *******   "
    println "${alert}Search on ${v}, exists: ${size}"
}
println " -------- Checking UAT users: "
uatUsers.each { k, v ->
    search.filter = "mail=${v}"
    int size = ldapConnection.search(search).size()
    String alert = ""
    if (size == 0) alert = " *******   "
    println "${alert}Search on ${v}, exists: ${size}"
}

println " -------- Checking PROD users: "
prodUsers.each { k, v ->
    search.filter = "mail=${v}"
    int size = ldapConnection.search(search).size()
    String alert = ""
    if (size == 0) alert = " *******   "
    println "${alert}Search on ${v}, exists: ${size}"
}
*/



