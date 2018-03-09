#include "2DMatrixJProxy.h"

Float2DMatrix::Float2DMatrix(int row, int col) 
	: mat_ptr(GCI_ecf_matrix(row, col)), nrow(row), ncol(col) { }

Float2DMatrix::~Float2DMatrix() {
	for (int i = 0; i < this->nrow; i++) {
		free(this->mat_ptr[i]);
	}
	free(this->mat_ptr);
}

Float2DMatrix *Float2DMatrix::asArray() {
	return this;
}
