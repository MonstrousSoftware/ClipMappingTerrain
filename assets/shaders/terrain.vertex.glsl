// shader to set the colour of each face equal to its normal vector
// (in negative direction the faces are black)


// attributes of this vertex
attribute vec4 a_position;


uniform sampler2D u_emissiveTexture;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_cameraPosition;

varying vec2 v_UV;
varying float v_fog;

void main() {


	//v_normal =  vec4(a_normal, 1);
	vec4 worldPos = u_worldTrans * a_position;

    v_UV = (worldPos.xz / (32512.0*4.0))+vec2(0.5);
    float heightSample = (v_UV.x < 0.0 || v_UV.x > 1.0 || v_UV.y < 0.0 || v_UV.y > 1.0) ? 0.0 : texture2D(u_emissiveTexture, v_UV).a;


	worldPos.y = 20000.0 * (heightSample - 0.5);
	//worldPos.y = 8.0 * sin(worldPos.x/3.0) * cos(worldPos.z/2.0);


    vec3 flen = u_cameraPosition.xyz - worldPos.xyz;
    float fog = dot(flen, flen) * u_cameraPosition.w;
    v_fog = min(fog, 1.0);

   	gl_Position = u_projViewTrans * worldPos;
}
