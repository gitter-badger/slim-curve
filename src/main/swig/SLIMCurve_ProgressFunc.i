%module SLIMCurve

%ignore t_jenv;
%ignore t_spa_prog;
%ignore update_SPA_progress;

%inline %{
#ifndef THREAD_JNIENV
#define THREAD_JNIENV
	// jenv corresponded to the current thread
thread_local JNIEnv *t_jenv;
#endif
thread_local float t_spa_prog = 0;
void update_SPA_progress(float prog) {
	t_spa_prog = prog;
}

float getSPAProgress() {
	return t_spa_prog;
}
%}

// numinputs=0 ignores the jni argument.
%typemap(in, numinputs=0) void (*progressfunc)(float) {
	// store jenv for callback
	t_jenv = jenv;

	$1 = &update_SPA_progress;
}
