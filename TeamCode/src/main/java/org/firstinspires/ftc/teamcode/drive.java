package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.ArmController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TeleOp(name = "drive", group = "main")
public class drive extends OpMode {
    private static final Logger log = LoggerFactory.getLogger(drive.class);
    ArmController armController;

    double xPower;
    double yPower;
    double headingPower;

    double drivePowerReduction = 0.8;
    double turnPowerReduction = 0.75;

    Gamepad currentGamepad;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;
    Vector2d position = new Vector2d(0, 0);
    Pose2d pose = new Pose2d(position, 90);

    boolean intakeOn = false;
    boolean outtakeOn = false;
    boolean closeShotOn = false;
    boolean farShotOn = false;

    String intakeState;
    String shotSpeedState;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);
        armController.initArm();

        previousGamepad = new Gamepad();

        mecanumDrive = new MecanumDrive(hardwareMap, pose);
    }

    @Override
    public void loop() {
        if (gamepad1.left_stick_x >= 0.2 || gamepad1.left_stick_x <= -0.2) {
            xPower = -gamepad1.left_stick_x;
        }
        if (gamepad1.left_stick_y >= 0.2 || gamepad1.left_stick_y <= -0.2) {
            yPower = -gamepad1.left_stick_y;
        }
        if (gamepad1.right_stick_x >= 0.2 || gamepad1.right_stick_x <= -0.2){
            headingPower = -gamepad1.right_stick_x;
        }

        xPower *= drivePowerReduction;
        yPower *= drivePowerReduction;
        headingPower *= drivePowerReduction;


        if (gamepad1.right_bumper && !previousGamepad.right_bumper){
            armController.hasUpdatedOuttakeTimer = false;
            armController.hasUpdatedAdjusterTimer = false;
            if (!closeShotOn) {
                armController.currentArmState = ArmController.armState.closeShot;
                closeShotOn = true;
            }
            else {
                armController.currentArmState = ArmController.armState.rest;
                closeShotOn = false;
            }
        }
        if ((gamepad1.right_trigger > 0.2) && !(previousGamepad.right_trigger > 0.2)){
            armController.hasUpdatedOuttakeTimer = false;
            armController.hasUpdatedAdjusterTimer = false;
            if (!farShotOn) {
                armController.currentArmState = ArmController.armState.farShot;
                farShotOn = true;
            }
            else{
                armController.currentArmState = ArmController.armState.rest;
                farShotOn = false;
            }
        }
        if (gamepad1.left_bumper && !previousGamepad.left_bumper) {
            if (!outtakeOn) {
                armController.currentArmState = ArmController.armState.outtake;
                outtakeOn = true;
            }
            else {
                armController.currentArmState = ArmController.armState.rest;
                outtakeOn = false;
            }
        }
        if ((gamepad1.left_trigger > 0.2) && !(previousGamepad.left_trigger > 0.2)){
            if (!closeShotOn) {
                armController.currentArmState = ArmController.armState.intake;
                closeShotOn = true;
            }
            else{
                armController.currentArmState = ArmController.armState.rest;
                closeShotOn = false;
            }
        }

        Vector2d gamepadInput = new Vector2d(xPower, yPower);
        PoseVelocity2d mecanumMotorPowers = new PoseVelocity2d(gamepadInput, headingPower);

        mecanumDrive.setDrivePowers(mecanumMotorPowers);

        armController.updateArmState(System.currentTimeMillis());

        previousGamepad.copy(gamepad1);


        if (armController.advancementServoSpeed > 0) {intakeState = "intake";}
        else if (armController.advancementServoSpeed < 0){intakeState = "outtake";}
        else {intakeState = "off";}

        if (armController.shotSpeed == 0.45) {shotSpeedState = "close";}
        else if (armController.shotSpeed == armController.farShotSpeed) {shotSpeedState = "far";}
        else {shotSpeedState = "off";}

        telemetry.update();

        telemetry.addData("X power", xPower);
        telemetry.addData("Y power", yPower);
        telemetry.addData("gamepad X", gamepad1.left_stick_x);
        telemetry.addData("gamepad Y", gamepad1.left_stick_y);
        telemetry.addData("Heading power", headingPower);
        telemetry.addData("Arm state", armController.currentArmState);
        telemetry.addData("Launch Speed", armController.shotSpeed);
        telemetry.addData("left trigger", gamepad1.left_trigger);
        telemetry.addData("Dc intake Speed", armController.dcIntakeSpeed);
    }


}
