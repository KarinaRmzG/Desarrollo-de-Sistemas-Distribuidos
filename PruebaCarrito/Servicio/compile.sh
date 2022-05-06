echo "Eliminando war anterior"
rm Servicio.war
export CATALINA_HOME=/home/elias/apache-tomcat-8.5.78
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
echo "Compilando programa"
javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. negocio/Servicio.java
rm WEB-INF/classes/negocio/*
cp negocio/*.class WEB-INF/classes/negocio/.
jar cvf Servicio.war WEB-INF META-INF
echo "\t ****** Compilacion exitosa ******"
echo "Eliminando archivo war del servidor"
rm /home/elias/apache-tomcat-8.5.78/webapps/Servicio.war
rm -r /home/elias/apache-tomcat-8.5.78/webapps/Servicio
echo "\t ****** Eliminacion exitosa ******"
echo "Copiando nuevo archivo war al servidor"
cp Servicio.war /home/elias/apache-tomcat-8.5.78/webapps/Servicio.war
echo "\t ****** Copia exitosa ******"
