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
    /*
    Declaración de la conexión, colección y de la base de datos de manera global
    para que pueda acceder a la conexión desde los métodos solo declarandolo una vez.
     */
    static MongoClient mongoClient = MongoDB.getClient();
    static MongoDatabase database = mongoClient.getDatabase("almacen");
    static MongoCollection<Document> collection = database.getCollection("articulos");

    public static void main(String[] args) {
        //El menú para poder realizar diferentes acciones con la BBDD.
        IO.println("Bienvenido al gestor del almacén");
        IO.println("?Qué quieres hacer?");
        List<String> option = List.of(
                "1.Buscar Articulo",
                "2.Modificar",
                "3.Añadir",
                "4.Añadir variable",
                "5.Eliminar Articulo",
                "6.Eliminar variable",
                "7.Salir"
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
                    addVariable();
                    break;
                case "5":
                    deleteArticulo();
                    break;
                case "6":
                    deleteVariable();
                    break;
                case "7":
                    //Cierra la aplicación y la caonexión con la BBDD.
                    System.out.println("Saliendo de la aplicación");
                    mongoClient.close();
                    return;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
    }

    /*
    Realiza una búsqueda en la base de datos MongoDB según los
     parámetros proporcionados por el usuario. Excluye el ID del
      objeto y muestra los resultados en formato JSON.
     */
    private static void buscarArticulo() {

        IO.print("¿Por qué opción quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");

        // Verificar si hay suficientes elementos en el array
        if (parts.length % 2 != 0) {
            IO.println("Entrada inválida. Debes proporcionar pares de nombre de variable y valor.");
            return;
        }
        //Guardando los parámetros para usarlos más cómodamente.
        String variable = parts[0].trim().toLowerCase();
        String valor = parts[1].trim().toLowerCase();
        //Excluir el id del objeto
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        //Realizar la busqueda en la base de datos, filtrando con los paramentros
        //que se han pasado previamente por terminal, excluyendo el id e imprimirlo de manera descente en formato json.
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

    /*
    Permite al usuario buscar y modificar un artículo de la base de datos.
    Muestra opciones, permite al usuario elegir un artículo, muestra sus detalles
    y solicita la modificación de variables específicas.
     */
    private static void modArticulo() {
        IO.print("¿Por qué opción quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");
        // Comprobamos de que userInput tenga unos valores válidos.
        if (parts.length < 2) {
            IO.println("Entrada inválida. Debes proporcionar tanto el nombre de la variable como el valor.");
            return;
        }
        String variable = parts[0].trim().toLowerCase();
        String valor = parts[1].trim().toLowerCase();

        // Crear un filtro dinámico
        Bson filter = Filters.eq(variable, valor);

        //Llenamos el array con la colleción que cumple con el filtro
        MongoCursor<Document> cursor = collection.find(filter)
                .sort(Sorts.descending("tipo")).iterator();

        //Comprobamos de que el array no esta vacío.
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
                //Imprimimos el array detrás de un int que es la opción que nos ayudara a elegir que objeto queremos modificar.
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
                // No permitir modificar la ID
                if (!entry.getKey().equals("_id")) {
                    IO.print("Introduce el nuevo valor para '" + entry.getKey() + "' (o deja en blanco para mantener el valor actual): ");
                    String nuevoValor = IO.readString();

                    if (!nuevoValor.isEmpty()) {
                        // Actualizar el valor en el documento
                        articuloSeleccionado.put(entry.getKey(), nuevoValor);
                    }
                }
            }

            // Actualizar el documento en la base de datos usando el _id original
            collection.replaceOne(Filters.eq("_id", articuloSeleccionado.getObjectId("_id")), articuloSeleccionado);
            IO.println("Artículo modificado con éxito.");
        } else {
            IO.println("Opción no válida.");
        }
    }

    /*
    Agrega un nuevo artículo a la base de datos solicitando información básica
    al usuario, como tipo, marca y cantidad. Opcionalmente, permite agregar más
    opciones al artículo.
     */
    private static void addArticulo() {

        IO.print("¿Qué opcion de artículo quieres modificar?");
        // Solicitar al usuario la información básica del nuevo artículo
        IO.println("Ingrese el tipo del artículo: ");
        String tipo = IO.readString().toLowerCase();

        IO.print("Ingrese la marca del artículo: ");
        String marca = IO.readString().toLowerCase();

        IO.print("Ingrese la cantidad del artículo: ");
        String cantidad ;
        do {
            cantidad = IO.readString();
            if (!esNumeroEntre0y9(cantidad)) {
                IO.print("La cantidad ingresada no es válida, ingrese una cantidad alfanumérica: ");
            }
        } while (!esNumeroEntre0y9(cantidad));

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

    /*
    Función auxiliar que verifica si una cadena es un número entre 0 y 9.
     */
    private static boolean esNumeroEntre0y9(String input) {
        return input.matches("[0-9]+");
    }

    /*
     Permite al usuario agregar una nueva variable a un artículo existente en
     la base de datos. Solicita información sobre la variable y su valor, luego
     actualiza el artículo en la base de datos.
     */
    private static void addVariable() {

        IO.print("¿Por qué opción quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");
        // Comprobamos de que userInput tenga unos valores válidos.
        if (parts.length < 2) {
            IO.println("Entrada inválida. Debes proporcionar tanto el nombre de la variable como el valor.");
            return;
        }
        String variable = parts[0].trim().toLowerCase();
        String valor = parts[1].trim().toLowerCase();

        // Crear un filtro dinámico
        Bson filter = Filters.eq(variable, valor);

        //Llenamos el array con la colleción que cumple con el filtro
        MongoCursor<Document> cursor = collection.find(filter)
                .sort(Sorts.descending("tipo")).iterator();

        //Comprobamos de que el array no esta vacío.
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
                //Imprimimos el array detrás de un int que es la opción que nos ayudara a elegir que objeto queremos modificar.
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

            // Solicitar al usuario el nombre y valor de la nueva variable
            IO.print("Introduce el nombre de la nueva variable: ");
            String nuevoNombreVariable = IO.readString().trim().toLowerCase();

            IO.print("Introduce el valor de la nueva variable: ");
            String nuevoValorVariable = IO.readString();

            // Agregar la nueva variable al documento
            articuloSeleccionado.put(nuevoNombreVariable, nuevoValorVariable);

            // Actualizar el documento en la base de datos usando el _id original
            collection.replaceOne(Filters.eq("_id", articuloSeleccionado.getObjectId("_id")), articuloSeleccionado);
            IO.println("Artículo modificado con éxito.");
        } else {
            IO.println("Opción no válida.");
        }
    }

    /*
    Permite al usuario buscar y eliminar un artículo de la base de datos según
    los parámetros proporcionados. Muestra opciones, permite al usuario elegir un
    artículo y elimina el artículo seleccionado.
     */
    private static void deleteArticulo() {

        IO.print("¿Por qué variable quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");
        if (parts.length < 2) {
            IO.println("Entrada inválida. Debes proporcionar tanto el nombre de la variable como el valor.");
            return;
        }
        String variable = parts[0].trim().toLowerCase();
        String valor = parts[1].trim().toLowerCase();

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

    /*
    Permite al usuario buscar y eliminar una variable de un artículo existente en
    la base de datos. Muestra opciones, permite al usuario elegir un artículo y una
    variable, luego elimina la variable del artículo en la base de datos.
     */
    private static void deleteVariable() {

        IO.print("¿Por qué variable quieres buscar? (nombreVariable:valor)");
        String userInput = IO.readString();

        // Dividir la entrada del usuario en nombre de variable y valor
        String[] parts = userInput.split(":");
        if (parts.length < 2) {
            IO.println("Entrada inválida. Debes proporcionar tanto el nombre de la variable como el valor.");
            return;
        }
        String variable = parts[0].trim().toLowerCase();
        String valor = parts[1].trim().toLowerCase();

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

        // Solicitar al usuario que elija un artículo para eliminar el campo
        int opcionEliminar = -1;
        while (opcionEliminar < 1 || opcionEliminar > opcionArticulo - 1) {
            IO.print("Seleccione el número del artículo que desea eliminar el campo (1-" + (opcionArticulo - 1) + "): ");
            opcionEliminar = IO.readInt();
        }

        // Obtener el artículo seleccionado
        Document articuloEliminarCampo = articulos.get(opcionEliminar - 1);

        IO.print("Indica que opción quieres eliminar");
        String campoEliminar = IO.readString();
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



