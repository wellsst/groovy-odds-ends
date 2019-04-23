package text

int nrOperators = 2
int nrSums = 50
int maxTerm = 50

for (int i = 0; i < nrSums; i++) {
    Random rand = new Random()
    String sum = rand.nextInt(maxTerm+1)

    for (int j = 0; j < nrOperators; j++) {
        sum += " + ${rand.nextInt(maxTerm+1)}"
    }
    println sum + " = "
}
