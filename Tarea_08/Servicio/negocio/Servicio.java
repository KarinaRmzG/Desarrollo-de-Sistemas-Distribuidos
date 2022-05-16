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
     
    /*if (articulo.nombre == null || articulo.nombre.equals(""))
      return Response.status(202).entity(j.toJson(new Error("Ingrese el nombre del artículo"))).build();*/

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
             return Response.status(202).entity(j.toJson("Artículo existente con la misma descripción")).build();
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
      conexion.commit();
    }catch (Exception e){
      conexion.rollback();
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
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.id_articulo, a.descripcion, a.precio, a.cantidad_almacen, b.foto FROM articulos a LEFT OUTER JOIN foto_articulos b ON a.id_articulo = b.id_articulo WHERE a.descripcion LIKE ?");
      try{
        stmt_1.setString(1, "%"+descripcion+"%");
            ResultSet rs = stmt_1.executeQuery();
        try{
          if (rs.next()){
            Articulo a = new Articulo();
            a.id_articulo = rs.getInt(1);
            a.descripcion = rs.getString(2);
            a.precio = rs.getFloat(3);
            a.cantidad = rs.getInt(4);
            a.foto = rs.getBytes(5);

            busca_articulo.add(a);//Añadimos el objeto "articulo" al ArrayList
          }
          if(busca_articulo.size() > 0){
            return Response.ok().entity(j.toJson(busca_articulo)).build();
          }else{
            return Response.status(202).entity(j.toJson("No hay coincidencias de búsqueda :( ")).build();
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
      return Response.status(500).entity(j.toJson(new Error(e.getMessage()))).build();
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
    public Response altaCarrito(@FormParam("descripcion") String descripcion, @FormParam("cantidad") int cantidad) throws Exception {
        Connection conexion = pool.getConnection();
        int cantidad_actual = -1;
        int id_articulo = -1;
        try {
            PreparedStatement stmt_1 = conexion
                    .prepareStatement("select id_articulo, cantidad from articulos where descripcion=?");
            try {
                stmt_1.setString(1, descripcion);
                ResultSet rs = stmt_1.executeQuery();
                try {
                    if (rs.next()) {
                        id_articulo = rs.getInt(1);
                        cantidad_actual = rs.getInt(2);
                    } else {
                        return Response.status(202).entity(j.toJson("El articulo ya no se encuentra en Stock"))
                                .build();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt_1.close();
            }
            if (cantidad <= cantidad_actual) {

                conexion.setAutoCommit(false);
                PreparedStatement stmt_2 = conexion
                        .prepareStatement("UPDATE articulos SET cantidad=? WHERE id_articulo=?");
                try {
                    stmt_2.setInt(1, cantidad_actual - cantidad);
                    stmt_2.setInt(2, id_articulo);
                    stmt_2.executeUpdate();
                } finally {
                    stmt_2.close();
                }
                PreparedStatement stmt_3 = conexion.prepareStatement("insert into carrito_compra VALUES (0,?,?)");
                try {
                    stmt_3.setInt(1, id_articulo);
                    stmt_3.setInt(2, cantidad);
                    stmt_3.executeUpdate();
                } finally {
                    stmt_3.close();
                }
                conexion.commit();

            } else {
                return Response.status(202).entity(j.toJson("Cantidad de piezas disponibles: "+cantidad_actual)).build();
            }
        } catch (Exception e) {
            conexion.rollback();
            return Response.status(500).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.status(200).entity(j.toJson("ok")).build();
    }

    /**
     * Método para consultar carrito
     * @return
     * @throws Exception
     */
    @POST
    @Path("consulta_carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaCarrito() throws Exception{
      ArrayList <Articulo> articulos_carrito = new ArrayList<Articulo>();
      Connection conexion= pool.getConnection();
      try{
        PreparedStatement stmt_1 = conexion.prepareStatement("SELECT a.id_articulo, a.descripcion, a.precio, b.cantidad, c.foto FROM carrito_compra b LEFT OUTER JOIN articulos a ON a.id_articulo = b.id_articulo LEFT OUTER JOIN foto_articulos c ON b.id_articulo = c.id_articulo");
        
        try{
          ResultSet rs = stmt_1.executeQuery();
          try{
            if (rs.next()){
              Articulo a = new Articulo();
              a.id_articulo = rs.getInt(1);
              a.descripcion = rs.getString(2);
              a.precio = rs.getFloat(3);
              a.cantidad = rs.getInt(4);
              a.foto = rs.getBytes(5);
              articulos_carrito.add(a);
            }
  
            if(articulos_carrito.size() > 0){
              return Response.ok().entity(j.toJson(articulos_carrito)).build();
            }else{
              return Response.status(202).entity(j.toJson(new Error("No hay articulos que coincidan :("))).build();
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
        return Response.status(500).entity(j.toJson(new Error(e.getMessage()))).build();
      }
      finally{
        conexion.close();
      }
    }

    /**
     * Método para eliminar todo el carrito de compras
     * @return
     * @throws Exception
     */
    @POST
    @Path("elimina_carrito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminaCarrito() throws Exception {
        Connection conexion = pool.getConnection();
        ArrayList<Integer> id_a_carrito = new ArrayList<Integer>();
        ArrayList<Integer> a_carrito = new ArrayList<Integer>();
        ArrayList<Integer> a_stock = new ArrayList<Integer>();
        String msj = "";
        try{
          PreparedStatement stmt_1 = conexion.prepareStatement("select id_articulo, cantidad from carrito_compra");
            try{
                ResultSet rs = stmt_1.executeQuery();
                try{
                    while(rs.next()){
                        id_a_carrito.add(rs.getInt(1));
                        a_carrito.add(rs.getInt(2));
                    }
                }finally{
                    rs.close();
                }
            }finally{
                stmt_1.close();
            }
            for(Integer id_articulo: id_a_carrito){
                PreparedStatement stmt_2 = conexion
                    .prepareStatement("select cantidad from articulos where id_articulo=?");
                try{
                    stmt_2.setInt(1, id_articulo);
                    ResultSet rs = stmt_2.executeQuery();
                    try{
                        if(rs.next()){
                            a_stock.add(rs.getInt(1));
                        }
                    }finally{
                        rs.close();
                    }
                }finally{
                    stmt_2.close();
                }
            }

            conexion.setAutoCommit(false);

            //Eliminar elementos del carrito
            for(Integer id_articulo: id_a_carrito){
                PreparedStatement stmt_3 = conexion
                    .prepareStatement("delete from carrito_compra where id_articulo=?");
                try{
                    stmt_3.setInt(1, id_articulo);
                    stmt_3.executeUpdate();
                }finally{
                    stmt_3.close();
                }
            }
            /// ---- end Eliminar elementos del carrito ----

            /// ------ Actualizar elementos del stock ------
            for(int pos = 0; pos < id_a_carrito.size(); pos++){
                PreparedStatement stmt_4 = conexion
                    .prepareStatement("UPDATE articulos SET cantidad=? WHERE id_articulo=?");
                try{
                    stmt_4.setInt(1, a_carrito.get(pos) + a_stock.get(pos) );
                    stmt_4.setInt(2, id_a_carrito.get(pos) );
                    stmt_4.executeUpdate();
                }finally{
                    stmt_4.close();
                }
            }
            /// ---- end Actualizar elementos del stock ----

            conexion.commit(); /// ------ Finaliza Transaccion ------

        } catch (Exception e) {
            conexion.rollback();
            return Response.status(500).entity(j.toJson(new Error(e.getMessage()))).build();
        } finally {
            conexion.setAutoCommit(true);
            conexion.close();
        }
        return Response.status(200).entity(j.toJson("ok")).build();
    }//end public Response eliminaCarrito()
}
