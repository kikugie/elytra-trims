#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:projection.glsl>

//? if >=1.21.6 {
#moj_import <minecraft:dynamictransforms.glsl>
//?} else {
/*uniform mat4 ModelViewMat;
uniform mat4 TextureMat;
uniform mat4 ProjMat;
uniform int FogShape;
*///?}

in vec3 Position;
in vec2 UV0;

out vec4 texProj0;
out vec2 texCoord0;

//? if >=1.21.6 {
out float sphericalVertexDistance;
out float cylindricalVertexDistance;
//?} else
/*out float vertexDistance;*/

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    texProj0 = projection_from_position(gl_Position);
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    //? if >=1.21.6 {
    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    //?} else
    /*vertexDistance = fog_distance(Position, FogShape);*/
}
