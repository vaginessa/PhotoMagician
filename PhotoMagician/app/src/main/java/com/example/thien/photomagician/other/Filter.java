package com.example.thien.photomagician.other;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Filter {

    public static final double PI = 3.14159d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;

    public static Bitmap grayscale(Mat ImageMat) {
        Mat tmp = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Imgproc.cvtColor(ImageMat, tmp, Imgproc.COLOR_RGBA2GRAY, 4);
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
        //Then convert the processed Mat to Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, resultBitmap);
        return resultBitmap;
    }

    public static Bitmap bgr(Mat ImageMat) {
        Mat tmp = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Imgproc.cvtColor(ImageMat, tmp, Imgproc.COLOR_RGB2BGR, 4);
        //Then convert the processed Mat to Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, resultBitmap);
        return resultBitmap;
    }

    public static Bitmap changeBitmapContrastBrightness(Mat ImageMat, float contrast, float brightness)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Sharpen(Mat ImageMat) {
        Mat tmp = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Imgproc.GaussianBlur(ImageMat, tmp, new Size(0, 0), 3);
        Core.addWeighted(ImageMat, 3, tmp, -2, 0, tmp);
        Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, resultBitmap);
        return resultBitmap;
    }

    public static Bitmap Blur(Mat ImageMat) {
        Mat tmp = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Imgproc.GaussianBlur(ImageMat, tmp, new Size(0, 0), 3);
        Core.addWeighted(ImageMat, -1, tmp, 2, 0, tmp);
        Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, resultBitmap);
        return resultBitmap;
    }

    public static Bitmap doInvert(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap undertone(Mat ImageMat)
    {
        Mat tmp = ImageMat.clone();
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, bmp);
        float v = 0.5f;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.213f + 0.787f * v, 0.715f - 0.715f * v, 0.072f - 0.072f * v, 0, 0,
                        0.213f - 0.213f * v, 0.715f + 0.285f * v, 0.072f - 0.072f * v, 0, 0,
                        0.213f - 0.213f * v, 0.715f - 0.715f * v, 0.072f + 0.928f * v, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Sepia(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.393f, 0.769f, 0.189f, 0, 0,
                        0.349f,0.686f,0.168f,0,0,
                        0.272f,0.534f,0.131f,0,0,
                        0, 0, 0, 1, 0

                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }


    public static Bitmap Polaroid(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1.438f, -0.122f, -0.016f, 0, -0.03f,
                        -0.062f, 1.378f, -0.016f, 0, 0.05f,
                        -0.062f, -0.122f, 1.483f, 0, -0.02f,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Vintage(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.6279345635605994f, 0.3202183420819367f, -0.03965408211312453f, 0, 9.651285835294123f,
                        0.02578397704808868f, 0.6441188644374771f, 0.03259127616149294f, 0, 7.462829176470591f,
                        0.0466055556782719f, -0.0851232987247891f, 0.5241648018700465f, 0, 5.159190588235296f,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Kodachrome(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1.1285582396593525f, -0.3967382283601348f, -0.03992559172921793f, 0, 63.72958762196502f,
                        -0.16404339962244616f, 1.0835251566291304f, -0.05498805115633132f, 0, 24.732407896706203f,
                        -0.16786010706155763f, -0.5603416277695248f, 1.6014850761964943f, 0, 35.62982807460946f,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Technicolor(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1.9125277891456083f, -0.8545344976951645f, -0.09155508482755585f, 0, 11.793603434377337f,
                        -0.3087833385928097f, 1.7658908555458428f, -0.10601743074722245f, 0, -70.35205161461398f,
                        -0.231103377548616f, -0.7501899197440212f, 1.847597816108189f, 0, 30.950940869491138f,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }


    public static Bitmap Luminance(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.2764723f, 0.9297080f, 0.0938197f, 0, -37.1f,
                        0.2764723f, 0.9297080f, 0.0938197f, 0, -37.1f,
                        0.2764723f, 0.9297080f, 0.0938197f, 0, -37.1f,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    public static Bitmap Brownie(Mat ImageMat)
    {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.5997023498159715f,0.34553243048391263f,-0.2708298674538042f,0,47.43192855600873f,
                        -0.037703249837783157f,0.8609577587992641f,0.15059552388459913f,0,-36.96841498319127f,
                        0.24113635128153335f,-0.07441037908422492f,0.44972182064877153f,0,-7.562075277591283f,
                        0,0,0,1,0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }


    public static Bitmap tintImage(Mat ImageMat, int degree) {
        Bitmap src = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, src);
        int width = src.getWidth();
        int height = src.getHeight();

        int[] pix = new int[width * height];
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;

        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                GY = (-30 * r + 41 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }

        Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
        outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return outBitmap;
    }

    public static Bitmap XYZ(Mat ImageMat) {
        Mat tmp = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Imgproc.cvtColor(ImageMat, tmp, Imgproc.COLOR_RGB2XYZ, 4);
        //Then convert the processed Mat to Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(tmp, resultBitmap);
        return resultBitmap;
    }

    public static Bitmap Erode(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0,
                        0.2125f, 0.7154f, 0.0721f, 0, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Dilate(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.1f, 0.4f, 0, 0, 0,
                        0.3f, 1, 0.3f, 0, 0,
                        0, 0.4f, 0.1f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap Cartoon(Mat ImageMat) {
        Mat destination;
        Mat gray = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Mat blur = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Mat edge = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        Mat cartoon = new Mat (ImageMat.rows(), ImageMat.cols(), CvType.CV_8U);
        destination = ImageMat;

        Imgproc.pyrDown(destination, destination);
        Imgproc.pyrUp(destination, destination);

        Imgproc.cvtColor(ImageMat, gray, Imgproc.COLOR_RGBA2GRAY, 4);
        Imgproc.medianBlur(gray, blur, 7);
        Imgproc.adaptiveThreshold(blur, edge, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 2);
        Imgproc.cvtColor(edge, edge, Imgproc.COLOR_GRAY2RGBA, 4);
        Core.bitwise_and(destination, edge, cartoon);
        //Then convert the processed Mat to Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cartoon, resultBitmap);
        return resultBitmap;
    }


    public static Bitmap warm(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1.06f, 0, 0, 0, 0,
                        0, 1.01f, 0, 0, 0,
                        0, 0, 0.93f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap cool(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.99f, 0, 0, 0, 0,
                        0, 0.93f, 0, 0, 0,
                        0, 0, 1.08f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap temperature(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        float v = 2;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1 + v, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 1 - v, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap tint(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        float v = 1;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1 + v, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 1 + v, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap threshold(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        float v = 0.1234f;
        float r_lum = 0.3086f; // 0.212671
        float g_lum = 0.6094f; // 0.715160
        float b_lum = 0.0820f; // 0.072169
        float r = r_lum * 256;
        float g = g_lum * 256;
        float b = b_lum * 256;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        r, g, b, 0, -255 * v,
                        r, g, b, 0, -255 * v,
                        r, g, b, 0, -255 * v,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap protanomaly(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.817f, 0.183f, 0, 0, 0,
                        0.333f, 0.667f, 0, 0, 0,
                        0, 0.125f, 0.875f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap deuteranomaly(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.8f, 0.2f, 0, 0, 0,
                        0.258f, 0.742f, 0, 0, 0,
                        0, 0.142f, 0.858f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap tritanomaly(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.967f, 0.033f, 0, 0, 0,
                        0, 0.733f, 0.267f, 0, 0,
                        0, 0.183f, 0.817f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap protanopia(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.567f, 0.433f, 0, 0, 0,
                        0.558f, 0.442f, 0, 0, 0,
                        0, 0.242f, 0.758f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap deuteranopia(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.625f, 0.375f, 0, 0, 0,
                        0.7f, 0.3f, 0, 0, 0,
                        0, 0.3f, 0.7f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap tritanopia(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.95f, 0.05f, 0, 0, 0,
                        0, 0.433f, 0.567f, 0, 0,
                        0, 0.475f, 0.525f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap achromatopsia(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0.299f, 0.587f, 0.114f, 0, 0,
                        0.299f, 0.587f, 0.114f, 0, 0,
                        0.299f, 0.587f, 0.114f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap achromatomaly(Mat ImageMat) {
        Bitmap bmp = Bitmap.createBitmap(ImageMat.cols(), ImageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ImageMat, bmp);
        ColorMatrix cm = new ColorMatrix(new float[]
                {       0.618f, 0.320f, 0.062f, 0, 0,
                        0.163f, 0.775f, 0.062f, 0, 0,
                        0.163f, 0.320f, 0.516f, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }


}