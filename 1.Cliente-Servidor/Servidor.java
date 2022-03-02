/***
 * Actividad 01:    Servidor
 * @author          Ramírez Galindo Karina
 * Descripción:     El programa Servidor.java va a esperar una conexión del cliente, 
 *                  entonces recibirá los datos que envía el cliente y a su vez, enviará datos al cliente.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Servidor {
    
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while(longitud > 0)
        {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }
    
    public static void main(String[] args) throws IOException, Exception {
        long ms = System.currentTimeMillis();
        System.out.println("Socket waiting clients...");
        
        ServerSocket servidor = new ServerSocket(50000);
        Socket conexion = servidor.accept();
        
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        /* RECIBIR 10000 NUMEROS MEDIANTE readDouble */
        /*for(double i=1; i<=10; i++){
            double x = entrada.readDouble();
            System.out.println(x);
        }
        System.out.println("Milisegundos que tarda en RECIBIR 10000 numeros:"+ms);*/

        salida.write("HOLA".getBytes());

        /* RECIBIR 10000 NUMEROS MEDIANTE readDouble */
       
        for(double i=1; i<=10; i++){
            byte [] a = new byte[10*8];
            read(entrada, a, 0,10*8);
            ByteBuffer b = ByteBuffer.wrap(a);
            System.out.println(b.getDouble());
        }
        System.out.println("Milisegundos que tarda en RECIBIR 10000 numeros:"+ms);
        
        /*int n = entrada.readInt();
        System.out.println("int: " + n);
        
        double x = entrada.readDouble();
        System.out.println("double: " + x);
        
        byte[] buffer = new byte[4];
        read(entrada, buffer, 0, 4);
        System.out.println("string: " + (new String(buffer, "UTF-8")));*/
        
        /*salida.write("HOLA".getBytes());
        byte [] a = new byte[10*8];
        read(entrada, a, 0,10*8);
    
        ByteBuffer b = ByteBuffer.wrap(a);
        for(int i = 1; i <= 10; i++) 
            System.out.println(b.getDouble());*/
        conexion.close();
    }
}
