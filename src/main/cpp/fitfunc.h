#pragma once

typedef void (*fitfunc)(float, float [], float *, float [], int);

class FitFunc {
public:
	virtual void fit(float x, float param[], float *y, float dy_dparam[]) const = 0; 
	virtual ~FitFunc() {}
	fitfunc func_ptr;
	int nparam; // hide from java side
};

static void do_fit(float x, float param[], float *y, float dy_dparam[], int nparam) {
	// pass fitFunc object as an hidden parameter, anything else does not know about this
	FitFunc **fitFunc = reinterpret_cast<FitFunc **>((void *) &param[nparam]);
	(*fitFunc)->nparam = nparam;
	if ((*fitFunc)->func_ptr) {
		(*fitFunc)->func_ptr(x, param, y, dy_dparam, nparam);
	} else {
		(*fitFunc)->fit(x, param, y, dy_dparam);
	}
}
