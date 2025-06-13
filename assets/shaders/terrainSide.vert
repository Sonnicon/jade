attribute vec4 a_position;
attribute vec2 a_positionA;
attribute vec2 a_positionB;
attribute vec2 a_positionC;
attribute vec2 a_positionD;
attribute vec2 a_texTransX;
attribute vec2 a_texTransY;

uniform mat4 u_projTrans;
uniform vec3 u_camCoord;

varying vec2 v_positionA;
varying vec2 v_positionB;
varying vec2 v_positionC;
varying vec2 v_positionD;
varying vec2 v_texTransX;
varying vec2 v_texTransY;


vec2 view_transform(in vec2 a) {
    return (u_projTrans * vec4(a, 0., 1.)).xy / 2. + .5;
}

void main() {
    gl_Position = u_projTrans * a_position;

    v_positionA = view_transform(a_positionA);
    v_positionB = view_transform(a_positionB);
    v_positionC = view_transform(a_positionC);
    v_positionD = view_transform(a_positionD);

    v_texTransX = a_texTransX;
    v_texTransY = a_texTransY;
}