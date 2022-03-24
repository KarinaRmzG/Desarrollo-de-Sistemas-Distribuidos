/**
 * Programa:        Matriz Interface RMI
 * @author          Ramírez Galindo Karina
 * Descripcion:     Interfaz que hereda de la clase "Remote" 
 *                  incluye los prototipos de los métodos a exportar
 * Ejecucion:       1. Compilar <MatrizInterfaceRMI.java, MatrizClaseRMI.java, MatrizServidorRMI.java, MatrizClienteRMI.java>
 *                  2. Ejecutar el programa "rmiregistry" 
 *                  3. Ejecutar el programa "java MatrizServidorRMI"
 *                  4. Ejecutar el programa "java MatrizClienteRMI"
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrizInterfaceRMI extends Remote{
    public int[][] multiplica_matrices(int[][] A, int[][] B, int N) throws RemoteException;
}