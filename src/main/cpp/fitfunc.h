#pragma once

typedef void (*fitfunc)(float, float [], float *, float [], int);

class FitFunc {
public:
	virtual void fit(float x, float param[], float *y, float dy_dparam[]) const = 0; 
	virtual ~FitFunc() {}
	fitfunc func_ptr;
	int nparam; // hide from java side
};
