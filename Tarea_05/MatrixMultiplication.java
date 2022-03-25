
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
 * 					4. Run the program "java RMIClient <size>" 
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrixMultiplication extends UnicastRemoteObject implements MatrixMultiplicationRemote {
	
	public MatrixMultiplication() throws RemoteException {
		super();
	}

	public float[][] multiply(float[][] a, float[][] b) { //Multiplies two matrices implying one has been transposed
		float[][] c = new float[a.length][b.length];
		for (int i = 0; i < c.length; i++)
		      for (int j = 0; j < c[i].length; j++)
		        for (int k = 0; k < a[0].length; k++)
		           c[i][j] += a[i][k] * b[j][k];
		return c;
	}

}
