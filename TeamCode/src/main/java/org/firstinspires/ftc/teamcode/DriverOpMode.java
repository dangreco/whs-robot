package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TeleOpTest")
public class DriverOpMode extends LinearOpMode {

    private static final double PI = Math.PI;

    private DcMotor motorLeftFront;
    private DcMotor motorLeftBack;
    private DcMotor motorRightFront;
    private DcMotor motorRightBack;
    private DcMotor liftMotor;

    private Joystick joystick;

    private Servo servoLeft;
    private Servo servoRight;

    // Config for wheel direction modifier. Goes FL, FR, BL, BR.
    private final int[] modUP = {-1, 1, -1, 1};
    private final int[] modDOWN = {1, -1, 1, -1};
    private final int[] modLEFT = {1, 1, -1, -1};
    private final int[] modRIGHT = {-1, -1, 1, 1};


    public void runOpMode() throws InterruptedException {

        /* Initialize motors */
        motorLeftFront = hardwareMap.dcMotor.get("motorLF");
        motorLeftBack = hardwareMap.dcMotor.get("motorLB");
        motorRightFront = hardwareMap.dcMotor.get("motorRF");
        motorRightBack = hardwareMap.dcMotor.get("motorRB");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        servoLeft = hardwareMap.servo.get("servoLeft");
        servoRight = hardwareMap.servo.get("servoRight");

        /* TODO: Check directions; pretty sure left has to be clockwise and right counter clockwise */
        servoLeft.setDirection(Servo.Direction.FORWARD);
        servoRight.setDirection(Servo.Direction.REVERSE);

        joystick = new Joystick(gamepad1);

        waitForStart();
        while (opModeIsActive()) {

            /* Test for button presses and move motors */
            robotMovement();
            controlLift();
            testGrab();
            testRelease();
            //testMotorOrientation();

            /* Update telemetry and wait for hardware thread to catch up */
            telemetry.update();
            idle();

        }

    }

    /**
     * Function to determine if a given value is within a given range (exclusive upper).
     * @param value => THe value to test.
     * @param lower => Lower bound of range (inclusive).
     * @param upper => Upper bound of range (exclusive).
     * @return Boolean value indicating if value is in range
     */
    public static boolean inRange(double value, double lower, double upper)
    {
        return (value >= lower && value < upper);
    }

    /**
     * Function to move lift up and down on robot.
     */
    private void controlLift()
    {
        /* Calculate stick positioning */
        int liftPosition = liftMotor.getCurrentPosition();
        double speed = Math.abs(gamepad1.right_stick_y);
        int direction = gamepad1.right_stick_y < 0 ? 1 : -1;

        /* Move lift */
        liftMotor.setTargetPosition(liftPosition + (int)Math.round(10 * speed * direction));

        /* Add telemetry data */
        telemetry.addData("Lift Position", liftMotor.getTargetPosition());
    }

    /**
     * Check if claw needs to grab.
     */
    private void testGrab(){
        if(gamepad1.y) {
            //move to position where it touches block ----might have to change angle based on trial----
            //Drastic angles at the moment for testing
            servoLeft.setPosition(0.5);
            servoRight.setPosition(0.5);
        }
        telemetry.addData("Servo Left Position", servoLeft.getPosition());
        telemetry.addData("Servo Right Position", servoLeft.getPosition());
    }

    /**
     * Check if the claw needs to release.
     */
    private void testRelease(){
        if(gamepad1.x) {
            // move to 0 degrees.
            servoLeft.setPosition(0);
            servoRight.setPosition(0);
        }
        telemetry.addData("Servo Left Position", servoLeft.getPosition());
        telemetry.addData("Servo Right Position", servoLeft.getPosition());
    }

    public void setToZero()
    {
        motorLeftFront.setPower(0);
        motorLeftBack.setPower(0);
        motorRightFront.setPower(0);
        motorRightBack.setPower(0);
    }

    /**
     * Function to control the rotation and physical movement of robot.
     */
    private void robotMovement() {

        /* If it will be rotating, don't drive */
        if (!gamepad1.left_bumper && !gamepad1.right_bumper) {

            /* Derive movement values from gamepad */
            Direction direction = joystick.getDirection();
            double power = joystick.getPower();

            int[] modifier;

            if (direction == Direction.UP) {
                modifier = modUP;
            } else if (direction == Direction.DOWN) {
                modifier = modDOWN;
            } else if (direction == Direction.RIGHT) {
                modifier = modRIGHT;
            } else {
                modifier = modLEFT;
            }

            motorLeftFront.setPower(modifier[0] * power);
            motorRightFront.setPower(modifier[1] * power);
            motorLeftBack.setPower(modifier[2] * power);
            motorRightBack.setPower(modifier[3] * power);


        } else {

            /* Rotation modifiers for sides of bot */
            double leftMod = gamepad1.left_bumper ? -1 : 1;
            double rightMod = gamepad1.left_bumper ? 1 : -1;

            /* Set motor power */
            motorLeftFront.setPower(leftMod * .5);
            motorLeftBack.setPower(leftMod * .5);
            motorRightFront.setPower(rightMod * .5);
            motorRightBack.setPower(rightMod * .5);

        }

        /* Add telemetry data */
        telemetry.addData("Back Left Motor Power", motorLeftBack.getPower());
        telemetry.addData("Back Right Motor Power", motorRightBack.getPower());
        telemetry.addData("Front Left Motor Power", motorLeftFront.getPower());
        telemetry.addData("Front Right Motor Power", motorRightFront.getPower());
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private static class Joystick {

        private Gamepad gamepad;
        private static final double MIN_MAGNITUDE = 0.3;
        private static final double MAX_MAGNITUDE = 1.0;

        public Joystick(Gamepad gamepad)
        {
            this.gamepad = gamepad;
        }

        public Direction getDirection()
        {
            double angle = Math.toDegrees(Math.atan2(-gamepad.left_stick_y, gamepad.left_stick_x));
            double angleFixed = angle < 0 ? 180.0 + (180 - Math.abs(angle)) : angle;
            if (angleFixed >= 45 && angleFixed < 135) return Direction.UP;
            if (angleFixed >= 135 && angleFixed < 225) return Direction.LEFT;
            if (angleFixed >= 225 && angleFixed < 315) return Direction.DOWN;
            return Direction.RIGHT;
        }

        public double getPower()
        {
            double pow = ((Math.abs(Math.sqrt(Math.pow(gamepad.left_stick_x, 2) + Math.pow(-gamepad.left_stick_y, 2))) - MIN_MAGNITUDE) / (MAX_MAGNITUDE - MIN_MAGNITUDE));
            return pow > 1.0 ? 1.0 : pow;
        }

    }

}

