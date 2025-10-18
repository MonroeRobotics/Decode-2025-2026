package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp(name = "shotTest", group = "main")
public class launchTest extends OpMode{
    public DcMotorEx motorR;
    public DcMotorEx motorL;

    @Override
    public void init() {
        motorL = hardwareMap.get(DcMotorEx.class, "leftMotor");
        motorR = hardwareMap.get(DcMotorEx.class, "rightMotor");

        motorL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorR.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        if (gamepad1.x){
            motorL.setPower(0.45);
            motorR.setPower(0.45);
        }
        if (gamepad1.a){
            motorL.setPower(0);
            motorR.setPower(0);
        }
    }
}
