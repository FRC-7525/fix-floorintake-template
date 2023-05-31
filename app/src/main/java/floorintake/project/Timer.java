package floorintake.project;

public class Timer {
    boolean running = false;
    double accumulatedTime = 0;
    double startTime = 0;
    
    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    public void stop() {
        if (running) {
            accumulatedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    public void reset() {
        accumulatedTime = 0;
        startTime = System.currentTimeMillis();
    }

    public double get() {
        double time = accumulatedTime;
        if (running) {
            time += System.currentTimeMillis() - startTime;
        }

        // Return seconds
        return time / 1000;
    }
}
