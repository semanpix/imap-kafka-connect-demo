package util;

import java.util.*;

public class Timer {

    long steps = 0;

    long started = -1;
    long lastStep = -1;

    public Timer() {
        started = System.currentTimeMillis();
    }

    public static Timer getTimer() {
        return new Timer();
    }

    public long getTimeElapsed() {
        return System.currentTimeMillis() - started;
    }

    public String getTimeElapsed_in_s() {
        long time = getTimeElapsed();
        long seconds = time / 1000;
        return seconds + " s";
    }

    public String getTimeElapsed_in_min() {
        long time = getTimeElapsed();
        long min = time / 1000 / 60;
        return min + " min";
    }

    public String getTimeElapsed_in_hours() {
        long time = getTimeElapsed();
        long h = time / 1000 / 60 / 60;
        return h + " h";
    }

    public String getTimeElapsed_in_days() {
        long time = getTimeElapsed();
        long days = time / 1000 / 60 / 60 / 24;
        return days + " d";
    }

    Hashtable<Long, String> events = new Hashtable<Long, String>();

    public long getTimeStep( String eventName ) {
        steps++;
        long now = System.currentTimeMillis();
        long tmp = lastStep;
        lastStep = now;
        if( steps == 1 ) tmp = now;
        events.put( steps, now - tmp + " - " + eventName );
        return (now - tmp) / 1000;
    }

    public void showEvents() {
        Set<Long> s = events.keySet();
        List<Long> list = new ArrayList<Long>();
        list.addAll(s);
        Collections.sort( list );
        for( Long k : list ) {
            System.out.println( k + " :: " + events.get( k ) );
        }
    }
}
