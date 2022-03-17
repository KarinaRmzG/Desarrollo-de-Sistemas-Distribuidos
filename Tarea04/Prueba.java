import java.util.*;
import java.io.*;
import java.net.*;

public class Prueba
{
    public static final String IP_MULTICAST = "230.0.0.0";
    public static final int PUERTO = 50000;
    public static final int MAX_LONGITUD_MENSAJE = 1024;

    static NetworkInterface interfazDeRed = null;
    static MulticastSocket socket = null;
    static InetSocketAddress grupoMulticast = null;

    static class Worker extends Thread
    {
        public void run()
        {
            for (;;)
            {
                try
                {
                    byte[] bytesMensaje = recibe_mensaje_multicast(socket, MAX_LONGITUD_MENSAJE);
                    String mensaje = new String(bytesMensaje).trim();
                    System.out.println(mensaje);
                }
                catch (Exception e)
                {
                    ;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length < 1)
        {
            imprimirAyuda();
            return;
        }
        interfazDeRed = obtenerInterfazDeRedConMulticast();
        socket = new MulticastSocket(PUERTO);
        grupoMulticast = new InetSocketAddress(InetAddress.getByName(IP_MULTICAST),
                                               PUERTO);

        new Worker().start();

        String nombre = args[0];
        Scanner lector = new Scanner(System.in);
        socket.joinGroup(grupoMulticast, interfazDeRed);
        while (lector.hasNextLine())
        {
            String entrada = lector.nextLine();
            String mensaje = nombre + " dice " + entrada;
            envia_mensaje_multicast(mensaje.getBytes(),
                                    IP_MULTICAST,
                                    PUERTO);
        }
        socket.leaveGroup(grupoMulticast, interfazDeRed);
    }

    static void envia_mensaje_multicast(byte[] buffer,
                                        String ip,
                                        int puerto) throws IOException
    {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    static byte[] recibe_mensaje_multicast(MulticastSocket socket,
                                           int longitud_mensaje) throws IOException
    {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
	return paquete.getData();
    }

    static NetworkInterface obtenerInterfazDeRedConMulticast() throws SocketException
    {
        Enumeration<NetworkInterface> interfacesDeRed
            = NetworkInterface.getNetworkInterfaces();
        while (interfacesDeRed.hasMoreElements())
        {
            NetworkInterface interfaz = interfacesDeRed.nextElement();
            if (interfaz.supportsMulticast())
            {
                return interfaz;
            }
        }
        return null;
    }

    static void imprimirAyuda()
    {
        System.out.println("Uso: java Chat <nombre de usuario>");
    }
}