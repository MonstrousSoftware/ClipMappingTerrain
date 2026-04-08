// RocketGame ubershader, compatible with attributes/uniforms from LibGDX DefaultShader
//
// for skybox

attribute vec4 a_position;

uniform mat4 u_worldTrans;
uniform mat4 u_projTrans;
uniform mat4 u_viewTrans;

varying vec3 v_texCoord;

void main() {
    v_texCoord = normalize(a_position.xyz); // for spherical mapping

    // keep only the rotational part of the view transform
    // and remove any view translation so that the skybox
    // is always centred on the camera position
    //
    mat4 view = mat4(mat3(u_viewTrans));
    gl_Position = u_projTrans * view * u_worldTrans * a_position;

    // note we don't use u_projViewTrans here but the individual project and view
    // transformations. This so we can zero out the view translations, but keep projection
    // unchanged.
}

