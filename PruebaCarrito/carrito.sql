drop database carrito;
create database carrito;
use carrito;

CREATE TABLE articulos
(
	id_articulo integer auto_increment primary key,
	descripcion varchar(500) not null,
	precio float not null,
	cantidad_almacen integer not null
);

CREATE TABLE foto_articulo
(
	id_foto integer auto_increment primary key,
	foto longblob,
	id_articulo integer not null
);

CREATE TABLE carrito_compra(
	id_carrito_articulo integer auto_increment primary key,
	id_articulo integer not null,
	cantidad integer not null	
);

alter table foto_articulo add foreign key (id_articulo) references articulos(id_articulo);
alter table carrito_compra add foreign key (id_articulo) references articulos(id_articulo);

create unique index articulo_1 on articulos(descripcion);