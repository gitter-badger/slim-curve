%module cLibrary

%include "arrays_java.i"
%include "typemaps.i"
%include "enums.swg"

%{
#include "GCI_Phasor.h"
#include "Ecf.h"
#include "EcfWrapper.h"
#include "EcfGlobal.h"
%}

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

%javaconst(1); // wraps enums
#define PHASOR_ERR_NO_ERROR                         0
#define PHASOR_ERR_INVALID_DATA                    -1
#define PHASOR_ERR_INVALID_WINDOW                  -2
#define PHASOR_ERR_INVALID_MODEL                   -3
#define PHASOR_ERR_FUNCTIONALITY_NOT_SUPPORTED     -4
%rename(NoiseType) noise_type; // java naming convention
typedef enum { NOISE_CONST, NOISE_GIVEN, NOISE_POISSON_DATA,
            NOISE_POISSON_FIT, NOISE_GAUSSIAN_FIT, NOISE_MLE } noise_type;
%rename(RestrainType) restrain_type;
typedef enum { ECF_RESTRAIN_DEFAULT, ECF_RESTRAIN_USER } restrain_type;

/* Functions from Ecf.h to be used as function pointers */
%javaconst(0); // Set back to 0 for function pointer constants
%constant void (*GCI_MULTIEXP_LAMBDA)(float, float[],float*,float[],int)= GCI_multiexp_lambda;
%constant void (*GCI_MULTIEXP_TAU)(float, float[],float*,float[],int)= GCI_multiexp_tau;
%constant void (*GCI_STRETCHEDEXP)(float, float[],float*,float[],int)= GCI_stretchedexp;

/* Custom typemaps */
%typemap(jni) float ** "jobjectArray"
%typemap(jtype) float** "float[]"
%typemap(jstype) float** "float[]"
%typemap(javain) float** "$javainput"

%typemap(in) float** (jint size) {    
    int i = 0;
    size = (*jenv)->GetArrayLength(jenv, $input);
    $1 = (float **) malloc((size+1)*sizeof(float *));
    jfloat *j_float = (*jenv)->GetFloatArrayElements(jenv, $input, 0);
    for (i = 0; i<size; i++) {
        $1[i] = malloc(sizeof(float));
        $1[i] = &j_float[i];
    }
}

/* Functions from EcfWrapper.h */
// Tell swig to treat these pointers as java arrays instead of pointer classes
%apply double *OUTPUT { double *z, double *a, double *tau, double *chi_square };
extern int RLD_fit(
     double x_inc,
     double y[],
     int fit_start,
     int fit_end,
     double instr[],
     int n_instr,
     int noise,//noise_type noise,
     double sig[],
     double *z,
     double *a,
     double *tau,
     double fitted[],
     double *chi_square,
     double chi_square_target
        );

extern int LMA_fit(
        double x_inc,
        double y[],
        int fit_start,
        int fit_end,
        double instr[],
        int n_instr,
        int noise,//noise_type noise,
        double sig[],
        double param[],
        int param_free[],
        int n_param,
        double fitted[],
        double *chi_square,
        double chi_square_target,
        double chi_square_delta
        );

/*Functions in EcfGlobal */
/*extern int GCI_marquardt_global_generic_instr(float xincr, float **trans,
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
                     */
/*Functions in GCI_Phasor */
extern int GCI_Phasor(float xincr, float y[], int fit_start, int fit_end, float *INPUT, float *OUTPUT, float *OUTPUT, float *OUTPUT, float *OUTPUT, float *OUTPUT, float *OUTPUT, float *OUTPUT, float *OUTPUT);
extern double GCI_Phasor_getPeriod();
