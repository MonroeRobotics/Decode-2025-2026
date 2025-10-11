package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name = "shotTest", group = "main")
public class launchTest extends OpMode{

    HardwareMap hardwareMap;
    Gamepad gamepad;
    public DcMotorEx motorR;
    public DcMotorEx motorL;

    @Override
    public void init() {
        motorL = hardwareMap.get(DcMotorEx.class, "motorL");
        motorR = hardwareMap.get(DcMotorEx.class, "motorL");

        motorL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorL.setDirection(DcMotorSimple.Direction.REVERSE);

        gamepad = new Gamepad();
    }

    @Override
    public void loop() {
        if (gamepad.x){
            motorL.setPower(0.45);
            motorR.setPower(0.45);
        }
        if (gamepad.a){
            motorL.setVelocity(0);
            motorR.setVelocity(0);
        }
    }
}
