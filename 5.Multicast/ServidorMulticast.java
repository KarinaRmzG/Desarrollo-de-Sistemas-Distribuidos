/***
 * Actividad 05:    Servidor Multicast
 * @author          Ramírez Galindo Karina
 * Descripción:     El programa ServidorMulticast.java es un ejemplo de un servidor que 
 *                  utiliza sockets UDP para enviar mensajes a un grupo de clientes.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/*Recordar que aqui se ejecuta primero el cliente*/
public class ServidorMulticast {
    
    static void envia_mensaje(byte[] buffer, String ip, int puerto) throws IOException{
        DatagramSocket socket = new DatagramSocket();
        //obtenemos el grupo correspondiente a la ip con getByName
        InetAddress grupo = InetAddress.getByName(ip);  
        
        //creamos un DatagramPacket para crear un paquete con el mensaje
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, grupo, puerto);
        
        socket.send(paquete);   //envio del paquete
        socket.close();     //cierra socket
    }

    
    public static void main(String[] args) throws IOException {
        /*Antes de que el servidor envie mensajes, necesitamos asignar true  a la propiedad 
        java.net.preferIPv4Stack  debido a que el programa utilizará sockets IPv4 y por default 
        Java utiliza sockets nativos IPv6*/
        System.setProperty("java.net.preferIPv4Stack", "true");
        
    //enviamos la cadena de caracteres hola, se envia al grupo identificado por la IP 230.0.0.0:
        envia_mensaje("hola".getBytes(), "230.0.0.0", 50000);
        
    /*se enviaran cinco numeros punto flotante de 64 bits. Primero empacaremos los números utilizando 
      un objeto ByteBuffer. Cinco numeros punto flotante de 64 bits ocupan 5 x 8 bytes (64 bits = 8 bytes). 
      Entonces vamos a crear un objeto tipo ByteBuffer con una capacidad de 40 bytes.*/
        ByteBuffer b = ByteBuffer.allocate(5*8);
        
        //Utilizamos el método putDouble para agregar cinco números al objeto ByteBuffer.
        b.putDouble(1.1);
        b.putDouble(1.2);
        b.putDouble(1.3);
        b.putDouble(1.4);
        b.putDouble(1.5);
        
        
    /*Para enviar el paquete de nums, convertimos el objeto ByteBuffer a un arreglo de bytes 
    utilizando el método array() de la clase ByteBuffer*/
        envia_mensaje(b.array(), "230.0.0.0", 50000);

    }
}