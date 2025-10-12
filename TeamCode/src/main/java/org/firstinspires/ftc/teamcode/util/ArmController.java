package org.firstinspires.ftc.teamcode.util;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ArmController {
    HardwareMap hardwareMap;

    final double closeShotSpeed = 0.45;
    final double farShotSpeed = 0; //placeholder
    final double shotSpeedOff = 0;
    double shotSpeed;

    final double intakeSpeedOn = 0; //placeholder
    final double intakeSpeedOff = 0;
    double intakeSpeed;


    DcMotorEx intakeMotor;
    DcMotorEx launchMotorL;
    DcMotorEx launchMotorR;

    int timer;
    int waitTime = 500; //time in milliseconds
    boolean hasUpdatedTimer = false;

    enum armState{
        rest,
        farShot,
        closeShot,
        intake,
    }
    static armState currentArmState = armState.rest;
    void initArm(){
        //Assigning each object to its correct port.
        launchMotorL = hardwareMap.get(DcMotorEx.class, "motorL");
        launchMotorR = hardwareMap.get(DcMotorEx.class, "motorR");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

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
    void  updateArmState(int time){
        switch(currentArmState){
            case rest:
                intakeSpeed = intakeSpeedOff;
                shotSpeed = shotSpeedOff;
                break;
            case farShot:
                //todo - adjust ramp angle once Kingston finds a method
                if (!hasUpdatedTimer){
                    updateTimer(time);
                    hasUpdatedTimer = true;
                }
                if (time >= timer){
                    shotSpeed = farShotSpeed;
                    intakeSpeed = intakeSpeedOn;
                    hasUpdatedTimer = false;
                }
                break;
            case closeShot:
                //todo - adjust ramp angle once Kingston finds a method
                if (!hasUpdatedTimer){
                    updateTimer(time);
                    hasUpdatedTimer = true;
                }
                if (time >= timer){
                    shotSpeed = closeShotSpeed;
                    intakeSpeed = intakeSpeedOn;
                    hasUpdatedTimer = false;
                }
                break;
            case intake:
                shotSpeed = shotSpeedOff;
                intakeSpeed = intakeSpeedOn;
                break;

        }

        launchMotorL.setPower(shotSpeed);
        launchMotorR.setPower(shotSpeed);
        intakeMotor.setPower(intakeSpeed);
    }
    double getShotSpeed(){return shotSpeed;}

    boolean isIntakeOn(){return intakeSpeed != 0;}

    void setIntakeSpeed(double Intake_Speed){intakeSpeed = Intake_Speed;}

    void setShotSpeed(double Shot_Speed){shotSpeed = Shot_Speed;}

    void updateTimer(int time){
        timer = time + waitTime;
    }


}