# These are the rules that actually work
# The `-keep class Server.ClientObject {*;}` line fixes blank client table bug

-injars kumo-1.0.jar
-outjars kumo-1.0-obf.jar
-target 1.8
-libraryjars "C:\Program Files\Java\jdk1.8.0_111\jre\lib"
 -overloadaggressively
 -obfuscationdictionary ..\..\obfuscation-dictionary.txt
 -classobfuscationdictionary ..\..\windows-dict.txt
-keep class * { public static void main(java.lang.String[]);}
-keep class Server.ClientObject {*;}
-optimizationpasses 3
-allowaccessmodification
-keepparameternames
