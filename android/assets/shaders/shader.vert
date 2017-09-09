

attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
//uniform vec3 u_quake;

varying vec4 v_color;
varying vec2 v_texCoord0;


void main() {
	gl_Position = u_projTrans * vec4(a_position/* + u_quake*/, 1.0);
	v_color = a_color;
	v_texCoord0 = a_texCoord0;
}
