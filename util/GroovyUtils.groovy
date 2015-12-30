package util

/**
 */
class GroovyUtils {

    static withTiming = { closure ->
        long start = System.currentTimeMillis()
        closure.call()
        long now = System.currentTimeMillis()
        now - start
    }

    /**
     * Credit: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     * @param bytes
     * @param si
     * @return
     */
    public static String humanReadableByteCount(long bytes, boolean si = true) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = new String((si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
