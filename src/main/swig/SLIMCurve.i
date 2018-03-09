%module SLIMCurve

%include "enums.swg"
%include "arrays_java.i" // for mapping arrays
%include "typemaps.i" // for mapping pointers

%{
#include "Ecf.h"
#include "EcfGlobal.h"
#include "EcfWrapper.h"
#include "2DMatrixJProxy.h"
%}

%javaconst(1);
// rename enums to meet java naming conventions
%rename(NoiseType) noise_type;
%rename(RestrainType) restrain_type;
%rename(FitType) fit_type;

// Tell swig to treat these pointers (EcfWrapper.h) as java arrays instead of pointer classes
%apply double *OUTPUT { double *z, double *a, double *tau, double *chi_square };

/* Conversion from Float2DMatrix to float** in arguments */
%typemap(jstype) float** "loci.slim.Float2DMatrix"
%typemap(javain) float** "$javainput.getCPtr($javainput)"
%typemap(jtype) float** "long"
%typemap(jni) float** "jlong"
%typemap(in) float** {
    $1 = ((Float2DMatrix*)$input)->mat_ptr;
}

/* Conversion from Float2DMatrix back into float[][] in Float2DMatrix::asArray */
/*%define Float2DMatrix *Float2DMatrix::asArray asArrFunc %enddef*/
%typemap(jstype) Float2DMatrix *Float2DMatrix::asArray "float[][]"
%typemap(javaout) Float2DMatrix *Float2DMatrix::asArray {return $jnicall;}
%typemap(jtype) Float2DMatrix *Float2DMatrix::asArray "float[][]"
%typemap(jni) Float2DMatrix *Float2DMatrix::asArray "jobjectArray"
%typemap(out) Float2DMatrix *Float2DMatrix::asArray {
    // $1 is the return value (this); the map
    int row = $1->nrow;
    int col = $1->ncol;
    float **data = $1->mat_ptr;
    jclass F2MClass = JCALL1(FindClass, jenv, "loci.slim.Float2DMatrix");
    $result = JCALL3(NewObjectArray, jenv, row, F2MClass, NULL);
    for (int i = 0; i < row; i++) {
        jfloatArray arow = JCALL1(NewFloatArray, jenv, col);
        JCALL4(SetFloatArrayRegion, jenv, arow, 0, col, data[i]);
        JCALL3(SetObjectArrayElement, jenv, $result, i, arow);
    }
}

// Grab functions from header files as class methods
%include "../c/2DMatrixJProxy.h"
%include "../c/Ecf.h"
%include "../c/EcfWrapper.h"

// Use FloatMatrix instead
%ignore GCI_ecf_matrix;
%ignore GCI_ecf_free_matrix;

// Add an additional methods
/*%extend Float2DMatrix {
    Float2DMatrix *asArray() {
        return this;
    }
}*/

/* Functions from Ecf.h to be used as function pointers */
%javaconst(0); // Set back to 0 for function pointer constants
%constant void (*GCI_MULTIEXP_LAMBDA)(float, float[],float*,float[],int)= GCI_multiexp_lambda;
%constant void (*GCI_MULTIEXP_TAU)(float, float[],float*,float[],int)= GCI_multiexp_tau;
%constant void (*GCI_STRETCHEDEXP)(float, float[],float*,float[],int)= GCI_stretchedexp;

/* Custom typemaps */
/*%typemap(jni) fitfunc "jobject"
%typemap(jtype) fitfunc "loci.slim.FittingFunction"
%typemap(jstype) fitfunc "loci.slim.FittingFunction"
%typemap(javain) fitfunc "$javainput"
%typemap(in) fitfunc {    
    FittingFunction* func = dynamic_cast<FittingFunction *>($input)
    $result = &func->func;
}*/

/*Functions in EcfGlobal */
extern int GCI_marquardt_global_generic_instr(float xincr, float **trans,
                    int ndata, int ntrans, int fit_start, int fit_end,
                    float instr[], int ninstr,
                    noise_type noise, float sig[],
                    float **param, int paramfree[], int nparam, int gparam[],
                    restrain_type restrain, float chisq_delta,
                    void (*fitfunc)(float, float [], float *, float [], int),
                    float **fitted, float **residuals,
                    float chisq_trans[], float *OUTPUT, int *OUTPUT);

extern int GCI_marquardt_global_exps_instr(float xincr, float **INPUT,
                     int ndata, int ntrans, int fit_start, int fit_end,
                     float instr[], int ninstr,
                     noise_type noise, float sig[], int ftype,
                     float **INPUT, int paramfree[], int nparam,
                     restrain_type restrain, float chisq_delta,
                     float **INPUT, float **INPUT,
                     float chisq_trans[], float *OUTPUT, int *OUTPUT,
                     int drop_bad_transients);
   

%pragma(java) jniclasscode=%{
static {
    try {
        System.loadLibrary("slim-curve");
        System.loadLibrary("slim-curve-java");
    } catch (UnsatisfiedLinkError e) {
        System.err.println("Native library failed to load. Exiting.\n" + e);
        System.exit(1);
    }
}
%}


/* Custom typemaps */
/*%typemap(jni) float** "jobjectArray"
%typemap(jtype) float** "float[][]"
%typemap(jstype) float** "float[][]"
%typemap(javain) float** "$javainput"
%typemap(in) float** {
    int dim0 = (*jenv)->GetArrayLength(jenv, $input);
    jfloatArray floatRow = (*jenv)->GetObjectArrayElement(jenv, $input, 0);
    // assume rectangular array, else double pointer does not make sense in C
    int dim1 = (*jenv)->GetArrayLength(jenv, floatRow);
    // $1 is the resulting array
    $1 = (float **) malloc(dim0 * sizeof(float *));
    for (int i = 0; i < dim0; i++) {
        floatRow = (*jenv)->GetObjectArrayElement(jenv, $input, i);
        jfloat* elements = (*jenv)->GetFloatArrayElements(jenv, floatRow, 0);
        $1[i] = malloc(dim1 * sizeof(float));
        for (int j = 0; j < dim1; j++)
            $1[i][j] = elements[j];
    }
}
%typemap(out) float** {
    
}*/
