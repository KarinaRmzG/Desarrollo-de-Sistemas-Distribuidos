/**
 * Tarea 7:         Cliente Web estilo REST
 * @author          Ramírez Galindo Karina
 * Compilación:     Para compilar el programa Cliente.java se debe ejecutar el siguiente comando:
 *                  javac -cp gson-2.8.6.jar Cliente.java
 * Ejecución:       Para ejecutar el programa en Windows:
 *                  java -cp gson-2.8.6.jar;. Cliente
 *                  Para ejecutar el programa en Linux:
 *                  java -cp gson-2.8.6.jar:. Cliente
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente{
    
    public static void main(String[] args) throws IOException, Exception {
        boolean salir = false;
        while(!salir){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            //Scanner br = new Scanner(System.in);
            System.out.println("MENU");
            System.out.println("****************************************************************************");
            System.out.println("a. Alta usuario");
            System.out.println("b. Consulta usuario");
            System.out.println("c. Borra usuario");
            System.out.println("d. Salir");
            System.out.print("Opcion: ");

            char opc = br.readLine().charAt(0);
            switch (opc){
                case 'a':
                    System.out.println("*******************************");
                    System.out.println("Alta Usuario");
                    System.out.println("*******************************");
                    Usuario usuario = new Usuario();

                    System.out.println("Email:");
                    usuario.setEmail(br.readLine());
        
                    System.out.println("Nombre:");
                    usuario.setNombre(br.readLine());
        
                    System.out.println("Apellido Paterno:");
                    usuario.setApellidoPaterno(br.readLine());
        
                    System.out.println("Apellido Materno:");
                    usuario.setApellidoMaterno(br.readLine());
        
                    System.out.println("Fecha de nacimiento:");
                    usuario.setFechaNacimiento(br.readLine());
        
                    System.out.println("Telefono:");
                    usuario.setTelefono(br.readLine());
        
                    System.out.println("Genero (M/F):");
                    usuario.setGenero(br.readLine());

                    altaUsuario(usuario);
                    break;
                case 'b':
                    System.out.println("*******************************");
                    System.out.println("Consulta Usuario");
                    System.out.println("*******************************");
                    System.out.println("Ingresa el email a consultar:");
                    String emailConsulta = br.readLine();
                    consultarUsuario(emailConsulta);
                    break;
                case 'c':
                    System.out.println("*******************************");
                    System.out.println("Borra Usuario");
                    System.out.println("*******************************");
                    System.out.println("Ingresa el email a borrar:");
                    String emailBorra = br.readLine();
                    borrarUsuario(emailBorra);
                    break;    
                case 'd':
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
        URL url = new URL("http://20.127.153.126:8080/Servicio/rest/ws/alta_usuario");
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
            while ((respuesta = br.readLine()) != null) System.out.println("BIENVENIDO"+respuesta);
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta; // el metodo web regresa una instancia de la clase Error en formato JSON
            System.out.println("ERROR:");
            while ((respuesta = br.readLine()) != null) System.out.println(respuesta);
            //throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());// dispara una excepcion para terminar el programa
        }
        conexion.disconnect();
    }

    /**
     * 
     * @param email
     * @throws IOException
     */
    public static void consultarUsuario(String email) throws IOException {
        Scanner sc = new Scanner(System.in);
        char opc2 = ' ';
        URL url = new URL("http://20.127.153.126:8080/Servicio/rest/ws/consulta_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");// en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// indica que la peticion estara codificada como URL
        String parametros = "email=" + URLEncoder.encode(email, "UTF-8");// el metodo web "consultarUsuario" recibe como parametro el email de un usuario
        try (OutputStream os = conexion.getOutputStream()) {
            os.write(parametros.getBytes());
            os.flush();
        }
        /* se debe verificar si hubo error */
        if (conexion.getResponseCode() == 200) { // no hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
            Usuario usuario_consultado = new Usuario();
            String respuesta;
            Gson j = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
            while ((respuesta = br.readLine()) != null){
                usuario_consultado = (Usuario) j.fromJson(respuesta, Usuario.class);
                usuario_consultado.Imprimir_datos();
            }
            System.out.println("Desea modificar el usuario? (S/N):");
            opc2 = sc.next().charAt(0);
			sc.nextLine();
            if(opc2 == 's'){
                Scanner sc2 = new Scanner(System.in);
                System.out.println("*******************************");
                System.out.println("Modifica Usuario");
                System.out.println("*******************************");
                Usuario usuario = new Usuario();

                usuario.setEmail(email);
        
                System.out.println("Nombre:");
                usuario.nombre = sc.nextLine();
                if(usuario.nombre.equals("")) usuario.nombre = usuario_consultado.nombre;
                
        
                System.out.println("Apellido Paterno:");
                usuario.apellido_paterno = sc.nextLine();
                if(usuario.apellido_paterno.equals("")) usuario.apellido_paterno = usuario_consultado.apellido_paterno;
        
                System.out.println("Apellido Materno:");
                usuario.apellido_materno = sc.nextLine();
                if(usuario.apellido_materno.equals("")) usuario.apellido_materno = usuario_consultado.apellido_materno;
        
                System.out.println("Fecha de nacimiento:");
                usuario.fecha_nacimiento = sc.nextLine();
                if(usuario.fecha_nacimiento.equals("")) usuario.fecha_nacimiento = usuario_consultado.fecha_nacimiento;
        
                System.out.println("Telefono:");
                usuario.telefono = sc.nextLine();
                if(usuario.telefono.equals("")) usuario.telefono = usuario_consultado.telefono;
        
                System.out.println("Genero (M/F):");
                usuario.genero = sc.nextLine();
                if(usuario.genero.equals("")) usuario.genero = usuario_consultado.genero;

                try {
                    modificarUsuario(usuario);
                } catch (Exception ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else { // hubo error
            BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
            String respuesta;
            // el método web regresa una instancia de la clase Error en formato JSON
            System.out.println("ERROR: ");
            while ((respuesta = br.readLine()) != null)
                System.out.println(respuesta);
            //throw new RuntimeException("Codigo de error HTTP: " + conexion.getResponseCode());// dispara una excepcion para terminar el programa
        }
        conexion.disconnect();
    }

    public static void modificarUsuario(Usuario usuario) throws Exception {
        URL url = new URL("http://20.127.153.126:8080/Servicio/rest/ws/modifica_usuario");
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
		if(conexion.getResponseCode() == 200){
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
			String respuesta;
			System.out.println("El usuario ha sido modificado");
			while ((respuesta = br.readLine()) != null ) System.out.println(respuesta);
		}else{
			BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
			String respuesta;
			System.out.println("ERROR: ");
			while((respuesta = br.readLine()) != null) System.out.println(respuesta);
		}
		conexion.disconnect();
    }

    public static void borrarUsuario(String email) throws IOException {
        URL url = new URL("http://20.127.153.126:8080/Servicio/rest/ws/borra_usuario");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setDoOutput(true);
        conexion.setRequestMethod("POST");// en este caso utilizamos el metodo POST de HTTP
        conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// indica que la peticion estara codificada como URL
        String parametros = "email=" + URLEncoder.encode(email, "UTF-8");// el metodo web "consultarUsuario" recibe como parametro el email de un usuario
        OutputStream os = conexion.getOutputStream();
        os.write(parametros.getBytes());
        os.flush();
        /* se debe verificar si hubo error */
        try {
            if (conexion.getResponseCode() == 200) { // no hubo error
                /*BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getInputStream())));
                String respuesta;*/
                System.out.println("El usuario con email: "+email+" ha sido borrado");
            } else { // hubo error
                BufferedReader br = new BufferedReader(new InputStreamReader((conexion.getErrorStream())));
                String respuesta;
                // el método web regresa una instancia de la clase Error en formato JSON
                System.out.println("ERROR: ");
                while ((respuesta = br.readLine()) != null)
                    System.out.println(respuesta);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conexion.disconnect();
    }
}
class Usuario{
    public Usuario() {
        this.foto = null;
    }
    
    String email;
    String nombre;
    String apellido_paterno;
    String apellido_materno;
    String fecha_nacimiento;
    String telefono;
    String genero;
    byte[] foto;
    
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