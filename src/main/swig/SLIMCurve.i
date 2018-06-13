%module(directors="1") SLIMCurve

%{
#include <algorithm>
#include <iostream>
#include "Ecf.h"
#include "EcfGlobal.h"
#include "EcfWrapper.h"
#include "GCI_Phasor.h"
#define PKG_NAME "slim"
%}

// custom typemaps
%include "SLIMCurve_1DArray.i" // arrays (with length parameter)
%include "SLIMCurve_2DMatrix.i" // 2D arrays (with/out length parameter)
%include "SLIMCurve_FittingFunc_n.i" // fitting function pointer
%include "SLIMCurve_DMSPVAF.i" // struct used by mode selection engine
%include "SLIMCurve_Enums.i" // all of the enums
%include "SLIMCurve_ProgressFunc.i" // progress function for SPA

%javaconst(1);
// rename enums to meet java naming conventions
ENUMMAP(FITTYPEENUM, fit_type, FitType);
%rename(NoiseType) noise_type;
%rename(RestrainType) restrain_type;

// input 1d array (with length) maps
ARRMAP(INTARRIN_LEN, 1, 1, int, Int, JNI_ABORT, false)
ARRMAP(FLTARRIN_LEN, 1, 1, float, Float, JNI_ABORT, false)
ARRMAP(FLTPTRIN_LEN, 0, 1, float, Float, JNI_ABORT, false)
ARRMAP(FLTARRIN_NUL, 1, 1, float, Float, JNI_ABORT, true)
ARRMAP(DBLARRIN_NUL, 1, 1, double, Double, JNI_ABORT, true)

// input 2d array maps
MATMAP(F2D_in, float, Float, F, Float2DMatrix)
MATMAP(I2D_in, int, Int, I, Int2DMatrix)

// Tell swig to use corresponding typemaps (OUTPUT defined in typemaps.i)
%apply int *INOUT { int* };
%apply float *INOUT { float *Z,  float *A, float *tau, float *residuals, float *fitted };
%apply float *OUTPUT { float * };
%apply double *INOUT { double * };
%apply FITTYPEENUM { int ftype };
%apply F2D_in {
	(float **trans, int ndata, int ntrans)
}
%apply INTARRIN_LEN {
	(int paramfree[], int nparam),
	(int paramfree[], int nparamfree),
	(int param_free[], int n_param)
}
%apply FLTARRIN_LEN {
	(float params[], int nparam),
	(float y[], int ndata)
}
%apply FLTPTRIN_LEN {
	(float *trans, int ndata)
}
%apply FLTARRIN_NUL {
	(float instr[], int ninstr)
}
%apply DBLARRIN_NUL {
	(double instr[], int n_instr)
}

// Grab functions from header files as class methods
%include "../c/Ecf.h"
%include "../c/EcfGlobal.h"
%include "../c/EcfWrapper.h"
%include "../c/GCI_Phasor.h"

%pragma(java) jniclassimports=%{
  import org.scijava.nativelib.NativeLoader;
  import java.io.IOException;
%}

%pragma(java) jniclasscode=%{
	static {
		try {
			NativeLoader.loadLibrary("slim-curve");
			NativeLoader.loadLibrary("slim-curve-jni");
		} catch (IOException e) {
			System.err.println("Failed to load library:\n" + e.getMessage());
			System.exit(1);
		}
	}
%}
