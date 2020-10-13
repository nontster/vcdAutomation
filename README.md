VMware vCloud Director Automation
=================================

## Environment preparation

### Download vCloud SDK for Java for vCloud Suite 5.5

Download [vCloud SDK for Java for vCloud Suite 5.5] (https://developercenter.vmware.com/web/sdk/5.5.0/vcloud-java)

### Install vCloud Director Java SDK library into Maven local repository


```
$ mvn install:install-file "-Dfile=vcloud-java-sdk-5.5.0.jar" "-DgroupId=com.vmware" "-DartifactId=vcloud-java-sdk" "-Dversion=5.5" "-Dpackaging=jar" "-DgeneratePom=true"
```
```
$ mvn install:install-file "-Dfile=rest-api-schemas-5.5.0.jar" "-DgroupId=com.vmware" "-DartifactId=rest-api-schemas" "-Dversion=5.5" "-Dpackaging=jar" "-DgeneratePom=true"
```

## Add Maven library in pom.xml

```
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
```

## Building and packaging MS Windows executable file
```
$ mvn package
```

## Usage

```
bee -l <vCloud Director URL> –u <username> -t <template file>.yml -o <output file>.xlsx
```
