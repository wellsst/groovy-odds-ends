package collections

class MyObj {
    String name
    int value

    @Override
    String toString() {
        return "MyObj{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}'
    }
}

List things = [
        new MyObj(name: "Fred", value: 1),
        new MyObj(name: "Jack", value: 2),
        new MyObj(name: "Fred", value: 3),
        new MyObj(name: "Fred", value: 4),
        new MyObj(name: "Jack", value: 5)
]

things.groupBy {it.name}.each { objGroup ->
    println objGroup
    objGroup.value.each { myObj ->
        println "   test result for: ${myObj}"
    }
    println "  Here send email to ${objGroup.key}"
}
/*
Fred=[MyObj{name='Fred', value=1}, MyObj{name='Fred', value=3}, MyObj{name='Fred', value=4}]
   test result for: MyObj{name='Fred', value=1}
   test result for: MyObj{name='Fred', value=3}
   test result for: MyObj{name='Fred', value=4}
  Here send email to Fred
Jack=[MyObj{name='Jack', value=2}, MyObj{name='Jack', value=5}]
   test result for: MyObj{name='Jack', value=2}
   test result for: MyObj{name='Jack', value=5}
  Here send email to Jack
*/