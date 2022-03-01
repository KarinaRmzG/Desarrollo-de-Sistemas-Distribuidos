/***
 * Tarea 01:        Cálculo de Pi
 * @author          Ramírez Galindo Karina
 * Descripción:     Programa distribuido, el cual calculará una aproximación de PI utilizando la 
 *                  serie de Gregory-Leibniz.
 *                  El programa va a ejecutar en forma distribuida sobre cinco nodos.
 *                  El nodo 0 actuará como cliente y los nodos 1, 2, 3 y 4 actuarán como servidores.
 *                  El nodo 0 deberá implementar re-intentos de conexión a cada servidor, de manera que 
 *                  se pueda iniciar la ejecución de cada nodo en cualquier orden.
 */
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Pi{
    static Object obj = new Object();
    static double pi = 0;
    
    
    static class Worker extends Thread {
        Socket conexion;//variable de tipo Socket llamada "conexión" la cual va a contener una instancia de la clase Socket
        int pto_servidor;//puerto donde se estara llevando a cabo la conexion
        
        Worker(int pto){
            this.pto_servidor = pto;
            this.conexion = null;
        }//end constructor Worker
        
        @Override
        public void run(){//cuerpo del hilo
            try{
                conexion = reconexion(pto_servidor);//conexion al servidor 50000 + nodo con re-intentos
                DataInputStream dis = new DataInputStream(conexion.getInputStream()); //stream de entrada para leer datos
                double suma = 0;
                suma = dis.readDouble();
                synchronized(obj){ //Bloque sincronizado
                    pi += suma; //sumatoria y soncronizacion 
                }//synchronized
                dis.close();
                conexion.close();
                System.out.printf("\n Conexion finalizada con el servidor por el puerto %d ",pto_servidor);
            }catch(Exception e){
                System.out.println("Intento de conexion con el pueto: "+ pto_servidor);
                System.out.println(e.getMessage());
            }//end try-catch
        }//end run
    }//end class Worker
    
    /***   ACCIONES DEL CLIENTE
     * Este método hace lo siguiente:
     * 1. Se conecta a cada nodo servidor.
     * 2. Espera el resultado de la sumatoria calculada por cada servidor.
     * 3. El valor de PI será la suma de las cuatro sumatorias calculadas por los servidores.
     * 4. Despliega el valor de PI calculado.
     * 5. Termina el programa.
     */
    public static void cliente(){
            System.out.println("Nodo cliente");
            Worker wk[] = new Worker[4];
            int pto = 0;
            for(int i=0; i < 4; i++){
                pto=50001+i;
                wk[i] = new Worker(pto);//se crean 4 threads del 1 al 4
                wk[i].start();//inicialización de los threads
            }//for
            
            for(int i=0; i < 4; i++){
                try {
                wk[i].join();//esperar a que terminen los threads (barrera)
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }//for
            
            System.out.println("\n\nEl valor de PI es: " + pi);//despliega el valor de la variable Pi
    }//end cliente
    
    /***    ACCIONES DEL SERVIDOR
     * Este método hace lo siguiente:
     * 1. Espera la conexión del nodo cliente (nodo 0).
     * 2. Calcula la sumatoria del millón de términos que le corresponden.
     * 3. Envia al cliente el resultado de la sumatoria.
     * 4. Termina el programa.
     * @param nodo      nodo que corresponde a cada servidor (1-4)
     */
    public static void servidor(int nodo){
        try {
            System.out.printf("Nodo Servidor %d ",nodo);
            System.out.println("\nPuerto: " + (50000+nodo)); 
            ServerSocket servidor = new ServerSocket(50000 + nodo); //abrir el puerto 50000+nodo;
            Socket cliente= servidor.accept();    //esperar la conexión del cliente
            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            double sum = 0;
            for(int i = 0; i <= 999999; i++){
                sum += 4.0 / (8 * i + 2 * (nodo-2) + 3); //calcular la sumatoria
            }
            sum = (nodo % 2 == 0) ? -sum : sum;
            dos.writeDouble(sum);//enviar la sumatoria al cliente
            dos.close();
            cliente.close();
            servidor.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }   
    }//end servidor
    
    /***    REINTENTOS DE CONEXION
     * Este método se encarga de llevar a cabo los re-intentos de conexión del cliente con los servidores
     * @param puerto    indica el numero de puerto donde se establezca la conexión
     * @return          se regresa la conexión 
     */
    public static Socket reconexion(int puerto){
        Socket conexion = null;
        for(;;){
            try {
                conexion = new Socket("localhost",puerto);
                break;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex1) {
                    System.out.println(ex1.getMessage());
                }
            }
        }
        return conexion;
    }//end Socket reconexion
    
   
    //Metodo principal
    public static void main(String[] args) throws Exception {
        if (args.length != 1) { // si la longitud no es 1, se termina el programa 
            System.err.println("Uso: java Pi <nodo>");
            System.exit(0);
	}//end if
	int nodo = Integer.valueOf(args[0]);//convertir args[0] a entero
	if (nodo == 0) {
            cliente();        // Acciones del Cliente
        }else{
            servidor(nodo);   // Acciones del Servidor
        }//else
    }//main
}//Pi