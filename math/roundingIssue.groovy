package math

import java.math.RoundingMode
import java.text.NumberFormat

public static String getDoubleToString(double dlDouble) throws Exception {
    String strdouble = "";
    Locale locale = null;
    NumberFormat numberFormat = null;
    try {
        strdouble = String.valueOf(dlDouble);
        if (strdouble.indexOf('e') != -1 || strdouble.indexOf('E') != -1) {
            locale = new Locale("", "");
            numberFormat = NumberFormat.getInstance(locale);
            numberFormat.setGroupingUsed(false);
            strdouble = numberFormat.format(dlDouble);
        }
        return strdouble;
    }
    catch (Exception ex) {
        return String.valueOf(dlDouble);
    }
}

public static double parseDouble(String strDouble) {
    double dlDouble = 0.00d;
    try {
        if (strDouble != null || !"".equals(strDouble)) {
            dlDouble = Double.parseDouble(strDouble);
           // println "dlDouble: ${dlDouble}"
        }
        return dlDouble;
    }
    catch (Exception ex) {
        System.out.println("Bad: " + ex.toString());
        return dlDouble;
    }
}

double added = 0.30 + 0.03
println "Added: ${added}"
println "Added: ${0.30 + 0.03}"
println "Added: ${0.30d + 0.03d}"

double d = 0.0
d += parseDouble("0.30")
d += parseDouble("0.03")

println "d is $d"

BigDecimal sum = new BigDecimal(0.0d)

sum = sum.add(new BigDecimal("0.30"))
sum = sum.add(new BigDecimal("0.03"))
sum = sum.setScale(6, RoundingMode.HALF_UP)

println "sum = ${sum} compareTo: ${sum.compareTo(new BigDecimal("0.33"))}"



/*

10.times {
    BigDecimal sumTest = new BigDecimal(0.0d)
    double r1 = Math.random()
    double r2 = Math.random()

    sumTest = sumTest.add(parseDouble(r1.toString()))
    sumTest = sumTest.add(parseDouble(r2.toString()))
    sumTest = sumTest.setScale(2, RoundingMode.HALF_UP)

    assert r1+r2 == sumTest
}
*/
