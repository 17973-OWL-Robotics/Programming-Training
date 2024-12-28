package org.firstinspires.ftc.teamcode.Utilities.TeleOp;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.ControllerParams.ArmControllerParams;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.ControllerParams.IntakeControllerParams;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.ControllerParams.LeftAssentControllerParams;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.ControllerParams.RightAssentControllerParams;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.PIDF_Controller;
import org.firstinspires.ftc.teamcode.Utilities.TeleOp.FileWriter.FileReadWriter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FieldCentricUtil extends LinearOpMode {

    @Override
    public void runOpMode() {

    }

    // Create All Motors
    DcMotorEx frontRightMotor;
    DcMotorEx backRightMotor;
    DcMotorEx frontLeftMotor;
    DcMotorEx backLeftMotor;
    DcMotorEx rightAssentMotor;
    DcMotorEx leftAssentMotor;
    DcMotorEx armMotor;
    DcMotorEx intakeMotor;

    // Create All Servos
    Servo intakePivot;
    Servo sampleServo;
    Servo specimenServo;

    // Create All CR Servos
    CRServo left;
    CRServo right;

    // Create sensor and imu
    ColorSensor sensor;
    IMU imu;

    // Create revBlinkinLedDriver for Led Strip Lights
    RevBlinkinLedDriver revBlinkinLedDriver;

    // Drive Motor Powers
    double front_left_power;
    double front_right_power;
    double back_left_power;
    double back_right_power;

    // Slide Motor Powers
    double arm_slide_power;
    double intake_slide_power;

    // Servo Positions
    double sample_servo_position;
    double specimen_servo_position;
    double intake_pivot_position;

    // Servo Powers
    double intake_wheel_power;

    // Hang Motor Powers
    int hang_slide_power;

    // Other Variables
    double rotX;
    double rotY;
    double speedModifier;
    boolean isUp;
    boolean doingIntake;
    boolean touchSet;
    boolean whichTF;

    // Values for Touchpad
    double TFOneRefX;
    double TFOneRefY;
    double TFTwoRefX;
    double TFTwoRefY;
    double TFOneX;
    double TFOneY;
    double TFTwoX;
    double TFTwoY;

    double armEncoderPosition;
    double intakeEncoderPosition;
    double leftAssentEncoderPosition;
    double rightAssentEncoderPosition;

    double robotPoseX;
    double robotPoseY;
    double robotHeading;

    FileReadWriter fileReadWriter;
    MecanumDrive drive;

    Pose2d startPose;

    // Hardware Maps
    public void hardwareMaps() {
        frontRightMotor = hardwareMap.get(DcMotorEx.class, "frontRightMotor");
        backRightMotor = hardwareMap.get(DcMotorEx.class, "backRightMotor");
        frontLeftMotor = hardwareMap.get(DcMotorEx.class, "frontLeftMotor");
        backLeftMotor = hardwareMap.get(DcMotorEx.class, "backLeftMotor");

        rightAssentMotor = hardwareMap.get(DcMotorEx.class, "rightAssentMotor");
        leftAssentMotor = hardwareMap.get(DcMotorEx.class, "leftAssentMotor");

        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        intakePivot = hardwareMap.get(Servo.class, "intakePivot");
        sampleServo = hardwareMap.get(Servo.class, "sampleServo");
        specimenServo = hardwareMap.get(Servo.class, "specimenClaw");

        left = hardwareMap.get(CRServo.class, "left");
        right = hardwareMap.get(CRServo.class, "right");

        sensor = hardwareMap.get(ColorSensor.class, "blockDet");
        revBlinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "rev");

        imu = hardwareMap.get(IMU.class, "imu");
    }

    public void Initialize() {
        try {
            fileReadWriter = new FileReadWriter();
            fileReadWriter.readFile();
        } catch (IOException e) {
            // Nothing
        }

        armEncoderPosition = fileReadWriter.read[1];
        intakeEncoderPosition = fileReadWriter.read[2];
        leftAssentEncoderPosition = fileReadWriter.read[3];
        rightAssentEncoderPosition = fileReadWriter.read[4];
        robotPoseX = fileReadWriter.read[5];
        robotPoseY = fileReadWriter.read[6];
        robotHeading = fileReadWriter.read[7];

        startPose = new Pose2d(robotPoseX, robotPoseY, Math.toRadians(robotHeading));
        drive = new MecanumDrive(hardwareMap, startPose);

        frontRightMotor = drive.rightFront;
        frontLeftMotor = drive.leftFront;
        backRightMotor = drive.rightBack;
        backLeftMotor = drive.leftBack;

        imu = drive.lazyImu.get();

        rightAssentMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftAssentMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftAssentMotor.setDirection(DcMotor.Direction.REVERSE);
        armMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        right.setDirection(CRServo.Direction.REVERSE);

        armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftAssentMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightAssentMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu.resetYaw();

        intake_pivot_position = 0.5;
        sample_servo_position = 0.8;
        specimen_servo_position = 0.7;
        isUp = true;

        revBlinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET);
    }

    // Motor Initialization
    // Sets all Motors to Break
    public void initializeMotors() {

        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightAssentMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftAssentMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        leftAssentMotor.setDirection(DcMotor.Direction.REVERSE);
        armMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        right.setDirection(CRServo.Direction.REVERSE);

        armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftAssentMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightAssentMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        touchSet = false;
    }

    // Servo Positions on opModeInInit()
    public void initializeServos() {
        intake_pivot_position = 0.5;
        sample_servo_position = 0.8;
        specimen_servo_position = 0.7;
        isUp = true;
    }

    public void initializeLights() {
        revBlinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET);
    }

    // IMU Initialization
    public void set_up_imu() {
        // Create a RevHubOrientationOnRobot object for use with an IMU in a REV Robotics Control
        // Hub or Expansion Hub, specifying the hub's orientation on the robot via the direction
        // that the REV Robotics logo is facing and the direction that the USB ports are facing.
        imu.initialize(
                new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                                RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        imu.resetYaw();
    }

    //changes stick intake to field centric
    public void rotate_x_and_y(double forward_back_stick, double left_right_stick) {
        YawPitchRollAngles myYawPitchRollAngles;
        double heading;

        myYawPitchRollAngles = imu.getRobotYawPitchRollAngles();
        heading = myYawPitchRollAngles.getYaw(AngleUnit.DEGREES) + 90;
        left_right_stick = -left_right_stick;
        rotX = left_right_stick * Math.cos(-heading / 180 * Math.PI) - forward_back_stick * Math.sin(-heading / 180 * Math.PI);
        rotY = left_right_stick * Math.sin(-heading / 180 * Math.PI) + forward_back_stick * Math.cos(-heading / 180 * Math.PI);
    }

    //provide 360 degree movement from a stick
    public void drive_control(double turning_stick) {
        if (gamepad2.touchpad_finger_1 && gamepad2.touchpad_finger_2) {
            if (!touchSet) {
                touchSet = true;
                if (gamepad2.touchpad_finger_1_x > gamepad2.touchpad_finger_2_x) {
                    TFOneRefX = gamepad2.touchpad_finger_1_x;
                    TFOneRefY = -gamepad2.touchpad_finger_1_y;
                    TFTwoRefX = gamepad2.touchpad_finger_2_x;
                    TFTwoRefY = gamepad2.touchpad_finger_2_y;
                    whichTF = true;
                } else {
                    TFTwoRefX = gamepad2.touchpad_finger_1_x;
                    TFTwoRefY = gamepad2.touchpad_finger_1_y;
                    TFOneRefX = gamepad2.touchpad_finger_2_x;
                    TFOneRefY = -gamepad2.touchpad_finger_2_y;
                    whichTF = false;
                }
            } else {
                if (whichTF) {
                     TFOneX = gamepad2.touchpad_finger_1_x;
                     TFOneY = -gamepad2.touchpad_finger_1_y;
                     TFTwoX = gamepad2.touchpad_finger_2_x;
                     TFTwoY = gamepad2.touchpad_finger_2_y;
                } else {
                     TFTwoX = gamepad2.touchpad_finger_1_x;
                     TFTwoY = gamepad2.touchpad_finger_1_y;
                     TFOneX = gamepad2.touchpad_finger_2_x;
                     TFOneY = -gamepad2.touchpad_finger_2_y;
                }
                front_left_power = ((TFOneY - TFOneRefY)
                         + (TFOneX - TFOneRefX)
                         + (TFTwoX - TFTwoRefX))*0.4;
                front_right_power = ((TFOneY - TFOneRefY)
                         - (TFOneX - TFOneRefX)
                         - (TFTwoX - TFTwoRefX))*0.4;
                back_left_power = ((TFOneY - TFOneRefY)
                         - (TFOneX - TFOneRefX)
                         + (TFTwoX - TFTwoRefX))*0.4;
                back_right_power = ((TFOneY - TFOneRefY)
                         + (TFOneX - TFOneRefX)
                         - (TFTwoX - TFTwoRefX))*0.5;
            }
        } else {
             front_left_power = rotY + rotX + turning_stick;
             front_right_power = (rotY - rotX) - turning_stick;
             back_left_power = (rotY - rotX) + turning_stick;
             back_right_power = (rotY + rotX) - turning_stick;
        }
    }

    // Sets motor power sets
    public void power_sets(boolean slowMode) {
        drive_power_sets(slowMode);
        slide_power_sets();
        servo_power_sets();
    }

    // Sets wrist servo to positions set by slide control
    public void sample_control(boolean output_up, boolean output_down) {
        try {
            if (output_up) {
                sampleServo.setPosition(0.1);
                sample_servo_position = 0.1;
                TimeUnit.MILLISECONDS.sleep(900);
                specimen_servo_position = 0.7;
            } else if (output_down) {
                sample_servo_position = 0.8;
            }
        } catch (InterruptedException e) {
            // Nothing
        }
    }

    // Opens and closes specimen claw
    public void specimen_control(boolean grab_specimen, boolean release_specimen) {
        if (grab_specimen) {
            specimen_servo_position = 0.5;
        } else if (release_specimen) {
            specimen_servo_position = 0.7;
        }

        if (gamepad1.left_bumper) {
            specimen_servo_position = 0.73;
        }
    }

    // Parameter for assent motors
    LeftAssentControllerParams leftAssentControllerParams = new LeftAssentControllerParams();
    PIDF_Controller leftAssentController;

    RightAssentControllerParams rightAssentControllerParams = new RightAssentControllerParams();
    PIDF_Controller rightAssentController;

    boolean assentInti = false;
    double leftAssentPosition;
    double rightAssentPosition;

    // Control the ascent motors with pidf
    public void assent_control(boolean ascend_button, boolean descend_button) {
        if (!assentInti) {
            leftAssentController = new PIDF_Controller.Builder()
                    .setControllerMotor(leftAssentMotor)
                    .setControllerParams(leftAssentControllerParams.params)
                    .setMaxSpeed(1)
                    .setEncoderPosition(leftAssentEncoderPosition)
                    .setStopOnTargetReached(false)
                    .build();

            rightAssentController = new PIDF_Controller.Builder()
                    .setControllerMotor(rightAssentMotor)
                    .setControllerParams(rightAssentControllerParams.params)
                    .setMaxSpeed(1)
                    .setEncoderPosition(rightAssentEncoderPosition)
                    .setStopOnTargetReached(false)
                    .build();

            assentInti = true;
        }

        if (ascend_button) {
            hang_slide_power = 1;
        } else if (descend_button) {
            hang_slide_power = -1;
        } else {
            hang_slide_power = 0;
        }
    }

    // Parameters for scoring slides
    ArmControllerParams armControllerParams = new ArmControllerParams();
    PIDF_Controller armController;

    IntakeControllerParams intakeControllerParams = new IntakeControllerParams();
    PIDF_Controller intakeController;

    boolean slideInit = false;
    double armPosition = 0;
    double intakePosition = 0;

    // controls lhe slides for scoring
    public void slide_control(double intake_slide_stick, double lifter_slide_stick, boolean reset, double deadZone) {

        if (!slideInit) {
            armController = new PIDF_Controller.Builder()
                    .setControllerMotor(armMotor)
                    .setControllerParams(armControllerParams.params)
                    .setMaxSpeed(1)
                    .setEncoderPosition(armEncoderPosition)
                    .setStopOnTargetReached(false)
                    .build();

            intakeController = new PIDF_Controller.Builder()
                    .setControllerMotor(intakeMotor)
                    .setControllerParams(intakeControllerParams.params)
                    .setMaxSpeed(1)
                    .setEncoderPosition(intakeEncoderPosition)
                    .setStopOnTargetReached(false)
                    .build();

            slideInit = true;
        }

        if (gamepad2.dpad_up) {
            armPosition = 24;
        } else if (gamepad2.dpad_down) {
            armPosition = 18;
        }

        if (lifter_slide_stick > deadZone) {
            armPosition = 37;
        } else if (lifter_slide_stick < -deadZone) {
            armPosition = 0;
        }

        if (gamepad2.a) {
            armController.runController(false);
            if (Math.abs(lifter_slide_stick) > deadZone) {
                armMotor.setPower(lifter_slide_stick);
            } else {
                armMotor.setPower(0);
            }
            if (gamepad2.dpad_down) {
                armPosition = 7;
            }
        } else {
            armController.runController(true);
            armController.extendTo(armPosition);
            armController.loopController();
        }

        // Intake slide power
        if (Math.abs(intake_slide_stick) > deadZone) {
            intakeMotor.setPower(-intake_slide_stick);
        } else {
            intakeMotor.setPower(0);
        }
    }

    public void red_intake_control(double intake_wheels_in,
                                   double intake_wheels_out,
                                   boolean intake_pivot_up,
                                   boolean intake_pivot_down,
                                   boolean intake_pivot_reset,
                                   double deadZone) {
        loopSensor();

        //automatic and manual control of intake wheels
        if (!isUp && sensorDistance > 3.5 && sensorDistance < 4.5) {
            intake_wheel_power = -0.3;
        } else if (intake_wheels_out > deadZone) {
            intake_wheel_power = 1;
        } else if (intake_wheels_in > deadZone) {
            intake_wheel_power = -1;
        } else if (intake_pivot_position < 0.3) {
            intake_wheel_power = 1;
        }else if (intakePivot.getPosition() > 0.60){
            intake_wheel_power = -1;
            intake_pivot_position = 0.5;
        } else {
            intake_wheel_power = 0;
        }

        //puts the intake pivot to is positions based on inputs
        if (intake_pivot_up) {
            intake_pivot_position = 0.69;
            isUp = true;
        } else if (intake_pivot_down) {
            intake_pivot_position = 0.12;
            intake_wheel_power = 1;
            isUp = false;
        } else if (intake_pivot_reset) {
            intake_pivot_position = 0.5;
            isUp = true;
        } else if (!isUp && sensorDistance < 1 && red || !isUp && sensorDistance < 1 && yellow) {
            intake_pivot_position = 0.50;
            doingIntake = true;
        } else if (!isUp && sensorDistance < 1 && blue) {
            intake_pivot_position = 0.31;
            intake_wheel_power = -1;
        }
    }

    public void blue_intake_control(double intake_wheels_in,
                                    double intake_wheels_out,
                                    boolean intake_pivot_up,
                                    boolean intake_pivot_down,
                                    boolean intake_pivot_reset,
                                    double deadZone) {

        loopSensor();

        if (!isUp && sensorDistance > 3.5 && sensorDistance < 4.5) {
            intake_wheel_power = -0.3;
        } else if (intake_wheels_out > deadZone) {
            intake_wheel_power = 1;
        } else if (intake_wheels_in > deadZone) {
            intake_wheel_power = -1;
        } else if (intake_pivot_position < 0.3) {
            intake_wheel_power = 1;
        } else {
            intake_wheel_power = 0;
        }

        try {
            if (intake_pivot_up) {
                specimenServo.setPosition(0.7);
                intakePivot.setPosition(0.69);
                TimeUnit.MILLISECONDS.sleep(300);
                left.setPower(-0.5);
                right.setPower(-0.5);
                TimeUnit.MILLISECONDS.sleep(300);
                specimenServo.setPosition(0.5);
                specimen_servo_position = 0.5;
                TimeUnit.MILLISECONDS.sleep(100);
                left.setPower(0);
                right.setPower(0);
                TimeUnit.MILLISECONDS.sleep(100);
                intakePivot.setPosition(0.5);
                isUp  = true;
            } else if (intake_pivot_down) {
                intake_pivot_position = 0.12;
                intake_wheel_power = 1;
                isUp = false;
            } else if (intake_pivot_reset) {
                intake_pivot_position = 0.5;
                isUp = true;
            } else if (!isUp && sensorDistance < 1 && blue || !isUp && sensorDistance < 1 && yellow){
                intake_pivot_position = 0.50;
                doingIntake = true;
            } else if (!isUp && sensorDistance < 1 && red){
                intake_pivot_position = 0.31;
                intake_wheel_power = -1;
            }
        } catch (InterruptedException e) {
            // Nothing
        }
    }

    public void doIntake(){
        if (intakeController.targetReached) {
            intake_pivot_position = 0.69;
            isUp  = true;

        }
    }

    // Power Sets
    public void servo_power_sets() {
        intakePivot.setPosition(intake_pivot_position);
        sampleServo.setPosition(sample_servo_position);
        specimenServo.setPosition(specimen_servo_position);
        left.setPower(intake_wheel_power);
        right.setPower(intake_wheel_power);
    }
    public void slide_power_sets() {
        leftAssentMotor.setPower(hang_slide_power);
        rightAssentMotor.setPower(hang_slide_power);
    }
    public void drive_power_sets(boolean slow_mode) {
        if (slow_mode) {
            speedModifier = 0.3;
        } else speedModifier = 1;
        backLeftMotor.setPower(back_left_power * speedModifier);
        backRightMotor.setPower(back_right_power * speedModifier);
        frontLeftMotor.setPower(front_left_power * speedModifier);
        frontRightMotor.setPower(front_right_power * speedModifier);
    }

    public void writeRobotInfo() {
        drive.updatePoseEstimate();

        armEncoderPosition = armMotor.getCurrentPosition();
        intakeEncoderPosition = intakeMotor.getCurrentPosition();
        leftAssentEncoderPosition = leftAssentMotor.getCurrentPosition();
        rightAssentEncoderPosition = rightAssentMotor.getCurrentPosition();
        robotPoseX = drive.pose.position.x;
        robotPoseY = drive.pose.position.y;
        robotHeading = Math.toDegrees(drive.pose.heading.toDouble());

        try {
            fileReadWriter.writeFile(
                    armEncoderPosition,
                    intakeEncoderPosition,
                    leftAssentEncoderPosition,
                    rightAssentEncoderPosition,
                    robotPoseX,
                    robotPoseY,
                    robotHeading
            );
        } catch (IOException e) {
            // Nothing
        }
    }

    // Telemetry for Debugging
    public void telemetry() {
        drive.updatePoseEstimate();
        telemetry.addLine("-----color distance sensor-----");
        telemetry.addData("distance", ((DistanceSensor) sensor).getDistance(DistanceUnit.CM));
        telemetry.addData("red", sensor.red());
        telemetry.addData("green", sensor.green());
        telemetry.addData("blue", sensor.blue());
        telemetry.addData("red", red);
        telemetry.addData("yellow", yellow);
        telemetry.addData("blue", blue);
        telemetry.addLine("-----intake assembly-----");
        telemetry.addData("intake slide position", intakeMotor.getCurrentPosition());
        telemetry.addData("intake slide power", intakePosition);
        telemetry.addData("intake wheel power", intake_wheel_power);
        telemetry.addData("intake wrist position", intake_pivot_position);
        telemetry.addData("IsUp", isUp);
        telemetry.addLine("-----lifter assembly-----");
        telemetry.addData("lifter slide position", armMotor.getCurrentPosition());
        telemetry.addData("lifter slide power", arm_slide_power);
        telemetry.addData("output trough position", sample_servo_position);
        telemetry.addData("specimen claw position", specimen_servo_position);
        telemetry.addData("IMU",imu.getRobotYawPitchRollAngles());
        telemetry.addData("Robot Pose X", drive.pose.position.x);
        telemetry.addData("Robot Pose Y", drive.pose.position.y);
        telemetry.addData("Robot Pose Heading", Math.toDegrees(drive.pose.heading.toDouble()));
        telemetry.update();
    }

    // Sensor Utilities
    public boolean red = false;
    public boolean yellow = false;
    public boolean blue = false;

    public double sensorDistance;

    public void loopSensor() {
        sensorDistance = ((DistanceSensor) sensor).getDistance(DistanceUnit.CM);

        if (sensor.red() > 4000 && sensor.green() > 6000) {
            yellow = true;
        } else if (sensor.blue() > 3000) {
            blue = true;
        } else if (sensor.red() > 3000) {
            red = true;
        } else {
            red = false;
            yellow = false;
            blue = false;
        }
    }
}
