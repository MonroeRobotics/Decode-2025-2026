package org.firstinspires.ftc.teamcode.util;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class ArmController {

    public ArmController (HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
    }
    HardwareMap hardwareMap;

    public final double closeShotSpeed = (0.4*100)*28;
    public final double shotSpeedOff = 0;
    public final double shotSpeedOuttake = (-0.05*100)*28;
    public double shotSpeed;

    public double P;
    public double F;

    public enum ShotSpeedState{close, far, undefined};
    ShotSpeedState shotSpeedState = ShotSpeedState.undefined;

    public final double dcIntakeSpeedOn = (0.5*100)*28;
    public final double dcIntakeSpeedOuttake = (-0.5*100)*28;
    public final double dcIntakeSpeedOff = 0;

    public final double advancementServoSpeedOn = 1;
    public final double advancementServoSpeedOuttake = -1;
    public final double advancementServoSpeedOff = 0;

    public double dcIntakeSpeed;
    public double advancementServoSpeed;




    DcMotorEx intakeMotor;
    DcMotorEx launchMotor;

    CRServo advancementServo;
    long brakeTimer;
    long spinupTimer;
    long spinupWaitTime = 650;
    long brakeWaitTime = 100;
    public boolean hasUpdatedSpinupTimer = false;
    public boolean hasUpdatedBrakeTimer = false;


    public enum armState{
        rest,
        farShot,
        closeShot,
        intake,
        autoIntake,
        outtake
    }
    public armState currentArmState = armState.rest;
    public void initArm(){
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        //Assigning each object to its correct port.
        launchMotor = hardwareMap.get(DcMotorEx.class, "launchMotor");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        advancementServo = hardwareMap.get(CRServo.class, "advancementServo");

        //Making all motors brake when not powered.
        launchMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        //Makes the motors more precise with high speeds.
        launchMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Correcting the spin direction of launch motor.
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        advancementServo.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void updateArmState(long time){
        switch(currentArmState){
            case rest:
                dcIntakeSpeed = dcIntakeSpeedOff;
                advancementServoSpeed = advancementServoSpeedOff;
                shotSpeed = shotSpeedOff;
                break;
            case closeShot:
                dcIntakeSpeed = dcIntakeSpeedOn;
                advancementServoSpeed = advancementServoSpeedOn;
                shotSpeed = closeShotSpeed;
                //todo - add spinup timer if nec
            case intake:
                if (!hasUpdatedBrakeTimer){
                    launchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    brakeTimer += time + brakeWaitTime;
                    hasUpdatedBrakeTimer = true;
                }
                if (time >= brakeTimer){
                    launchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
                shotSpeed = shotSpeedOff;
                advancementServoSpeed = advancementServoSpeedOn;
                dcIntakeSpeed = dcIntakeSpeedOn;
                break;
            case outtake:
                shotSpeed = shotSpeedOff;
                advancementServoSpeed = advancementServoSpeedOuttake;
                dcIntakeSpeed = dcIntakeSpeedOuttake;
                break;

        }

        launchMotor.setVelocity(shotSpeed);
        intakeMotor.setVelocity(dcIntakeSpeed);

        advancementServo.setPower(advancementServoSpeed);
    }
    public double getShotSpeed(){return shotSpeed;}

    public boolean isIntakeOn(){return dcIntakeSpeed != 0;}

    public void setIntakeSpeed(double Intake_Speed){dcIntakeSpeed = Intake_Speed;}

    public void setShotSpeed(double Shot_Speed){shotSpeed = Shot_Speed;}


}