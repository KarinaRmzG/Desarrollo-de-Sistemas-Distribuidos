let foto = null;  // por default la foto es nula
function get(id) {
  return document.getElementById(id);
}
function readSingleFile(files, imagen) {
  let file = files[0];
  if (!file) return;
  let reader = new FileReader();
  reader.onload = function (e) {
    imagen.src = reader.result;
    // reader.result incluye al principio: "data:image/jpeg;base64,"
    foto = reader.result.split(',')[1];
  };
  reader.readAsDataURL(file);
}
function enviar_formulario(event) {
  event.preventDefault();//Evitamos que el formulario se propague
  swal("Enviando ..."); //alert("Enviando ...");
  let enviar = true;
  let descripcion = get("descripcion").value;
  let precio = get("precio").value;
  let cantidad = get("cantidad").value;
  if (descripcion === '') {
    swal({
      title: "Ops!",
      text: "Insete la descripción del articulo",
      icon: "warning", //"success","info","error","warning"
      button: "Ok",
    });
    enviar = false;
  }
  if (precio === '') {
    swal({
      title: "¡Cuidado!",
      text: "No olvides el precio",
      icon: "warning", //"success","info","error","warning"
      button: "Ok",
    });
    enviar = false;
  }
  if (cantidad === '') {
    swal({
      title: "¡Cuidado!",
      text: "Inserte artículos",
      icon: "error", //"success","info","error","warning"
      button: "Ok",
    });
    enviar = false;
  }
  if (foto == null) {
    swal({
      title: "Insere foto del articulo",
      icon: "error", //"success","info","error","warning"
      button: "Ok",
    });
    enviar = false;
  }
  let informacion = {
    articulo: {
      descripcion: descripcion,
      cantidad: cantidad,
      precio: precio,
      imagen: foto,
    }
  };
  if (enviar) registraArticulo(informacion);
}

function registraArticulo(informacion) {
  //Recorrer cada elemento del objeto, el cual se serializa en formato URL
  let pairs = [];
  for (objeto in informacion) {
    let valor = informacion[objeto];
    if (typeof valor !== "string") valor = JSON.stringify(valor);
    pairs.push(objeto + "=" + encodeURI(valor).replace(/=/g, '%3D').replace(/&/g, '%26').replace(/%20/g, '+'));
  }
  //Empaquetamos los objetos serializados en formato URL
  let body = "";
  body = pairs.join("&");
  //Le decimos a la peticion HTTP que vamos usar el formato URL de serializacion
  let head = new Headers();
  head.set('Content-Type', 'application/x-www-form-urlencoded');
  //Enviar la peticion HTTP diciendo el tipo de metodo, encabezado e informacion serializada
  swal({
    title: "¿Esta de acuerdo?",
    text: "El producto/articulo sera registrado",
    icon: "warning",
    buttons: {
      defeat: {
        text: "Simon",
        closeModal: false,
      },
      cancel: 'Ni maiz',
    },
    dangerMode: true,
  }).then((respuesta) => { //Respuesta del boton
    if (respuesta == "defeat") {
      return fetch("/Servicio/rest/ws/alta_articulo", {
        method: 'POST',
        headers: head,
        body: body,
      });
    }
    if (respuesta === null) return new Response("null");
  }).then(results => {//Respuesta del fetch
    return results.json()
  }).then((respuesta) => {
    if (respuesta != null) {
      if (respuesta == 'ok') {
        swal("Respuesta", `${respuesta}`, "success");
        setTimeout(() => {
          window.location.href = "/compraArticulos.html";
        }, 1000);
      } else {
        swal("Respuesta", `${respuesta}`, "info");
      }
    }
  }).catch((error) => {
    swal("Error", `${error}`, "error");
    console.log(error);
  });
}

function quita_foto() {
  foto = null;
  get('consulta_imagen').src = 'usuario_sin_foto.png';
  get('consulta_file').value = '';
}
