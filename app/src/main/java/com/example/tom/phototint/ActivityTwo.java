package com.example.tom.phototint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.tom.phototint.MainActivity.restaure;

public class ActivityTwo extends AppCompatActivity {

    /**
     * compute the minimum and the maximum of the histogram scale (min : min_max[0], max : min_max[1])
     * @param bmp the bitmap which store this informations
     * @return an array of size 2 of this minimum and maximum
     */
    public int[] minMaxLum(Bitmap bmp) {
        int [] min_max = new int[2];
        min_max[0] = 255;
        min_max[1] = 0;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        int r;
        int g;
        int b;
        int lum;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            lum = (int) ((0.3*r) + (0.59*g) + (0.11*b));
            if (lum < min_max[0]) {
                min_max[0] = lum;
            }
            if (lum > min_max[1]) {
                min_max[1] = lum;
            }
        }
        return min_max;
    }

    /**
     * to increase the contrast of the bitmap with the dynamic extension method
     * @param bmp the bitmap to increase the contrast
     */
    public void increaseContrastDE(Bitmap bmp) {
        int [] min_max = minMaxLum(bmp);
        int [] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            lut[i] = (255*(i - min_max[0])) / (min_max[1] - min_max[0]);
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        int a;
        int r;
        int g;
        int b;
        int lum;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            a = Color.alpha(pixs[i]);
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            lum = (int) ((0.3*r) + (0.59*g) + (0.11*b));
            lum = lut[lum];
            pixs[i] = Color.argb(a, lum, lum, lum);
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    /**
     * to compute the histogram of the bitmap
     * @param bmp the bitmap we want the histogram
     * @return an array of size 256 representing the histogram
     */
    public int[] histogram(Bitmap bmp) {
        int [] hist = new int[256];
        for (int i = 0; i < 256; i++) {
            hist[i] = 0;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        int r;
        int g;
        int b;
        int lum;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            lum = (int) ((0.3*r) + (0.59*g) + (0.11*b));
            hist[lum] = hist[lum] + 1;
        }
        return hist;
    }

    /**
     * to compute the cumulative histogram of the bitmap
     * @param bmp the bitmap we want its cumulative histogram
     * @return an array of size 256 representing the histogram
     */
    public int[] cHistogram(Bitmap bmp) {
        int [] hist = histogram(bmp);
        int [] c = new int[256];
        for (int i = 0; i < 256; i++) {
            c[i] = 0;
            for (int j = 0; j <= i; j++) {
                c[i] = c[i] + hist[j];
            }
        }
        return c;
    }

    /**
     * to increase the contrast of the bitmap with the histogram equalization method
     * @param bmp the bitmap to increase the contrast
     */
    public void increaseContrastHE(Bitmap bmp) {
        int [] c_hist = cHistogram(bmp);
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int nb_pixs = width*height;
        int [] pixs = new int[nb_pixs];
        int a;
        int r;
        int g;
        int b;
        int lum;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < nb_pixs; i++) {
            a = Color.alpha(pixs[i]);
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            lum = (int) ((0.3*r) + (0.59*g) + (0.11*b));
            lum = (c_hist[lum]*255) / nb_pixs;
            pixs[i] = Color.argb(a, lum, lum, lum);
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    /**
     * to decrease the contrast of the bitmap (by default size by two the scale of the histogram)
     * @param bmp the bitmap to decrease the contrast
     */
    public void decreaseContrast(Bitmap bmp) {
        int [] min_max = minMaxLum(bmp);
        int delta = min_max[1] - min_max[0];
        int new_min = min_max[0] + (delta/4);
        int new_max = min_max[1] - (delta/4);
        int [] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            lut[i] = ((new_max*(i - min_max[0])) + (new_min*(min_max[1] - i))) / (min_max[1] - min_max[0]);
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        int a;
        int r;
        int g;
        int b;
        int lum;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            a = Color.alpha(pixs[i]);
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            lum = (int) ((0.3*r) + (0.59*g) + (0.11*b));
            lum = lut[lum];
            pixs[i] = Color.argb(a, lum, lum, lum);
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    public void convolution(Bitmap bmp, int length_kernel, int nb_operators, int[] kernels) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int nb_pixs = width*height;
        int [] pixs = new int[nb_pixs];
        int r;
        int g;
        int b;
        int [] lums = new int[nb_pixs];
        int [] new_lums = new int[nb_pixs];
        int size_kernel = length_kernel*length_kernel;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < nb_pixs; i++) {
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            lums[i] = (int) ((0.3*r) + (0.59*g) + (0.11*b));
        }
        for (int j = 0; j < nb_operators; j++) {
            for (int k = 0; k < nb_pixs; k++) {
                new_lums[k] = 0;
                for (int l = 0; l < size_kernel; l++) {
                    if ((k + l - ((width + 1)*(length_kernel/2))) < 0 || (k + l - ((width + 1)*(length_kernel/2))) >= nb_pixs) {
                        new_lums[k] = lums[k];
                        break;
                    }
                    else {
                        new_lums[k] = new_lums[k] + (kernels[l] * lums[k + l - (length_kernel/2)]);
                    }
                }
            }
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inMutable = true;
        final Bitmap b_lady = bf.decodeResource(getResources(), R.drawable.lady, bfo);
        final Bitmap b_backup = b_lady.copy(Bitmap.Config.ARGB_8888, false);
        ImageView iv_lady = (ImageView) findViewById(R.id.iv_pic2);
        iv_lady.setImageBitmap(b_lady);
        TextView tv_size = (TextView) findViewById(R.id.tv_size2);
        tv_size.setText("size = " + String.valueOf(bfo.outWidth) + "x" + String.valueOf(bfo.outHeight));
        Button b_back = (Button) findViewById(R.id.b_backup2);
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                restaure(b_lady, b_backup);
            }
        });
        Button b_i_contrastde = (Button) findViewById(R.id.b_contrastde1);
        b_i_contrastde.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                increaseContrastDE(b_lady);
            }
        });
        Button b_dec_contrast = (Button) findViewById(R.id.b_d_contrast);
        b_dec_contrast.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decreaseContrast(b_lady);
            }
        });
        Button b_i_contrast_he = (Button) findViewById(R.id.b_contrasthe);
        b_i_contrast_he.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                increaseContrastHE(b_lady);
            }
        });
        Button b_prev = (Button) findViewById(R.id.b_act_main);
        b_prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ActivityTwo.this, MainActivity.class));
            }
        });
    }

}
