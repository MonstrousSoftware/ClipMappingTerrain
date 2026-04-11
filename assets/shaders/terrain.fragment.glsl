#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;

varying vec4 v_normal;
varying vec2 v_UV;

void main() {
    vec4 diffuse = texture2D(u_diffuseTexture, v_UV);

	gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
}
