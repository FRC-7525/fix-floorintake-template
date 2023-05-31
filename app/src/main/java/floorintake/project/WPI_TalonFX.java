package floorintake.project;

public class WPI_TalonFX {
    double speed = 0;
    
    public WPI_TalonFX(int id) {}

    public void set(double speed) {
        this.speed = speed;
    }

    public double get() {
        return this.speed;
    }
}
