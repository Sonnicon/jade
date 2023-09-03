#define SUBTILE_DELTA 8.
#define TICK_SIZE_DIV 4.
#define TICK_CURVINESS 100.

uniform mat4 u_projTrans;
uniform vec2 u_resolution;
uniform vec2 u_cursor;

varying vec2 v_position;

float iDistance(vec2 a, vec2 b) {
    float xd = abs(a.x - b.x);
    float yd = abs(a.y - b.y);
    return xd + yd + xd * yd * TICK_CURVINESS;
}

void main() {
    float resRatio = u_resolution.x / u_resolution.y;
    // Distance between each tick
    vec2 spacing = (u_projTrans * vec4(SUBTILE_DELTA, SUBTILE_DELTA, 0., 0.)).xy;
    // Local position between ticks
    vec2 pos = mod((u_projTrans * vec4(v_position, 0., 0.)).xy + spacing / 2., spacing);
    pos.x *= resRatio;
    // Center of nearest tick
    vec2 spacedCenter = vec2(resRatio * spacing.x / 2., spacing.y / 2.);
    // Warped distance to tick
    float dist = iDistance(pos, spacedCenter) / (spacing.x * resRatio / TICK_SIZE_DIV);
    if (dist < 1.) {
        float cursorDistance = distance(gl_FragCoord.xy, u_cursor) / length(u_resolution / 2.);
        float alpha = pow(.1, dist) / (8. * max(pow(cursorDistance * .8 / spacing.x, 1.8), 1.));
        gl_FragColor = vec4(1., 1., 1., alpha);
    } else {
        gl_FragColor = vec4(0.);
    }
}