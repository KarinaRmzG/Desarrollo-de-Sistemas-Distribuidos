let foto = null;  // por default la foto es nula
function get(id){
	return document.getElementById(id);
}
function readSingleFile(files,imagen){
	let file = files[0];
	if (!file) return;
	let reader = new FileReader();
	reader.onload = function(e){
		imagen.src = reader.result;
		// reader.result incluye al principio: "data:image/jpeg;base64,"
		foto = reader.result.split(',')[1];
	};
	reader.readAsDataURL(file);
}
function enviar_formulario(event) {
	event.preventDefault();//Evitamos que el formulario se propague
	swal("Enviando ..."); //alert("Enviando ...");
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
	}
	if(precio===''){
		//TODO: Acompletar que no ingreso precio
		swal({
			title: "¡Cuidado!",
			text: "No olvides el precio",
			icon: "warning", //"success","info","error","warning"
			button: "Ok",
		});
	}
	if(cantidad===''){
		//TODO: Acompletar que no ingreso cantidad
		swal({
			title: "¡Cuidado!",
			text: "Inserte artículos",
			icon: "error", //"success","info","error","warning"
			button: "Ok",
		});
	}
	if(foto==null){
		//TODO: Acompletar que no falta foto
		swal({
			title: "Insere foto del articulo",
			icon: "error", //"success","info","error","warning"
			button: "Ok",
		});
	}
}
function quita_foto(){
	foto=null;
	get('consulta_imagen').src='usuario_sin_foto.png';
	get('consulta_file').value='';
}
