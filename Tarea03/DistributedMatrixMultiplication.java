/**
 * Program:         Distributed matrix multiplication using message passing 
 * Students:        - Ramírez Galindo Karina
 *                  - Toledo Espinosa Cristina Aline
 *                  - Vásquez Hérnandez Alan Mauricio
 * Languaje:        Java
 * Description:     Compute the product of two square matrices in distributed form over four nodes
 * Way to run it:	java DistributedMatrixMultiplication <node> <N> <ip>
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
//para ejecutar :  java DistrubutedMatrixMultiplication nodo N
public class DistributedMatrixMultiplication {
	static String ip;
    private static int N;                       //Matrix dimension 
	static String ip_Nodo_1;    //ip del nodo servidor 1    (Cliente)
    static String ip_Nodo_2;    //ip del nodo servidor 2    (Cliente)
    static String ip_Nodo_3;    //ip del nodo servidor 3    (Cliente)
	
    public static void main(String[] args) throws InterruptedException{
        int node = Integer.valueOf(args[0]);    //Variable to know the number of the node
        N = Integer.valueOf(args[1]); 
		ip = args[1];          
        //Matrix initialization
		double[][] a = new double[N][N];
		double[][] b = new double[N][N];
		
		for (int i = 0; i < N; i++)
		      for (int j = 0; j < N; j++) {
		    	  a[i][j]= i + 5 * j;
		    	  b[i][j] = 5 * i - j;
		      }
		if (node == 0) //If node is 0 then init as a client, otherwise init as a server
			try {
				initClient(a,b);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        else
            initServer(node, 50000 + node); //local
			//initServer(node, 50000); //virtual machine
    }
    
    /**
     * Method to multiply A[][] by B[][], result C[][]
     * @param a         matrix A[][]
     * @param b         matrix B[][]
     * @return          matrix C[][]
     */
    public static double[][] multiply(double[][] a, double[][] b) { //Multiplies two matrices implying one has been transposed
		double[][] c = new double[a.length][b.length];
		for (int i = 0; i < c.length; i++)
		      for (int j = 0; j < c[i].length; j++)
		        for (int k = 0; k < a[0].length; k++)
		           c[i][j] += a[i][k] * b[j][k];
		return c;
	}
	
	/**
     * Method to transpose matrix B[][] into itself
     * @param matrix    matrix to be transposed
     * @param N         matrix dimension
     */
	public static void transpose(double[][] matrix, int N) {
		double temp;
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
	public static void printMatrix(double[][] matrix) {
		for(double[] row : matrix) {
			for(double element : row)
				System.out.print(element + "\t");
			System.out.println();
		}
	}
	
    /**
     * Method to fill matrix
     * @param node          
     * @param matrix
     * @param result
     */
	public static void fillMatrix(int node, double[][] matrix, double[][] result) {
		int rowShift = node > 2 ? N/2: 0; //row shift only occurs on node 2 & 0
		int columnShift = (node + 1) % 2 * N/2; //column shift only occurs on node 3 & 0
		for(int i = 0; i < result.length ;i++)
			for(int j = 0; j < result[i].length; j++) {
				matrix[i + rowShift][j + columnShift] = result[i][j];
			}
	}
	
    /**
     * Method to get the checksum, by the sum of all its elements 
     * @param matrix
     * @return
     */
	public static double getChecksum(double[][] matrix) {
		double checksum = 0;
		for(double[] row : matrix)
			for(double element : row)
				checksum += element;
		return checksum;
	}
	
	
    /**
     * Client functions
     * @param a
     * @param b
     * @throws InterruptedException
     */
	public static void initClient(double[][] a, double[][] b) throws InterruptedException {
		
		//Slicing the matrices
		double[][] a1 = new double[N/2][];
		double[][] a2 = new double[N/2][];
		double[][] b1 = new double[N/2][];
		double[][] b2 = new double[N/2][];
		for(int i = 0; i < a1.length;i++) {
			a1[i] = a[i];
			a2[i] = a[i + N/2];
			b1[i] = b[i];
			b2[i] = b[i + N/2];
		}
		if(N == 8) {
			System.out.println("Matrix A:");
			printMatrix(a);
			System.out.println("Matrix B:");
			printMatrix(b);
		}
		transpose(b, N);
		double[][] c = new double[N][N];
		ExecutorService service = null;
		try {
			double result = 0;
			service = Executors.newFixedThreadPool(3); //Creates a pool of 3 threads
			List<Callable<double[][]>> serverTasks = new ArrayList<>();
			//Creates tasks for each thread
			serverTasks.add(createTask(1,a1,b1));
			serverTasks.add(createTask(2,a1,b2));
			serverTasks.add(createTask(3,a2,b1));
			List<Future<double[][]>> serverResults = service.invokeAll(serverTasks); //Executes all tasks and receives a Future for each one
			double[][] c4 = multiply(a2,b2);
			for (int i = 0; i < serverResults.size();i++) {
				while (!serverResults.get(i).isDone()) //If thread result is done, then add it to the final result
					Thread.sleep(100);
				fillMatrix(i + 1,c,serverResults.get(i).get());
			}
			fillMatrix(4,c,c4); //Fill results of node 0
			if (N == 8) {
				System.out.println("The result is -> Matrix C:");
				printMatrix(c);
			}
			System.out.println("Checksum: " + getChecksum(c));
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			if(service != null) service.shutdown();
		}
		//
	}
	
    /**
     * Server functions
     * @param node      connection node
     * @param port      connection port
     */
	public static void initServer(int node, int port) {
        System.out.printf(" Server Node %d\n port: %d",node,50000+node);//local
		//System.out.printf(" Server Node %d\n port %d",node,50000);// virtual machine
		try(ServerSocket server = new ServerSocket(port);
			Socket connection = server.accept();

			//System.out.prinf(connection.getPort());//direccion y puerto de donde recivo la conexión
			ObjectInputStream input = new ObjectInputStream(
					connection.getInputStream());
			ObjectOutputStream output = new ObjectOutputStream(
					connection.getOutputStream())) { //Connects with the node 0
			System.out.println("Connection to the node " + node + " accepted");
			double[][] a = (double[][]) input.readObject();
			double[][] b = (double[][]) input.readObject();
			double[][] c = multiply(a,b);
			output.writeObject(c);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * The Callable() method of Executors class returns a Callable object that, when called, 
     * runs the given task and returns null.
     * @param node          connection node
     * @param a             matrix A[][]
     * @param b             matrix B[][]
     * @return              result
     */
	public static Callable<double[][]> createTask(int node, double[][] a, double[][] b) { //Returns a callable containing the task to be executed by the threads
		return () -> {
			Socket socket = null;
			while(true) { //Waits until the connection with the server is established
				try {
					//socket = new Socket(ip, 50000 + node);
					//socket = new Socket("localhost", 50000);
					socket = new Socket(ip, 50000);
					System.out.println("Connection to the node " + node
							+ " accepted");
					System.out.println("Connection to the port" + socket.getPort()
							+ " accepted");
					break;
				} catch (IOException e) {
						Thread.sleep(100);
				}
			}
			double[][] serverResult = null;
			try(ObjectOutputStream output = new ObjectOutputStream(
					socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(
						socket.getInputStream())) {
				output.writeObject(a);
				output.writeObject(b);
				serverResult = (double[][]) input.readObject();
				System.out.println("Matrix received");
			}
			socket.close();
			return serverResult;
		};
	}
}
