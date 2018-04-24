%module SLIMCurve

%include "arrays_java.i" // arrays (without length)
%include "typemaps.i" // pointers as array

// macro for 1d array maps
%define ARRMAP(MAPNAME, IS_ARR, HAS_LEN, jType, JType, IO, NULLACC, KEEP_ALLOC)
// construct the correct typemap name
// TODO: change to readable macros
#undef MAPNAME
#if IS_ARR == 1
	#if HAS_LEN == 1
		%define MAPNAME (jType ARR_INPUT_LEN_##IO##_##NULLACC[], int ARRLEN) %enddef
	#else
		%define MAPNAME jType ARR_INPUT_LEN_##IO##_##NULLACC[] %enddef
	#endif
#else
	#if HAS_LEN == 1
		%define MAPNAME (jType *PTR_INPUT_LEN_##IO##_##NULLACC, int ARRLEN) %enddef
	#else
		%define MAPNAME jType *PTR_INPUT_##IO##_##NULLACC %enddef
	#endif
#endif
%typemap(jstype) MAPNAME "jType[]"
%typemap(javain) MAPNAME "$javainput"
%typemap(javadirectorin) MAPNAME "$jniinput"
%typemap(jtype) MAPNAME "jType[]"
	// cannot use ## macro inside " "
%typemap(jni) MAPNAME "JXXXARRAY(jType)"
%typemap(in) MAPNAME (j##jType *jarr, j##jType##Array *combined_arr = 0, bool do_clean) {
	// local reference to the java array and the length of it
	jsize len;
	jType *carr;

	// $input: jType[](J)
	// $1: jType *ARR_INPUT, $2: int ARRLEN(C)
	if (NULLACC && !$input) {
		// if accepts null input and turns out to be null, don't delete afterwards
		// and skip input array creation
		do_clean = false;
		$1 = NULL;
		len = 0;
	}
	else {
		do_clean = true;
		
		len = (jsize) JCALL1(GetArrayLength, jenv, $input);
		if (!SWIG_JavaArrayIn##JType##(jenv, &jarr, (jType **)&carr, $input))
			exit(1);

#if KEEP_ALLOC == 1
// "KEEP_ALLOC" is used to keep the array alive in case it is used later
// (e.g. allocated for a DecayModelSelParamValuesAndFit struct)
		// make sure there is enough space for the jarray pointer
		combined_arr = (j##jType##Array *)std::malloc(len * sizeof(jType) + sizeof(j##jType##Array*));
		// and the reference jarr is not garbage collected
		combined_arr[0] = (j##jType##Array)JCALL1(NewWeakGlobalRef, jenv, $input);
		// skip the pointer
		$1 = (jType*)(combined_arr + 1);
#else
		$1 = carr;
#endif

		for (int i = 0; i < len; i++)
			$1[i] = (j##jType) carr[i];

#if KEEP_ALLOC == 1
		delete[] carr;
#endif
	}

#if HAS_LEN == 1
		$2 = len;
#endif
}
%typemap(argout) MAPNAME %{
	if (do_clean$argnum) {
#if IO != JNI_ABORT
		// update java array if needed
		int i;
		jsize len = jenv->GetArrayLength($input);
		for (i=0; i<len; i++)
			jarr$argnum[i] = (j##jType)$1[i];
#endif
		jenv->Release##JType##ArrayElements($input, jarr$argnum, IO);
	}
%}
%typemap(freearg) MAPNAME %{
#if KEEP_ALLOC != 1
	if (do_clean$argnum) {
		// release the resources before exiting
		// skip if $1 (combined_arr) should be kept alive
		delete[] $1;
	}
#endif
%}
%enddef
