package org.firstinspires.ftc.teamcode;

/**
 * Created by R on 2/12/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.math.BigInteger;

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

    private boolean liftMaxed = false;
    private int liftPosition = 0;


    // private Servo arms
    public void runOpMode() throws InterruptedException {
        motorLeftFront = hardwareMap.dcMotor.get("motorLeftFront");
        motorLeftBack = hardwareMap.dcMotor.get("motorLeftBack");
        motorRightFront = hardwareMap.dcMotor.get("motorRightFront");
        motorRightBack = hardwareMap.dcMotor.get("motorRightBack");

        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        servoLeft = hardwareMap.servo.get("servoLeft");
        servoRight = hardwareMap.servo.get("servoRight");


        waitForStart();
        while (opModeIsActive()) {

            robotMovement();
            controlLift();

            idle();

        }


    }

    private void controlLift()
    {
        // Calculate stick positioning
        int liftPosition = liftMotor.getCurrentPosition();
        double speed = Math.abs(gamepad1.right_stick_y);
        int direction = gamepad1.right_stick_y < 0 ? 1 : -1;

        // Move lift
        liftMotor.setTargetPosition(liftPosition + (int)Math.round(10 * speed * direction));

        // Add telemetry data
        telemetry.addData("Lift Position", liftMotor.getTargetPosition());

    }
    private void grab(){

    }

    private void robotMovement() {

        // Do not move if rotating
        if (!gamepad1.left_bumper && !gamepad1.right_bumper) {

            // Get motor values
            float stickLeftX = gamepad1.left_stick_x;
            float stickLeftY = -gamepad1.left_stick_y;


            // Calc motor power & angle
            double stickLeftMagnitude = Math.sqrt(Math.pow(stickLeftX, 2) + Math.pow(stickLeftY, 2));
            double stickLeftAngle = Math.atan2(stickLeftX, stickLeftY) * 180 / PI + 180;

            // Init wheel direction modifiers
            int modFL, modFR, modBL, modBR;
            modFL = modFR = modBL = modBR = 0;

            // Set wheel direction modifiers
            if (stickLeftAngle > PI / 4 && stickLeftAngle < 3 * PI / 4) {
                // Stick is up
                modFL = modFR = modBR = modBL = 1;
            } else if (stickLeftAngle > 3 * PI / 4 && stickLeftAngle < 5 * PI / 4) {
                // Stick is left
                modFR = modBL = 1;
                modFL = modBR = 1;
            } else if (stickLeftAngle > 5 * PI / 4 && stickLeftAngle < 7 * PI / 4) {
                // Stick is down
                modFL = modFR = modBR = modBL = -1;
            } else if (stickLeftAngle > 7 * PI / 4 && stickLeftAngle < PI / 4) {
                // Stick is right
                modFL = modBR = 1;
                modFR = modBL = -1;
            }

            // Set wheel power
            motorLeftFront.setPower(modFL * stickLeftMagnitude);
            motorRightFront.setPower(modFR * stickLeftMagnitude);
            motorLeftBack.setPower(modBL * stickLeftMagnitude);
            motorRightBack.setPower(modBR * stickLeftMagnitude);

        } else {

            // Side modifiers
            double leftMod = gamepad1.left_bumper ? -1 : 1;
            double rightMod = gamepad1.left_bumper ? 1 : -1;

            // Set motor power
            motorLeftFront.setPower(leftMod * .5);
            motorLeftBack.setPower(leftMod * .5);
            motorRightFront.setPower(rightMod * .5);
            motorRightBack.setPower(rightMod * .5);

        }

        // Add telemetry data
        telemetry.addData("Back Left Motor Power", motorLeftBack.getPower());
        telemetry.addData("Back Right Motor Power", motorRightBack.getPower());
        telemetry.addData("Front Left Motor Power", motorLeftFront.getPower());
        telemetry.addData("Front Right Motor Power", motorRightFront.getPower());

    }

}

