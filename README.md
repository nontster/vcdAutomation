vCloud Director Automation
==========================

Install vCloud Director Java SDK into maven local repository

$ mvn install:install-file -Dfile=vcloud-java-sdk-5.5.0.jar -DgroupId=com.vmware -DartifactId=vcloud-java-sdk -Dversion=5.5 -Dpackaging=jar -DgeneratePom=true

$ mvn install:install-file -Dfile=rest-api-schemas-5.5.0.jar -DgroupId=com.vmware -DartifactId=rest-api-schemas -Dversion=5.5 -Dpackaging=jar -DgeneratePom=true


Add Maven library in pom.xml

<dependency>
   <groupId>com.vmware</groupId>
   <artifactId>rest-api-schemas</artifactId>
   <version>5.5</version>
</dependency>
<dependency>
   <groupId>com.vmware</groupId>
   <artifactId>vcloud-java-sdk</artifactId>
   <version>5.5</version>
</dependency>
