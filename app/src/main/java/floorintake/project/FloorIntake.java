package floorintake.project;

enum FloorIntakeStates {
    OFF,
    ON,
    OUTTAKE,
    DOWN_HOLD
}

public class FloorIntake {
    static final double FLOOR_INTAKE_SPEED = 0.5;

    String stateString;
    WPI_TalonFX motor = new WPI_TalonFX(14);
    Solenoid solenoid = new Solenoid(2);
    Timer isDown = new Timer();
    
    FloorIntakeStates state = FloorIntakeStates.OFF;

    public void reset() {
        this.setState(FloorIntakeStates.OFF);
    }

    public void setState(FloorIntakeStates state) {
        this.state = state;
    }

    public boolean isDown() {
        return (isDown.get() > 0.8);
    }

    public void periodic() {
        if (state == FloorIntakeStates.OFF) {
            isDown.start();       
            motor.set(0);
            stateString = "Floor Intake In ";
            solenoid.set(false);
            isDown.reset();
            isDown.stop();
        } else if (state == FloorIntakeStates.ON) {
            isDown.start();
            stateString = "On (Wheels On)";
            solenoid.set(true);
            motor.set(FloorIntake.FLOOR_INTAKE_SPEED);
            if (isDown()) { // check if problem
                isDown.reset();
                isDown.stop();
            }
            
        } else if (state == FloorIntakeStates.OUTTAKE) {
            isDown.start();
            stateString = "Outtake Cube";
            solenoid.set(true);
            motor.set(-FloorIntake.FLOOR_INTAKE_SPEED);
            if (isDown()) { // check if problem
                isDown.reset();
                isDown.stop();
            }
        } else if (state == FloorIntakeStates.DOWN_HOLD) {
            isDown.start();
            stateString = "On (Wheels Off)";
            solenoid.set(true);
            motor.set(0);
            if (isDown()) { // check if problem
                isDown.reset();
                isDown.stop();
            }
        }

        SmartDashboard.putString("floorIntakeState", stateString);
    }

    // Testing functions, do not edit

    public boolean getSolenoidStatus() {
        return solenoid.get();
    }

    public double getMotorSpeed() {
        return motor.get();
    }

    public static void main(String[] args) {
        System.out.println("Run ./gradlew test to run tests on this class!");
    }
}
