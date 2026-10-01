package com.bitbytestudio.fluidbackground.Shader

const val FLUID_SHADER = """
uniform float2 resolution;
uniform float time;
uniform float intensity;

uniform color color1;
uniform color color2;
uniform color color3;
uniform color color4;

// ---------------------------------------------------------
// Smooth Hash
// ---------------------------------------------------------
float hash21(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

// ---------------------------------------------------------
// Smooth Value Noise
// ---------------------------------------------------------
float noise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float a = hash21(i);
    float b = hash21(i + float2(1.0, 0.0));
    float c = hash21(i + float2(0.0, 1.0));
    float d = hash21(i + float2(1.0, 1.0));

    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// ---------------------------------------------------------
// 2D Rotation Matrix
// ---------------------------------------------------------
float2 rotate2D(float2 p, float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return float2(p.x * c - p.y * s, p.x * s + p.y * c);
}

// ---------------------------------------------------------
// Curl Noise Field (Divergence-free physical fluid vortex)
// ---------------------------------------------------------
float2 curlNoise(float2 p) {
    float eps = 0.015;
    float n1 = noise(p + float2(0.0, eps));
    float n2 = noise(p - float2(0.0, eps));
    float n3 = noise(p + float2(eps, 0.0));
    float n4 = noise(p - float2(eps, 0.0));

    float dx = (n3 - n4) / (2.0 * eps);
    float dy = (n1 - n2) / (2.0 * eps);

    return float2(-dy, dx);
}

// ---------------------------------------------------------
// Multi-Octave Rotational Fluid Field
// ---------------------------------------------------------
float fluidFBM(float2 p, float t, float intens) {
    float value = 0.0;
    float amp = 0.5;
    float freq = 1.0;

    for (int i = 0; i < 3; i++) {
        float2 curl = curlNoise(p * freq + float2(t * 0.12, -t * 0.08));
        p += curl * 0.38 * intens;
        value += noise(p * freq) * amp;
        freq *= 2.1;
        amp *= 0.48;
        p = rotate2D(p, 0.6);
    }
    return value;
}

// ---------------------------------------------------------
// Main Strong Fluid Shader
// ---------------------------------------------------------
half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution.xy;
    float aspect = resolution.x / resolution.y;
    float2 p = uv;
    p.x *= aspect;

    float t = time * 0.45;

    // Primary & Secondary Fluid Vortices (Domain Warping)
    float2 curl1 = curlNoise(p * 1.4 + float2(t * 0.18, t * 0.12));
    float2 q = p + curl1 * 0.45 * intensity;

    float2 curl2 = curlNoise(q * 2.2 - float2(t * 0.14, t * 0.22));
    float2 r = q + curl2 * 0.35 * intensity;

    // Fluid Density Field
    float density1 = fluidFBM(r * 1.2, t, intensity);
    float density2 = fluidFBM(r * 2.2 + float2(1.8, 2.5), t * 1.1, intensity);

    float fluid = mix(density1, density2, 0.5);
    float strongFluid = smoothstep(0.12, 0.88, fluid);

    // 3D Liquid Specular Sheen / Surface Caustics
    float eps = 0.006;
    float dX = fluidFBM(r * 1.2 + float2(eps, 0.0), t, intensity) - fluidFBM(r * 1.2 - float2(eps, 0.0), t, intensity);
    float dY = fluidFBM(r * 1.2 + float2(0.0, eps), t, intensity) - fluidFBM(r * 1.2 - float2(0.0, eps), t, intensity);
    
    float3 normal = normalize(float3(-dX, -dY, 0.12));
    float3 lightDir = normalize(float3(0.5, 0.8, 1.0));
    float specular = pow(max(dot(normal, lightDir), 0.0), 10.0) * 0.50 * intensity;

    // Rich Liquid Color Palette
    half3 c1 = color1.rgb;
    half3 c2 = color2.rgb;
    half3 c3 = color3.rgb;
    half3 c4 = color4.rgb;

    // Multi-layer Vibrant Color Interpolation
    half3 fluidColor = mix(c1, c2, smoothstep(0.05, 0.45, strongFluid));
    fluidColor = mix(fluidColor, c3, smoothstep(0.35, 0.75, strongFluid));

    // Dynamic Swirl Wave Highlights
    float vortexWave = sin(length(r - float2(aspect * 0.5, 0.5)) * 9.0 - t * 2.2) * 0.5 + 0.5;
    fluidColor = mix(fluidColor, c4, vortexWave * 0.45 * strongFluid);

    // Add Specular Liquid Reflection
    fluidColor += half3(specular);

    // Subtle Vignette
    float2 centered = uv - 0.5;
    float vignette = 1.0 - dot(centered, centered) * 0.35;
    fluidColor *= vignette;

    // Dither to eliminate color banding
    float dither = (hash21(fragCoord + float2(time, time * 1.5)) - 0.5) * (1.0 / 128.0);
    fluidColor += half3(dither);

    return half4(fluidColor, 1.0);
}
"""
