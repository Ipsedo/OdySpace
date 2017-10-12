uniform mat4 u_MVPMatrix;
uniform vec3 u_Color;
attribute vec3 v_Position;

void main() {
    gl_Position = u_MVPMatrix * vec4(v_Position, 1.0);
}
