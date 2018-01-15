package com.example.fareedalimahar.emotionapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.microsoft.projectoxford.emotion.contract.FaceRectangle;

/**
 * Created by Fareed Ali Mahar on 1/7/2018.
 */

public class RectangleHelper {
    public static Bitmap RecDrawer(Bitmap btm, FaceRectangle fr, String status){
        Bitmap bitmap=btm.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        canvas.drawRect(fr.left,fr.top,fr.left+fr.width,fr.top+fr.height,paint);

        int cX=fr.left+fr.width;
        int cY=fr.top+fr.height;

        textDrawer(canvas,50,cX/2+cX/5,cY+70,Color.BLACK,status);

        return bitmap;
    }

    private static void textDrawer(Canvas canvas, int textSize, int cX, int cY, int color, String status) {
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        canvas.drawText(status,cX,cY,paint);
    }
}
