package com.example.text_recognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class OcrActivity extends AppCompatActivity {

    TextView mResult;

    ImageView img;

    Uri imgUri;

    Button btnNext;

    EditText editName;

    public static final String TEXT_OCR ="TEXTOCR";
    public static final String TEXT_NAME ="TEXTNAME";
    public static final String IMAGE_OCR ="IMAGEOCR";
    public static final String BUNDLE ="BUNDLE";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        mResult =findViewById(R.id.result);

        img = findViewById(R.id.imageResult);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        CropImage.activity().start(OcrActivity.this);


        btnNext = findViewById(R.id.btnNext);


        editName = findViewById(R.id.editName);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mResult.getText().toString()))
                {
                    Toast.makeText(OcrActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    byBundle();
                }
            }
        });





    }

    private void byBundle() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, bs);
        byte[] b = bs.toByteArray();

        Intent intent = new Intent(OcrActivity.this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_OCR, mResult.getText().toString());
        bundle.putByteArray(IMAGE_OCR, b);
        intent.putExtra(BUNDLE, bundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();

                img.setImageURI(imgUri);


                BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();

                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();


                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        stringBuilder.append(myItem.getValue());
                        stringBuilder.append("\n");

                    }

                    mResult.setText(stringBuilder.toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
