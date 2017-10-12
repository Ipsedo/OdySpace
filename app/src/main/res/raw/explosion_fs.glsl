precision mediump float;

uniform mat4 u_MVPMatrix;
uniform vec3 u_Color;

void main() {
    gl_FragColor = vec4(u_Color, 0.4);
}
