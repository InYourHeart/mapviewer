package coalitionofpowers.Utility;

public class Timer {

    private static long startTime;
    private static long endTime;

    public static void start() {
        startTime = System.nanoTime();
        System.out.println("Timer start: " + startTime);
    }

    public static void stop() {
        endTime = System.nanoTime();
        System.out.println("Timer stop: " + endTime + " (" + +((endTime - startTime) / 1000000) + "ms)");
    }
}
