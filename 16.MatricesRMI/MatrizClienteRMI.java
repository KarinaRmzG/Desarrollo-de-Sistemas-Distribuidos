/***
 * Actividad 16:    Matriz Cliente RMI
 * @author          Ramírez Galindo Karina
 * Descripción:     Programa distribuido que calcule el producto de matrices cuadradas utilizando Java RMI
 * Ejecucion:       1. Compilar <MatrizInterfaceRMI.java, MatrizClaseRMI.java, MatrizServidorRMI.java, MatrizClienteRMI.java>
 *                  2. Ejecutar el programa "rmiregistry" 
 *                  3. Ejecutar el programa "java MatrizServidorRMI"
 *                  4. Ejecutar el programa "java MatrizClienteRMI"
 */
import java.rmi.Naming;

public class MatrizClienteRMI {
  static int N=6;
  static int[][] A = new int [N][N];
  static int[][] B = new int [N][N];
  static int[][] C = new int [N][N];
  
  /**
   * Este método recibe como parámetros la matriz a dividir y el renglón inicial. 
   * El método regresará una matriz de tamaño (N/2)xN.
   * @param A         matriz A que se va a dividir
   * @param inicio    Posición de inicio
   * @param N         Tamaño de l amatriz
   * @return
   */
  static int[][] separa_matriz(int[][] A,int inicio,int N){
    int[][] M = new int[N/2][N];
    for (int i = 0; i < N/2; i++)
      for (int j = 0; j < N; j++)
        M[i][j] = A[i + inicio][j];
    return M;
  }
  
  /**
   *  Este método permite construir la matriz C a partir de las matrices C1, C2, C3 y C4
   * @param C         matriz C
   * @param A         sub-matriz A que se va a acomodar
   * @param renglon   Posicion del renglón
   * @param columna   Posición de la columna
   * @param N
   */
  static void acomoda_matriz(int[][] C,int[][] A,int renglon,int columna,int N){
    for (int i = 0; i < N/2; i++)
      for (int j = 0; j < N/2; j++)
        C[i + renglon][j + columna] = A[i][j];
      }
      
      public static void main(String[] args) throws Exception {

      // en este caso el objeto remoto se llama "prueba", notar que se utiliza el puerto default 1099
      String url = "rmi://localhost/prueba";
      // obtiene una referencia que "apunta" al objeto remoto asociado a la URL
      MatrizInterfaceRMI r = (MatrizInterfaceRMI)Naming.lookup(url);
      
      //podemos obtener las matrices A1, A2, B1 y B2 de la siguiente manera:
      int[][] A1 = separa_matriz(A,0,N);
      int[][] A2 = separa_matriz(A,N/2,N);
      int[][] B1 = separa_matriz(B,0,N);
      int[][] B2 = separa_matriz(B,N/2,N);

      //Dadas las matrices A1, A2, B1 y B2, podemos obtener las matrices C1, C2, C3 y C4 
      //utilizando el método multiplica_matrices():
      int[][] C1 = r.multiplica_matrices(A1,B1,N);
      int[][] C2 = r.multiplica_matrices(A1,B2,N);
      int[][] C3 = r.multiplica_matrices(A2,B1,N);
      int[][] C4 = r.multiplica_matrices(A2,B2,N);

      //obtencion de la matriz C
      int[][] C = new int[N][N];
      acomoda_matriz(C,C1,0,0,N);
      acomoda_matriz(C,C2,0,N/2,N);
      acomoda_matriz(C,C3,N/2,0,N);
      acomoda_matriz(C,C4,N/2,N/2,N);
        
    }
}
