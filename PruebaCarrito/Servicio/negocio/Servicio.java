/*
  Servicio.java
  Servicio web tipo REST
  Carlos Pineda Guerrero, marzo 2022.
*/

package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import com.google.gson.*;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio
{
  static DataSource pool = null;
  static
  {		
    try
    {
      Context ctx = new InitialContext();
      pool = (DataSource)ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder()
		.registerTypeAdapter(byte[].class,new AdaptadorGsonBase64())
		.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
		.create();
  
  /**
   * Método Para dar de alta un artículo
   * @param articulo
   * @return
   * @throws Exception
   */
  @POST
  @Path("alta_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(@FormParam("articulo") Articulo articulo) throws Exception{
    Connection conexion = pool.getConnection();

    //El estado HTTP 202 indica que la solicitud se ha aceptado para su procesamiento, 
    //pero el procesamiento no se ha completado.
     
    if (articulo.nombre == null || usuario.nombre.equals(""))
      return Response.status(202).entity(j.toJson(new Error("Ingrese el nombre del artículo"))).build();

    if (articulo.descripcion == null || articulo.descripcion.equals(""))
      return Response.status(202).entity(j.toJson(new Error("Ingrese la descripción del artículo"))).build();

    if (articulo.precio <= 0.0f)
      return Response.status(202).entity(j.toJson(new Error("Precio no válido"))).build();

    if (articulo.cantidad <= 0)
      return Response.status(202).entity(j.toJson(new Error("Cantidad no válida"))).build();

    if (articulo.foto == null)
      return Response.status(202).entity(j.toJson(new Error("Ingrese fotografía del artículo"))).build();
    
    
    //COMIENZA TRANSACCIÓN
    try{
      conexion.setAutoCommit(false);//para mantener la integridad de los datos. Una operación de actualización del sistema puede involucrar múltiples tablas y requerir múltiples sentencias SQL para operar
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_articulo FROM articulos WHERE description=?");
      try{
        stmt_1.setString(1,articulo.descripcion);
        ResultSet rs = stmt_1.executeQuery();
        try{
          if (rs.next())
             return Response.status(202).entity(j.toJson(new Error("Artículo existente con la misma descripción"))).build();
        }
        finally{
          rs.close();
        }
      }
      finally{
        stmt_1.close();
      }

      PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO articulos VALUES (0,?,?,?,?)");
      try{
        stmt_2.setString(1,articulo.descripcion);
        stmt_2.setFloat(2,articulo.precio);
        stmt_2.setInt(3,articulo.cantidad);
        stmt_2.setBytes(4,articulo.foto);
        stmt_2.executeUpdate();
      }
      finally{
        stmt_2.close();
      }

      //ALTA IMAGEN
      if (articulo.foto != null){
        PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO foto_ARTICULO VALUES (0,?,(SELECT id_articulo FROM articulos WHERE descripcion=?))");
        try{
          stmt_3.setBytes(1,articulo.foto);
          stmt_3.setString(2,articulo.descripcion);
          stmt_3.executeUpdate();
        }
        finally{
          stmt_3.close();
        }
      }
    }catch (Exception e){
      return Response.status(500).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally{
      conexion.setAutoCommit(true);
      conexion.close();
    }
    return Response.status(200).entity(j.toJson("ok")).build();
  }

  /**
   * Método para consultar artículos utilizando la cláusula LIKE
   * @param email
   * @return
   * @throws Exception
   */
  @POST
  @Path("consulta_articulo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consulta(@FormParam("descripcion") String descripcion) throws Exception{
    Connection conexion= pool.getConnection();
    ArrayList<Articulo> busca_articulo = new ArrayList<Articulo>();
    try{
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.cantidad_almacen, b.foto FROM articulos a LEFT OUTER JOIN foto_articulos b ON a.id_articulo = b.id_articulo WHERE a.descripcion LIKE ?");
      try{
        stmt_1.setString(1, "%"+descripcion+"%");
            ResultSet rs = stmt_1.executeQuery();
        try{
          if (rs.next()){
            Articulo a = new Articulo();
            a.id_articulo = rs.getInt(1);
            a.nombre = rs.getString(2);
            a.descripcion = rs.getString(3);
            a.precio = rs.getFloat(4);
            a.cantidad = rs.getInt(5);
            a.foto = rs.getBytes(6);

            busca_articulo.add(a);//Añadimos el objeto "articulo" al ArrayList
          }
          if(busca_articulo.size() > 0){
            return Response.ok().entity(j.toJson(r)).build();
          }else{
            return Response.status(400).entity(j.toJson(new Error("No hay coincidencias de búsqueda :( "))).build();
          }
        }
        finally{
          rs.close();
        }
      }
      finally{
        stmt_1.close();
      }
    }catch (Exception e){
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
  }

  /**
   * Método para el carrito de compras
   * @param usuario
   * @return
   * @throws Exception
   */
  @POST
  @Path("alta_carrito")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response modifica(@FormParam("articulo") Articulo articulo) throws Exception{
    Connection conexion= pool.getConnection();
    int stock = 0;
    int id_articulo_carrito;

    if(articulo.cantidad < 0)

    if (usuario.email == null || usuario.email.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el email"))).build();

    if (usuario.nombre == null || usuario.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre"))).build();

    if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el apellido paterno"))).build();

    if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la fecha de nacimiento"))).build();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("UPDATE usuarios SET nombre=?,apellido_paterno=?,apellido_materno=?,fecha_nacimiento=?,telefono=?,genero=? WHERE email=?");
      try
      {
        stmt_1.setString(1,usuario.nombre);
        stmt_1.setString(2,usuario.apellido_paterno);
        stmt_1.setString(3,usuario.apellido_materno);
        stmt_1.setString(4,usuario.fecha_nacimiento);
        stmt_1.setString(5,usuario.telefono);
        stmt_1.setString(6,usuario.genero);
        stmt_1.setString(7,usuario.email);
        stmt_1.executeUpdate();
      }
      finally
      {
        stmt_1.close();
      }

      if (usuario.foto != null)
      {
        PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=(SELECT id_usuario FROM usuarios WHERE email=?)");
        try
        {
          stmt_2.setString(1,usuario.email);
          stmt_2.executeUpdate();
        }
        finally
        {
          stmt_2.close();
        }

        PreparedStatement stmt_3 = conexion.prepareStatement("INSERT INTO fotos_usuarios VALUES (0,?,(SELECT id_usuario FROM usuarios WHERE email=?))");
        try
        {
          stmt_3.setBytes(1,usuario.foto);
          stmt_3.setString(2,usuario.email);
          stmt_3.executeUpdate();
        }
        finally
        {
          stmt_3.close();
        }
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
    return Response.ok().build();
  }

  @POST
  @Path("borra_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borra(@FormParam("email") String email) throws Exception
  {
    Connection conexion= pool.getConnection();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT 1 FROM usuarios WHERE email=?");
      try
      {
        stmt_1.setString(1,email);

        ResultSet rs = stmt_1.executeQuery();
        try
        {
          if (!rs.next())
		return Response.status(400).entity(j.toJson(new Error("El email no existe"))).build();
        }
        finally
        {
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }
      PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=(SELECT id_usuario FROM usuarios WHERE email=?)");
      try
      {
        stmt_2.setString(1,email);
	stmt_2.executeUpdate();
      }
      finally
      {
        stmt_2.close();
      }

      PreparedStatement stmt_3 = conexion.prepareStatement("DELETE FROM usuarios WHERE email=?");
      try
      {
        stmt_3.setString(1,email);
	stmt_3.executeUpdate();
      }
      finally
      {
        stmt_3.close();
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
    return Response.ok().build();
  }
}
