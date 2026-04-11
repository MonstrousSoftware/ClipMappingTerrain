// shader to set the colour of each face equal to its normal vector
// (in negative direction the faces are black)


// attributes of this vertex
attribute vec4 a_position;
attribute vec3 a_normal;

uniform sampler2D u_diffuseTexture;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec4 v_normal;
varying vec2 v_UV;

void main() {


	v_normal =  vec4(a_normal, 1);
	vec4 worldPos = u_worldTrans * a_position;

    v_UV = (worldPos.xz / 25600.0)-vec2(0.5);
    vec4 diffuse = texture2D(u_diffuseTexture, v_UV);


	worldPos.y = 8192.0 * (diffuse.r - 0.5);
	//worldPos.y += 8.0 * sin(worldPos.x/16.0) * cos(worldPos.z/17.0);

   	gl_Position = u_projViewTrans * worldPos;
}
