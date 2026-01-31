package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.util.ArmController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "drive", group = "main")
public class drive extends OpMode {
    private static final Logger log = LoggerFactory.getLogger(drive.class);
    ArmController armController;
    DcMotorEx launchL;

    double xPower;
    double yPower;
    double headingPower;

    double drivePowerReduction = 0.85;
    double turnPowerReduction = 0.75;

    boolean closeShotOn = false;
    boolean sequenceActive = false;
    boolean outtakePhaseActive = false;
    boolean spinupPhaseActive = false;

    ElapsedTime sequenceTimer = new ElapsedTime();


    ElapsedTime spinupTimer = new ElapsedTime();

    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;
    Vector2d position = new Vector2d(0, 0);
    Pose2d pose = new Pose2d(position, 90);

    boolean intakeOn = false;
    boolean outtakeOn = false;
    boolean farShotOn = false;

    String intakeState;
    String shotSpeedState;


    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);
        armController.setLaunchSpeed = 0.413;
        armController.initArm();

        previousGamepad = new Gamepad();

        mecanumDrive = new MecanumDrive(hardwareMap, pose);
    }

    @Override
    public void loop() {
        if (gamepad1.left_stick_x >= 0.05 || gamepad1.left_stick_x <= -0.05) {
            yPower = -gamepad1.left_stick_x;
        }
        if (gamepad1.left_stick_y >= 0.05 || gamepad1.left_stick_y <= -0.05) {
            xPower = -gamepad1.left_stick_y;
        }
        if (gamepad1.right_stick_x >= 0.05 || gamepad1.right_stick_x <= -0.05){
            headingPower = -gamepad1.right_stick_x;
        }

        xPower *= drivePowerReduction;
        yPower *= drivePowerReduction;
        headingPower *= drivePowerReduction;


        if (gamepad1.right_bumper && !previousGamepad.right_bumper) {

            if (!sequenceActive) {
                // Start full sequence
                armController.currentArmState = ArmController.armState.closeShotOuttake;
                sequenceTimer.reset();

                sequenceActive = true;
                outtakePhaseActive = true;
                spinupPhaseActive = false;
                closeShotOn = true;
            }
            else {
                // Cancel everything
                armController.currentArmState = ArmController.armState.rest;

                sequenceActive = false;
                outtakePhaseActive = false;
                spinupPhaseActive = false;
                closeShotOn = false;
            }
        }

        if (sequenceActive) {

            // Phase 1 → Phase 2 (after 0.75 s)
            if (outtakePhaseActive && sequenceTimer.seconds() >= 0.75) {
                armController.currentArmState = ArmController.armState.spinupShot;
                sequenceTimer.reset();

                outtakePhaseActive = false;
                spinupPhaseActive = true;
            }

            // Phase 2 → Phase 3 (after 1.75 s)
            else if (spinupPhaseActive && sequenceTimer.seconds() >= 2.5) {
                armController.currentArmState = ArmController.armState.closeShot;

                spinupPhaseActive = false;
                // sequenceActive stays true until canceled
            }
        }


        if ((gamepad1.right_trigger > 0.2) && !(previousGamepad.right_trigger > 0.2)){
            if (!farShotOn) {
                armController.currentArmState = ArmController.armState.closeShot;
                farShotOn = true;
            }
            else{
                armController.currentArmState = ArmController.armState.rest;
                farShotOn = false;
            }
        }
        if ((gamepad1.left_trigger > 0.2) && !(previousGamepad.left_trigger > 0.2)){
            if (!outtakeOn) {
                armController.currentArmState = ArmController.armState.outtake;
                outtakeOn = true;
            }
            else {
                armController.currentArmState = ArmController.armState.rest;
                outtakeOn = false;
            }
        }
        if (gamepad1.left_bumper && !previousGamepad.left_bumper){
            if (!intakeOn) {
                armController.currentArmState = ArmController.armState.intake;
                intakeOn = true;
            }
            else{
                armController.currentArmState = ArmController.armState.rest;
                intakeOn = false;
            }
        }
        if (gamepad1.dpadUpWasPressed()){
            armController.setLaunchSpeed += 0.01;
        }
        if (gamepad1.dpadDownWasPressed()){
            armController.setLaunchSpeed -= 0.01;
        }

        Vector2d gamepadInput = new Vector2d(xPower, yPower);
        PoseVelocity2d mecanumMotorPowers = new PoseVelocity2d(gamepadInput, headingPower);

        mecanumDrive.setDrivePowers(mecanumMotorPowers);

        armController.updateArmState(System.currentTimeMillis());

        previousGamepad.copy(gamepad1);


        telemetry.update();

        telemetry.addData("X power", xPower);
        telemetry.addData("Y power", yPower);
        telemetry.addData("gamepad X", gamepad1.left_stick_x);
        telemetry.addData("gamepad Y", gamepad1.left_stick_y);
        telemetry.addData("Heading power", headingPower);
        telemetry.addData("Arm state", armController.currentArmState);
        telemetry.addData("left trigger", gamepad1.left_trigger);
        telemetry.addData("Dc intake Speed", armController.dcIntakeSpeedOn);
        telemetry.addData("Actual State", armController.currentArmState);
        telemetry.addData("shotLaunchSpeed", armController.setLaunchSpeed);
    }


}