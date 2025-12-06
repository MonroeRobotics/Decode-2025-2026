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

@Config
@Autonomous(name = "red auto", group = "Autonomous")
public class redAuto extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;
    enum autoState{
        start,
        shoot

    }

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

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);

        armController.initArm();

        previousGamepad = new Gamepad();
        mecanumDrive = new MecanumDrive(hardwareMap, redStart);

        TrajectoryActionBuilder path = mecanumDrive.actionBuilder(redStart)
                .waitSeconds(2)
                .strafeToLinearHeading(redPickupLineup1, Math.toRadians(90))
                .strafeToLinearHeading(redPickup1, Math.toRadians(90))
                .strafeToLinearHeading(redPickupLineup1, Math.toRadians(90))
                .strafeToLinearHeading(redCloseShot, Math.toRadians(130))
                .waitSeconds(2)
                .strafeToLinearHeading(redCloseShotTransition, Math.toRadians(135))
                .turnTo(Math.toRadians(315))
                .splineTo(redPickupLineup2, Math.toRadians(90))
                .strafeToLinearHeading(redPickup2, Math.toRadians(90))
                .strafeToLinearHeading(redPickupLineup2, Math.toRadians(90))
                .turnTo(Math.toRadians(270))
                .splineTo(redCloseShot, Math.toRadians(130))
                .waitSeconds(2)
                .strafeToLinearHeading(redCloseShotTransition, Math.toRadians(135))
                .turnTo(Math.toRadians(315))
                .splineTo(redPickupLineup3, Math.toRadians(90))
                .strafeToLinearHeading(redPickup3, Math.toRadians(90))
                .strafeToLinearHeading(redPickupLineup3, Math.toRadians(90))
                .turnTo(Math.toRadians(270))
                .splineTo(redCloseShot, Math.toRadians(130))
                .waitSeconds(2)
                .strafeToLinearHeading(redCloseShotTransition, Math.toRadians(135))
                .turnTo(Math.toRadians(315));

        Action pathAction = path.build();
        Actions.runBlocking(new SequentialAction(pathAction));

    }
}
