package collections

static int sumItems(int... items) {
    int total = 0
    for (int i : items) {
       total += i
    }
    total
}

println sumItems(1,2,3,4,5,6,7,8)
