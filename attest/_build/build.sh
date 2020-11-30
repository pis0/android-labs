
export ADOBE_SDK="C:/workspace/_airSDK"
export BASE_PATH="C:/workspace/pipa/_labs/android-labs/attest/_build"

export ADT=$ADOBE_SDK"/bin/adt.bat"
export EXTENSION_PATH=$BASE_PATH"/extension.xml"
export SWC_PATH=$BASE_PATH"/lib.swc"
export ANDROID_PATH=$BASE_PATH"/android"
export DEFAULT_PATH=$BASE_PATH"/default"
export ANE_OUTPUT_PATH=$BASE_PATH"/com.assukar.air.android.attest.ane"

$ADT \
-package -target ane $ANE_OUTPUT_PATH \
$EXTENSION_PATH -swc $SWC_PATH \
-platform Android-ARM -C $ANDROID_PATH . \
-platform Android-x86 -C $ANDROID_PATH . \
-platform default -C $DEFAULT_PATH . 