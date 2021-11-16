#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

//based off: https://www.youtube.com/watch?v=rvDo9LvfoVE

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_cursor;
uniform vec2 u_resolution;

float Hash21(vec2 p) {
    p = fract(p * vec2(123.45, 456.21));
    p += dot(p, p + 45.67);
    return fract(p.x * p.y);
}

float Star(vec2 centerScreenPos, float flare) {
    float size = 0.05;
    float dist = length(centerScreenPos);
    float color = size / dist;

    float rays = max(0.0, 1.0 - abs(centerScreenPos.x * centerScreenPos.y * 3000.0)); //gradient from 0-1 textcords(edge to edge)
    color += rays;
    color *= smoothstep(0.5, 0.1, dist);//keep within cell
    return color;
}

void main() {
    float starDensity = 20.0;
    vec3 fragColor = vec3(0, 0, 0);

    // pixel coord relative to center of screen
    vec2 screenCoord = (gl_FragCoord.xy / u_resolution.xy) - 0.5;
    screenCoord *= starDensity;

    vec2 cellCoord = fract(screenCoord) - 0.5;
    vec2 cellID = floor(screenCoord);

    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 neighborOffset = vec2(x, y);

            //random offset within cell
            float shiftX = Hash21(cellID + neighborOffset); // rand 0 - 1
            float shiftY = fract(shiftX * 35.0);
            vec2 offset = vec2(shiftX, shiftY) - 0.5;

            fragColor += Star(cellCoord - neighborOffset - offset, 1.0);

            //raw center cell star
            //fragColor += Star(cellCoord, 1.0) / 9.0;
        }
    }


    //debug show border
    //if (cellCoord.x>0.48 || cellCoord.y>0.48) fragColor.r = 1.0;

    //fragColor.rg += cellID;
    //fragColor.rg = u_resolution.xy;

    gl_FragColor = vec4(fragColor, 1.0);
}