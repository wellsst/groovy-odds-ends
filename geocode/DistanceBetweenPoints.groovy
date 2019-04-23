package geocode

/*
 * Calculate distance between two points in latitude and longitude taking
 * into account height difference. If you are not interested in height
 * difference pass 0.0. Uses Haversine method as its base.
 *
 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
 * el2 End altitude in meters
 * @returns Distance in Meters
 */

public static double distance(double lat1, double lat2, double lon1,
                              double lon2, double el1, double el2) {

    final int R = 6371; // Radius of the earth

    Double latDistance = Math.toRadians(lat2 - lat1);
    Double lonDistance = Math.toRadians(lon2 - lon1);
    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000; // convert to meters

    double height = el1 - el2;

    distance = Math.pow(distance, 2) + Math.pow(height, 2);

    return Math.sqrt(distance);
}

int secsBetween = 2

def d = distance(-27.48325316235423d, -27.48343580402434d, 153.2390188705176d, 153.2390170264989d, 8.0d, 8.8d)
println d

println "Speed ${d/secsBetween} m/s"
println "Speed ${d/secsBetween*3.6} kmh"