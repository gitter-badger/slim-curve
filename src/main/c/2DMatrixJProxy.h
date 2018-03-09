#include "Ecf.h"
#include <stdlib.h>

#pragma once

using namespace std;

class Float2DMatrix {
public:
	float **const mat_ptr;
	const int nrow;
	const int ncol;
	 Float2DMatrix(int row, int col);
	~Float2DMatrix();
	Float2DMatrix *asArray();
};
