package org.firstinspires.ftc.teamcode.util;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class pidfTuner extends OpMode {

    DcMotorEx motor;

    double highVelocity = 1500;
    double lowVelocity = 900;
    double curTargetVelocity = highVelocity;

    double F = 0;
    double P = 0;

    double[] stepSizes = {10, 1, 0.1, 0.01, 0.001, 0.0001};
    int stepIndex = 10;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "motor");

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //motor.setDirection(DcMotorSimple.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("Init done.");
    }

    @Override
    public void loop() {
        if (gamepad1.yWasPressed()){
            if (curTargetVelocity == highVelocity){
                curTargetVelocity = lowVelocity;
            }
            else {
                curTargetVelocity = lowVelocity;
            }
        }

        if (gamepad1.bWasPressed()){
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()){
            F += stepSizes[stepIndex];
        }
       if (gamepad1.dpadLeftWasPressed()){
           F -= stepSizes[stepIndex];
       }

       if (gamepad1.dpadUpWasPressed()){
           P += stepSizes[stepIndex];
       }
       if (gamepad1.dpadDownWasPressed()){
           P -= stepSizes[stepIndex];
       }


       PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
       motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

       motor.setVelocity(curTargetVelocity);
       double curVelocity = motor.getVelocity();
       double curError = curTargetVelocity - curVelocity;
       telemetry.addData("target velocity", curTargetVelocity);
        telemetry.addData("current velocity", curVelocity);
        telemetry.addData("error","%.2f" , curError);
        telemetry.addLine("-----------------------------------");
        telemetry.addData("P value","%.4f" , P);
        telemetry.addData("F value","%.4f" ,  F);
        telemetry.addData("Step size","%.4f" , stepSizes[stepIndex]);
    }

}
