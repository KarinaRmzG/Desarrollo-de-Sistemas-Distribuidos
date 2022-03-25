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

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ServerRMI {
	
	private static String url = "rmi://localhost/test";

	public static void main(String[] args) {
		if (args.length < 1){
            System.out.println("Use: java ServerRMI <node>");
            return;
        }
		int node = Integer.parseInt(args[0]);
	    try {
	    	MatrixMultiplication obj = new MatrixMultiplication();
			Naming.rebind(url + node,obj);

			System.out.println("ServerRMI of node: " + node +  " ready");
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}

	}

}