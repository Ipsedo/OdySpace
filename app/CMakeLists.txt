# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

#add_library( triangle SHARED src/main/cpp/triangle.cpp )

add_library( collision SHARED src/main/cpp/collision.cpp )

set( glm_src_DIR ../glm )
set( glm_build_DIR ../glm/outputs )
file(MAKE_DIRECTORY ${glm_build_DIR})
add_subdirectory( ${glm_src_DIR} ${glm_build_DIR} )
add_library( lib_glm STATIC IMPORTED )
set_target_properties( lib_glm PROPERTIES IMPORTED_LOCATION ${glm_build_DIR}/${ANDROID_ABI}/lib_glm.a )
include_directories( ../glm/glm)
include_directories( ../glm/glm/detail)
include_directories( ../glm/glm/gtc)
include_directories( ../glm/glm/gtx)
include_directories( ../glm/glm/simd)
include_directories( ${glm_src_DIR}/include )
link_libraries( collision lib_glm )