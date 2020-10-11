package com.ketan_studio.example.mlkit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PoseDetector poseDetector;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button button;
    ImageView img;
    TextView info;
    Bitmap resizedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);

        button = (Button)findViewById(R.id.take);
        info = (TextView)findViewById(R.id.info);
        img = (ImageView)findViewById(R.id.img);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        poseDetector = PoseDetection.getClient(options);
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();
            float scaleWidth = ((float) 480) / width;
            float scaleHeight = ((float) 360) / height;
            // CREATE A MATRIX FOR THE MANIPULATION...
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP....
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            resizedBitmap = Bitmap.createBitmap(
                    imageBitmap, 0, 0, width, height, matrix, false);
            imageBitmap.recycle();
            img.setImageBitmap(resizedBitmap);
            final InputImage image = InputImage.fromBitmap(resizedBitmap, 0);

            try {
                Task<Pose> result = poseDetector.process(image).addOnSuccessListener(new OnSuccessListener<Pose>() {
                    @Override
                    public void onSuccess(Pose pose) {
                        Toast.makeText(MainActivity.this, "Pose Added", Toast.LENGTH_SHORT).show();
                        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
                        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
                        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
                        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
                        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
                        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
                        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
                        PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
                        PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
                        PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
                        PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);
//                      Toast.makeText(MainActivity.this, "LeftSholder: "+leftShoulder+"", Toast.LENGTH_SHORT).show();
                        PointF leftShoulderP = leftShoulder.getPosition();
                        PointF rightShoulderP = rightShoulder.getPosition();
                        float lsx = leftShoulderP.x;
                        float lsy = leftShoulderP.y;
                        float rsx = rightShoulderP.x;
                        float rsy = rightShoulderP.y;

                        PointF leftElbowP = leftElbow.getPosition();
                        float lbowX = leftElbowP.x;
                        float lbowY = leftElbowP.y;
                        PointF rightElbowP = rightElbow.getPosition();
                        float erx = rightElbowP.x;
                        float ery = rightElbowP.y;


                        PointF leftWristP = leftWrist.getPosition();
                        float lwx = leftWristP.x;
                        float lwy = leftWristP.y;
                        PointF rightWristP = rightWrist.getPosition();
                        float rwx = rightWristP.x;
                        float rwy = rightWristP.y;


                        PointF leftHipP = leftHip.getPosition();
                        float lHx = leftHipP.x;
                        float lHy = leftHipP.y;
                        PointF rightHipP = rightHip.getPosition();
                        float rHx = rightHipP.x;
                        float rHy = rightHipP.y;


                        PointF leftKneeP = leftKnee.getPosition();
                        float lKx = leftKneeP.x;
                        float lKy = leftKneeP.y;
                        PointF rightKneeP = rightKnee.getPosition();
                        float rKx = rightKneeP.x;
                        float rKy = rightKneeP.y;

                        PointF leftAnkleP = leftAnkle.getPosition();
                        float lAx = leftAnkleP.x;
                        float lAy = leftAnkleP.y;
                        PointF rightAnkleP = rightAnkle.getPosition();
                        float rAx = rightAnkleP.x;
                        float rAy = rightAnkleP.y;

                        DisplayAll(lsx,lsy,rsx,rsy,lbowX,lbowY,erx,ery,lwx,lwy,rwx,rwy,lHx,lHy,rHx,rHy,lKx,lKy,rKx,rKy,lAx,lAy,rAx,rAy);
                        Toast.makeText(MainActivity.this, "lsx :" + lsx + "lsy :" + lsy, Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
                Toast.makeText(this, "Exceeption"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayAll(float lsx, float lsy, float rsx, float rsy, float lbowX, float lbowY, float erx, float ery, float lwx, float lwy, float rwx, float rwy, float lHx, float lHy, float rHx, float rHy, float lKx, float lKy, float rKx, float rKy, float lAx, float lAy, float rAx, float rAy) {
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.RED);
        Bitmap result = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), resizedBitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(resizedBitmap, 0f, 0f, null);
        canvas.drawLine(lsx,lsy,rsx,rsy,paint);
        canvas.drawLine(rsx,rsy,erx,ery,paint);
        canvas.drawLine(erx,ery,rwx,rwy,paint);
        canvas.drawLine(lsx,lsy,lbowX,lbowY,paint);
        canvas.drawLine(lbowX,lbowY,lwx,lwy,paint);
        canvas.drawLine(rHx,rHy,rKx,rKy,paint);
        canvas.drawLine(rKx,rKy,rAx,rAy,paint);
        canvas.drawLine(lHx,lHy,lKx,lKy,paint);
        canvas.drawLine(lKx,lKy,lAx,lAy,paint);
        img.setImageBitmap(result);
    }

    private void displayLine(float lsx, float lsy, float rsx, float rsy) {
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.RED);
        Bitmap result = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), resizedBitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(resizedBitmap, 0f, 0f, null);
        canvas.drawLine(lsx,lsy,rsx,rsy,paint);
        img.setImageBitmap(result);

    }
}