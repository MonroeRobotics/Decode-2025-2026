package org.firstinspires.ftc.teamcode.auto;

// RR-specific imports

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.ArmController;

@Config
@Autonomous(name = "Slide Auto", group = "Autonomous")
public class slideAuto extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;

    Pose2d blueStart = new Pose2d(0,0, Math.toRadians(180)); //203
    Vector2d blueCloseShot = new Vector2d(-40,-44); //203
    Vector2d blueCloseShotAdvance  = new Vector2d(-40, -49); //203
    Vector2d blueCloseShotTransition = new Vector2d(-15, -20); //203
    Vector2d blueStop = new Vector2d(0, -28); //180
    Vector2d bluePickupLineup1 = new Vector2d(35.5, -32); //270
    Vector2d bluePickupLineup2 = new Vector2d(11.5, -32); //270
    Vector2d bluePickupLineup3 = new Vector2d(-12, -32); //270
    Vector2d bluePickup1 = new Vector2d(35.5, -50); //270
    Vector2d bluePickup2 = new Vector2d(11.5, -50); //270
    Vector2d bluePickup3 = new Vector2d(-12, -50); //270

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
    AutoState autoState = slideAuto.AutoState.SHOT;

    long shotWaitTimer;
    boolean shotTimerStarted = false;

    int cycleNum = 0;


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
                case SHOT_APPROACH:
                    TrajectoryActionBuilder slide = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToConstantHeading(blueStop);
                    Actions.runBlocking(slide.build());
                    autoState = AutoState.STOP;
                    break;
                case STOP:
                    break;
            }
            armController.updateArmState(System.currentTimeMillis());
            telemetry.addData("shotLaunchSpeed", armController.setLaunchSpeed);
        }
    }
}