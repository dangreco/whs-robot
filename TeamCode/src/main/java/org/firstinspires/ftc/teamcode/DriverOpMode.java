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
        motorLeftFront = hardwareMap.dcMotor.get("motorLeftFront");
        motorLeftBack = hardwareMap.dcMotor.get("motorLeftBack");
        motorRightFront = hardwareMap.dcMotor.get("motorRightFront");
        motorRightBack = hardwareMap.dcMotor.get("motorRightBack");
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

            /* Update telemetry and wait for hardware thread to catch up */
            telemetry.update();
            idle();

        }

    }

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

    /**
     * Function to control the rotation and physical movement of robot.
     */
    private void robotMovement() {

        /* If it will be rotating, don't drive */
        if (!gamepad1.left_bumper && !gamepad1.right_bumper) {

            /* Derive movement values from gamepad */
            float stickLeftX = gamepad1.left_stick_x;
            float stickLeftY = -gamepad1.left_stick_y;
            double stickLeftMagnitude = Math.sqrt(Math.pow(stickLeftX, 2) + Math.pow(stickLeftY, 2));
            double stickLeftAngle = findAngle(stickLeftX, stickLeftY);

            /* Modify wheel power by direction of stick (90deg intervals) */
            int modFL, modFR, modBL, modBR;
            modFL = modFR = modBL = modBR = 0;

            if (inRange(stickLeftAngle, 45, 135)) {
                /* Up */
                modFL = modFR = modBR = modBL = 1;
            } else if (inRange(stickLeftAngle, 135, 225)) {
                /* Left */
                modFR = modBL = 1;
                modFL = modBR = -1;
            } else if (inRange(stickLeftAngle, 225, 315)) {
                /* Down */
                modFL = modFR = modBR = modBL = -1;
            } else if (inRange(stickLeftAngle, 315, 45)) {
                /* Right */
                modFL = modBR = 1;
                modFR = modBL = -1;
            }

            /* Set motor power */
            motorLeftFront.setPower(modFL * stickLeftMagnitude);
            motorRightFront.setPower(modFR * stickLeftMagnitude);
            motorLeftBack.setPower(modBL * stickLeftMagnitude);
            motorRightBack.setPower(modBR * stickLeftMagnitude);

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

