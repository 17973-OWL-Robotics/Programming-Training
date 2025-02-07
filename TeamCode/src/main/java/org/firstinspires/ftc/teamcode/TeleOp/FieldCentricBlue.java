package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Utilities.TeleOp.FieldCentricUtil;

@TeleOp(name = "Blue TeleOp")
public class FieldCentricBlue extends FieldCentricUtil {

    @Override
    public void runOpMode() {

        hardwareMaps();

        Initialize();
        waitForStart();
        if (opModeIsActive()) {
            resetRuntime();
            // Put run blocks here.
            initializeServos();
            servo_power_sets();
            initializeLights();
            while (opModeIsActive()) {
                FieldCentric(-gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.right_bumper);
                assent_control(gamepad2.right_bumper, gamepad2.left_bumper);
                slide_control(-gamepad2.right_stick_y, -gamepad2.left_stick_y, 0.1);
                blue_intake_control(gamepad2.right_trigger, gamepad2.left_trigger, gamepad2.x, gamepad2.dpad_right, gamepad2.touchpad, 0.1);
                sample_control(gamepad2.y, gamepad2.a);
                specimen_control(gamepad1.x, gamepad1.b);
                plowControl(gamepad2.touchpad);
                power_sets();
                telemetry();
            }
        }
    }
}