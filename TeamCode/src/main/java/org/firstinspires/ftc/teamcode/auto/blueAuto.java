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
@Autonomous(name = "blue auto", group = "Autonomous")
public class blueAuto extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;

    Pose2d blueStart = new Pose2d(-40,-44, Math.toRadians(180)); //90
    Vector2d blueCloseShot = new Vector2d(-40,-44); //203
    Vector2d blueCloseShotAdvance  = new Vector2d(-40, -49); //123
    Vector2d blueCloseShotTransition = new Vector2d(-15, -20); //123
    Vector2d blueStop = new Vector2d(-20, -50); //90
    Vector2d bluePickupLineup1 = new Vector2d(35.5, -32); //90
    Vector2d bluePickupLineup2 = new Vector2d(11.5, -32); //90
    Vector2d bluePickupLineup3 = new Vector2d(-12, -32); //90
    Vector2d bluePickup1 = new Vector2d(35.5, -50); //90
    Vector2d bluePickup2 = new Vector2d(11.5, -50); //90
    Vector2d bluePickup3 = new Vector2d(-12, -50); //90

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
    AutoState autoState = blueAuto.AutoState.SHOT;

    long shotWaitTimer;
    boolean shotTimerStarted = false;


    TrajectoryActionBuilder toShot;
    TrajectoryActionBuilder toShotLeave;

    TrajectoryActionBuilder toStop;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);
        armController.setLaunchSpeed = 0.43;
        armController.initArm();

        previousGamepad = new Gamepad();

        while (opModeInInit()){
            mecanumDrive = new MecanumDrive(hardwareMap, blueStart);
        }

        while (opModeIsActive()){
            switch (autoState){
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
                        autoState = blueAuto.AutoState.SHOT_LEAVE;
                    }
                    break;
                case SHOT_LEAVE:
                    shotTimerStarted = false;
                    armController.currentArmState = ArmController.armState.rest;

                    // 1. Build the trajectory
                    TrajectoryActionBuilder leaveAction = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(bluePickupLineup1, Math.toRadians(90));

                    // 2. RUN the trajectory (This is the missing step)
                    Actions.runBlocking(leaveAction.build());

                    autoState = blueAuto.AutoState.STOP;
                    break;
                case STOP:
                    break;
            }
            armController.updateArmState(System.currentTimeMillis());
            telemetry.addData("shotLaunchSpeed", armController.setLaunchSpeed);
        }
    }
}