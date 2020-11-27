
export ADOBE_SDK="C:/workspace/_airSDKs/32.0.0.116"
export BASE_PATH="C:/workspace/Assukar/dev/client/libs/customnativeextensions/android/utils/_build"

export ADT=$ADOBE_SDK"/bin/adt.bat"
export EXTENSION_PATH=$BASE_PATH"/extension.xml"
export SWC_PATH=$BASE_PATH"/com.assukar.air.android.utils.swc"
export ANDROID_PATH=$BASE_PATH"/android"
export DEFAULT_PATH=$BASE_PATH"/default"
export ANE_OUTPUT_PATH=$BASE_PATH"/com.assukar.air.android.utils.ane"

$ADT \
-package -target ane $ANE_OUTPUT_PATH \
$EXTENSION_PATH -swc $SWC_PATH \
-platform Android-ARM -C $ANDROID_PATH . \
-platform Android-x86 -C $ANDROID_PATH . \
-platform default -C $DEFAULT_PATH . 