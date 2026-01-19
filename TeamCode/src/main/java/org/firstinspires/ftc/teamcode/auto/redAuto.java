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
public class redAuto extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;

    Pose2d redStart = new Pose2d(-54, 46, Math.toRadians(123)); //123


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


    TrajectoryActionBuilder toShot;
    TrajectoryActionBuilder toShotLeave;

    TrajectoryActionBuilder toStop;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);

        armController.initArm();

        previousGamepad = new Gamepad();

        while (opModeInInit()){
            mecanumDrive = new MecanumDrive(hardwareMap, redStart);
        }

        while (opModeIsActive()){
            switch (autoState){
                case SHOT:
                    armController.currentArmState = ArmController.armState.closeShot;
                    if (!shotTimerStarted){
                        shotWaitTimer += 7000; //7 seconds
                        shotTimerStarted = true;
                    }
                    if (shotWaitTimer >= System.currentTimeMillis()){
                        autoState = AutoState.SHOT_LEAVE;
                    }
                    break;
                case SHOT_LEAVE:
                    shotTimerStarted = false;
                    armController.currentArmState = ArmController.armState.rest;
                    toShotLeave = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(redStop, Math.toRadians(90));
                    autoState = AutoState.STOP;
                    break;
                case STOP:
                    break;
            }
            armController.updateArmState(System.currentTimeMillis());
        }
    }
}
