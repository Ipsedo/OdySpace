// @author samuel
// Created by samuel on 13/06/17.
//


#define CROSS(dest, v1, v2)                     \
               dest[0]=v1[1]*v2[2]-v1[2]*v2[1]; \
               dest[1]=v1[2]*v2[0]-v1[0]*v2[2]; \
               dest[2]=v1[0]*v2[1]-v1[1]*v2[0];



void computeP0(float* p0, float n1[3], float q1[3], float n2[3], float r1[3]) {
    p0[0] = (n1[0] * q1[0] * n2[1] + n1[1] * (q1[1] * n2[1] - n2[0] * r1[1] - n2[1] * r1[1] - n2[2] * r1[2] + n2[2]) + n1[2] * (q1[2] - 1) * n2[1]) / (n1[0] * n2[1] - n2[1] * n2[0]);
    p0[1] = (n1[0] * (q1[0] * n2[0] - n2[0] * r1[0] - n2[1] * r1[1] - n2[2] * r1[2] + n2[2]) + n1[1] * q1[1] * n2[0] + n1[2] * (q1[2] - 1) * n2[0]) / (n1[1] * n2[0] - n1[0] * n2[1]);
    p0[2] = 1f;
}

