1. This project was cretated with the mvn command:

mvn archetype:generate \
\
-DarchetypeGroupId=org.mule.tools.devkit \
-DarchetypeArtifactId=mule-devkit-archetype-generic \
-DarchetypeVersion=3.2 \
\
-DarchetypeRepository=http://repository.mulesoft.org/releases/ \
-DmuleVersion=3.2.0 \
\
-DmuleModuleName=GoogleC2dm \
-DgroupId=org.mule.modules \
-DartifactId=mule-module-google-c2dm \
-Dversion=1.0.0-SNAPSHOT \
-Dpackage=org.mule.modules.googlec2dm

2. This projects requires maven 3 to build