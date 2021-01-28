package tool.cfg;

public class ProcessingRange {

    static public int MIN = 1;
    static public int MAX = 10;

    public static void setRange_10_after( long minid ) {
        MIN = (int)minid;
        MAX = (int)minid + 10;
    }

    public static String asString() {
        return "[ " + MIN + ", ... , " + MAX +" ]";
    }

    public static void setRange_from_to(long minid, long maxid) {
        MIN = (int)minid;
        MAX = (int)maxid;
    }
}
