package org.firstinspires.ftc.teamcode.Utilities.Auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.ControllerParams.IntakeControllerParams;
import org.firstinspires.ftc.teamcode.Utilities.PIDF_Controller.PIDF_Controller;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/*
    This file contains Actions for the robot intake.
    These actions can be called in actions.runBlocking
*/
public class Intake {

    // Create Motor and Servos
    DcMotorEx intakeMotor;
    CRServo left;
    CRServo right;
    Servo intakePivot;
    Servo sampleServo;

    // Create distanceSensor
    DistanceSensor distanceSensor;

    // Variables for Intake
    boolean sampleDetected = false;

    // Create PIDF Controller and Intake Parameters
    IntakeControllerParams intakeControllerParams = new IntakeControllerParams();
    PIDF_Controller controller;

    // Intake Constructor
    public Intake(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        left = hardwareMap.get(CRServo.class, "left");
        right = hardwareMap.get(CRServo.class, "right");
        intakePivot = hardwareMap.get(Servo.class, "intakePivot");
        sampleServo = hardwareMap.get(Servo.class, "specimenClaw");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "blockDet");

        right.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        controller = new PIDF_Controller.Builder()
                .setControllerMotor(intakeMotor)
                .setControllerParams(intakeControllerParams.params)
                .setMaxSpeed(1)
                .setStopOnTargetReached(false)
                .build();
    }

    // Create init() for Auto
    public void init() {
        intakePivot.setPosition(0.5);
    }

    // The IntakeOut class & action tell the intake slide to fully extend
    // The maximum intake extension is 19in
    public class IntakeOut implements Action {
        private boolean initialized = false;

        @Override
        public boolean run(@NonNull TelemetryPacket p) {

            if (!initialized) {
                controller.extendTo(19);
                initialized = true;
            }

            double pos = intakeMotor.getCurrentPosition();
            p.put("Motor Position: ", pos);
            if (controller.running) {
                controller.loopController();
                return true;
            } else {
                return false;
            }
        }
    }
    public Action intakeOut() {
        return new IntakeOut();
    }

    // The IntakeIn class and action returns the intake slide to the 0 position (0in)
    public class IntakeIn implements Action {
        private boolean initialized = false;

        @Override
        public boolean run(@NonNull TelemetryPacket p) {

            if (!initialized) {
                controller.retractTo(0);
                initialized = true;
            }

            double pos = intakeMotor.getCurrentPosition();
            p.put("Motor Position: ", pos);
            if (controller.targetReached) {
                return false;
            } else {
                controller.loopController();
                return true;
            }
        }
    }
    public Action intakeIn() {
        return new IntakeIn();
    }

    // Intakes a sample
    public class IntakeSample implements Action {
        private boolean initialized = false;

        @Override
        public boolean run(@NonNull TelemetryPacket p) {

            if (!initialized) {
                intakePivot.setPosition(0.12);
                left.setPower(1);
                right.setPower(1);
                intakeMotor.setPower(0.8);
                initialized = true;
                sampleDetected = false;
            }

            if (sampleDetected) {
                intakePivot.setPosition(0.5);
                left.setPower(0);
                right.setPower(0);
                intakeMotor.setPower(0);
                return false;
            } else {
                SampleDetection();
                return true;
            }
        }
    }
    public Action intakeSample() {
        return new IntakeSample();
    }

    // Transfers the sample
    public class TransferSample implements Action {

        @Override
        public boolean run(@NonNull TelemetryPacket p) {

            try {
                sampleServo.setPosition(0.65);
                intakePivot.setPosition(0.73);
                TimeUnit.MILLISECONDS.sleep(500);
                left.setPower(-0.3);
                right.setPower(-0.3);
                TimeUnit.MILLISECONDS.sleep(150);
                sampleServo.setPosition(0.78);
                left.setPower(0);
                right.setPower(0);
                TimeUnit.MILLISECONDS.sleep(100);
                intakePivot.setPosition(0.5);
            } catch (InterruptedException e) {
                // Nothing
            }
            return false;
        }
    }
    public Action transferSample() {
        return new TransferSample();
    }

    // Sample detection is required to tell if the robot has a sample
    public void SampleDetection() {
        if (!sampleDetected) {
            if (distanceSensor.getDistance(DistanceUnit.CM) < 4.5) {
                sampleDetected = true;
            }
        } else {
            sampleDetected = false;
        }
    }
}
