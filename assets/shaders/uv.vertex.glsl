// shader to set the colour of each face equal to its normal vector
// (in negative direction the faces are black)


// attributes of this vertex
attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;


uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec4 v_normal;
varying vec2 v_texCoords;

void main() {
	v_normal =  vec4(a_normal, 1);
    v_texCoords    = a_texCoord0;
   	gl_Position = u_projViewTrans * u_worldTrans * a_position;
}