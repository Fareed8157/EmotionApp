package com.example.fareedalimahar.emotionapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    int picCode=100,reqCode=101;
    ImageView imageView;
    Button takePic,process;
    Bitmap bm;

    EmotionServiceClient esc=new EmotionServiceRestClient("ccde4dd4a15b48e8ac1040692d7b8833");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeWidgets();

        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.INTERNET
            },reqCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==reqCode){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeWidgets() {
        imageView=(ImageView)findViewById(R.id.imageView);
        takePic=(Button)findViewById(R.id.takePic);
        process=(Button)findViewById(R.id.process);

        takePic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                takePicFromGallery();
            }
        });


        process.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                processImage();
            }
        });

    }

    private void processImage() {
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,bo);
        ByteArrayInputStream inputStream=new ByteArrayInputStream(bo.toByteArray());


        AsyncTask<InputStream,String,List<RecognizeResult>> as=new AsyncTask<InputStream, String, List<RecognizeResult>>() {
            ProgressDialog pd=new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute(){
                pd.show();
            }


            @Override
            protected void onProgressUpdate(String... values) {
                pd.setMessage(values[0]);
            }

            @Override
            protected List<RecognizeResult> doInBackground(InputStream... bis) {
                publishProgress("Please wait...");
                List<RecognizeResult> rs=null;
                 try {
                        rs=esc.recognizeImage(bis[0]);
                    } catch (EmotionServiceException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                return rs;
            }

            @Override
            protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                pd.dismiss();
                for(RecognizeResult res: recognizeResults){
                    String status=getEmotions(res);
                    imageView.setImageBitmap(RectangleHelper.RecDrawer(bm,res.faceRectangle,status));

                }
            }
        };
        as.execute(inputStream);
    }

    private String getEmotions(RecognizeResult res) {
        List<Double> list=new ArrayList<>();
        Scores score=res.scores;
        list.add(score.anger);
        list.add(score.happiness);
        list.add(score.contempt);
        list.add(score.disgust);
        list.add(score.fear);
        list.add(score.neutral);
        list.add(score.sadness);
        list.add(score.surprise);

        Collections.sort(list);

        double maxVal=list.get(list.size()-1);

        if(maxVal==score.anger)
            return "Anger";
        else if(maxVal==score.happiness)
            return "Happiness";
        else if(maxVal==score.contempt)
            return "Contempt";
        else if(maxVal==score.disgust)
            return "Disgust";
        else if(maxVal==score.fear)
            return "Fear";
        else if(maxVal==score.neutral)
            return "Neutral";
        else if(maxVal==score.sadness)
            return "Saddness";
        else if(maxVal==score.surprise)
            return "Surprise";
        else
            return "Can't Detect";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==picCode){
            Uri selectedImageUri=data.getData();
            InputStream in=null;
            try{
                in=getContentResolver().openInputStream(selectedImageUri);
            }catch (FileNotFoundException ex){
                ex.printStackTrace();
            }
            bm= BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(bm);
        }

    }

    private void takePicFromGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,picCode);
    }

}
