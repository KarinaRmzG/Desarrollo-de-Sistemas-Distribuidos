var foto = null;  // por default la foto es nula
function get(id){
	return document.getElementById(id);
}
function readSingleFile(files,imagen){
	var file = files[0];
	if (!file) return;
	var reader = new FileReader();
	reader.onload = function(e){
		imagen.src = reader.result;
		// reader.result incluye al principio: "data:image/jpeg;base64,"
		foto = reader.result.split(',')[1];
	};
	reader.readAsDataURL(file);
}
function quita_foto(){
	foto=null;
	get('consulta_imagen').src='usuario_sin_foto.png';
	get('consulta_file').value='';
}

function ver_carrito(){
	oculta(get("div_busqueda"));
	muestra(get("div_carrito"));
	//oculta(get("btn_crear_elem"));
	oculta(get("btn_busqueda"));
	oculta(get("busqueda_articulo"));
	muestra(get("btn_volver"));
	consulta_carrito();
const busqueda = get("div_busqueda");
 busqueda.innerHTML = '';
}