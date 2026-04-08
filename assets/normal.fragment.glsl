#ifdef GL_ES 
precision mediump float;
#endif


varying vec4 v_normal;

void main() {
	gl_FragColor = v_normal;
}