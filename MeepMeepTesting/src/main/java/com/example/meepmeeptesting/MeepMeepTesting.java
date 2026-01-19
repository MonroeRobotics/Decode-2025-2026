package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {

        Vector2d blueStart = new Vector2d(60, -12); //90
        Vector2d redStart = new Vector2d(-54, 46); //90

        Vector2d blueFarShot = new Vector2d(56,-12); //203
        Vector2d blueHumanPlayer = new Vector2d(56, -54); //180
        Vector2d bluePark = new Vector2d(37.5, 32); //90

        Vector2d redFarShot = new Vector2d(-56, 12); //157
        Vector2d redCloseShot  = new Vector2d(-56.5, 43.5); //126.3
        Vector2d redCloseShotTransition = new Vector2d(-53.5, 40.5); //126.3
        Vector2d redHumanPlayer = new Vector2d(56, 54); //0
        Vector2d redPark = new Vector2d(-18, 47); //90
        Vector2d redPickupLineup1 = new Vector2d(35.5, 32); //90
        Vector2d redPickupLineup2 = new Vector2d(11.5, 32); //90
        Vector2d redPickupLineup3 = new Vector2d(-12, 32); //90
        Vector2d redPickup1 = new Vector2d(35.5, 50); //90
        Vector2d redPickup2 = new Vector2d(11.5, 50); //90
        Vector2d redPickup3 = new Vector2d(-12, 50); //90



        MeepMeep meepMeep = new MeepMeep(800);
        RoadRunnerBotEntity redBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();
        redBot.runAction(redBot.getDrive().actionBuilder(new Pose2d(redStart, Math.toRadians(123)))
                .waitSeconds(7)
                .strafeToLinearHeading(redPark, Math.toRadians(90))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(redBot)
                .start();
    }
}