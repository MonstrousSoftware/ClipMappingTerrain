#ifdef GL_ES 
precision mediump float;
#endif


varying vec4 v_normal;
varying vec2 v_texCoords;

void main() {
	gl_FragColor = vec4(v_texCoords, 0, 1);
}