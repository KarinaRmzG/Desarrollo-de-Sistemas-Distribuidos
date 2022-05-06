package negocio;

import com.google.gson.*;

public class Articulo {
    int id_articulo;
    String nombre;
    String descripcion;
    int cantidad;
    float precio;
    byte[] foto;

    public Articulo(int id_articulo, String nombre, String descripcion, int cantidad, float precio, byte[] foto) {
        this.id_articulo = id_articulo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.foto = foto;
    }

    public Articulo() {

    }

    // @FormParam necesita un metodo que convierta una String al objeto de tipo
    // Articulo
    public static Articulo valueOf(String s) throws Exception {
        Gson j = new GsonBuilder().registerTypeAdapter(byte[].class, new AdaptadorGsonBase64()).create();
        return (Articulo) j.fromJson(s, Articulo.class);
    }
}
