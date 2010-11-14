
#include <jni.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <android/log.h>

#ifndef _Included_ru_waterdist_ProgressTest
#define _Included_ru_waterdist_ProgressTest
#ifdef __cplusplus
extern "C" {
#endif

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "PIPES_DEMO", __VA_ARGS__)

JNIEXPORT jint JNICALL Java_ru_jecklandin_cats_PipesDemo_mkfifo
  (JNIEnv *env, jobject, jstring path) { 
  
    const char* cpath = env->GetStringUTFChars(path, NULL);
  	struct stat buf;
  	int res = 0;
	if (stat(cpath, &buf) < 0) {
    	if (mkfifo(cpath, S_IRWXU) < 0 ) {
    		LOGD("Cannot create a pipe");
    		res = -1;
    	} else {
    		LOGD("The pipe was created");
    	}
    } else {
    	LOGD("Pipe already exists");
    }
    env->ReleaseStringUTFChars(path, cpath);
    return res;
}

JNIEXPORT jint JNICALL Java_ru_jecklandin_cats_ProcessingService_process
  (JNIEnv * env, jobject, jstring path) {
    
	const char* cpath = env->GetStringUTFChars(path, NULL);
	struct stat buf;
	
	if ( stat(cpath, &buf) < 0 || ! (buf.st_mode | S_IFIFO)) {
    	LOGD("The file isn't a pipe");
    	return -1;
    }  
	
    int fp = open(cpath, O_WRONLY);
    if(fp == -1) {
        LOGD("Could not open the pipe");
        return -1;
    } 
      
    for (int i=0; i<10; ++i) {
    	sleep(1);
    	write(fp, &i, 1);
    }
    close(fp);
    env->ReleaseStringUTFChars(path, cpath);
    return 0;
}
  
#ifdef __cplusplus
}
#endif
#endif
