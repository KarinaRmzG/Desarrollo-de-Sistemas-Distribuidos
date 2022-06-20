import json
import logging
import mysql
import pathlib
import azure.functions as func

def obtenerSSLMySQL():
    path = pathlib.Path(__file__).parent.parent
    return str(path / 'BaltimoreCyberTrustRoot.crt.pem')

def conexionMySQLPaaS():
    servidor = "mysql2015170921.mysql.database.azure.com"
    puerto = "3306"
    usuario = "KarinaRG@mysql2015170921"
    contrasenia = "sisdis_KRG99"
    bd = "carrito"
    certificado = obtenerSSLMySQL()
    return mysql.connector.connect(host=servidor, port=puerto, user=usuario, passwd=contrasenia, db=bd,ssl_ca=certificado )

def main(req: func.HttpRequest) -> func.HttpResponse:
    con = conexionMySQLPaaS()
    cursor = con.cursor()
    try:
        stmt_1 = "select a.descripcion, a.precio, b.imagen, c.cantidad, c.id_compra from articulos a left outer join imagenes_articulo b on a.id_articulo=b.id_articulo left outer join carrito_compra c on a.id_articulo=c.id_articulo where c.cantidad is not null"
        cursor.execute(stmt_1)
        data = cursor.fetchall()
        articulos = []
        for row in data:
            articulo = {
                'descripcion': row[0],
                'precio': row[1],
                'imagen': row[2],
                'cantidad': row[3]
            }
        articulos.append(articulo)
        return func.HttpResponse(json.dumps(articulos), status_code=200)
    except Exception as e:
        return func.HttpResponse(f"{e}\n", status_code=500)
    finally:
        if con.is_connected():
            cursor.close()
            con.close()
