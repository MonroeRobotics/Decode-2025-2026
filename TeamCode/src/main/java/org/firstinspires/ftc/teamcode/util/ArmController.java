package org.firstinspires.ftc.teamcode.util;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ArmController {
    public ArmController (HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
    }
    HardwareMap hardwareMap;

    public final double closeShotSpeed = 0.45;
    public final double farShotSpeed = 0; //placeholder
    public final double shotSpeedOff = 0;
    public double shotSpeed;

    public enum ShotSpeedState{close, far, undefined};
    ShotSpeedState shotSpeedState = ShotSpeedState.undefined;

    public final double dcIntakeSpeedOn = 0; //placeholder
    public final double dcIntakeSpeedOff = 0;

    public final double advancementServoSpeedOn = 1; //placeholder
    public final double advancementServoSpeedOff = 0;

    public double dcIntakeSpeed;
    public double advancementServoSpeed;




    DcMotorEx intakeMotor;
    DcMotorEx launchMotorL;
    DcMotorEx launchMotorR;

    CRServo advancementServo;

    long timer;
    long waitTime = 500; //time in milliseconds
    boolean hasUpdatedTimer = false;

    public enum armState{
        rest,
        farShot,
        closeShot,
        intake,
        outtake
    }
    public armState currentArmState = armState.rest;
    public void initArm(){
        //Assigning each object to its correct port.
        launchMotorL = hardwareMap.get(DcMotorEx.class, "launchMotorL");
        launchMotorR = hardwareMap.get(DcMotorEx.class, "launchMotorR");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        advancementServo = hardwareMap.get(CRServo.class, "advancementServo");

        //Making all motors brake when not powered.
        launchMotorL.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        launchMotorR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        //Makes the motors more precise with high speeds.
        launchMotorL.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        launchMotorR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Correcting the spin direction of launch motor.
        launchMotorL.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void updateArmState(long time){
        switch(currentArmState){
            case rest:
                dcIntakeSpeed = dcIntakeSpeedOff;
                advancementServoSpeed = advancementServoSpeedOff;
                shotSpeed = shotSpeedOff;
                break;
            case farShot:
                //todo - adjust ramp angle
                if (shotSpeedState == ShotSpeedState.close || shotSpeedState == ShotSpeedState.undefined) {
                    if (!hasUpdatedTimer) {
                        updateTimer(time);
                        hasUpdatedTimer = true;
                    }
                    if (time >= timer) {
                        advancementServoSpeed = advancementServoSpeedOn;
                        dcIntakeSpeed = dcIntakeSpeedOn;
                        shotSpeed = farShotSpeed;
                        shotSpeedState = ShotSpeedState.far;
                        hasUpdatedTimer = false;
                    }
                }
                else {
                    advancementServoSpeed = advancementServoSpeedOn;
                    dcIntakeSpeed = dcIntakeSpeedOn;
                    shotSpeed = farShotSpeed;
                    hasUpdatedTimer = false;
                    shotSpeedState = ShotSpeedState.far;
                }
                break;
            case closeShot:
                //todo - adjust ramp angle
                if (shotSpeedState == ShotSpeedState.far || shotSpeedState == ShotSpeedState.undefined) {
                    if (!hasUpdatedTimer) {
                        updateTimer(time);
                        hasUpdatedTimer = true;
                    }
                    if (time >= timer) {
                        advancementServoSpeed = advancementServoSpeedOn;
                        dcIntakeSpeed = dcIntakeSpeedOn;
                        shotSpeed = closeShotSpeed;
                        shotSpeedState = ShotSpeedState.close;
                        hasUpdatedTimer = false;
                    }
                }
                else {
                    advancementServoSpeed = advancementServoSpeedOn;
                    dcIntakeSpeed = dcIntakeSpeedOn;
                    shotSpeed = closeShotSpeed;
                    hasUpdatedTimer = false;
                    shotSpeedState = ShotSpeedState.close;
                }
                break;
            case intake:
                shotSpeed = shotSpeedOff;
                advancementServoSpeed = advancementServoSpeedOn;
                dcIntakeSpeed = dcIntakeSpeedOn;
                break;
            case outtake:
                shotSpeed = shotSpeedOff;
                advancementServoSpeed = -advancementServoSpeedOn;
                dcIntakeSpeed = -dcIntakeSpeedOn;
                break;

        }

        launchMotorL.setPower(shotSpeed);
        launchMotorR.setPower(shotSpeed);
        intakeMotor.setPower(dcIntakeSpeed);

        advancementServo.setPower(advancementServoSpeed);
    }
    public double getShotSpeed(){return shotSpeed;}

    public boolean isIntakeOn(){return dcIntakeSpeed != 0;}

    public void setIntakeSpeed(double Intake_Speed){dcIntakeSpeed = Intake_Speed;}

    public void setShotSpeed(double Shot_Speed){shotSpeed = Shot_Speed;}

    void updateTimer(long time){timer = time + waitTime;}


}