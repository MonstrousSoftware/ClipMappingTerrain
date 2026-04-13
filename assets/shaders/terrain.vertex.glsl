// shader to set the colour of each face equal to its normal vector
// (in negative direction the faces are black)


// attributes of this vertex
attribute vec4 a_position;
attribute vec3 a_normal;

//uniform sampler2D u_diffuseTexture;
uniform sampler2D u_emissiveTexture;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec4 v_normal;
varying vec2 v_UV;
varying vec4 v_heightSample;

void main() {


	v_normal =  vec4(a_normal, 1);
	vec4 worldPos = u_worldTrans * a_position;

    v_UV = (worldPos.xz / (8*16128.0))-vec2(0.5);
    vec4 heightSample = texture2D(u_emissiveTexture, v_UV);
    v_heightSample = heightSample;


	//worldPos.y = 2640.0 * (heightSample.r*256.0 + heightSample.g - 0.5);
	worldPos.y = 20000.0 * (heightSample.a - 0.5);
	//worldPos.y = 8.0 * sin(worldPos.x/16.0) * cos(worldPos.z/17.0);

    //rldPos.y = 0;

   	gl_Position = u_projViewTrans * worldPos;
}
