package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepAuto3Sample {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity botBlue = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(16, 18)
                .setColorScheme(new ColorSchemeBlueDark())
                .build();
        RoadRunnerBotEntity botRed = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(16, 18)
                .setColorScheme(new ColorSchemeRedDark())
                .build();

        botBlue.runAction(botBlue.getDrive().actionBuilder(new Pose2d(16.5, 62, Math.toRadians(0)))
                .waitSeconds(2)
                .setTangent(Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(10.5, 40, Math.toRadians(-90)), Math.toRadians(-90))
                .waitSeconds(0.1)
                .setTangent(Math.toRadians(-90))
                .splineToLinearHeading(new Pose2d(10.5, 30, Math.toRadians(-90)), Math.toRadians(-90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(90))
                .splineToSplineHeading(new Pose2d(40, 44, Math.toRadians(90)), Math.toRadians(0))
                .splineToConstantHeading(new Vector2d(49, 44), Math.toRadians(0))
                .waitSeconds(2)
                .setTangent(Math.toRadians(45))
                .splineToLinearHeading(new Pose2d(55, 55, Math.toRadians(45)), Math.toRadians(90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(45))
                .splineToLinearHeading(new Pose2d(58, 58, Math.toRadians(45)), Math.toRadians(45))
                .waitSeconds(2)
                .setTangent(Math.toRadians(-90))
                .splineToLinearHeading(new Pose2d(58, 44, Math.toRadians(90)), Math.toRadians(-90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(55, 55, Math.toRadians(45)), Math.toRadians(90))
                .waitSeconds(2)
                .setTangent(Math.toRadians(45))
                .splineToLinearHeading(new Pose2d(58, 58, Math.toRadians(45)), Math.toRadians(45))

                .setTangent(Math.toRadians(-90))
                .splineToLinearHeading(new Pose2d(60, 38, Math.toRadians(125)), Math.toRadians(-90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(new Pose2d(55, 55, Math.toRadians(45)), Math.toRadians(90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(45))
                .splineToLinearHeading(new Pose2d(58, 58, Math.toRadians(45)), Math.toRadians(45))
                .waitSeconds(1)

                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(24, -12, Math.toRadians(0)), Math.toRadians(180))
                .build());

        botRed.runAction(botRed.getDrive().actionBuilder(new Pose2d(-16.5, -62, Math.toRadians(180)))
                .waitSeconds(2)
                .setTangent(Math.toRadians(90))
                .splineToSplineHeading(new Pose2d(-10.5, -40, Math.toRadians(90)), Math.toRadians(90))
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(-10.5, -30, Math.toRadians(90)), Math.toRadians(90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-49, -44, Math.toRadians(-90)), Math.toRadians(180))
                .waitSeconds(2)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-55, -55, Math.toRadians(-135)), Math.toRadians(-90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-58, -58, Math.toRadians(-135)), Math.toRadians(-135))
                .waitSeconds(2)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(-58, -44, Math.toRadians(-90)), Math.toRadians(90))
                .waitSeconds(1)
                .setTangent(Math.toRadians(-90))
                .splineToLinearHeading(new Pose2d(-55, -55, Math.toRadians(-135)), Math.toRadians(-90))
                .waitSeconds(2)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-58, -58, Math.toRadians(-135)), Math.toRadians(-135))

                .setTangent(Math.toRadians(45))
                .splineToLinearHeading(new Pose2d(-50, -25, Math.toRadians(0)), Math.toRadians(45))
                .waitSeconds(1)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-55, -55, Math.toRadians(-135)), Math.toRadians(-135))
                .waitSeconds(1)
                .setTangent(Math.toRadians(-135))
                .splineToLinearHeading(new Pose2d(-58, -58, Math.toRadians(-135)), Math.toRadians(-135))
                .waitSeconds(1)

                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(new Pose2d(-24, 12, Math.toRadians(180)), Math.toRadians(0))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(botBlue)
                .addEntity(botRed)
                .start();
    }
}
