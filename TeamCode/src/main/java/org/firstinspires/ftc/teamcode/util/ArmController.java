package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class ArmController {

    /* ================= Hardware ================= */

    private final DcMotorEx launchL;
    private final DcMotorEx launchR;
    private final CRServo advancementServo;
    private final DcMotorEx intakeMotor;

    /* ================= Constants ================= */

    // Encoder + motor constants
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_LAUNCH_RPM = 6000.0;

    // Shot speeds as percent (0–1)
    public final double shotSpeedOff = 0.0;
    public final double shotSpeedOuttake = -0.05;

    // Intake speeds
    public final double dcIntakeSpeedOn = 0.4;
    public final double dcIntakeSpeedOuttake = -0.5;
    public final double dcIntakeSpeedOff = 0.0;

    public final double advancementServoSpeedOn = 1.0;
    public final double advancementServoSpeedAuto = 0.3;
    public final double advancementServoSpeedOuttake = -1.0;
    public final double advancementServoSpeedOff = 0.0;

    public double actualVelocity = 0.0;
    public static double setLaunchSpeed = 0.41;


    /* ================= State ================= */

    private double shotSpeed = 0.0;
    private double dcIntakeSpeed = 0.0;
    private double advancementServoSpeed = 0.0;

    public enum armState {
        rest,
        closeShot,
        farShot,
        intake,
        autoIntake,
        outtake,
        spinupShot,
        closeShotOuttake,
        autoCloseShot
    }

    public armState currentArmState = armState.rest;

    /* ================= Timers ================= */

    private long brakeTimer;
    private boolean hasUpdatedBrakeTimer = false;
    private final long brakeWaitTime = 100;

    /* ================= PIDF ================= */

    public double P = 1.8;
    public double F = 13.05;

    /* ================= Constructor ================= */

    public ArmController(HardwareMap hardwareMap) {

        launchL = hardwareMap.get(DcMotorEx.class, "launchL");
        launchR = hardwareMap.get(DcMotorEx.class, "launchR");
        advancementServo = hardwareMap.get(CRServo.class, "advancementServo");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        launchL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launchL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchL.setDirection(DcMotorSimple.Direction.FORWARD);

        launchR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launchR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchR.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);



        advancementServo.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public double getTargetVelocity() {
        return percentToVelocity(shotSpeed);
    }
    /* ================= Init ================= */

    public void initArm() {
        PIDFCoefficients pidf = new PIDFCoefficients(P, 0, 0, F);
        launchL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        launchR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
    }

    /* ================= Core Update ================= */

    public void updateArmState(long time) {
        // FORCE the mode every loop to ensure it doesn't slip into Power mode
        switch (currentArmState) {

            case rest:
                shotSpeed = shotSpeedOff;
                dcIntakeSpeed = dcIntakeSpeedOff;
                advancementServoSpeed = advancementServoSpeedOff;
                break;

            case closeShot:
                shotSpeed = setLaunchSpeed;
                dcIntakeSpeed = dcIntakeSpeedOn;
                advancementServoSpeed = advancementServoSpeedOn;
                break;
            case autoCloseShot:
                shotSpeed = setLaunchSpeed;
                dcIntakeSpeed = dcIntakeSpeedOn;
                advancementServoSpeed = advancementServoSpeedAuto;
                break;
            case intake:
                // Gently reverse launcher to keep artifact in divot
                shotSpeed = -0.25;// 25% reverse
                dcIntakeSpeed = dcIntakeSpeedOn;
                advancementServoSpeed = advancementServoSpeedOn;
                break;

            case outtake:
                shotSpeed = shotSpeedOff;
                dcIntakeSpeed = dcIntakeSpeedOuttake;
                advancementServoSpeed = advancementServoSpeedOuttake;
                break;

            case spinupShot:
                shotSpeed = setLaunchSpeed; // Launcher spin up
                dcIntakeSpeed = dcIntakeSpeedOff; // No feeding
                advancementServoSpeed = advancementServoSpeedOff;
                break;
            case closeShotOuttake:
                shotSpeed = -0.25;
                advancementServoSpeed = advancementServoSpeedOuttake;
                dcIntakeSpeed = dcIntakeSpeedOff;
        }

        // ===== ONLY PLACE velocity is set =====
        launchL.setVelocity(percentToVelocity(shotSpeed));
        launchR.setVelocity(percentToVelocity(shotSpeed));
        actualVelocity = launchL.getVelocity();
        intakeMotor.setPower(dcIntakeSpeed);
        advancementServo.setPower(advancementServoSpeed);
    }

    /* ================= Helpers ================= */

    public double percentToVelocity(double percent) {
        double rpm = percent * MAX_LAUNCH_RPM;
        return (rpm * TICKS_PER_REV) / 60.0;
    }

    /* ================= Telemetry / Access ================= */

    public double getShotSpeedPercent() {
        return shotSpeed;
    }
    public boolean isIntakeOn() {
        return dcIntakeSpeed != 0;
    }
}