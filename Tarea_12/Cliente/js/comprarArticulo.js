function buscar() {
  let busqueda = document.getElementById("busca").value;
  swal(busqueda);
  let informacion = {
    busqueda,
  }
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
    title: "¡A buscar!",
    text: "Ahi te voy san pedro",
    icon: "warning",
    buttons: {
      defeat: {
        text: "Si",
        closeModal: false,
      },
      cancel: 'No',
    },
    dangerMode: true,
  }).then((respuesta) => { //Respuesta del boton
    if (respuesta == "defeat") {
      return fetch("/Servicio/rest/ws/consulta_articulos", {
        method: 'POST',
        headers: head,
        body: body,
      }).then(response => {
        if (!response.ok) {
          throw new Error(response.statusText);
        }
        return response.json();
      });
    }
    if (respuesta === null) return new Response("null");
  }).then((respuesta) => {
    if (respuesta != null) {
      if (Array.isArray(respuesta)) {
        // swal("Genial", "Si hay merca wey!!!", "success");
        swal.close();
        agregaArticulos(respuesta);
        respuesta.map(console.log);
      } else {
        swal("Respuesta", `${respuesta}`, "info");
      }
    }
  }).catch((error) => {
    swal("Error", `${error}`, "error");
    console.log(error);
  });
}

function agregaArticulos(articulos) {
  let listaArti = "";
  articulos.map((e) => {
    listaArti += `
        <div class="col d-flex justify-content-center-mb-4 animate__animated animate__fadeInDown animate__fast">
          <div class="card shadow mb-1 rounded" style="width: 20rem;">
            <img src="data:image/jpeg;base64,${e.imagen}" alt='foto' />
            <div class="card-body">
              <p class="card-text text-white-50 description">${e.descripcion}</p>
              <h5>Precio: <span class="precio">$ ${e.precio}</span></h5>
              <h5>Cantidad: ${e.cantidad}</h5>
              <div class="d-grid gap-2">
                <form id=${e.descripcion} onsubmit="handleShop(event)">
                  <input id="descripcion" type="hidden" value="${e.descripcion}" />
                  <input id="cantidadOld" type="hidden" value=${e.cantidad} />
                  <input id="cantidad" type="number" value="1" step="1" min="1" class="form-control" placeholder="Cantidad a comprar"
                  aria-label="Inserte la Cantidad a comprar" aria-describedby="button-addon2">
                  <button class="btn btn-primary button" id="añadir"> Añadir a Carrito</button>
                </form>
              </div>
            </div>
          </div>
        </div>`;
  });
  let div = document.getElementById("MuestraArticulos");
  div.innerHTML = listaArti;
}

function handleShop(event) {
  event.preventDefault();
  const cantidad = parseInt(event.target.cantidad.value);
  const descripcion = event.target.descripcion.value;
  const maxCantidad = parseInt(event.target.cantidadOld.value);
  if (cantidad > maxCantidad) {
    swal("Atención", "No hay tantas piezas mamon", "info");
  } else {
    const data = {
      descripcion: descripcion,
      cantidad: cantidad,
    };
    let body = "";
    let name;
    let pairs = [];
    try {
      for (name in data) {
        let value = data[name];
        if (typeof value !== "string") value = JSON.stringify(value);
        pairs.push(
          name +
          "=" +
          encodeURI(value)
            .replace(/=/g, "%3D")
            .replace(/&/g, "%26")
            .replace(/%20/g, "+")
        );
      }
    } catch (error) {
      alert("Error: " + error.message);
    }
    body = pairs.join("&");
    let encabezados = new Headers();
    encabezados.set('Content-Type', 'application/x-www-form-urlencoded');
    swal({
      title: "¿Esta seguro?",
      text: `Se añadira ${cantidad} pieza(s) del articulo ${descripcion} `,
      icon: "warning",
      buttons: {
        defeat: {
          text: "Si",
          closeModal: false,
        },
        cancel: 'No',
      },
      dangerMode: true,
    }).then((respuesta) => { //Respuesta del boton
      if (respuesta == "defeat") {
        return fetch("/Servicio/rest/ws/alta_carrito", {
          method: 'POST',
          headers: encabezados,
          body: body,
        }).then(response => {
          if (!response.ok) {
            throw new Error(response.statusText);
          }
          return response.json();
        });
      }
      if (respuesta === null) return new Response("null");
    }).then((respuesta) => {
      if (respuesta != null) {
        swal("Respuesta", `${respuesta}`, "info");
        setTimeout(() => {
          window.location.href = "/carrito.html";
        }, 1000);
      }
    }).catch((error) => {
      swal("Error", `${error}`, "error");
      console.log(error);
    });
  }
}