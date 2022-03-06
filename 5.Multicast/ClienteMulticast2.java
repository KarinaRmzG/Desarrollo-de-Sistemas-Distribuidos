/***
 * Actividad 05:    Cliente Multicast
 * @author          Ramírez Galindo Karina
 *
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/*Recordar que aquí se ejecuta primero el cliente*/

public class ClienteMulticast2 {
    static byte[] recibe_mensaje(MulticastSocket socket, int longitud_mensaje) throws IOException{
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        


        MulticastSocket socket = new MulticastSocket(50000);//socket al pto 50000
        InetSocketAddress grupo = new InetSocketAddress(InetAddress.getByName("230.0.0.0"),50000);//grupo y puerto al que se va a transmitir (50000)
        NetworkInterface netInter = NetworkInterface.getByName("eth7");//listado de targetas de red
        socket.joinGroup(grupo,netInter);
        System.out.println("Servicio iniciado y unido al grupo.. comienza escucha de mensajes");
        
    /*Entonces el cliente puede recibir los mensajes enviados al grupo por el servidor.
    Se recibirá una cadena de caracteres.*/
        byte[] a = recibe_mensaje(socket, 4);
        System.out.println(new String(a, "UTF-8"));
        
    /*Ahora vamos a recibir 5 números punto flotante de 64 bits empacados como arreglo de bytes:*/
        byte[] buffer = recibe_mensaje(socket, 5*8);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        for(int i = 0; i < 5; i++)
            System.out.println(b.getDouble());
        
    
            socket.leaveGroup(grupo,netInter);//abandonar grupo
        socket.close();
    }

}