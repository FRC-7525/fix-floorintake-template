package floorintake.project;

public class Solenoid {
    boolean state = false;
    public Solenoid(int id) {}

    public void set(boolean state) {
        this.state = state;
    }

    public boolean get() {
        return this.state;
    }
}
