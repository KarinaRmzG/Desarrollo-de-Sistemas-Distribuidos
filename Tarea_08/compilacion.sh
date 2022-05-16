echo "Procediendo a montar el nuevo servico para TomCat"
rm home/KarinaRG/PruebaCarrito/Servicio/Servicio.war
echo "Variables de entorno para ejecutar TomCat"
export CATALINA_HOME=/home/KarinaRG/apache-tomcat-8.5.78
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

#COMPILACION DEL SERVICIO.JAVA, puede tener cualquier nombre el archivo, pero debe de estar en la carpeta de Servicio
#donde se encuentran los directorios de META-INF, WEB-INF,negocio (aqui se encuentran los archivos del back-ent)
echo "Compilamos el archivo Servicio.java"
javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. /home/KarinaRG/PruebaCarrito/Servicio/negocio/Servicio.java
echo "Compilacion exitosa"

#Aqui creamos nuestro archivo .war de nuestro servicio
echo "Creacion del servicio de TomCat"
rm /home/KarinaRG/Servicio/WEB-INF/classes/negocio/*
cp /home/KarinaRG/Servicio/WEB-INF/classes/negocio/*.class /home/KarinaRG/Servicio/WEB-INF/classes/negocio/.
jar cvf Servicio.war WEB-INF META-INF
echo "Creacion del servicio de TomCat exitosa"

echo "Eliminamos el archivo war anterior"
rm /home/KarinaRG/apache-tomcat-8.5.78/webapps/Servicio.war
rm -r /home/KarinaRG/apache-tomcat-8.5.78/webapps/Servicio

echo "Copiando nuevo servicio"
#Copiamos nuestro archivo .war a la carpeta de webapps
cp /home/KarinaRG/Servicio/Servicio.war /home/KarinaRG/apache-tomcat-8.5.78/webapps
