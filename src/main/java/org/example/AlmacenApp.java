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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        IO.print("¿Qué tipo de artículo quieres buscar?");
        String tipo = IO.readString();

        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(Filters.eq("tipo", tipo))
                .projection(projectionFields)
                .sort(Sorts.descending("tipo")).iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }

    }

    private static void modArticulo() {

    }

        private static void addArticulo () {
            MongoClient mongoClient = MongoDB.getClient();
            MongoDatabase database = mongoClient.getDatabase("almacen");
            MongoCollection<Document> collection = database.getCollection("articulos");

            // Solicitar al usuario la información básica del nuevo artículo
            IO.print("Ingrese el tipo del artículo: ");
            String tipo = IO.readString();

            IO.print("Ingrese la marca del artículo: ");
            String marca = IO.readString();

            IO.print("Ingrese la cantidad del artículo: ");
            int cantidad = IO.readInt();

            // Crear un documento con la información básica proporcionada por el usuario
            Document nuevoArticulo = new Document()
                    .append("tipo", tipo)
                    .append("marca", marca)
                    .append("cantidad", cantidad);

            // Preguntar al usuario si desea agregar más opciones
            IO.print("¿Desea agregar más opciones? (si/no): ");
            String agregarOpciones = IO.readString();

            // Si la respuesta es afirmativa, solicitar la cantidad de opciones adicionales
            if ("si".equalsIgnoreCase(agregarOpciones)) {
                IO.print("¿Cuántas opciones adicionales desea agregar?: ");
                int numOpciones = IO.readInt();

                // Iterar sobre las opciones y solicitar información al usuario
                for (int i = 0; i < numOpciones; i++) {
                    IO.print("Ingrese el nombre de la opción " + (i + 1) + ": ");
                    String nombreOpcion = IO.readString();

                    IO.print("Ingrese el valor de la opción " + (i + 1) + ": ");
                    String valorOpcion = IO.readString();

                    // Agregar la opción al documento del artículo
                    nuevoArticulo.append(nombreOpcion, valorOpcion);
                }
            }

            // Insertar el nuevo artículo en la colección
            collection.insertOne(nuevoArticulo);

            System.out.println("Artículo agregado correctamente.");
        }


        private static void deleteArticulo () {
        }
    }



