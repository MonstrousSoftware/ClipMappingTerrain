# ClipMappingTerrain

Demo of terrain rendering using the clip mapping technique.


Based on: Asirvatham & Hoppe in GPU Gems 3
https://developer.nvidia.com/gpugems/gpugems2/part-i-geometric-complexity/chapter-2-terrain-rendering-using-gpu-based-geometry


The terrain is rendered as a set of concentric regular grids with decreasing granularity.
The y-position of each vertex is read from a height texture by the vertex shader.

Sample height map of Everest from https://manticorp.github.io/unrealheightmap.
The height map is a 16 bit grey scale png of 2048 by 2048.


## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `teavm`: Web backend that supports most JVM languages.
