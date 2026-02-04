package org.firstinspires.ftc.teamcode.auto;

// RR-specific imports
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;

// Non-RR imports
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.ArmController;

@Config
@Autonomous(name = "red auto", group = "Autonomous")
public class redAutoClose extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;

    Pose2d redStart = new Pose2d(-60, 52, Math.toRadians(123)); //123


    Vector2d redFarShot = new Vector2d(56, 12); //157
    Vector2d redCloseShot  = new Vector2d(-60, 52); //123
    Vector2d redCloseShotAdvance  = new Vector2d(-60, 56); //123

    Vector2d redCloseShotTransition = new Vector2d(-14, 18); //123

    Vector2d redStop = new Vector2d(-18, 47); //90
    Vector2d redPickupLineup1 = new Vector2d(35.5, 32); //90
    Vector2d redPickupLineup2 = new Vector2d(11.5, 32); //90
    Vector2d redPickupLineup3 = new Vector2d(-12, 32); //90
    Vector2d redPickup1 = new Vector2d(35.5, 50); //90
    Vector2d redPickup2 = new Vector2d(11.5, 50); //90
    Vector2d redPickup3 = new Vector2d(-12, 50); //90
    enum AutoState {
        PICKUP,
        SHOT_APPROACH,
        SHOT,
        SHOT_ADVANCE,
        SHOT_ADVANCE_SHOT,
        SHOT_LEAVE,
        STOP,
        TRUE_STOP

    }
    AutoState autoState = AutoState.SHOT;

    long shotWaitTimer;
    boolean shotTimerStarted = false;
    int cycleNum = 0;




    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);
        armController.setLaunchSpeed = 0.435;
        armController.initArm();

        previousGamepad = new Gamepad();

        while (opModeInInit()){
            mecanumDrive = new MecanumDrive(hardwareMap, redStart);
        }

        while (opModeIsActive()){
            switch (autoState){
                case SHOT_APPROACH:
                    TrajectoryActionBuilder toShot = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(redCloseShot, Math.toRadians(123));
                    Actions.runBlocking(toShot.build());
                    autoState = AutoState.SHOT;
                    break;
                case SHOT:
                    if (!shotTimerStarted) {
                        shotWaitTimer = System.currentTimeMillis();
                        shotTimerStarted = true;
                    }

                    long elapsed = System.currentTimeMillis() - shotWaitTimer;

                    // 1. Initial Spin Up (0 to 5s)
                    if (elapsed < 5000) {
                        armController.currentArmState = ArmController.armState.spinupShot;
                    }
                    // 2. Fire First Volley (5.5s to 6.0s) -> 0.5s duration
                    else if (elapsed < 5500) {
                        armController.currentArmState = ArmController.armState.autoCloseShot;
                    }
                    // 3. First Pause (5.5s to 6.0s) -> 1.0s duration
                    else if (elapsed < 6500) {
                        armController.currentArmState = ArmController.armState.spinupShot;
                    }
                    // 4. Fire Second Volley (6.5s to 8s) -> 1.5s duration
                    else if (elapsed < 8000) {
                        armController.currentArmState = ArmController.armState.autoCloseShot;
                    }
                    // 5. NEW: Second Pause (8s to 9s) -> 1.0s duration
                    else if (elapsed < 9000) {
                        armController.currentArmState = ArmController.armState.spinupShot;
                    }
                    // 6. Fire Remaining (9s onwards)
                    else {
                        armController.currentArmState = ArmController.armState.autoCloseShot;
                    }

                    // Leave SHOT after 11 seconds total (increased slightly to accommodate gaps)
                    if (elapsed >= 11000) {
                        shotTimerStarted = false;
                        autoState = AutoState.SHOT_LEAVE;
                    }
                    break;
                case SHOT_LEAVE:
                    shotTimerStarted = false;
                    armController.currentArmState = ArmController.armState.rest;
                    if (cycleNum == 0){
                        TrajectoryActionBuilder pickup1 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(redCloseShotTransition, Math.toRadians(90))
                                .strafeToLinearHeading(redPickupLineup2, Math.toRadians(90))
                                .strafeToLinearHeading(redPickup2, Math.toRadians(90))
                                .strafeToLinearHeading(redPickupLineup2, Math.toRadians(90))
                                .strafeToLinearHeading(redCloseShotTransition, Math.toRadians(123));
                        Actions.runBlocking(pickup1.build());
                        cycleNum += 1;
                        autoState = AutoState.SHOT_APPROACH;
                    }
                    else {
                        // 1. Build the trajectory
                        TrajectoryActionBuilder leaveAction = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(redStop, Math.toRadians(90));

                        // 2. RUN the trajectory (This is the missing step)
                        Actions.runBlocking(leaveAction.build());
                        autoState = AutoState.STOP;
                    }
                    break;
                case STOP:
                    break;
            }
            armController.updateArmState(System.currentTimeMillis());
            telemetry.addData("shotLaunchSpeed", armController.setLaunchSpeed);
        }
    }
}