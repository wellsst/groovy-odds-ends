package text




static long humanMemorySizeToBytes(String memSize) {


    try {
        Integer.parseInt(memSize)
    } catch (all) {
        Map memPow = [k: 1, m: 2, g: 3]
        def memTok = memSize =~ (/^(.*)([kmgt])$/)

        String number = memTok[0][1]
        String memId = memTok[0][2]

        new BigDecimal(number).multiply(new BigDecimal("1024").pow(memPow."${memId}"))
    }

}

assert 1024 == humanMemorySizeToBytes("1024")
assert 1024 == humanMemorySizeToBytes("1k")
assert 2048 == humanMemorySizeToBytes("2k")
assert Math.pow(1024,2) == humanMemorySizeToBytes("1m")
assert 2*Math.pow(1024,2) == humanMemorySizeToBytes("2m")
assert Math.pow(1024,3) == humanMemorySizeToBytes("1g")
assert 2*Math.pow(1024,3) == humanMemorySizeToBytes("2g")
assert 2.5*Math.pow(1024,3) == humanMemorySizeToBytes("2.5g")
