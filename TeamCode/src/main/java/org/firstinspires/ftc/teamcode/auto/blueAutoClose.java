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
@Autonomous(name = "blue auto Champ", group = "Autonomous")
public class blueAutoClose extends LinearOpMode{
    ArmController armController;
    Gamepad previousGamepad;

    MecanumDrive mecanumDrive;

    Pose2d blueStart = new Pose2d(-60,-52, Math.toRadians(203)); //90
    Vector2d blueCloseShot = new Vector2d(-60,-52); //203
    Vector2d blueCloseShotAdvance  = new Vector2d(-40, -49); //123
    Vector2d blueCloseShotTransition = new Vector2d(-40, -35); //123
    Vector2d blueStop = new Vector2d(35.5, -32); //90
    Vector2d bluePickupLineup1 = new Vector2d(21, -35); //90
    Vector2d bluePickupLineup2 = new Vector2d(11.5, -32); //90
    Vector2d bluePickupLineup3 = new Vector2d(-12, -32); //90
    Vector2d bluePickup1 = new Vector2d(21, -62); //90
    Vector2d bluePickup2 = new Vector2d(11.5, -50); //90
    Vector2d bluePickup3 = new Vector2d(-12, -50); //90
    // ---------------- STATES ----------------
    enum AutoState {
        SHOT,
        PICKUP,
        SHOT_APPROACH,
        LEAVE,
        DONE
    }

    AutoState autoState = AutoState.SHOT;

    // ---------------- SHOT TIMER ----------------
    long shotStartTime = 0;
    boolean shotTimerRunning = false;
    int shotCycle = 0;

    @Override
    public void runOpMode() {

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        armController = new ArmController(hardwareMap);
        ArmController.setLaunchSpeed = 0.435;
        armController.initArm();

        mecanumDrive = new MecanumDrive(hardwareMap, blueStart);

        waitForStart();

        while (opModeIsActive() && autoState != AutoState.DONE) {

            switch (autoState) {

                // =====================================================
                // SHOT SEQUENCE (FAST SPINUP, 0.8s SHOTS)
                // =====================================================
                case SHOT:

                    if (!shotTimerRunning) {
                        shotStartTime = System.currentTimeMillis();
                        shotTimerRunning = true;
                    }

                    long elapsed = System.currentTimeMillis() - shotStartTime;

                    if (elapsed < 2200) {
                        armController.currentArmState = ArmController.armState.spinupShot;
                    }
                    else if (elapsed < 3000) {
                        armController.currentArmState = ArmController.armState.autoCloseShot;
                    }
                    else if (elapsed < 3500) {
                        armController.currentArmState = ArmController.armState.spinupShot;
                    }
                    else if (elapsed < 4300) {
                        armController.currentArmState = ArmController.armState.autoCloseShot;
                    }
                    else if (elapsed < 4800) {
                        armController.currentArmState = ArmController.armState.spinupShot;
                    }
                    else {
                        armController.currentArmState = ArmController.armState.autoCloseShot;
                    }

                    if (elapsed >= 5600) {
                        shotTimerRunning = false;
                        armController.currentArmState = ArmController.armState.rest;
                        shotCycle++;

                        if (shotCycle == 1) {
                            autoState = AutoState.PICKUP;
                        } else {
                            autoState = AutoState.LEAVE; // Go to LEAVE after the second shot set
                        }
                    }
                    break;

                // =====================================================
                // PICKUP (PERPENDICULAR + AUTOINTAKE)
                // =====================================================
                case PICKUP:
                    // 1. Face wall first
                    Actions.runBlocking(
                            mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                    .turnTo(Math.toRadians(45))
                                    .build()
                    );
                    // 2. TURN INTAKE ON (Sets the state, but updateArmState() actually runs it)
                    armController.currentArmState = ArmController.armState.intake;

                    // 3. Define the path
                    com.acmerobotics.roadrunner.Action pickupPath =
                            mecanumDrive.actionBuilder(new Pose2d(mecanumDrive.localizer.getPose().position, Math.toRadians(90)))
                                    .strafeTo(blueCloseShotTransition)
                                    .strafeTo(bluePickupLineup1)
                                    .strafeTo(bluePickup1)
                                    .strafeTo(bluePickupLineup1)
                                    .build();

                    // 4. MANUAL LOOP: Updates drive and arm at the same time
                    while (opModeIsActive() && pickupPath.run(new com.acmerobotics.dashboard.telemetry.TelemetryPacket())) {
                        armController.updateArmState(System.currentTimeMillis());
                        // This ensures the intake motor keeps getting its "ON" command while moving
                    }

                    // 5. TURN INTAKE OFF
                    armController.currentArmState = ArmController.armState.rest;
                    armController.updateArmState(System.currentTimeMillis());

                    autoState = AutoState.SHOT_APPROACH;
                    break;

                // =====================================================
                // RETURN TO SHOT
                // =====================================================
                case SHOT_APPROACH:
                    // 1. KEEP INTAKE ON while returning to grab any loose pixels
                    armController.currentArmState = ArmController.armState.intake;

                    // 2. Build the return path
                    com.acmerobotics.roadrunner.Action returnPath =
                            mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                    .strafeToLinearHeading(blueCloseShot, Math.toRadians(123))
                                    .build();

                    // 3. Manual loop to keep motors spinning during movement
                    while (opModeIsActive() && returnPath.run(new com.acmerobotics.dashboard.telemetry.TelemetryPacket())) {
                        armController.updateArmState(System.currentTimeMillis());
                    }

                    // 4. Reset arm to rest (or spinup) before entering SHOT state
                    armController.currentArmState = ArmController.armState.rest;
                    armController.updateArmState(System.currentTimeMillis());

                    autoState = AutoState.SHOT;
                    break;
                case LEAVE:
                    // Define the path simply using strafeTo and the coordinate
                    com.acmerobotics.roadrunner.Action leavePath =
                            mecanumDrive.actionBuilder(mecanumDrive.localizer.getPose())
                                    .strafeTo(new Vector2d(-20, 45))
                                    .build();

                    // Manual loop keeps the arm controller alive while moving
                    while (opModeIsActive() && leavePath.run(new com.acmerobotics.dashboard.telemetry.TelemetryPacket())) {
                        armController.updateArmState(System.currentTimeMillis());
                    }

                    autoState = AutoState.DONE;
                    break;
            }

            armController.updateArmState(System.currentTimeMillis());
            telemetry.addData("State", autoState);
            telemetry.addData("Shot Cycle", shotCycle);
            telemetry.update();
        }
    }
}