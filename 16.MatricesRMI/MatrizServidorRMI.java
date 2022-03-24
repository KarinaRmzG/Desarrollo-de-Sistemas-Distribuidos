/**
 * Programa:        Matriz Servidor RMI
 * @author          Ramírez Galindo Karina
 * Descripcion:     Registra en el rmiregistry una instancia de la clase ClaseRMI utilizando el método rebind().
 * Ejecucion:       1. Compilar <MatrizInterfaceRMI.java, MatrizClaseRMI.java, MatrizServidorRMI.java, MatrizClienteRMI.java>
 *                  2. Ejecutar el programa "rmiregistry" 
 *                  3. Ejecutar el programa "java MatrizServidorRMI"
 *                  4. Ejecutar el programa "java MatrizClienteRMI"
 */
import java.rmi.Naming;

public class MatrizServidorRMI {
    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost/prueba";
        MatrizClaseRMI objetoRMI = new MatrizClaseRMI();
        Naming.rebind(url, objetoRMI);
    }
}