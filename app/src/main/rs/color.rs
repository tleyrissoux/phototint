#pragma version (1)
#pragma rs java_package_name(com.example.tom.phototint)

float hue; //hue is set by the java code

uchar4 RS_KERNEL colorize(uchar4 in) {
    float r = in.r/255.0f; //converting in float
    float g = in.g/255.0f;
    float b = in.b/255.0f;
    float max_rgb = max(r, max(g, b)); //maximum intensity of the rgb colors
    float min_rgb = min(r, min(g, b)); //minimum
    float delta_rgb = max_rgb - min_rgb;
    float s; //for saturation
    if (max_rgb == 0.0f) {
        s = 0.0f;
    }
    else {
        s = delta_rgb / max_rgb;
    }
    float v = max_rgb; //for value (or lightness)
    float c = v*s; //c, x, m : three floats to compute the new r, g and b
    float h = hue/60.0f;
    h = fmod(h, 2.0f) - 1.0f;
    h = fabs(h);
    float x = c*(1.0f - h);
    float m = v - c;
    if (hue < 60.0f) { //r, g and b depend on hue
        r = c;
        g = x;
        b = 0.0f;
    }
    else {
        if (hue < 120.0f) {
            r = x;
            g = c;
            b = 0.0f;
        }
        else {
            if (hue < 180.0f) {
                r = 0.0f;
                g = c;
                b = x;
            }
            else {
                if (hue < 240.0f) {
                    r = 0.0f;
                    g = x;
                    b = c;
                }
                else {
                    if (hue < 300.0f) {
                        r = x;
                        g = 0.0f;
                        b = c;
                    }
                    else {
                        r = c;
                        g = 0.0f;
                        b = x;
                    }
                }
            }
        }
    }
    in.r = (uchar) ((r + m)*255.0f); //converting in uchar
    in.g = (uchar) ((g + m)*255.0f);
    in.b = (uchar) ((b + m)*255.0f);
    return in;
}
