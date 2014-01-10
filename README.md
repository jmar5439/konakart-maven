konakart-maven
==============

Konakart Maven style directory layout 
-------------------------------------

Konakart release  6.5.1.0

How to run 
----------

create profiles.xml with
```xml
 <profilesXml
xmlns="http://maven.apache.org/PROFILES/1.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/PROFILES/1.0.0 http://maven.apache.org/xsd/profiles-1.0.0.xsd">
<profiles>
   <profile>
      <id>env-dev</id>
      <activation>
        <property>
          <name>environment</name>
          <value>dev</value>
        </property>
      </activation>
      <properties>
        <mail_server>...smtp mail server</mail_server>
        <mail_port>...smtp mail port...</mail_port>
        <connection.url> connection url</connection.url>
		<connection.username>connection username</connection.username>
		<connection.password>connection password</connection.password>
		</properties>
		</profile>
</profiles>
</profilesXml>
```

Database

http://www.konakart.com/docs/manualInstallation.html

[Jordi Martí](https://twitter.com/alquilerjoven)
