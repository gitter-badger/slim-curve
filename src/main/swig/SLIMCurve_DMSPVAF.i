%module SLIMCurve

// struct name of DecayModelSelParamValuesAndFit
%extend _DMSPVAF {
	// Add constructor
	_DMSPVAF(
		void (*fitfunc)(float, float [], float *, float [], int),
		float params[],
		int nparam,
		int paramfree[],
		int nparamfree,
		restrain_type restrain,
		float *fitted_buf,
		float *residuals_buf,
		float chisqtarget,
		float chisqdelta,
		int chisqpercent,
		float chisq,
		float **covar,
		float **alpha,
		float **erraxes) {
		DecayModelSelParamValuesAndFit* ret = new DecayModelSelParamValuesAndFit();
		// nparam cannot be determined until now (paramfree does not proceed
		// fitfunc in typemap)
		extern thread_local FitFunc* t_fitfunc;
		t_fitfunc->nparam = nparam;
		ret->fitfunc = fitfunc;
		ret->nparam = nparam;
		std::copy(params, params + nparam, ret->params);
		ret->nparamfree = nparamfree;
		std::copy(paramfree, paramfree + nparam, ret->paramfree);
		ret->restrain = restrain;
		// named _buf to distinguish them from variables with the same name 
		// but different typemaps, see SLIMCurve.i)
		ret->fitted = fitted_buf;
		ret->residuals = residuals_buf;
		ret->chisq_target = chisqtarget;
		ret->chisq_delta = chisqdelta;
		ret->chisq_percent = chisqpercent;
		ret->chisq = chisq;
		ret->covar = covar;
		ret->alpha = alpha;
		ret->erraxes = erraxes;
		return ret;
	}
}

// Conversion: DecayModelSelParamValuesAndFit[2](J) -> (C) */
%define DMSPVAF DecayModelSelParamValuesAndFit* paramsandfits %enddef
%typemap(jstype) DMSPVAF "DecayModelSelParamValuesAndFit[]"
%typemap(javain, pre="
	if($javainput.length != 2)
		throw new IllegalArgumentException(\"Requires 2 models.\");
	/*if($javainput[0].params.length > 20 || $javainput[1].params.length > 20)
		throw new IllegalArgumentException(\"More than 20 parameters.\");
	if($javainput[0].paramfree.length != $javainput[0].paramfree.length ||
		$javainput[1].paramfree.length != $javainput[1].paramfree.length)
		throw new IllegalArgumentException(\"Lengths of param and paramfree disagree.\");*/
	if($javainput[0] == null || $javainput[1] == null)
		throw new NullPointerException(\"Array contains null\");
") DMSPVAF "$javainput"
%typemap(jtype) DMSPVAF "DecayModelSelParamValuesAndFit[]"
%typemap(jni) DMSPVAF "jobjectArray"
%typemap(in) DMSPVAF {
	// $input = DecayModelSelParamValuesAndFit[]
	// $1 = DecayModelSelParamValuesAndFit*
	// This conversion recovers the objects from $input.cPtr (jlong)
	$1 = new DecayModelSelParamValuesAndFit[3]; // index starts at 1 in GCI_EcfModelSelectionEngine
	jclass DMSClass = JCALL1(FindClass, jenv, PKG_NAME"/DecayModelSelParamValuesAndFit");
	jfieldID ptrID = JCALL3(GetFieldID, jenv, DMSClass, "swigCPtr", "J");
	jobject dms_0 = JCALL2(GetObjectArrayElement, jenv, $input, 0);
	$1[1] = *(DecayModelSelParamValuesAndFit*)JCALL2(GetLongField, jenv, dms_0, ptrID);
	jobject dms_1 = JCALL2(GetObjectArrayElement, jenv, $input, 1);
	$1[2] = *(DecayModelSelParamValuesAndFit*)JCALL2(GetLongField, jenv, dms_1, ptrID);
}
%typemap(freearg) DMSPVAF {
	// there is a jarray pointer at the head of each kept-alive arrays
	jfloatArray* jarrs[] = {
		(jfloatArray*)($1[1].fitted) - 1,
		(jfloatArray*)($1[1].residuals) - 1,
		(jfloatArray*)($1[2].fitted) - 1,
		(jfloatArray*)($1[2].residuals) - 1
	};
	for (int i = 0; i < 4; i++) {
		// if still not garbage collected, commit and release array
		if (!JCALL2(IsSameObject, jenv, *jarrs[i], NULL)) {
			jsize len = JCALL1(GetArrayLength, jenv, *jarrs[i]);
			JCALL4(SetFloatArrayRegion, jenv, *jarrs[i], 0, len, (jfloat*)(jarrs[i] + 1));
			JCALL1(DeleteWeakGlobalRef, jenv, *jarrs[i]);
		}
		std::free(jarrs[i]);
	}
	delete[] $1;
}

// do not generate getter/setter for members in DecayModelSelParamValuesAndFit (one-time use)
%rename("$ignore", fullname=1, regextarget=1, %$isvariable)"_DMSPVAF::.*";
%ignore _DMSPVAF::fitfunc; // not sure why I have to ignore it again
