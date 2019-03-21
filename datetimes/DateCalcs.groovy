package datetimes

import groovy.time.TimeCategory
def maxHoursBack = 4390 // 6 months in hours
def maxMonthsBack = Math.round(maxHoursBack / 24 / 30)

println maxMonthsBack

use (TimeCategory) {
    Date months6 = new Date() - 6.month
    Date months18 = new Date() - 18.month

    def range = months18..months6
    println range.size()
    println range.size() * 24
    println new Date() in range
}

