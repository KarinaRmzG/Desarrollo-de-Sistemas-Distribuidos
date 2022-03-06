/***
 * Actividad 04:    ClienteSSL
 * @author          Ramírez Galindo Karina
 * 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;

public class ClienteSSL {
    public static void main(String[] args) throws IOException {
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion = cliente.createSocket("localhost", 50000);
        
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        
        salida.writeDouble(123456789.123456789);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClienteSSL.class.getName()).log(Level.SEVERE, null, ex);
        }
        conexion.close();

    }

    /**
     * Este método sirve para que el cliente pueda leer el archivo del disco local
     * @param archivo
     * @return 
     * @throws Exception
     */
    static byte[] lee_archivo(String archivo) throws Exception{
        FileInputStream f = new FileInputStream(archivo);
        byte[] buffer;
        try{
            buffer = new byte[f.available()];
            f.read(buffer);
        }
        finally{
            f.close();
        }
        return buffer;
    }
}

