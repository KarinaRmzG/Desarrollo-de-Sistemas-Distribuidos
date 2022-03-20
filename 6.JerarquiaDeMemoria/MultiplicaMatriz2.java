/***
 * Actividad 06:    Multiplica Matrices
 * @author          Ramírez Galindo Karina
 * Descripcion:     Ahora vamos a modificar el algoritmo de multiplicación de matrices de manera que 
 *                  incrementemos la localidad espacial haciendo que el acceso a la matriz B sea por renglones y 
 *                  no por columnas.
 */
public class MultiplicaMatriz2{
  static int N;
  static int[][] A;
  static int[][] B;
  static int[][] C;
  public static void main(String[] args){
    if (args.length < 1){
      System.out.println("Uso: java MultiplicaMatriz <valor de N>");
      return;
    }
    N = Integer.valueOf(args[0]);
    System.out.println("N tiene el valor de "+N);
    A = new int[N][N];
    B = new int[N][N];
    C = new int[N][N];
    long t1 = System.currentTimeMillis();
    // inicializa las matrices A y B
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
      {
        A[i][j] = 2 * i - j;
        B[i][j] = i + 2 * j;
        C[i][j] = 0;
      }
    // transpone la matriz B, la matriz traspuesta queda en B
    for (int i = 0; i < N; i++)
      for (int j = 0; j < i; j++)
      {
        int x = B[i][j];
        B[i][j] = B[j][i];
        B[j][i] = x;
      }
    // multiplica la matriz A y la matriz B, el resultado queda en la matriz C
    // notar que los indices de la matriz B se han intercambiado
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        for (int k = 0; k < N; k++)
           C[i][j] += A[i][k] * B[j][k];
    long t2 = System.currentTimeMillis();
    System.out.println("Tiempo: " + (t2 - t1) + "ms");
  }
}
