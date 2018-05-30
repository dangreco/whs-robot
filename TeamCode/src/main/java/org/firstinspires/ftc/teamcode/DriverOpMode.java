package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TeleOpTest")
public class DriverOpMode extends LinearOpMode {

    private static final double PI = Math.PI;

    private DcMotor motorLeftFront;
    private DcMotor motorLeftBack;
    private DcMotor motorRightFront;
    private DcMotor motorRightBack;
    private DcMotor liftMotor;

    private Servo servoLeft;
    private Servo servoRight;

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
    /*
    public void testMotorOrientation()
    {
        if (gamepad1.dpad_up) {
            motorRightBack.setPower(1.0);
        }
        if (gamepad1.dpad_down) {
            motorRightFront.setPower(1.0);
        }
        if (gamepad1.dpad_left) {
            motorLeftBack.setPower(1.0);
        }
        if (gamepad1.dpad_right){
            motorLeftFront.setPower(1.0);
        }
    }

*/

    /**
     * Function to find the absolute angle (0-360) of vector given its components.
     * @param x => The x component of the vector.
     * @param y => The y component of the vector.
     * @return The angle from 0-360deg of the vector.
     */
    public static double findAngle(double x, double y)
    {
        double angleDeg = Math.atan(Math.abs(x/y)) * 180 / PI;
        boolean xNeg = x < 0;
        boolean yNeg = y < 0;
        if (xNeg && !yNeg)
            angleDeg += 90;
        if (xNeg && yNeg)
            angleDeg += 180;
        if (!xNeg && yNeg)
            angleDeg += 270;
        return angleDeg;
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

            if (gamepad1.y) {
                motorLeftFront.setPower(-1.0);
                motorLeftBack.setPower(-1.0);
                motorRightFront.setPower(1.0);
                motorRightBack.setPower(1.0);
            }
            if (gamepad1.a) {
                motorLeftFront.setPower(1.0);
                motorLeftBack.setPower(1.0);
                motorRightFront.setPower(-1.0);
                motorRightBack.setPower(-1.0);
            }
            if (gamepad1.b) {
                motorLeftFront.setPower(-1.0);
                motorLeftBack.setPower(1.0);
                motorRightFront.setPower(-1.0);
                motorRightBack.setPower(1.0);
            }
            if (gamepad1.x){
                motorLeftFront.setPower(1.0);
                motorLeftBack.setPower(-1.0);
                motorRightFront.setPower(1.0);
                motorRightBack.setPower(-1.0);
            }


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

}

