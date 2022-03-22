import java.rmi.Naming;

public class ServidorRMI {
    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost/prueba";
        ClaseRMI objetoRMI = new ClaseRMI();
        Naming.rebind(url, objetoRMI);
    }
}