%module(directors="1") SLIMCurve

%rename(FitFuncNative) FitFunc;

%typemap(javaclassmodifiers) FitFunc "class"
%typemap(javainterfaces) FitFunc "FitFunc"

%ignore do_fit;
%ignore FitFunc::func_ptr;
%ignore FitFunc::nparam;
%ignore GCI_multiexp_lambda;
%ignore GCI_multiexp_tau;
%ignore GCI_stretchedexp;

%inline %{
#ifndef THREAD_JNIENV
#define THREAD_JNIENV
	// jenv corresponded to the current thread
	thread_local JNIEnv *t_jenv;
#endif
#include "fitfunc.h"
thread_local FitFunc *t_fitfunc;

static void do_fit(float x, float param[], float *y, float dy_dparam[], int nparam) {
	if (t_fitfunc->func_ptr) {
		t_fitfunc->func_ptr(x, param, y, dy_dparam, nparam);
	} else {
		t_fitfunc->fit(x, param, y, dy_dparam);
	}
}
//typedef enum { GCI_MULTIEXP_LAMBDA, GCI_MULTIEXP_TAU, GCI_STRETCHEDEXP } fit_funcs;
%}

///////////////////////////////////////////////////////////////////////////////

// Conversion: FitFunc(J) -> void (*fitfunc)(float, float [], float *, float [], int)(C) in arguments
%define fFunction
void (*fitfunc)(float, float [], float *, float [], int)
%enddef
%typemap(jstype) fFunction "FitFunc"
%typemap(javain,pgcppname="n", pre="    FitFuncNative n = makeNative($javainput);")
		fFunction  "FitFuncNative.getCPtr(n)"
%typemap(jtype) fFunction "long"
%typemap(jni) fFunction "jlong"
%typemap(in) fFunction {
	t_jenv = jenv;
	$1 = &do_fit;
}

// maps that copies param[] etc to and from java arguments before and after jni call
%typemap(directorin,descriptor="[F") float[] %{
	jenv->SetFloatArrayRegion($input, 0, 1, $1);
%}
%typemap(directorargout) float* %{
	jenv->GetFloatArrayRegion($input, 0, 1, $1);
%}
%typemap(directorin,descriptor="[F") float[] %{
	// length is hidden from java (set before function call in do_fit)
	$input = jenv->NewFloatArray(this->nparam);
	jenv->SetFloatArrayRegion($input, 0, this->nparam, $1);
%}
%typemap(directorargout) float[] {
	jsize sz = jenv->GetArrayLength($input);
	jenv->GetFloatArrayRegion($input, 0, this->nparam, $1);
}

%feature("director") FitFunc;

ARRMAP(FLTARRIN, 1, 0, float, Float, JNI_ABORT, false)

%apply float *OUTPUT { float * y };
%apply FLTARRIN { float param[], float dy_dparam[] };

%include "../cpp/fitfunc.h"

%typemap(javacode) FitFuncNative %{
	public static final FitFunc GCI_MULTIEXP_LAMBDA = null;
	public static final FitFunc GCI_MULTIEXP_TAU = null;
	public static final FitFunc GCI_STRETCHEDEXP = null;

	private static class FitFuncNativeProxy extends FitFuncNative {
		private FitFunc delegate;
		public FitFuncNativeProxy(FitFunc i) {
			delegate = i;
		}

		public void fit(float x, float[] param, float[] y, float[] dy_dparam, int nparam) {
			return delegate.fit(x, param, y, dy_dparam, nparam);
		}
	}

  // (2.5)
	private static FitFuncNative makeNative(FitFunc i) {
		if (i instanceof FitFuncNative) {
      // If it already *is* a FitFuncNative don't bother wrapping it again
			return (FitFuncNative)i;
		}
		return new FitFuncNativeProxy(i);
	}
%}
