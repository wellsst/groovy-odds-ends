package collections

def list = [1,2,3,4,5,6,7,8]

assert [1,2,3] == list[0..2]

println list[0..3]
println list[0..<3]

println list.take(20) // cant do: list[0..20]

// println list[0..<100]
assert list == list.take(100)
assert [3,4] == list.drop(2).take(2)

assert [1,2,3] == list - [4,5,6,7,8]

// Test for updating of roles on CompanyPRofiles
List existingRole = [1,2,3]
List newRoles = [1,2,4]

println "Roles to add ${newRoles-existingRole}"
println "Roles to delete ${existingRole - newRoles}"