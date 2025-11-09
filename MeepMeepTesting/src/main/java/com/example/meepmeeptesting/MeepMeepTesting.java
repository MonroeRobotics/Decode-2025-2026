package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {

        Vector2d blueStart = new Vector2d(60, -12); //90
        Vector2d redStart = new Vector2d(60, 12); //90

        Vector2d blueFarShot = new Vector2d(56,-12); //203
        Vector2d blueHumanPlayer = new Vector2d(56, -54); //180
        Vector2d bluePark = new Vector2d(37.5, 32); //90

        Vector2d redFarShot = new Vector2d(56, 12); //157
        Vector2d redHumanPlayer = new Vector2d(56, 54); //0
        Vector2d redPark = new Vector2d(37.5, -32); //90



        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity blueBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();
        RoadRunnerBotEntity redBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        blueBot.runAction(blueBot.getDrive().actionBuilder(new Pose2d(blueStart, Math.toRadians(180)))
                .waitSeconds(2)
                .strafeToLinearHeading(blueFarShot, Math.toRadians(203))
                .waitSeconds(2)
                .turnTo(Math.toRadians(270))
                .strafeToLinearHeading(blueHumanPlayer, Math.toRadians(270))
                .strafeToLinearHeading(blueFarShot, Math.toRadians(203))
                .waitSeconds(2)
                .turnTo(Math.toRadians(270))
                .strafeToLinearHeading(blueHumanPlayer, Math.toRadians(270))
                .strafeToLinearHeading(blueFarShot, Math.toRadians(203))
                .waitSeconds(2)
                .turnTo(Math.toRadians(270))
                .strafeToLinearHeading(blueHumanPlayer, Math.toRadians(270))
                .strafeToLinearHeading(blueFarShot, Math.toRadians(203))
                .waitSeconds(2)
                .strafeToLinearHeading(bluePark, Math.toRadians(180))
                .build());
        redBot.runAction(redBot.getDrive().actionBuilder(new Pose2d(redStart, Math.toRadians(180)))
                .waitSeconds(2)
                .strafeToLinearHeading(redFarShot, Math.toRadians(157))
                .waitSeconds(2)
                .turnTo(Math.toRadians(90))
                .strafeToLinearHeading(redHumanPlayer, Math.toRadians(90))
                .strafeToLinearHeading(redFarShot, Math.toRadians(157))
                .waitSeconds(2)
                .turnTo(Math.toRadians(90))
                .strafeToLinearHeading(redHumanPlayer, Math.toRadians(90))
                .strafeToLinearHeading(redFarShot, Math.toRadians(157))
                .waitSeconds(2)
                .turnTo(Math.toRadians(90))
                .strafeToLinearHeading(redHumanPlayer, Math.toRadians(90))
                .strafeToLinearHeading(redFarShot, Math.toRadians(157))
                .waitSeconds(2)
                .strafeToLinearHeading(redPark, Math.toRadians(180))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(blueBot)
                .addEntity(redBot)
                .start();
    }
}