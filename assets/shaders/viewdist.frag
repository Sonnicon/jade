uniform float u_radius;

void main() {
    float dist = 1. - distance(gl_FragCoord.xy, vec2(u_radius)) / u_radius;
    float value = smoothstep(0., .2, dist);
    gl_FragColor = vec4(0., 0., 0., 1. - value);
}