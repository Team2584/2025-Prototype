// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import choreo.auto.AutoChooser;
import choreo.auto.AutoFactory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.generated.TunerConstants;
import frc.robot.AutoRoutines;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;



public class RobotContainer {
    private double governor = 0.35; // Added to slow MaxSpeed to 35%. Set to 1 for full speed.
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12VoltsMps desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(1*Math.PI).in(RadiansPerSecond); // 1/2 of a rotation per second max angular velocity


    // THIS IS ALL SWERVE DRIVE SETUP
    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();
    private final SwerveRequest.RobotCentric forwardStraight = new SwerveRequest.RobotCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);



    // Creates a xbox controller to use in driver control
    private final CommandXboxController joystick = new CommandXboxController(0); 



    // Creates an instance of the drivetrain
    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain(); 
                
    private final Telemetry logger = new Telemetry(MaxSpeed); // This is not important right now. It is used for logging robot data


     /* Path follower */
    private final AutoFactory autoFactory; // Choreo Auto
    private final AutoRoutines autoRoutines; // Choreo Auto
    private final AutoChooser autoChooser = new AutoChooser(); // Choreo Autochooser

    private final Field2d m_field = new Field2d(); //IGNORE ME


/*---------------------------------------------------------------------------------------------------------------- */
// FOR PEOPLE LEARNING, YOU ONLY CARE ABOUT THINGS BELOW THIS LINE!!! (FOR NOW) 
// MOSTLY LINES 77-92 and 147-157

    // A custom builder method that creates an instance of ExampleSubsystem
    private ExampleSubsystem buildSubsystem() {
        return new ExampleSubsystem(15);
    }
    private ElevatorSubsystem buildSubsystem() {
        return new ElevatorSubsystem(15);
    }


    // A custom getter class so that we dont accidentally mess up the variable (exSub) that we have created
    //If you need to access the subsystem, USE THIS!
    private ExampleSubsystem getExampleSubsystem() {
        return exSub;
    }

    private ElevatorSubsystem getElevatorSubsystem() {
        return exSub;
    }


    private final ExampleSubsystem exSub = buildSubsystem(); //Calls method to create subsystem instance and puts it in a variable we can use
    private final ElevatorSubsystem exSub = buildSubsystem(); //Calls method to create subsystem instance and puts it in a variable we can use



// YOU DONT CARE ABOUT THIS YET!
    public RobotContainer() {

        autoFactory = drivetrain.createAutoFactory();
        autoRoutines = new AutoRoutines(autoFactory);

        autoChooser.addRoutine("SimplePath", autoRoutines::simplePathAuto);
        SmartDashboard.putData("Auto Chooser", autoChooser);

        SmartDashboard.putData("Field", m_field);

        configureBindings();

    }

    private void configureBindings() {
      // Note that X is defined as forward according to WPILib convention,
      // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-joystick.getLeftY() * MaxSpeed * governor) // Drive forward with negative Y (forward)
                    .withVelocityY(-joystick.getLeftX() * MaxSpeed * governor) // Drive left with negative X (left)
                    .withRotationalRate(-joystick.getRightX() * MaxAngularRate * governor) // Drive counterclockwise with negative X (left)
            )
        );

        joystick.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-joystick.getLeftY(), -joystick.getLeftX()))
        ));

        joystick.pov(0).whileTrue(drivetrain.applyRequest(() ->
            forwardStraight.withVelocityX(0.5).withVelocityY(0))
        );
        joystick.pov(180).whileTrue(drivetrain.applyRequest(() ->
            forwardStraight.withVelocityX(-0.5).withVelocityY(0))
        );



        //NOTE: LAMBDA EXPRESSION DEFINITION
        //A lambda expression creates a command that only exists when it is called. It is created, run, and then destroyed to save memory. 
        //This is the function of the runOnce(() -> (method to run)) structure
        //PS: THEY ARE VERY CONVIENIENT TO USE!

        //While the "a" button is being pressed on the controller, run a lambda expression on the subsystem.
        //This one gets the a specific motor within the subsytem and sets the speed 
        //joystick.a().whileTrue(getExampleSubsystem().runOnce(() -> getExampleSubsystem().setMotorSpeed(0.15)));
        //                               /\                           /\                /\    
        //                               |                            |                 |
        //                               |                            |                 |
        //                               |                            |                 |
        //                    Subsystem to be accessed          Subsystem that       The method within the subsystem 
        //                                                      has the method       for the intended action
        //                                                                           (in this example we are setting the speed of a motor)
    


        //Same as above example but instead turns the motor off on the "x" button
        //joystick.x().whileTrue(getExampleSubsystem().runOnce(() -> getExampleSubsystem().setMotorSpeed(0)));


        joystick.a().whileTrue(getElevatorSubsystem().runOnce(() -> getElevatorSubsystem().setMotorSpeed(0.15)));
        joystick.x().whileTrue(getElevatorSubsystem().runOnce(() -> getElevatorSubsystem().setMotorSpeed(0)));

        //IGNORE THIS! THIS IS LOGGING!
        drivetrain.registerTelemetry(logger::telemeterize);
    }

  public Command getAutonomousCommand() { 
      /* First put the drivetrain into auto run mode, then run the auto */
      return autoChooser.selectedCommand();
  }
}