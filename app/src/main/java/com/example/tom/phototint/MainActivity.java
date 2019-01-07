package com.example.tom.phototint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * restaure a Bitmap at its default values thanks to a copy
     * @param bmp the bitmap to restaure
     * @param backup the copy (must have exactly the same size as bmp)
     */
    public static void restaure(Bitmap bmp, Bitmap backup) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        backup.getPixels(pixs, 0, width, 0, 0, width, height);
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    /**
     * transform the bitmap into a greyscale bitmap thanks to getPixel() and setPixel()
     * @param bmp the bitmap to transform
     */
    public void toGreyV1(Bitmap bmp) {
        int color;
        int a;
        int r;
        int g;
        int b;
        int lum;
        for (int i = 0; i < bmp.getWidth(); i ++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                color = bmp.getPixel(i, j);
                a = Color.alpha(color);
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                lum = (int) ((0.3*r) + (0.59*g) + (0.11*b));
                color = Color.argb(a, lum, lum, lum);
                bmp.setPixel(i, j, color);
            }
        }
    }

    /**
     * transform the bitmap into a greyscale bitmap thanks to getPixels() and setPixels()
     * @param bmp the bitmap to transform
     */
    public void toGreyV2(Bitmap bmp) {
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
            pixs[i] = Color.argb(a, lum, lum, lum);
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    /**
     * transform the bitmap into a greyscale bitmap thanks to a Renderscript algorithm
     * @param bmp the bitmap to transform
     */
    public void toGreyRS(Bitmap bmp) {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_grey greyScript = new ScriptC_grey(rs);

        greyScript.forEach_toGrey(input, output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        greyScript.destroy();
        rs.destroy();
    }

    /**
     * color the bitmap with an uniform hue thanks to getPixels() ans setPixels()
     * @param bmp the bitmap to color
     */
    public void colorize(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixs = new int[width*height];
        int a;
        int r;
        int g;
        int b;
        float[] hsv = new float[3];
        float hue = (float) (Math.random() * 360.0);
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            a = Color.alpha(pixs[i]);
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            Color.RGBToHSV(r, g, b, hsv);
            hsv[0] = hue;
            pixs[i] = Color.HSVToColor(a, hsv);
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    /**
     * color the bitmap with an uniform hue thanks to a Renderscript algorithm
     * @param bmp the bitmap to color
     */
    public void colorizeRS(Bitmap bmp) {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_color colorScript = new ScriptC_color(rs);

        colorScript.set_hue((float) (Math.random() * 360.0));

        colorScript.forEach_colorize(input, output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        colorScript.destroy();
        rs.destroy();
    }

    /**
     * transform the "non red" part of the bitmap into greyscale thanks to getPixels() and setPixels()
     * @param bmp the bitmap to transform
     */
    public void toGreyAndRed(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixs = new int[width*height];
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
            if (r < 125 || g > 125 || b > 125) {
                pixs[i] = Color.argb(a, lum, lum, lum);
            }
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    /**
     * transform the "non red" part of the bitmap into greyscale thanks to a Renderscript algorithm
     * @param bmp the bitmap to transform
     */
    public void toGreyAndRedRS(Bitmap bmp) {
        RenderScript rs = RenderScript.create(this);

        Allocation input = Allocation.createFromBitmap(rs, bmp);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptC_grey_red grey_redScript = new ScriptC_grey_red(rs);

        grey_redScript.forEach_toGreyAndRed(input, output);

        output.copyTo(bmp);

        input.destroy();
        output.destroy();
        grey_redScript.destroy();
        rs.destroy();
    }

    /**
     * compute the minimums and maximums of red (min : min_max[0], max : min_max[1]), green (min : min_max[2], max : min_max[3]), blue (min : min_max[4], max : min_max[5]) histograms scales of the bitmap
     * @param bmp the bitmap which store this informations
     * @return an array of size 6 of this minimums and maximums
     */
    public int[] minMaxColors(Bitmap bmp) {
        int [] min_max = new int[6];
        min_max[0] = 255;
        min_max[1] = 0;
        min_max[2] = 255;
        min_max[3] = 0;
        min_max[4] = 255;
        min_max[5] = 0;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        int r;
        int g;
        int b;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            if (r < min_max[0]) {
                min_max[0] = r;
            }
            if (r > min_max[1]) {
                min_max[1] = r;
            }
            if (g < min_max[2]) {
                min_max[2] = g;
            }
            if (g > min_max[3]) {
                min_max[3] = g;
            }
            if (b < min_max[4]) {
                min_max[4] = b;
            }
            if (b > min_max[5]) {
                min_max[5] = b;
            }
        }
        return min_max;
    }

    /**
     * to increase the contrast of the bitmap with the dynamic extension method
     * @param bmp the bitmap we want to increase the contrast
     */
    public void increaseContrastDE(Bitmap bmp) {
        int [] min_max = minMaxColors(bmp);
        int [] lut_red = new int[256];
        int [] lut_green = new int[256];
        int [] lut_blue = new int[256];
        for (int i = 0; i < 256; i++) {
            lut_red[i] = (255*(i - min_max[0])) / (min_max[1] - min_max[0]);
            lut_green[i] = (255*(i - min_max[2])) / (min_max[3] - min_max[2]);
            lut_blue[i] = (255*(i - min_max[4])) / (min_max[5] - min_max[4]);
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int [] pixs = new int[width*height];
        int a;
        int r;
        int g;
        int b;
        bmp.getPixels(pixs, 0, width, 0, 0, width, height);
        for (int i = 0; i < (width*height); i++) {
            a = Color.alpha(pixs[i]);
            r = Color.red(pixs[i]);
            g = Color.green(pixs[i]);
            b = Color.blue(pixs[i]);
            r = lut_red[r];
            g = lut_green[g];
            b = lut_blue[b];
            pixs[i] = Color.argb(a, r, g, b);
        }
        bmp.setPixels(pixs, 0, width, 0, 0, width, height);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BitmapFactory bf = new BitmapFactory();
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inMutable = true;
        final Bitmap b_poivron = bf.decodeResource(getResources(), R.drawable.poivron, bfo);
        final Bitmap b_backup = b_poivron.copy(Bitmap.Config.ARGB_8888, false);
        ImageView iv_poivron = (ImageView) findViewById(R.id.iv_pic1);
        iv_poivron.setImageBitmap(b_poivron);
        TextView tv_size = (TextView) findViewById(R.id.tv_size1);
        tv_size.setText("size = " + String.valueOf(bfo.outWidth) + "x" + String.valueOf(bfo.outHeight));
        Button b_back = (Button) findViewById(R.id.b_backup1);
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                restaure(b_poivron, b_backup);
            }
        });
        Button b_greyrs = (Button) findViewById(R.id.b_grey);
        b_greyrs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toGreyRS(b_poivron);
            }
        });
        Button b_colorrs = (Button) findViewById(R.id.b_color);
        b_colorrs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorizeRS(b_poivron);
            }
        });
        Button b_grey_redrs = (Button) findViewById(R.id.b_grey_red);
        b_grey_redrs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toGreyAndRedRS(b_poivron);
            }
        });
        Button b_i_contrastde = (Button) findViewById(R.id.b_contrastde2);
        b_i_contrastde.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                increaseContrastDE(b_poivron);
            }
        });
        Button b_next = (Button) findViewById(R.id.b_act_two);
        b_next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ActivityTwo.class));
            }
        });
    }

}
