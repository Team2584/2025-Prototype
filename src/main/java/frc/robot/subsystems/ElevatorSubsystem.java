package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ElevatorSubsystem extends SubsystemBase {

    // Create a motor variable for use in code 
    private final TalonFX elevator_motor_1; 
    private final TalonFX elevator_motor_2; 


    // Initialization of Subsystem and it's motors
    // This is what is called when you create a new object of class ExampleSubsystem
    public ElevatorSubsystem(int motorID) {
        elevator_motor_1 = new TalonFX(motorID); // Sets the motor variable equal to the constuctor for a CTRE motor given the motor ID
        elevator_motor_2 = new TalonFX(motorID); // Sets the motor variable equal to the constuctor for a CTRE motor given the motor ID
    }


    /** IMPORTANT FOR PEOPLE LEARNING
    The following two methods are examples of custom methods. They can be called in other files as a way of interacting with the subsystem
    and the motors within the subsystem.
    THIS IS HOW YOU WILL INTERACT WITH SUBSYSTEMS!!!. All important variables should be private and have public getter and/or setter methods
    that can be used instead.
    */


     /**
     * CUSTOM SETTER METHOD: 
     * Set speed of motor 
     * USE THIS TO SET THE SPEED OF THE MOTOR
     */
    public void setMotorSpeed(double speed){ 
        elevator_motor_1.set(speed); // This is a CTRE function sets the speed of the motor to the given speed
        elevator_motor_2.set(speed); // This is a CTRE function sets the speed of the motor to the given speed
    }


    /**
     * CUSTOM GETTER METHOD:
     * Get motor instance
     * USE THIS TO GET A SPECIFIC MOTOR WITHIN A SUBSYSTEM
     */
    public TalonFX getMotorA(){
        return elevator_motor_1;
    }

    public TalonFX getMotorB(){
        return elevator_motor_2;
    }




    // The following two methods are in every subsystem and run everytime the code scheduler checks the motor

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }

    @Override
    public void simulationPeriodic() {
    
    }
}
