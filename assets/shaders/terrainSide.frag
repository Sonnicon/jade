varying vec2 v_positionA;
varying vec2 v_positionB;
varying vec2 v_positionC;
varying vec2 v_positionD;
varying vec2 v_texTransX;
varying vec2 v_texTransY;

uniform sampler2D u_texture;
uniform vec2 u_resolution;

vec2 reinterpolate(in vec2 p, in vec2 a, in vec2 b, in vec2 c, in vec2 d) {
    float y = (p.y - a.y) / (b.y - a.y);
    float i = (p.y - a.y) / ((b.y - a.y) / (b.x - a.x)) + a.x;
    float j = (p.y - d.y) / ((c.y - d.y) / (c.x - d.x)) + d.x;
    float x = (p.x - i) / (j - i);
    return vec2(x, y);
}

void main() {
    vec2 p = gl_FragCoord.xy / u_resolution;

    vec2 uv;
    if (abs(v_positionA.y - v_positionD.y) < 0.001) {
        uv = reinterpolate(p, v_positionA, v_positionB, v_positionC, v_positionD);
    } else {
        uv = reinterpolate(
            vec2(-p.y, p.x),
            vec2(-v_positionA.y, v_positionA.x),
            vec2(-v_positionB.y, v_positionB.x),
            vec2(-v_positionC.y, v_positionC.x),
            vec2(-v_positionD.y, v_positionD.x)
        );
    }

    vec2 uvt = vec2(uv.x * v_texTransX.x + v_texTransX.y, uv.y * v_texTransY.x + v_texTransY.y);
    gl_FragColor = texture2D(u_texture, uvt);
}