// RocketGame ubershader, compatible with attributes/uniforms from LibGDX DefaultShader
//
// skybox shader using cube map
//

#ifdef GL_ES 
precision mediump float;
#endif


uniform samplerCube u_environmentCubemap;

varying vec3 v_texCoord;

void main() {
	gl_FragColor = vec4(textureCube(u_environmentCubemap, v_texCoord).rgb, 1.0);
}
