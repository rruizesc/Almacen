package main.java.org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public class AlmacenApp {
    public static void main(String[] args) {
        // Obtener el cliente de MongoDB
        MongoClient mongoClient = MongoDB.getClient();

        // Acceder a la base de datos y la colección
        MongoDatabase database = mongoClient.getDatabase("tu-base-de-datos");
        MongoCollection<Document> usuarioCollection = database.getCollection("usuarios");
        MongoCollection<Document> articuloCollection = database.getCollection("articulos");

        // Ejemplo: Crear un usuario
        Usuario usuario = new Usuario();
        usuario.setUsername("nombreUsuario");
        usuario.setPassword("contrasenaSegura");

        // Insertar usuario en la colección
        Document usuarioDoc = new Document("username", usuario.getUsername())
                .append("password", usuario.getPassword());
        usuarioCollection.insertOne(usuarioDoc);

        // Ejemplo: Crear un artículo
        Articulo articulo = new Articulo();
        articulo.setNombre("Articulo1");
        articulo.setCantidad(10);

        // Insertar artículo en la colección
        Document articuloDoc = new Document("nombre", articulo.getNombre())
                .append("cantidad", articulo.getCantidad());
        articuloCollection.insertOne(articuloDoc);

        // Imprimir usuarios y artículos en la consola (puedes adaptar esto a tu lógica real)
        System.out.println("Usuarios en la base de datos:");
        usuarioCollection.find().forEach(document -> {
            System.out.println(document.toJson());
        });

        System.out.println("\nArtículos en el inventario:");
        articuloCollection.find().forEach(document -> {
            System.out.println(document.toJson());
        });
    }
}

