#pragma version (1)
#pragma rs java_package_name(com.example.tom.phototint)

static const float4 weight = {0.299f, 0.587f, 0.114f, 0.0f};

uchar4 RS_KERNEL toGrey(uchar4 in) {
    const float4 pixelf = rsUnpackColor8888(in);
    const float grey = dot(pixelf, weight); //multiplying the vector pixelf and the vector weight to get the greyscale
    return rsPackColorTo8888(grey, grey, grey, pixelf.a);
}
