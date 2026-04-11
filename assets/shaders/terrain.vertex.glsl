// shader to set the colour of each face equal to its normal vector
// (in negative direction the faces are black)


// attributes of this vertex
attribute vec4 a_position;
attribute vec3 a_normal;


uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec4 v_normal;

void main() {
	v_normal =  vec4(a_normal, 1);
	vec4 worldPos = u_worldTrans * a_position;
	worldPos.y = 32.0 * sin(worldPos.x/32.0);
   	gl_Position = u_projViewTrans * worldPos;
}
