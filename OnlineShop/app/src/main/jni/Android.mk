LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := Library
LOCAL_SRC_FILES := MyJni.cpp
include $(BUILD_SHARED_LIBRARY)