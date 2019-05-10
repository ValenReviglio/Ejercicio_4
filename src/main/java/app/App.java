package app;

import com.google.gson.Gson;
import controller.AppController;
import domain.Item;
import org.elasticsearch.client.RestHighLevelClient;
import service.ConnectionService;
import service.ItemService;
import util.StandardResponse;

import java.io.IOException;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
            get("/item/:id", (req, res) -> {
                res.type("application/json");
                String itemId = req.params(":id");
                StandardResponse response = AppController.GetItem(itemId);
                return new Gson().toJson(response);
            });

            post("/item", (req, res) -> {
                res.type("application/json");
                StandardResponse response = AppController.InsertItem(req.body());
                return new Gson().toJson(response);
            });

            put("/item/:id", (req, res) -> {
                res.type("application/json");
                String itemId = req.params(":id");
                StandardResponse response = AppController.EditItem(itemId, req.body());
                return new Gson().toJson(response);
            });

            delete("/item/:id", (req, res) -> {
                res.type("application/json");
                String itemId = req.params(":id");
                StandardResponse response = AppController.DeleteItem(itemId);
                return new Gson().toJson(response);
            });

    }
}
