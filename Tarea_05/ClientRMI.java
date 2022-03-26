/**
 * Program:         Matrix multiplication using distributed objects
 * Students:        - Ramírez Galindo Karina
 *                  - Toledo Espinosa Cristina Aline
 *                  - Vásquez Hérnandez Alan Mauricio
 * Languaje:        Java
 * Description:     Distributed system that calculates the product of two square matrices using Java RMI
 * Way to run it:	1. Compile <MatrixMultiplicationRemote.java, MatrixMultiplication.java, ServerRMI.java, ClientRMI.java>
 * 					2. Run the program "rmiregistry"
 * 					3. Run the program "java RMIServer <node>"
 * 					4. Run the program "java RMIClient <N>" 
 */
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientRMI {
	private static int N;
	
	private static float[][][] aSlices;
	private static float[][][] bSlices;
	private static float[][] c;

	public static void main(String[] args) {
		if (args.length < 1){
            System.out.println("Use: java ClientRMI <size>");
            return;
        }
		N = Integer.valueOf(args[0]);
		
		//Matrix initialization
		float[][] a = new float[N][N];
		float[][] b = new float[N][N];
		for (int i = 0; i < N; i++)
		      for (int j = 0; j < N; j++) {
		    	  a[i][j]= i + 2 * j;
		    	  b[i][j] = 3 * i - j;
		      }
		
		if(N == 8) {
			System.out.println("Matrix A:");
			printMatrix(a);
			System.out.println("Matrix B:");
			printMatrix(b);
		}
		//Transposes b
		transpose(b,N);
		
		ExecutorService service = Executors.newFixedThreadPool(4);//Creates a pool of 4 threads
		//Contains slices of the matrix
		aSlices = new float[4][N/4][];
		bSlices = new float[4][N/4][];
		for(int i = 0; i < aSlices.length;i++)
			for(int j = 0; j < aSlices[i].length;j++) { //Creates slices of the matrices
				aSlices[i][j] = a[j + (i * N / 4)];
				bSlices[i][j] = b[j + (i * N / 4)];
			}
		c = new float[N][N];
		List<Future> tasks = new ArrayList<>();
		for(int i = 0; i < 4;i++)
			tasks.add(service.submit(createTask(i + 1))); //Executes each task
		//Waits for tasks to end in order to print the result
		while(!tasks.isEmpty())
			if(tasks.get(0).isDone())
				tasks.remove(0);
			else
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		if(N == 8) {
			System.out.println("Matrix C:");
			printMatrix(c);
		}
		System.out.println("Checksum: " + getChecksum(c));	
		service.shutdown();
	}
	
	/**
	 * Method to fill matrix
	 * @param part
	 * @param matrix
	 * @param result
	 */
	public static void fillMatrix(int part, float[][] matrix, float[][] result) {
		int rowShift = part / 4 * N / 4;
		int columnShift = part % 4 * N / 4;
		for(int i = 0; i < result.length; i++)
			for(int j = 0; j < result[i].length; j++) {
				matrix[i + rowShift][j + columnShift] = result[i][j];
			}
	}
	
	/**
	 * Method to transpose matrix B[][] into itself
	 * @param matrix    matrix to be transposed
     * @param N         matrix dimension
	 */
	public static void transpose(float[][] matrix, int N) {
		float temp;
		for(int i = 0; i < N;i++)
			for(int j = 0;j < i;j++) {
				temp = matrix[i][j];
				matrix[i][j] = matrix[j][i];
				matrix[j][i] = temp;
			}
	}

	/**
     * Method to print matrix
     * @param matrix    matrix to print
     */
	public static void printMatrix(float[][] matrix) {
		for(float[] row : matrix) {
			for(float element : row)
				System.out.print(element + "\t");
			System.out.println();
		}
	}
	
	/**
     * Method to get the checksum, by the sum of all its elements 
     * @param matrix
     * @return
     */
	public static float getChecksum(float[][] matrix) {
		float checksum = 0;
		for(float[] row : matrix)
			for(float element : row)
				checksum += element;
		return checksum;
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public static Runnable createTask(int node) { //creates the task that each thread will perform
		return () -> {
			try {
				MatrixMultiplicationRemote connection;
				switch (node) {
					case 1:
						connection = 
								(MatrixMultiplicationRemote)Naming.lookup("rmi://10.0.0.5/test" + node);
						break;
					case 2:
						connection = 
								(MatrixMultiplicationRemote)Naming.lookup("rmi://10.0.0.6/test" + node);
						break;
					case 3:
						connection = 
								(MatrixMultiplicationRemote)Naming.lookup("rmi://10.0.0.7/test" + node);
						break;
					case 4:
						connection = 
								(MatrixMultiplicationRemote)Naming.lookup("rmi://10.0.0.8/test" + node);
						break;
					default:
						break;
				}
				for(int i = 0; i < 4; i++)
						fillMatrix(((node - 1) * 4) + i, c, new MatrixMultiplication().multiply(aSlices[node - 1], bSlices[i]));
			} catch (RemoteException | MalformedURLException| NotBoundException e) {
				e.printStackTrace();
			}
		};
	}

}