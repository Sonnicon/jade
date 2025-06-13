attribute vec4 a_position;
attribute vec2 a_texCoord;

uniform mat4 u_projTrans;
uniform vec3 u_camCoord;

varying vec2 v_texCoord;
varying vec2 v_fowCoord;

void main() {
    v_texCoord = a_texCoord;

    vec2 diff = (a_position.xy - u_camCoord.xy) * 1.2 + u_camCoord.xy;
    gl_Position = u_projTrans * vec4(diff, a_position.ba);
    v_fowCoord = (u_projTrans * a_position).xy / 2. + .5;
}