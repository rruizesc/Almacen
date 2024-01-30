package main.java.org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import main.java.org.example.io.IO;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlmacenApp {

    public static void main(String[] args) {

        IO.println("Bienvenido al gestor del almacén");
        IO.println("?Qué quieres hacer?");
        List<String> option = List.of(
                "1.Buscar Articulo",
                "2.Modificar",
                "3.Añadir",
                "4.Eliminar Articulo",
                "5.Eliminar variable",
                "6.Salir"
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
                    deleteVariable();
                    break;
                case "6":
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

        IO.print("¿Por qué opción quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");

        // Verificar si hay suficientes elementos en el array
        if (parts.length % 2 != 0) {
            IO.println("Entrada inválida. Debes proporcionar pares de nombre de variable y valor.");
            return;
        }

        String variable = parts[0].trim();
        String valor = parts[1].trim().toString();

        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        try (MongoCursor<Document> cursor = collection.find(Filters.eq(variable, valor))
                .projection(projectionFields)
                .sort(Sorts.descending("tipo")).iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void modArticulo() {
        MongoClient mongoClient = MongoDB.getClient();
        MongoDatabase database = mongoClient.getDatabase("almacen");
        MongoCollection<Document> collection = database.getCollection("articulos");

        IO.print("¿Por qué opción quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");
        if (parts.length < 2) {
            IO.println("Entrada inválida. Debes proporcionar tanto el nombre de la variable como el valor.");
            return;
        }
        String variable = parts[0].trim();
        String valor = parts[1].trim().toString();

        // Crear un filtro dinámico
        Bson filter = Filters.eq(variable, valor);

        MongoCursor<Document> cursor = collection.find(filter)
                .sort(Sorts.descending("tipo")).iterator();

        if (!cursor.hasNext()) {
            IO.println("No se encontraron artículos con la variable especificada. Verifica la variable y el valor e intenta nuevamente.");
            return;
        }

        // Almacenar los artículos en una lista
        List<Document> articulos = new ArrayList<>();
        int opcionArticulo = 1;

        try {
            while (cursor.hasNext()) {
                Document articulo = cursor.next();
                IO.print("Opción " + opcionArticulo + ": " + articulo.toJson());

                System.out.println();  // Salto de línea después de mostrar los detalles
                opcionArticulo++;

                // Almacenar el artículo en la lista
                articulos.add(articulo);
            }
        } finally {
            cursor.close();
        }

        IO.println("Seleccione el artículo que desea modificar ingresando el número correspondiente:");
        int opcionSeleccionada = IO.readInt();

        // Validar la opción seleccionada
        if (opcionSeleccionada >= 1 && opcionSeleccionada <= opcionArticulo - 1) {
            // Obtener el artículo seleccionado de la lista
            Document articuloSeleccionado = articulos.get(opcionSeleccionada - 1);

            IO.print("Detalles del artículo a modificar: ");
            // Mostrar los detalles del artículo directamente aquí
            for (Map.Entry<String, Object> entry : articuloSeleccionado.entrySet()) {
                IO.print(entry.getKey() + ": " + entry.getValue() + " | ");
            }
            System.out.println();  // Salto de línea después de mostrar los detalles

            // Iterar sobre las variables y permitir al usuario modificar cada una
            for (Map.Entry<String, Object> entry : articuloSeleccionado.entrySet()) {
                IO.print("Introduce el nuevo valor para '" + entry.getKey() + "' (o deja en blanco para mantener el valor actual): ");
                String nuevoValor = IO.readString();

                if (!nuevoValor.isEmpty()) {
                    // Actualizar el valor en el documento
                    articuloSeleccionado.put(entry.getKey(), nuevoValor);
                }
            }

            // Actualizar el documento en la base de datos usando el _id original
            collection.replaceOne(Filters.eq("_id", articuloSeleccionado.getObjectId("_id")), articuloSeleccionado);
            IO.println("Artículo modificado con éxito.");
        } else {
            IO.println("Opción no válida.");
        }
    }


    private static void addArticulo() {
        MongoClient mongoClient = MongoDB.getClient();
        MongoDatabase database = mongoClient.getDatabase("almacen");
        MongoCollection<Document> collection = database.getCollection("articulos");
        IO.print("¿Qué opcion de artículo quieres modificar?");
        String opcion = IO.readString();
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


    private static void deleteArticulo() {
        MongoClient mongoClient = MongoDB.getClient();
        MongoDatabase database = mongoClient.getDatabase("almacen");
        MongoCollection<Document> collection = database.getCollection("articulos");

        IO.print("¿Qué tipo de artículo quieres eliminar?");
        String tipo = IO.readString();

        // Obtener los artículos del tipo seleccionado
        Bson filter = Filters.eq("tipo", tipo);

        MongoCursor<Document> cursor = collection.find(filter)
                .sort(Sorts.descending("tipo")).iterator();

        // Almacenar los artículos en una lista
        List<Document> articulos = new ArrayList<>();
        int opcionArticulo = 1;

        try {
            while (cursor.hasNext()) {
                Document articulo = cursor.next();
                IO.print("Opción " + opcionArticulo + ": ");
                // Mostrar los detalles del artículo directamente aquí
                for (Map.Entry<String, Object> entry : articulo.entrySet()) {
                    IO.print(entry.getKey() + ": " + entry.getValue() + " | ");
                }
                System.out.println();  // Salto de línea después de mostrar los detalles
                opcionArticulo++;

                // Almacenar el artículo en la lista
                articulos.add(articulo);
            }
        } finally {
            cursor.close();
        }
        // Solicitar al usuario que elija un artículo para eliminar
        int opcionEliminar = -1;
        while (opcionEliminar < 1 || opcionEliminar > opcionArticulo - 1) {
            IO.print("Seleccione el número del artículo que desea eliminar (1-" + (opcionArticulo - 1) + "): ");
            opcionEliminar = IO.readInt();
        }

        // Obtener el artículo seleccionado
        Document articuloEliminar = articulos.get(opcionEliminar - 1);

        // Obtener el _id del artículo seleccionado
        ObjectId idArticuloEliminar = articuloEliminar.getObjectId("_id");

        // Construir el filtro para eliminar el artículo
        Bson filtroEliminar = Filters.eq("_id", idArticuloEliminar);

        // Eliminar el artículo de la base de datos
        DeleteResult deleteResult = collection.deleteOne(filtroEliminar);

        // Verificar si se eliminó con éxito
        if (deleteResult.getDeletedCount() > 0) {
            IO.print("Artículo eliminado con éxito.");
            System.out.println();
        } else {
            IO.print("No se pudo eliminar el artículo. Verifique la existencia del artículo o inténtelo nuevamente.");
            System.out.println();
        }
    }
    private static void deleteVariable() {
        MongoClient mongoClient = MongoDB.getClient();
        MongoDatabase database = mongoClient.getDatabase("almacen");
        MongoCollection<Document> collection = database.getCollection("articulos");

        IO.print("Indica que opción quieres eliminar");
        String campoEliminar = IO.readString();

        Bson filter = Filters.exists(campoEliminar);

        MongoCursor<Document> cursor = collection.find(filter)
                .sort(Sorts.descending("tipo")).iterator();

        if (!cursor.hasNext()) {
            IO.println("No se encontraron artículos con la variable especificada. Verifica la variable y el valor e intenta nuevamente.");
            return;
        }

        // Almacenar los artículos en una lista
        List<Document> articulos = new ArrayList<>();
        int opcionArticulo = 1;

        try {
            while (cursor.hasNext()) {
                Document articulo = cursor.next();
                IO.print("Opción " + opcionArticulo + ": " + articulo.toJson());

                System.out.println();  // Salto de línea después de mostrar los detalles
                opcionArticulo++;

                // Almacenar el artículo en la lista
                articulos.add(articulo);
            }
        } finally {
            cursor.close();
        }

        // Solicitar al usuario que elija un artículo para eliminar el campo
        int opcionEliminar = -1;
        while (opcionEliminar < 1 || opcionEliminar > opcionArticulo - 1) {
            IO.print("Seleccione el número del artículo que desea eliminar el campo (1-" + (opcionArticulo - 1) + "): ");
            opcionEliminar = IO.readInt();
        }

        // Obtener el artículo seleccionado
        Document articuloEliminarCampo = articulos.get(opcionEliminar - 1);

        // Eliminar el campo del artículo seleccionado
        articuloEliminarCampo.remove(campoEliminar);

        // Obtener el _id del artículo seleccionado
        ObjectId idArticuloEliminarCampo = articuloEliminarCampo.getObjectId("_id");

        // Construir el filtro para actualizar el artículo
        Bson filtroEliminarCampo = Filters.eq("_id", idArticuloEliminarCampo);

        // Actualizar el artículo en la base de datos para eliminar el campo
        UpdateResult updateResult = collection.updateOne(filtroEliminarCampo, new Document("$unset", new Document(campoEliminar, "")));

        // Verificar si se eliminó el campo con éxito
        if (updateResult.getModifiedCount() > 0) {
            IO.print("Campo eliminado con éxito.");
            System.out.println();
        } else {
            IO.print("No se pudo eliminar el campo. Verifique la existencia del artículo o inténtelo nuevamente.");
            System.out.println();
        }
    }

}



