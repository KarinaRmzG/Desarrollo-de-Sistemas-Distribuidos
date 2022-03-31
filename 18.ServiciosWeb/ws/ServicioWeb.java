/**
 * Actividad 18:    Servicio web SOAP utilizando JDK8.
 * @author          Ramírez Galindo Karina
 * Dscripción:      En este caso, nuestro servicio web va incluir las operaciones suma 
 *                  (suma dos números punto flotante), mayúsculas (convierte a mayúsculas una 
 *                  cadena de caracteres) y suyma2 (suma los elementos de dos listas de enteros).
 * Compilación:     Para compilar el servicio web ejecutamos el siguiente comando en el directorio padre 
 *                  del directorio "ws":
 *                  javac ws/ServicioWeb.java
 */
package ws;
  import javax.jws.WebService;
  import javax.jws.WebMethod;
  import javax.jws.WebParam;
  import java.util.ArrayList;
  import java.util.List;
  @WebService
  public class ServicioWeb
  {
    @WebMethod
    public double suma(@WebParam(name="a") double a,@WebParam(name="b") double b)
    {
      return a + b;
    }
    @WebMethod
    public String mayusculas(@WebParam(name="s") String s)
    {
      return s.toUpperCase();
    }
    @WebMethod
    public List<Integer> suma2(@WebParam(name="a") List<Integer> a,@WebParam(name="b") List<Integer> b)
    {
      List<Integer> c = new ArrayList<Integer>();
      for (int i = 0; i < a.size(); i++)
        c.add(a.get(i) + b.get(i));
      return c;
    }
  }

