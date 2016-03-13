LOCAL_PATH := $(call my-dir)
TESSERACT_PATH := $(LOCAL_PATH)/com_googlecode_tesseract_android/src
LEPTONICA_PATH := $(LOCAL_PATH)/com_googlecode_leptonica_android/src

LOCAL_C_INCLUDES += $(LOCAL_PATH)/com_googlecode_leptonica_android/src/ccmain
LOCAL_C_INCLUDES += $(LOCAL_PATH)/com_googlecode_tesseract_android/src/ccmain

# Just build the Android.mk files in the subdirs
include $(call all-subdir-makefiles)
