%module SLIMCurve

%ignore t_jenv;
%ignore update_SPA_progress;

%inline %{
#ifndef THREAD_JNIENV
#define THREAD_JNIENV
	// jenv corresponded to the current thread
thread_local JNIEnv *t_jenv;
#endif
void update_SPA_progress(float prog) {
	jclass cls = t_jenv->FindClass(PKG_NAME"/SLIMCurve");
	jfieldID fieldId = t_jenv->GetStaticFieldID(cls, "SPAProgress", "float");

	t_jenv->SetStaticFloatField(cls, fieldId, prog);
}
%}

// numinputs=0 ignores the jni argument.
%typemap(in, numinputs=0) void (*progressfunc)(float) {
	// store jenv for callback
	t_jenv = jenv;

	$1 = &update_SPA_progress;
}

%pragma(java) modulecode=%{
	public static float SPAProgress;
%}
