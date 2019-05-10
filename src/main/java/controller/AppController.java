package controller;

import com.google.gson.*;
import domain.Item;
import service.ItemService;
import util.StandardResponse;
import util.StatusResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AppController {

    public static StandardResponse GetItem(String id) throws IOException {
        Item newItem = ItemService.getItemById(id);
        return new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(newItem));
    }

    public static StandardResponse InsertItem(String body) throws IOException {
        Item item = new Gson().fromJson(body, Item.class);
        boolean siteExist = checkSite(item.getSite_id());
        if(siteExist) {
            boolean catExist = checkCategory(item.getCategory_id());
            if(catExist) {
                Item newItem = ItemService.insertItem(item);
                return new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(newItem));
            } else {
                return new StandardResponse(StatusResponse.ERROR, "Error al crear Item: La categoría ingresada no existe");
            }
        } else {
            return new StandardResponse(StatusResponse.ERROR, "Error al crear Item: El site ingresado no existe");
        }
    }

    public static StandardResponse EditItem(String id, String body) throws IOException {
        Item item = new Gson().fromJson(body, Item.class);
        boolean res = checkSite(item.getSite_id());
        if(res) {
            Item newItem = ItemService.updateItemById(id, item);
            return new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(newItem));
        } else {
            return new StandardResponse(StatusResponse.ERROR, "Error al editar nuevo Item");
        }
    }

    public static StandardResponse DeleteItem(String id) throws IOException {
        String result = ItemService.deleteItemById(id);
        if(result.equals("NOT_FOUND")) {
            return new StandardResponse(StatusResponse.ERROR, "El item que intenta borrar no existe en la Base de Datos");
        }
        if(result.equals("")) {
            return new StandardResponse(StatusResponse.ERROR, "Error al borrar el Item");
        }
        return new StandardResponse(StatusResponse.SUCCESS, "El item fue borrado correctamente");
    }


    private static boolean checkSite(String id) throws IOException{
        JsonElement result = readUrl("https://api.mercadolibre.com/sites/"+id);
        if(result != null) {
            return true;
        }
        return false;
    }

    private static boolean checkCategory(String id) throws IOException{
        JsonElement result = readUrl("https://api.mercadolibre.com/categories/"+id);
        if(result != null) {
            return true;
        }
        return false;
    }

    public static JsonElement readUrl(String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            char[] chars = new char[1024];
            StringBuffer buffer = new StringBuffer();
            int read = 0;
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            JsonParser parser = new JsonParser();
            JsonObject resultObj = parser.parse(buffer.toString()).getAsJsonObject();
            return resultObj;
        } catch(FileNotFoundException e){
            return null;
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
