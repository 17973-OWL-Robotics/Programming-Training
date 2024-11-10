package org.firstinspires.ftc.teamcode.Autonomous;

import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

// Non-RR imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Utilities.Auto.Arm;
import org.firstinspires.ftc.teamcode.Utilities.Auto.Intake;
import org.firstinspires.ftc.teamcode.Utilities.Auto.PoseUpdate;
import org.firstinspires.ftc.teamcode.Utilities.Auto.RedTrajectories;

public class Red extends LinearOpMode {

    @Override
    public void runOpMode() {

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-36 ,-60, Math.toRadians(180)));
        RedTrajectories trajectories = new RedTrajectories(drive);
        Arm arm = new Arm(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        PoseUpdate pose = new PoseUpdate();

        waitForStart();
        if (opModeIsActive()) {
            Actions.runBlocking(
                    new SequentialAction(

                    )
            );
        }
    }
}
