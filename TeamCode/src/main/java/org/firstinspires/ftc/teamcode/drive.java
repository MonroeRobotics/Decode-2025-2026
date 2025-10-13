package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.ArmController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TeleOp(name = "drive", group = "main")
public class drive extends OpMode {
    HardwareMap hardwareMap;
    private static final Logger log = LoggerFactory.getLogger(drive.class);
    ArmController armController;

    double xPower;
    double yPower;
    double headingPower;
    Gamepad currentGamepad;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;
    Vector2d position = new Vector2d(0, 0); //placeholder
    Pose2d pose = new Pose2d(position, 90); //placeholder

    String intakeState;
    String shotSpeedState;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);
        armController.initArm();

        currentGamepad = new Gamepad();
        previousGamepad = new Gamepad();

        mecanumDrive = new MecanumDrive(hardwareMap, pose);
    }

    @Override
    public void loop() {
        if (currentGamepad.left_stick_x >= 0.2 || currentGamepad.left_stick_x <= -0.2) {
            xPower = currentGamepad.left_stick_x;
        }
        if (currentGamepad.left_stick_y >= 0.2 || currentGamepad.left_stick_y <= -0.2) {
            yPower = currentGamepad.left_stick_y;

            if (armController.advancementServoSpeed != 0) {intakeState = "on";}
            else {intakeState = "off";}

            if (armController.shotSpeed == 0.45) {shotSpeedState = "close";}
            else if (armController.shotSpeed == armController.farShotSpeed) {shotSpeedState = "far";}
            else {shotSpeedState = "off";}

            telemetry.addData("launchMotorSpeed", armController.shotSpeed);
            telemetry.addData("advancement servo Status", intakeState);
        }
    }
}
