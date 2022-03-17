/***
 * Tarea 04:        Chat Multicast
 * @author          Ramírez Galindo Karina
 * Descripción:     Desarrollar un solo programa en Java en modo consola que implemente un chat 
 *                  utilizando comunicación multicast mediante datagramas.
 */
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Scanner;

class Chat {
    public static final int PUERTO = 50000;
    public static final String IP_MULTICAST = "230.0.0.0";
    public static final int MAX_BITS_DATAGRAM = 1024;

    /**
     * Función para enviar los mensajes multicast
     * @param buffer    mensaje a enviar
     * @param ip        grupo multicast
     * @param puerto    puerto al que se va a transmitir
     * @throws IOException
     */
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    /**
     * Función para recibir los mensajes multicast
     * @param socket                multicast socket
     * @param longitud_mensaje      
     * @return
     * @throws IOException
     */
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static class Worker extends Thread {
        public void run() {
            // En un ciclo infinito se recibirán los mensajes enviados al grupo
            // 230.0.0.0 a través del puerto 50000 y se desplegarán en la pantalla.
            while(true) {
                try{
                    NetworkInterface ni = NetworkInterface.getByName("em1");//nombre de la tarjeta de red
                    MulticastSocket socket = new MulticastSocket(PUERTO);//creo socket MULTICAST pto-->50000
                    socket.setReuseAddress(true);//reutilizacion de direcciones
                    socket.setTimeToLive(255);//transmito paquetes-->> modificacion TTL
                    InetAddress dir = InetAddress.getByName(IP_MULTICAST);//grupo multicast IPv4
                    SocketAddress grupo = new InetSocketAddress(dir,PUERTO);//grupo y puerto al que se va a transmitir (50000)
                    socket.joinGroup(grupo, ni);//reporte de membresia IGMP --> uniendose a la direccion 230.0.0.0 por la NIC seleccionada
                    byte[] buffer = recibe_mensaje_multicast(socket, MAX_BITS_DATAGRAM);
                    PrintStream salida = new PrintStream(System.out, true, "Cp850");
                    //PrintStream salida = new PrintStream(System.out,true,"ISO-8859-1");
                    salida.print(new String(buffer)+"\n");
                    socket.leaveGroup(grupo, ni);
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("Uso: java Chat <nombre de usuario>");
            return;
        }
        Worker w = new Worker();
        w.start();
        String nombre = args[0];
        try (Scanner scanner = new Scanner(System. in,"Cp850")) {
            System.out.println(nombre + " te has unido al chat");
            // En un ciclo infinito se leerá cada mensaje del teclado y se enviará el
            // mensaje al grupo 230.0.0.0 a través del puerto 50000.
            while(true) {
                System.out.println("Ingrese el mensaje a enviar:");
                String mensaje = scanner. nextLine();
                byte buffer[] = String.format("%s dice %s", nombre, mensaje).getBytes();
                //System.out.println("Ingrese el mensaje a enviar:");
                envia_mensaje_multicast(buffer, IP_MULTICAST,PUERTO);
            }
        }
    }

}
