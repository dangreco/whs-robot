package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Joystick {

    private Gamepad gamepad;
    private static final double MIN_MAGNITUDE = 0.3;
    private static final double MAX_MAGNITUDE = 1.0;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Joystick(Gamepad gamepad)
    {
        this.gamepad = gamepad;
    }

    public boolean leftShouldMove()
    {
        double mag = Math.abs(Math.sqrt(Math.pow(gamepad.left_stick_x, 2) + Math.pow(-gamepad.left_stick_y, 2)));
        return mag > MIN_MAGNITUDE;
    }

    public Direction getLeftDirection()
    {
        double angle = Math.toDegrees(Math.atan2(-gamepad.left_stick_y, gamepad.left_stick_x));
        double angleFixed = angle < 0 ? 180.0 + (180 - Math.abs(angle)) : angle;
        if (angleFixed >= 45 && angleFixed < 135) return Direction.UP;
        if (angleFixed >= 135 && angleFixed < 225) return Direction.LEFT;
        if (angleFixed >= 225 && angleFixed < 315) return Direction.DOWN;
        return Direction.RIGHT;
    }

    public double getLeftPower()
    {
        double pow = ((Math.abs(Math.sqrt(Math.pow(gamepad.left_stick_x, 2) + Math.pow(-gamepad.left_stick_y, 2))) - MIN_MAGNITUDE) / (MAX_MAGNITUDE - MIN_MAGNITUDE));
        return pow > 1.0 ? 1.0 : pow;
    }


    public boolean rightShouldMove()
    {
        double mag = Math.abs(Math.sqrt(Math.pow(gamepad.right_stick_x, 2) + Math.pow(-gamepad.right_stick_y, 2)));
        return mag > MIN_MAGNITUDE;
    }

    public Direction getRightDirection()
    {
        double angle = Math.toDegrees(Math.atan2(gamepad.right_stick_x, -gamepad.right_stick_y));
        return angle < 0 ? Direction.LEFT : Direction.RIGHT;
    }

    public double getRightPower()
    {
        double pow = ((Math.abs(Math.sqrt(Math.pow(gamepad.right_stick_x, 2) + Math.pow(-gamepad.right_stick_y, 2))) - MIN_MAGNITUDE) / (MAX_MAGNITUDE - MIN_MAGNITUDE));
        return pow > 1.0 ? 1.0 : pow;
    }

}