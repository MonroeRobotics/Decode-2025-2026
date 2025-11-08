package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.util.ArmController;

public class intakeTest extends OpMode {
    ArmController armController;

    MecanumDrive mecanumDrive;

    @Override
    public void init() {
        armController.initArm();
    }

    @Override
    public void loop() {

    }
}
