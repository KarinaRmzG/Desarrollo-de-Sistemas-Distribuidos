/***
 * Actividad 02:    Cliente
 * @author          Ramírez Galindo Karina
 * Descripción:     El programa Cliente.java es un ejemplo de un cliente de sockets TCP 
 *                  que se conecta a un servidor y posteriormente envía y recibe datos.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Cliente {
    
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception{
        while(longitud > 0)
        {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    
    public static void main(String[] args) throws IOException, Exception{
        Socket conexion = null;
        
        for(;;){
            try{
                conexion = new Socket("localhost", 50000);

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                salida.writeInt(123);
                salida.writeDouble(1234567890.1234567890);
                salida.write("hola".getBytes());

                byte[] buffer = new byte [4];
                /*entrada.read(buffer,0,4);
                System.out.println(new String(buffer, "UTF-8"));*/

                read(entrada, buffer, 0, 4);
                System.out.println(new String(buffer, "UTF-8"));

                ByteBuffer b = ByteBuffer.allocate(5*8);
                b.putDouble(1.1);
                b.putDouble(1.2);
                b.putDouble(1.3);
                b.putDouble(1.4);
                b.putDouble(1.5);

                byte[] a = b.array();
                salida.write(a);

                Thread.sleep(1000);
                conexion.close();
                break;
                
            }catch (Exception e){
                Thread.sleep(100);
            }
        }
    }
}