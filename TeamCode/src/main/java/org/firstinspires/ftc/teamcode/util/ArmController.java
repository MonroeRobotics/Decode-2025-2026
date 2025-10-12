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
    void  updateArmState(){
        switch(currentArmState){
            case rest:
                intakeSpeed = intakeSpeedOff;
                shotSpeed = shotSpeedOff;
                break;
            case farShot:
                //todo - adjust angle of ramp once method is found
                intakeSpeed = intakeSpeedOn;
                shotSpeed = farShotSpeed;

                break;
            case closeShot:
                //todo - adjust angle of ramp once method is found
                intakeSpeed = intakeSpeedOn;
                shotSpeed = closeShotSpeed;
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
}
