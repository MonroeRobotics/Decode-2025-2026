package org.firstinspires.ftc.teamcode.auto;

// RR-specific imports
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;

// Non-RR imports
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

        import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.ArmController;

@Autonomous(name = "red auto", group = "Autonomous")
@Config
public class redAuto extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;
    Pose2d blueStart = new Pose2d(60,-12, Math.toRadians(180)); //90
    Pose2d redStart = new Pose2d(60, 12, Math.toRadians(180)); //90

    Vector2d blueFarShot = new Vector2d(56,-12); //203
    Vector2d blueHumanPlayer = new Vector2d(56, -54); //180
    Vector2d bluePark = new Vector2d(37.5, 32); //90

    Vector2d redFarShot = new Vector2d(56, 12); //157
    Vector2d redCloseShot  = new Vector2d(-51.5, 46.5); //130
    Vector2d redCloseShotTransition = new Vector2d(-49, 44); //130
    Vector2d redHumanPlayer = new Vector2d(56, 54); //0
    Vector2d redPark = new Vector2d(37.5, -32); //90
    Vector2d redPickupLineup1 = new Vector2d(35.5, 32); //90
    Vector2d redPickupLineup2 = new Vector2d(11.5, 32); //90
    Vector2d redPickupLineup3 = new Vector2d(-12, 32); //90
    Vector2d redPickup1 = new Vector2d(35.5, 50); //90
    Vector2d redPickup2 = new Vector2d(11.5, 50); //90
    Vector2d redPickup3 = new Vector2d(-12, 50); //90
    enum AutoState{
        PICKUP,
        SHOT_APPROACH,
        SHOT,
        SHOT_LEAVE,
        STOP

    }
    AutoState autoState = AutoState.PICKUP;
    double cycleNumber = 1;
    double waitTimer;
    boolean shotTimerStarted = false;

    TrajectoryActionBuilder toPickup1;
    TrajectoryActionBuilder toPickup2;
    TrajectoryActionBuilder toPickup3;

    TrajectoryActionBuilder toShot;
    TrajectoryActionBuilder toShotLeave;


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);

        armController.initArm();

        previousGamepad = new Gamepad();

        while (opModeInInit()){
            mecanumDrive = new MecanumDrive(hardwareMap, redStart);
        }

        waitForStart();
        while (opModeIsActive()){
            switch (autoState){
                case PICKUP:
                    armController.currentArmState = ArmController.armState.intake;
                    armController.updateArmState(System.currentTimeMillis());
                    if (cycleNumber == 1) {
                        toPickup1 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(redPickupLineup2, Math.toRadians(90))
                                .strafeToLinearHeading(redPickup2, Math.toRadians(90))
                                .strafeToLinearHeading(redPickupLineup2, Math.toRadians(90));
                        Actions.runBlocking(toPickup1.build());
                        autoState = AutoState.SHOT_APPROACH;
                    }
                    else if (cycleNumber == 2) {
                        toPickup2 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(redPickupLineup3, Math.toRadians(90))
                                .strafeToLinearHeading(redPickup3, Math.toRadians(90))
                                .strafeToLinearHeading(redPickupLineup3, Math.toRadians(90));
                        Actions.runBlocking(toPickup2.build());
                        autoState = AutoState.SHOT_APPROACH;
                    }
                    else if (cycleNumber == 3) {
                        toPickup3 = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                .strafeToLinearHeading(redPickupLineup1, Math.toRadians(90))
                                .strafeToLinearHeading(redPickup1, Math.toRadians(90))
                                .strafeToLinearHeading(redPickupLineup1, Math.toRadians(90));
                        Actions.runBlocking(toPickup3.build());
                        autoState = AutoState.SHOT_APPROACH;
                    }
                    break;
                case SHOT_APPROACH:
                    toShot = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .turnTo(270)
                            .splineTo(redCloseShot, Math.toRadians(130));
                    Action toShotAction =  toShot.build();
                    Actions.runBlocking(new SequentialAction(toShotAction));
                    autoState = AutoState.SHOT;
                    break;
                case SHOT:
                    armController.currentArmState = ArmController.armState.closeShot;
                    armController.updateArmState(System.currentTimeMillis());
                    if (!shotTimerStarted){
                        waitTimer = System.currentTimeMillis() + 2000; //2 seconds
                        shotTimerStarted = true;
                    }
                    if (System.currentTimeMillis() >= waitTimer){
                        armController.currentArmState = ArmController.armState.rest;
                        autoState = AutoState.SHOT_LEAVE;
                        armController.updateArmState(System.currentTimeMillis());
                    }
                case SHOT_LEAVE:
                    toShotLeave = mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                            .strafeToLinearHeading(redCloseShotTransition, Math.toRadians(130))
                            .turnTo(Math.toRadians(315));
                    Action toShotLeaveAction = toShotLeave.build();
                    Actions.runBlocking(new SequentialAction(toShotLeaveAction));
                    if (cycleNumber < 3){
                        cycleNumber += 1;
                        autoState = AutoState.PICKUP;
                    }
                    else{
                        autoState = AutoState.STOP;
                    }
                case STOP:
                    break;

            }
        }
    }
}
