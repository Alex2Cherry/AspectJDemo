package demo.billy.com.aspectjdemo.aspectj;

/**
 * 计时器
 * @author billy.qi
 * @since 16/12/2 16:44
 */
public class TimeWatcher {
    private long startTime;
    private long endTime;
    private long elapsedTime;

    public void reset() {
        startTime = 0;
        endTime = 0;
        elapsedTime = 0;
    }

    public void start() {
        reset();
        startTime = System.nanoTime();
    }

    public void stop() {
        if (startTime != 0) {
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
        } else {
            reset();
        }
    }

    public long getTotalTimeInNano(){
        return elapsedTime;
    }

    public long getTotalTimeInMillis() {
        return elapsedTime / 1000000;
    }
}
