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
public class blueAuto extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;

    Pose2d blueStart = new Pose2d(60,-12, Math.toRadians(180)); //90
    Vector2d blueCloseShot = new Vector2d(-40,-40); //203
    Vector2d blueCloseShotAdvance  = new Vector2d(-40, -45); //123
    Vector2d blueCloseShotTransition = new Vector2d(14, -18); //123
    Vector2d blueStop = new Vector2d(11, 52); //90
    Vector2d bluePickupLineup1 = new Vector2d(35.5, -32); //90
    Vector2d bluePickupLineup2 = new Vector2d(11.5, -32); //90
    Vector2d bluePickupLineup3 = new Vector2d(-12, -32); //90
    Vector2d bluePickup1 = new Vector2d(35.5, -50); //90
    Vector2d bluePickup2 = new Vector2d(11.5, -50); //90
    Vector2d bluePickup3 = new Vector2d(-12, -50); //90


    enum AutoState{
        PICKUP,
        SHOT_APPROACH,
        SHOT,
        SHOT_ADVANCE,
        SHOT_ADVANCE_SHOT,
        SHOT_LEAVE,
        STOP,
        TRUE_STOP

    }
    AutoState autoState = AutoState.SHOT_APPROACH;

    double cycleNumber = 1;
    long shotWaitTimer;
    long advanceWaitTimer;
    boolean shotTimerStarted = false;
    boolean shotAdvanceTimerStarted = false;
    boolean happyDanceTimerStarted = false;

    TrajectoryActionBuilder toPickup1;
    TrajectoryActionBuilder toPickup2;
    TrajectoryActionBuilder toPickup3;
    TrajectoryActionBuilder toTurn;

    TrajectoryActionBuilder toShot;
    TrajectoryActionBuilder toShotAdvance;
    TrajectoryActionBuilder toShotLeave;

    TrajectoryActionBuilder toStop;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);

        armController.outtakeWaitTime = 1100;

        armController.initArm();

        previousGamepad = new Gamepad();

        while (opModeInInit()){
            mecanumDrive = new MecanumDrive(hardwareMap, blueStart);
        }

        while (opModeIsActive()){
            switch (autoState){
                case SHOT_APPROACH:
                    if (cycleNumber == 1){
                        armController.currentArmState = ArmController.armState.autoIntake;
                    }
                    else{
                        armController.currentArmState = ArmController.armState.intake;
                    }
                    armController.updateArmState(System.currentTimeMillis());
                    shotTimerStarted = false;
                    toShot = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(blueCloseShot, Math.toRadians(-120));
                    Actions.runBlocking(toShot.build());
                    autoState = AutoState.SHOT;
                    break;
                case SHOT:
                    armController.currentArmState = ArmController.armState.closeShot;
                    armController.updateArmState(System.currentTimeMillis());

                    if (!shotTimerStarted){
                        shotWaitTimer = System.currentTimeMillis() + 4000; //2 seconds
                        shotTimerStarted = true;
                    }
                    if (System.currentTimeMillis() >= shotWaitTimer){
                        autoState = AutoState.SHOT_ADVANCE;
                    }
                    break;
                case SHOT_ADVANCE:
                    toShotAdvance = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(blueCloseShotAdvance, Math.toRadians(-123));
                    Actions.runBlocking(toShotAdvance.build());
                    autoState = AutoState.SHOT_ADVANCE_SHOT;
                    break;
                case SHOT_ADVANCE_SHOT:
                    if (!shotAdvanceTimerStarted){
                        advanceWaitTimer = System.currentTimeMillis() + 4000;
                        shotAdvanceTimerStarted = true;
                    }
                    if (System.currentTimeMillis() >= advanceWaitTimer){
                        armController.currentArmState = ArmController.armState.rest;
                        armController.updateArmState(System.currentTimeMillis());
                        autoState = AutoState.SHOT_LEAVE;
                    }
                    break;
                case SHOT_LEAVE:
                    if (cycleNumber < 0){ //change to 3 if doing full auto
                        toShotLeave = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(blueCloseShotTransition, Math.toRadians(123));
                        Actions.runBlocking(toShotLeave.build());
                        cycleNumber += 1;
                        shotTimerStarted = false;
                        shotAdvanceTimerStarted = false;
                        armController.hasUpdatedOuttakeTimer = false;
                        armController.hasUpdatedAdjusterTimer = false;
                        armController.hasUpdatedAdvancementTimer = false;
                        autoState = AutoState.PICKUP;
                    }
                    else{
                        autoState = AutoState.STOP;
                    }
                    break;
                case PICKUP:
                    armController.currentArmState = ArmController.armState.intake;
                    armController.updateArmState(System.currentTimeMillis());

                    // Build the correct trajectory depending on the cycle
                    if (cycleNumber == 1) {
                        toPickup1 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(bluePickupLineup2, Math.toRadians(90))
                                .strafeToLinearHeading(bluePickup2, Math.toRadians(90))
                                .strafeToLinearHeading(bluePickupLineup2, Math.toRadians(90))
                                .strafeToLinearHeading(blueCloseShotTransition, Math.toRadians(123));
                    }
                    else if (cycleNumber == 2) {
                        toPickup1 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(bluePickupLineup3, Math.toRadians(90))
                                .strafeToLinearHeading(bluePickup3, Math.toRadians(90))
                                .strafeToLinearHeading(bluePickupLineup3, Math.toRadians(90))
                                .strafeToLinearHeading(blueCloseShotTransition, Math.toRadians(123));
                    }
                    else if (cycleNumber == 3) {
                        toPickup1 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(bluePickupLineup1, Math.toRadians(90))
                                .strafeToLinearHeading(bluePickup1, Math.toRadians(90))
                                .strafeToLinearHeading(bluePickupLineup1, Math.toRadians(90))
                                .strafeToLinearHeading(blueCloseShotTransition, Math.toRadians(123));
                    }

                    // RUN the built trajectory
                    Actions.runBlocking(toPickup1.build());
                    autoState = AutoState.SHOT_APPROACH;
                    break;
                case STOP:
                    toStop = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(blueStop, Math.toRadians(90));
                    Actions.runBlocking(toStop.build());
                    if (!happyDanceTimerStarted){
                        shotWaitTimer = System.currentTimeMillis() + 3000;
                        happyDanceTimerStarted = true;
                    }
                    if (System.currentTimeMillis() >= shotWaitTimer){
                        autoState = AutoState.TRUE_STOP;
                    }
                    break;
                case TRUE_STOP:
                    break;
            }
            armController.updateArmState(System.currentTimeMillis());
        }
    }
}