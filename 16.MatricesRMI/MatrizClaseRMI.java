/**
 * Programa:        Matriz Clase RMI
 * @author          Ramírez Galindo Karina
 * Descripcion:     Clase que hereda de la clase "UnicastRemoteObject" 
 *                  e implementa la interfaz "MatrizInterfaceRMI".
 * Ejecucion:       1. Compilar <MatrizInterfaceRMI.java, MatrizClaseRMI.java, MatrizServidorRMI.java, MatrizClienteRMI.java>
 *                  2. Ejecutar el programa "rmiregistry" 
 *                  3. Ejecutar el programa "java MatrizServidorRMI"
 *                  4. Ejecutar el programa "java MatrizClienteRMI"
 */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrizClaseRMI extends UnicastRemoteObject implements MatrizInterfaceRMI{
    
    public MatrizClaseRMI() throws RemoteException{
        super();
    }
    
    /** 
     * Este método recibe como parámetros dos matrices de tamaño (N/2)xN y regresa como resultado una 
     * matriz cuadrada de tamaño (N/2)x(N/2).
     * 
     * Cuando el método multiplica_matrices() se invoca localmente, recibe como parámetros las referencias a 
     * las matrices A y B y regresa una referencia a la matriz C.
     * 
     * Cuando el método es invocado en forma remota, entonces la capa RMI serializa las matrices A y B en el cliente y las des-serializa en el servidor. 
     * De la misma forma, la capa RMI serializa la matriz C en el servidor y la des-serializa en el cliente.
     */
    public int[][] multiplica_matrices(int[][] A, int[][] B, int N){
        int[][] C = new int[N/2][N/2];
        for(int i = 0; i < N / 2; i++)
            for(int j = 0; j < N / 2; j++)
                for(int k = 0; k < N; k++)
                    C[i][j] += A[i][k] * B[j][k];
        return C;
    }
    
    
}