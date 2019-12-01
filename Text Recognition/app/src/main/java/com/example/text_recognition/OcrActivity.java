package com.example.text_recognition;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class OcrActivity extends AppCompatActivity {

    EditText mResult;

    ImageView img;

    Uri imgUri;


    Button copy;

    Button importPDF;


//    public static final String TEXT_OCR ="TEXTOCR";
//    public static final String TEXT_NAME ="TEXTNAME";
//    public static final String IMAGE_OCR ="IMAGEOCR";
//    public static final String BUNDLE ="BUNDLE";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        mResult =findViewById(R.id.result);

        img = findViewById(R.id.imageResult);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        CropImage.activity().start(OcrActivity.this);

        copy = findViewById(R.id.btnCopy);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", mResult.getText().toString());
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(OcrActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        importPDF = findViewById(R.id.importPDF);

        importPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pi);

                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawPaint(paint);

                bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(), bitmap.getHeight(), true);
                paint.setColor(Color.BLUE);

                canvas.drawBitmap(bitmap,0,0,null);

                pdfDocument.finishPage(page);


                // save bitmap image

                File folder = new File(Environment.getExternalStorageDirectory(),"PDF Folder");

                if(!folder.exists())
                {
                    folder.mkdir();
                }

                String namePdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

                File filePdf = new File(folder, namePdf + ".pdf");

                try
                {
                    FileOutputStream fileOutputStream = new FileOutputStream(filePdf);
                    pdfDocument.writeTo(fileOutputStream);

                    Toast.makeText(OcrActivity.this, filePdf + " is saved to " + filePdf +".pdf", Toast.LENGTH_SHORT).show();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                pdfDocument.close();


            }
        });


    }

    /*private void byBundle() {
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
    }*/

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
