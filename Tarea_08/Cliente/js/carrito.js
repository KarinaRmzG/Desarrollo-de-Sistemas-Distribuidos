function buscaCarrito() {
  fetch(`/Servicio/rest/ws/consulta_carrito`,
    {
      method: 'POST',
    }).then(response => {
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      return response.json();
    }).catch(error => {
      swal(`Request failed`, `${error}`, `error`);
    }).then(respo => {
      if (typeof respo === 'object') {
        generaArticulos(respo);
        console.log(respo);
      } else {
        swal(`Atención`, `${respo}`, `info`);
        setTimeout(() => {
          window.location.href = "/compraArticulos.html";
        }, 1000);
      }
    });
}

function generaArticulos(articulos) {
  let listaArti = "";
  let total = 0.0;
  articulos.map((e) => {
    listaArti += `
        <div class="col d-flex justify-content-center-mb-4 animate__animated animate__fadeInDown animate__fast">
          <div class="card shadow mb-1 rounded" style="width: 20rem;">
            <img src="data:image/jpeg;base64,${e.imagen}" alt='foto' />
            <div class="card-body">
              <p class="card-text text-white-50 description">${e.descripcion}</p>
              <h5>Precio: <span class="precio">$ ${e.precio}</span></h5>
              <h5>Cantidad: ${e.cantidad}</h5>
              <h5>Costo a pagar: ${e.precio * e.cantidad}</h5>
              <div class="d-grid gap-2">
                <form id=${e.descripcion} onsubmit="handleShop(event)">
                  <input id="descripcion" type="hidden" value="${e.descripcion}" />
                  <input id="cantidadOld" type="hidden" value="${e.cantidad}" />
                  <button class="btn btn-primary button" id="añadir"> <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">                  <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5Zm-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5ZM4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06Zm6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528ZM8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5Z" /> </svg>
                    Eliminar producto
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>`;
    total += (e.precio * e.cantidad);
  });
  let divTotal = document.getElementById("TotalCarrito");
  divTotal.innerHTML = `<h4 style="text-align: center;" class="animate__animated animate__fadeInDown">Total: ${total}</h4>`;
  let div = document.getElementById("MuestraArticulos");
  div.innerHTML = listaArti;
}

function handleShop(event) {
  event.preventDefault();
  let describe = event.target.descripcion.value;
  const data = {
    descripcion: describe,
  }
  let body = "";
  let name;
  let pairs = [];
  try {
    for (name in data) {
      let value = data[name];
      if (typeof value !== "string") value = JSON.stringify(value);
      pairs.push(
        name + "=" + encodeURI(value).replace(/=/g, "%3D").replace(/&/g, "%26").replace(/%20/g, "+")
      );
    }
  } catch (error) {
    alert("Error: " + error);
  }
  body = pairs.join("&");
  let encabezados = new Headers();
  encabezados.set('Content-Type', 'application/x-www-form-urlencoded');
  swal({
    title: "¿Esta seguro?",
    text: `Solo se eliminara el articulo ${describe} del carrito`,
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
      return fetch("/Servicio/rest/ws/elimina_e_carrito", {
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
        window.location.href = "/compraArticulos.html";
      }, 1000);
    }
  }).catch((error) => {
    swal("Error", `${error}`, "error");
    console.log(error);
  });
}

function eliminaCarrito() {
  swal({
    title: "¿Esta seguro?",
    text: "Se eliminara TODO el carrito",
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
      return fetch(`/Servicio/rest/ws/elimina_carrito_all`,
        {
          method: 'POST',
        }).then(response => {
          if (!response.ok) {
            throw new Error(response.statusText)
          }
          return response.json();
        }).catch(error => {
          swal(`Request failed`, `${error}`, `error`);
        })
    }
    if (respuesta === null) return new Response("null");
  }).then((respuesta) => {
    if (respuesta != null) {
      swal("Respuesta", `${respuesta}`, "info");
      setTimeout(() => {
        window.location.href = "/compraArticulos.html";
      }, 1000);
    }
  }).catch((error) => {
    swal("Error", `${error}`, "error");
    console.log(error);
  });
}