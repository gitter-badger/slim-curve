%module(directors="1") SLIMCurve

%rename(FitFuncNative) FitFunc;

%typemap(javaclassmodifiers) FitFunc "class"
%typemap(javainterfaces) FitFunc "FitFunc"

%{
#include "fitfunc.h"
//typedef enum { GCI_MULTIEXP_LAMBDA, GCI_MULTIEXP_TAU, GCI_STRETCHEDEXP } fit_funcs;
%}

/*// Conversion: FitFunc(J) -> void (*fitfunc)(float, float [], float *, float [], int)(C) in arguments
%define fFunction
void (*fitfunc)(float, float [], float *, float [], int)
%enddef
%typemap(jstype) fFunction "FitFunc"
%typemap(javain) fFunction "$javainput.swigValue()"
%typemap(jtype) fFunction "int"
%typemap(jni) fFunction "jint"
%typemap(in) fFunction {
	switch($input) {
	case GCI_MULTIEXP_LAMBDA:
		$1 = GCI_multiexp_lambda;
		break;
	case GCI_MULTIEXP_TAU:
		$1 = GCI_multiexp_tau;
		break;
	case GCI_STRETCHEDEXP:
		$1 = GCI_stretchedexp;
		break;
	}
}*/

%ignore GCI_multiexp_lambda;
%ignore GCI_multiexp_tau;
%ignore GCI_stretchedexp;

//typedef enum { GCI_MULTIEXP_LAMBDA, GCI_MULTIEXP_TAU, GCI_STRETCHEDEXP } fit_funcs;

///////////////////////////////////////////////////////////////////////////////

// Conversion: FitFunc(J) -> void (*fitfunc)(float, float [], float *, float [], int)(C) in arguments
%define fFunction
void (*fitfunc)(float, float [], float *, float [], int)
%enddef
%typemap(jstype) fFunction "FitFunc"
%typemap(javain) fFunction "$javainput.getCPtr($javainput)"
%typemap(jtype) fFunction "long"
%typemap(jni) fFunction "jlong"
%typemap(in) fFunction {
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

%ignore do_fit;
%ignore FitFunc::func_ptr;
%ignore FitFunc::nparam;
%include "../cpp/fitfunc.h"

/*%typemap(javain,pgcppname="n",
         pre="    FitFuncNative n = makeNative($javainput);")
        const Interface&  "FitFuncNative.getCPtr(n)"
*/
%typemap(javacode) FitFuncNative %{
	private static class FitFuncNativeProxy extends FitFuncNative {
		private Interface delegate;
		public FitFuncNativeProxy(Interface i) {
			delegate = i;
		}

		public String foo() {
			return delegate.foo();
		}
	}

  // (2.5)
	private static FitFuncNative makeNative(Interface i) {
		if (i instanceof FitFuncNative) {
      // If it already *is* a FitFuncNative don't bother wrapping it again
			return (FitFuncNative)i;
		}
		return new FitFuncNativeProxy(i);
	}
%}
