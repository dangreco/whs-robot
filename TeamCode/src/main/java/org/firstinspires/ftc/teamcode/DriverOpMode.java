package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TeleOpTest")
public class DriverOpMode extends LinearOpMode {

    private DcMotor motorLeftFront;
    private DcMotor motorLeftBack;
    private DcMotor motorRightFront;
    private DcMotor motorRightBack;
    private DcMotor liftMotor;

    private Joystick joystick;

    private Servo servoLeft;
    //private Servo servoRight;

    // DO NOT MESS WITH THESE VALUES UNLESS YOU ARE DAN OR ME ..... PRANKED -RISHABH

    //LA DI DA DI DAAA SHLOB ON ME KNOB, PASS ME THE SYRUP...

    // Config for wheel direction modifier. Goes FL, FR, BL, BR.
    private final int[] modUP = {-1, 1, -1, 1};
    private final int[] modDOWN = {1, -1, 1, -1};
    private final int[] modLEFT = {1, 1, -1, -1};
    private final int[] modRIGHT = {-1, -1, 1, 1};

    private int frontInc = 0;
    private int backInc = 0;

    public void runOpMode() throws InterruptedException {

        /* Initialize motors */
        motorLeftFront = hardwareMap.dcMotor.get("motorLF");
        motorLeftBack = hardwareMap.dcMotor.get("motorLB");
        motorRightFront = hardwareMap.dcMotor.get("motorRF");
        motorRightBack = hardwareMap.dcMotor.get("motorRB");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        servoLeft = hardwareMap.servo.get("servoLeft");
        //servoRight = hardwareMap.servo.get("servoRight");

        /* TODO: Check directions; pretty sure left has to be clockwise and right counter clockwise */
        servoLeft.setDirection(Servo.Direction.FORWARD);
        //servoRight.setDirection(Servo.Direction.FORWARD);
        servoLeft.setPosition(0);

        joystick = new Joystick(gamepad1);

        waitForStart();
        while (opModeIsActive()) {

            /* Test for button presses and move motors */
            robotMovement();
            controlLift();
            //testGrab();
            //testRelease();
            //testMotorOrientation();
            testServo();

            /* Update telemetry and wait for hardware thread to catch up */
            telemetry.update();
            idle();

        }

    }


    private void testServo(){
        //servo position ranges from 0-1
        if(gamepad1.y) {
            servoLeft.setPosition(.5);
            servoLeft.setDirection(Servo.Direction.REVERSE);
            servoLeft.setPosition(.5);
        }
        //Should theoretically move the servo back and forth

    }

    /**
     * Function to move lift up and down on robot.
     */
    private void controlLift()
    {
       if (gamepad1.dpad_up) {
           liftMotor.setPower(-0.5);
       } else if (gamepad1.dpad_down) {
           liftMotor.setPower(0.5);
       } else {
           liftMotor.setPower(0);
       }
    }

    /**
     * Check if claw needs to grab.
     */
    private void testGrab()
    {
        if(gamepad1.y) {
            //move to position where it touches block ----might have to change angle based on trial----
            //Drastic angles at the moment for testing
            servoLeft.setPosition(0.5);
            //servoRight.setPosition(0.5);
        }
    }

    private void powerChange()
    {
        if (gamepad1.left_bumper) {
            ++backInc;
            if (backInc > 20) backInc = 20;
        } else if (gamepad1.left_trigger > 0.75) {
            --backInc;
            if (backInc < 0) backInc = 0;
        }

        if (gamepad1.right_bumper) {
            ++frontInc;
            if (frontInc > 20) frontInc = 20;
        } else if (gamepad1.right_trigger > 0.75) {
            --frontInc;
            if (frontInc < 0) frontInc = 0;
        }

    }

    /**
     * Check if the claw needs to release.
     */
    private void testRelease()
    {
        if(gamepad1.x) {
            // move to 0 degrees.
            servoLeft.setPosition(0);
            //servoRight.setPosition(0);
        }
        telemetry.addData("Servo Left Position", servoLeft.getPosition());
        //telemetry.addData("Servo Right Position", servoRight.getPosition());
    }

    public void setMotorPowerToZero()
    {
        motorLeftFront.setPower(0);
        motorLeftBack.setPower(0);
        motorRightFront.setPower(0);
        motorRightBack.setPower(0);
    }

    /**
     * Function to control the rotation and physical movement of robot.
     */
    private void robotMovement()
    {

        /* If it will be rotating, don't drive */
        if (!joystick.rightShouldMove()) {

            if (joystick.leftShouldMove()) {

                /* Derive movement values from gamepad */
                Joystick.Direction direction = joystick.getLeftDirection();
                double power = joystick.getLeftPower();

                int[] modifier;

                if (direction == Joystick.Direction.UP) {
                    modifier = modUP;
                } else if (direction == Joystick.Direction.DOWN) {
                    modifier = modDOWN;
                } else if (direction == Joystick.Direction.RIGHT) {
                    modifier = modRIGHT;
                } else {
                    modifier = modLEFT;
                }

                motorLeftFront.setPower(modifier[0] * ((power * 0.8) + (0.01 * frontInc)));
                motorRightFront.setPower(modifier[1] * ((power * 0.8) + (0.01 * frontInc)));
                motorLeftBack.setPower(modifier[2] * ((power * 0.8) + (0.01 * backInc)));
                motorRightBack.setPower(modifier[3] * ((power * 0.8) + (0.01 * backInc)));

            } else {
                setMotorPowerToZero();
            }

        } else {

            /* Rotation modifiers for sides of bot */
            Joystick.Direction d = joystick.getRightDirection();
            double power = joystick.getRightPower();
            boolean left = d == Joystick.Direction.LEFT;
            double sideMod = left ? 1 : -1;

            /* Set motor power */
            motorLeftFront.setPower(sideMod * power);
            motorLeftBack.setPower(sideMod * power);
            motorRightFront.setPower(sideMod * power);
            motorRightBack.setPower(sideMod * power);

        }

    }

}

