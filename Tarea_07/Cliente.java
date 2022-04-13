/**
 * Tarea 7:         Cliente Web estilo REST
 * @author          Ramírez Galindo Karina
 * Compilación:     Para compilar el programa Cliente.java se debe ejecutar el siguiente comando:
 *                  javac -cp gson-2.8.6.jar Cliente.java
 * Ejecución:       Para ejecutar el programa en Windows:
 *                  java -cp gson-2.8.6.jar;. Cliente.java 
 *                  Para ejecutar el programa en Linux:
 *                  java -cp gson-2.8.6.jar:. Cliente.java 
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Cliente{
    
    public static void main(String[] args) throws IOException, Exception {
        boolean salir = false;
        while(!salir){
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Scanner br = new Scanner(System.in);
            System.out.println("CLIENTE WEB");
            System.out.println("****************************************************************************");
            System.out.println("a. Alta usuario");
            System.out.println("b. Consulta usuario");
            System.out.println("c. Borra usuario");
            System.out.println("d. Salir");
            System.out.print("Opcion: ");

            //char opc = br.readLine().charAt(0);
            char opc = br.nextLine().charAt(0);
            switch (opc){
                case 'a':
                    System.out.println("*******************************");
                    System.out.println("Alta Usuario");
                    System.out.println("*******************************");
                    Usuario usuario = new Usuario();

                    System.out.println("Email:");
                    ///usuario.setEmail(br.readLine());
                    usuario.setEmail(br.nextLine());
        
                    System.out.println("Nombre:");
                    ///usuario.setNombre(br.readLine());
                    usuario.setNombre(br.nextLine());
        
                    System.out.println("Apellido Paterno:");
                    ///usuario.setApellidoPaterno(br.readLine());
                    usuario.setApellidoPaterno(br.nextLine());
        
                    System.out.println("Apellido Materno:");
                    ///usuario.setApellidoMaterno(br.readLine());
                    usuario.setApellidoMaterno(br.nextLine());
        
                    System.out.println("Fecha de nacimiento:");
                    ///usuario.setFechaNacimiento(br.readLine());
                    usuario.setFechaNacimiento(br.nextLine());
        
                    System.out.println("Telefono:");
                    ///usuario.setTelefono(br.readLine());
                    usuario.setTelefono(br.nextLine());
        
                    System.out.println("Genero (M/F):");
                    ///usuario.setGenero(br.readLine());
                    usuario.setGenero(br.nextLine());
                    altaUsuario(usuario);
                    break;
                case 'b':
                    System.out.println("*******************************");
                    System.out.println("Consulta Usuario");
                    System.out.println("*******************************");
                    System.out.println("Ingresa el email a consultar:");
                    //String emailConsulta = br.readLine();
                    String emailConsulta = br.nextLine();
                    consultarUsuario(emailConsulta);
                    break;
                case 'c':
                    System.out.println("*******************************");
                    System.out.println("Borra Usuario");
                    System.out.println("*******************************");
                    //borrarUsuario();
                    break;    
                case 'd':
                    //borrarTodos();
                    break;
                case 'e':
                    br.close();
                    salir = true;
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }
        }
    }// fin main
    
    /**
     * Método para dar de alta un Usuario
     */
    public static void altaUsuario(Usuario usuario) throws Exception {
        //URL url = new URL("http://20.127.153.126:8080/Servicio/rest/ws/alta_usuario");
        URL url = new URL("http://20.127.25.141:8080/Servicio/rest/ws/alta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);// true si se va a enviar un "body", en este caso el "body" son los parámetros
        conexion.setRequestMethod("POST");// en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// indica que la peticion estara codificada como URL
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        String body = gson.toJson(usuario);
        String parametros = "usuario=" + URLEncoder.encode(body, "UTF-8");
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        /* se debe verificar si hubo error */
        if (conexion.getResponseCode() == 200) { // no hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta;
            // el metodo web regresa una string en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println("Se agrego el usuario con ID " + respuesta);
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            // dispara una excepcion para terminar el programa
            throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());
        }
        conexion.disconnect();
    }
    /**
     * 
     * @param email
     * @throws IOException
     */
    public static void consultarUsuario(String email) throws IOException {
        //URL url = new URL("http://20.127.153.126:8080/Servicio/rest/ws/consulta_usuario");
        URL url = new URL("http://20.127.25.141:8080/Servicio/rest/ws/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");// en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// indica que la peticion estara codificada como URL
        String parametros = "email=" + URLEncoder.encode(email, "UTF-8");// el metodo web "consultarUsuario" recibe como parametro el email de un usuario
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        /* se debe verificar si hubo error */
        if (conexion.getResponseCode() == 200) { // no hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            String respuesta;
            Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
            while ((respuesta = br.readLine()) != null){
                Usuario usuario = (Usuario) j.fromJson(respuesta, Usuario.class);
                usuario.Imprimir_datos();
            }
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            // dispara una excepcion para terminar el programa
            throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());
        }

        conexion.disconnect();

    }
    
}
class Usuario{
    public Usuario() {
        this.foto = null;
    }
    int id_usuario;
    String email;
    String nombre;
    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;

    /*int getUsuario() { return this.id_usuario;}
    String getEmail() { return this.email; }
    String getNombre() { return this.nombre; }
    String getApellidoPaterno() { return this.apellido_paterno; }
    String getApellidoMaterno() { return this.apellido_materno; }
    String getFechaNacimiento() { return this.fecha_nacimiento; }
    String getTelefono() { return this.telefono; }
    String getGenero() { return this.genero; }
    byte[] getFoto() { return this.foto; }*/

    void setId(int id){ this.id_usuario = id;}
    void setEmail(String email) { this.email = email; }
    void setNombre(String nombre) { this.nombre = nombre; }
    void setApellidoPaterno(String apellidoPaterno) { this.apellido_paterno = apellidoPaterno; }
    void setApellidoMaterno(String apellidoMaterno) { this.apellido_materno = apellidoMaterno; }
    void setFechaNacimiento(String fechaNacimiento) { this.fecha_nacimiento = fechaNacimiento; }
    void setTelefono(String telefono) { this.telefono = telefono; }
    void setGenero(String genero) { this.genero = genero; }
    void setFoto(byte[] foto) { this.foto = foto; }

    public void Imprimir_datos() {
        System.out.println( "Email: " + email + "\n" +
                            "Nombre: " + nombre + "\n" +
                            "Apellido Paterno: " + apellido_paterno + "\n" +
                            "Apellido Materno: " + apellido_materno + "\n" + 
                            "Fecha de nacimiento: " + fecha_nacimiento + "\n" + 
                            "Telefono: " + telefono + "\n" + 
                            "Genero: " + genero + "\n" +
                            "Foto: null");
        }
    }