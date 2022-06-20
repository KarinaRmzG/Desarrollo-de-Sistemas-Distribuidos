drop database if exists carrito;
create database carrito;
use carrito;

create table articulos(
    id_articulo integer auto_increment primary key,
    descripcion varchar(256) not null,
    precio float not null,
    cantidad integer not null
);

create table imagenes_articulo(
    id_imagen integer auto_increment primary key,
    imagen longblob,
    id_articulo integer not null
);

alter table imagenes_articulo add foreign key(id_articulo) references articulos(id_articulo);

create unique index articulo_1 on articulos(descripcion);

create table carrito_compra(
    id_compra integer auto_increment primary key,
    id_articulo integer not null,
    cantidad integer not null
);

alter table carrito_compra add foreign key(id_articulo) references articulos(id_articulo);