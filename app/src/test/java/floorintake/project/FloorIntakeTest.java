/*
 * THIS IS THE TEST FILE! Do not edit.
 */
package floorintake.project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FloorIntakeTest {
    // Floor intake starts down
    @Test void construction() {
        FloorIntake floorIntake = new FloorIntake();
        assertNotNull(floorIntake, "Floor intake should be constructable");
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
    }

    // OFF state
    @Test void off() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.periodic();
        floorIntake.setState(FloorIntakeStates.OFF);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
        assertFalse(floorIntake.getSolenoidStatus(), "Solenoid should be in");
        assertEquals(0, floorIntake.getMotorSpeed(), "Floor intake should not be moving");

        // Ensure consistency
        Thread.sleep(1000);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
        assertFalse(floorIntake.getSolenoidStatus(), "Solenoid should be in");
        assertEquals(0, floorIntake.getMotorSpeed(), "Floor intake should not be moving");
    }

    // ON State
    @Test void on() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.periodic();
        floorIntake.setState(FloorIntakeStates.ON);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
        assertTrue(floorIntake.getSolenoidStatus(), "Solenoid should be out");
        assertEquals(FloorIntake.FLOOR_INTAKE_SPEED, floorIntake.getMotorSpeed(), "Floor intake should be moving");

        // Ensure consistency
        Thread.sleep(1000);
        floorIntake.periodic();
        assertTrue(floorIntake.isDown(), "Floor intake should be down");
        assertTrue(floorIntake.getSolenoidStatus(), "Solenoid should be out");
        assertEquals(FloorIntake.FLOOR_INTAKE_SPEED, floorIntake.getMotorSpeed(), "Floor intake should be moving");
    }

    // DOWN_HOLD state
    @Test void downHold() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.periodic();
        floorIntake.setState(FloorIntakeStates.DOWN_HOLD);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
        assertTrue(floorIntake.getSolenoidStatus(), "Solenoid should be out");
        assertEquals(0, floorIntake.getMotorSpeed(), "Floor intake should not be moving");

        // Ensure consistency
        Thread.sleep(1000);
        floorIntake.periodic();
        assertTrue(floorIntake.isDown(), "Floor intake should be down");
        assertTrue(floorIntake.getSolenoidStatus(), "Solenoid should be out");
        assertEquals(0, floorIntake.getMotorSpeed(), "Floor intake should not be moving");
    }

    // OUTTAKE state
    @Test void outtake() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.periodic();
        floorIntake.setState(FloorIntakeStates.OUTTAKE);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
        assertTrue(floorIntake.getSolenoidStatus(), "Solenoid should be out");
        assertEquals(-FloorIntake.FLOOR_INTAKE_SPEED, floorIntake.getMotorSpeed(), "Floor intake should not be moving");

        // Ensure consistency
        Thread.sleep(1000);
        floorIntake.periodic();
        assertTrue(floorIntake.isDown(), "Floor intake should be down");
        assertTrue(floorIntake.getSolenoidStatus(), "Solenoid should be out");
        assertEquals(0, floorIntake.getMotorSpeed(), "Floor intake should not be moving");
    }

    // For ON, OUTTAKE, and DOWN_HOLD, the floor intake should say it's on after 1 second
    @Test void downStates() throws InterruptedException {
        FloorIntakeStates[] states = {FloorIntakeStates.ON, FloorIntakeStates.OUTTAKE, FloorIntakeStates.DOWN_HOLD};
        for (FloorIntakeStates state : states) {
            FloorIntake floorIntake = new FloorIntake();
    
            floorIntake.setState(state);
            floorIntake.periodic();
            assertFalse(floorIntake.isDown(), "Floor intake should not be down");
    
            // Wait 1 second and check again -- should be down after a second!
            Thread.sleep(1000);
            floorIntake.periodic();
            assertTrue(floorIntake.isDown(), "Floor intake should be down");
        }
    }

    // The moment the floor intake is commanded to go off, it should report it's not down
    // for safety (if it's in between, wouldn't want an arm collision)
    @Test void turnOffImmediately() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.setState(FloorIntakeStates.ON);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");

        // Wait 1 second and check again -- should be down after a second!
        Thread.sleep(1000);
        floorIntake.periodic();
        assertTrue(floorIntake.isDown(), "Floor intake should be down");

        // Should report not down immediately for safety
        floorIntake.setState(FloorIntakeStates.OFF);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");
    }

    // If the floor intake transitions between down states, it remains reporting that it's down
    // This happens in cube intake (ON <-> OUTTAKE), but all should work for consistency
    @Test void transitionsBetweenDownStates() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.setState(FloorIntakeStates.ON);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");

        // Wait 1 second and check again -- should be down after a second!
        Thread.sleep(1000);
        floorIntake.periodic();
        assertTrue(floorIntake.isDown(), "Floor intake should be down");

        FloorIntakeStates[] states = {FloorIntakeStates.OUTTAKE, FloorIntakeStates.DOWN_HOLD, FloorIntakeStates.ON};
        for (FloorIntakeStates state : states) {
            floorIntake.setState(state);
            floorIntake.periodic();
            assertTrue(floorIntake.isDown(), "Floor intake should still be reported down");
        }

        // And finally, turn off
        floorIntake.setState(FloorIntakeStates.OFF);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should immediately report not down");
    }
    
    // If the floor intake transitions while it's in the process of going down, 
    // it shouldn't magically report it is down after the transition
    @Test void transitionsWhileGoingDown() throws InterruptedException {
        FloorIntake floorIntake = new FloorIntake();

        floorIntake.setState(FloorIntakeStates.ON);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down");

        Thread.sleep(200);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down yet");
        
        // Attempt transition -- shouldn't become down
        floorIntake.setState(FloorIntakeStates.OUTTAKE);
        floorIntake.periodic();
        assertFalse(floorIntake.isDown(), "Floor intake should not be down yet");

        // Finish going down
        Thread.sleep(800);
        floorIntake.periodic();
        assertTrue(floorIntake.isDown(), "Floor intake should be down");
    }
}
