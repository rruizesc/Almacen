package main.java.org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import main.java.org.example.io.IO;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

import static com.mongodb.client.model.Filters.lt;

public class AlmacenApp {

    public static void main(String[] args) {

        IO.println("Bienvenido al gestor del almacén");
        IO.println("?Qué quieres hacer?");
        List<String> option = List.of(
                "1.Buscar Articulo",
                "2.Modificar",
                "3.Añadir",
                "4.Eliminar",
                "5.Salir"
        );

        while (true) {
            IO.println(option);
            switch (IO.readString()) {
                case "1":
                    buscarArticulo();
                    break;
                case "2":
                    modArticulo();
                    break;
                case "3":
                    addArticulo();
                    break;
                case "4":
                    deleteArticulo();
                    break;
                case "5":
                    System.out.println("Saliendo de la aplicación");
                    return;
                default:
                    System.out.println("Opción no válida");
                    break;

            }
        }
    }
    private static void buscarArticulo() {
        MongoClient mongoClient = MongoDB.getClient();
        MongoDatabase database = mongoClient.getDatabase("almacen");
        MongoCollection<Document> collection = database.getCollection("articulos");
        IO.print("Que vas a buscar");
        Integer a = IO.readInt();
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(lt("cantidad", a))
                .projection(projectionFields)
                .sort(Sorts.descending()).iterator();
        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

    }

    private static void modArticulo() {

    }

    private static void addArticulo() {

    }

    private static void deleteArticulo() {
    }
//        // Ejemplo: Crear un usuario
//        Usuario usuario = new Usuario();
//        usuario.setUsername("nombreUsuario");
//        usuario.setPassword("contrasenaSegura");
//
//        // Insertar usuario en la colección
//        Document usuarioDoc = new Document("username", usuario.getUsername())
//                .append("password", usuario.getPassword());
//        usuarioCollection.insertOne(usuarioDoc);
//
//        // Ejemplo: Crear un artículo
//        Articulo articulo = new Articulo();
//        articulo.setNombre("Articulo1");
//        articulo.setCantidad(10);
//
//        // Insertar artículo en la colección
//        Document articuloDoc = new Document("nombre", articulo.getNombre())
//                .append("cantidad", articulo.getCantidad());
//        articuloCollection.insertOne(articuloDoc);
//
//        // Imprimir usuarios y artículos en la consola (puedes adaptar esto a tu lógica real)
//        System.out.println("Usuarios en la base de datos:");
//        usuarioCollection.find().forEach(document -> {
//            System.out.println(document.toJson());
//        });
//
//        System.out.println("\nArtículos en el inventario:");
//        articuloCollection.find().forEach(document -> {
//            System.out.println(document.toJson());
//        });
}



