#pragma version (1)
#pragma rs java_package_name(com.example.tom.phototint)

static const float4 weight = {0.299f, 0.587f, 0.114f, 0.0f};

uchar4 RS_KERNEL toGreyAndRed(uchar4 in) {
    if (in.r < 125 || in.g > 125 || in.b > 125) {
        const float4 pixelf = rsUnpackColor8888(in);
        const float grey = dot(pixelf, weight);
        return rsPackColorTo8888(grey, grey, grey, pixelf.a);
    }
    else { //no change for the pixel if it is straight red
        return in;
    }
}
