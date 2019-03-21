package numbers
//see if blindly betting half your avaialble money on a dice throw could earn

int startingPot = 100 // dollars?
float betFactor = 0.1

int diceSize = 2
float takings = 0 // any wins them put them here

Random rand = new Random()

while (startingPot > 0) {

    int myRoll = rand.nextInt(diceSize) + 1
    int bankRoll = rand.nextInt(diceSize) + 1

    println "myRoll: ${myRoll}, bankRoll: ${bankRoll}, pot: ${startingPot}"
    
    def winLooseAmount = startingPot * betFactor
    if (myRoll == bankRoll) {
        //startingPot += (startingPot*betFactor)
        takings += winLooseAmount
        println " --- WIN! ${winLooseAmount}, takings ${takings}"
    } else {
        startingPot -= winLooseAmount
    }

}

println "Final takings: ${takings}"