# Keep rules for the release build (R8).
#
# The app reads its dictionary with org.json (part of Android), shows the Nunito font
# from res/font, and keeps settings in DataStore. Compose, Navigation and DataStore ship
# their own rules; the lines below only add safety for DataStore's generated classes.

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
