/***
 * Actividad 05:    Cliente Multicast
 * @author          Ramírez Galindo Karina
 *
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/*Recordar que aquí se ejecuta primero el cliente*/

public class ClienteMulticast {
    static byte[] recibe_mensaje(MulticastSocket socket, int longitud_mensaje) throws IOException{
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        
    
        InetAddress grupo = InetAddress.getByName("230.0.0.0");//grupo multicast IPv4

        MulticastSocket socket = new MulticastSocket(50000);
        
    /*Para que el cliente pueda recibir los mensajes enviados al grupo 230.0.0.0 unimos el socket al 
    grupo utilizando el método joinGroup() de la clase MulticastSocket:*/
        socket.joinGroup(grupo);
        System.out.println("Servicio iniciado y unido al grupo.. comienza escucha de mensajes");
        
    /*Entonces el cliente puede recibir los mensajes enviados al grupo por el servidor.
    Primeramente vamos a recibir una cadena de caracteres.*/
        byte[] a = recibe_mensaje(socket, 4);
        System.out.println(new String(a, "UTF-8"));
        
    /*Ahora vamos a recibir 5 números punto flotante de 64 bits empacados como arreglo de bytes:*/
        byte[] buffer = recibe_mensaje(socket, 5*8);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        for(int i = 0; i < 5; i++)
            System.out.println(b.getDouble());
        
    
        socket.leaveGroup(grupo);//abandonar grupo
        socket.close();
        
    }

}