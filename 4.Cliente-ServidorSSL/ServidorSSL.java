/***
 * Actividad 04:    ServidorSSL
 * @author          Ramírez Galindo Karina
 * 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSSL extends Thread{
    public string nombreArchivo;
    public ServidorSSL(String nombreArchivo){
        this.nombreArchivo = nombreArchivo;
    }
    public void run(){
        for (int i = 0; i < 100000; i++)synchronized(obj){
            n++;
        }
    }
    public static void main(String[] args) throws IOException {
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor = socket_factory. createServerSocket(50000);
        
        Socket conexion = socket_servidor.accept();
        
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        
        double x = entrada.readDouble();
        System.out.println(x);

        conexion.close();
    }
/**
 * Este métodp sirve para escribir el contenido del arreglo de bytes al disco local
 * @param archivo   nombre del archivo
 * @param buffer    tamaño
 * @throws Exception
 */   
    static void escribe_archivo(String archivo,byte[] buffer) throws Exception{
        FileOutputStream f = new FileOutputStream(archivo);
        try
        {
            f.write(buffer);
        }
        finally{
            f.close();
        }
    }
}
