package com.example.jtaraque.reconocimiento;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class DetectionActivity extends AppCompatActivity {

    private static final int IMAGEN_CARGADA = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        context = getApplicationContext();

        Button btnCargarImagen = findViewById(R.id.button3);
        btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGEN_CARGADA);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {

            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);


                        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                                .setTrackingEnabled(false)
                                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                                .build();
                        Detector<Face> safeDetector = new com.example.jtaraque.reconocimiento.patch.SafeFaceDetector(detector);
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<Face> faces = safeDetector.detect(frame);

                        FaceView overlay = findViewById(R.id.faceView);
                        overlay.setContent(bitmap, faces);
                        safeDetector.release();

                        cursor.close();
                    }
                } else {Toast.makeText(context, "Imagen no encontrada",Toast.LENGTH_SHORT).show();

                }

            }
        } else {
            Toast.makeText(context, "¡No has seleccionado ninguna imagen!", Toast.LENGTH_SHORT).show();

        }
    }
}

