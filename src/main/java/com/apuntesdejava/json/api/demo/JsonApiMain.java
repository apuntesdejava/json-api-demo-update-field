package com.apuntesdejava.json.api.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/**
 * Probando la modificación de campos en un objeto JSON con JSON-API, GSON y
 * JSON.org
 *
 * @author diego.silva@apuntesdejava.com
 */
public class JsonApiMain {

    static final String NOMBRE_ARCHIVO = "ejemplo.json";

    private static final Logger LOG = Logger.getLogger(JsonApiMain.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new JsonApiMain().start();
    }

    private void start() {
        LOG.info("Guardando JSON");
        saving();
        LOG.info("Leyendo y modificando con JSON Api");
        udpatingJsonP();
        LOG.info("Leyendo y modificando con GSON");
        udpatingGson();

    }

    private void writeJsonObject(JsonObject json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {

            JsonWriter jsonWriter = Json.createWriter(writer);
            jsonWriter.writeObject(json);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private JsonObject readJsonObject(String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            JsonReader jsonReader = Json.createReader(reader);
            return jsonReader.readObject();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void saving() {
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        JsonObject obj = objBuilder.add("nombre", "Java")
                .add("tipo", "compilado")
                .add("lema", "Compila una vez, c\u00f3rrelo muchas veces")
                .add("version", 1.7)
                .build();
        LOG.log(Level.INFO, "Guardando JSON en archivo:{0}", obj);
        writeJsonObject(obj, NOMBRE_ARCHIVO);
    }

    private void udpatingJsonP() {
        JsonObject obj = readJsonObject(NOMBRE_ARCHIVO);
        if (obj != null) {
            try {

                JsonNumber version = obj.getJsonNumber("version");
                LOG.log(Level.INFO, "Version obtenida:{0}", version);

                JsonObject value = Json.createObjectBuilder().add("version", 1.8).build();
                //Lanza excepcion java.lang.UnsupportedOperationException            
                obj.replace("version", value.getJsonNumber("version"));
                writeJsonObject(obj, NOMBRE_ARCHIVO);

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error cambiando campo usando JSON Api", ex);
            }
        }
    }

    private void udpatingGson() {
        Map<String, Object> obj = null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        try (FileReader reader = new FileReader(NOMBRE_ARCHIVO)) {
            Gson gson = gsonBuilder.create();
            obj = gson.fromJson(reader, Map.class); //lo leo como mapa
            LOG.log(Level.INFO, "Objeto leido con GSON:{0}", obj);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (obj != null) { 
            LOG.log(Level.INFO, "Modificando el campo version:{0}", obj.get("version"));
            obj.put("version", 1.8); //cambio el campo
            Gson gson = gsonBuilder.create();
            //y lo escribo en archivo
            try (FileWriter writer = new FileWriter(NOMBRE_ARCHIVO)) {                
                com.google.gson.stream.JsonWriter jsonWriter = gson.newJsonWriter(writer);                
                //es necesario para el metodo .toJson() siguiente
                java.lang.reflect.Type typeOfSrc = new TypeToken<Map<String, Object>>() {
                }.getType();
                gson.toJson(obj, typeOfSrc, jsonWriter);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }

        }

    }

   

}
