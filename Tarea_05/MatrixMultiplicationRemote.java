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
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixMultiplicationRemote extends Remote {
	
	public float[][] multiply(float[][] a, float[][] b) throws RemoteException;

}
