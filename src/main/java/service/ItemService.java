package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Item;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemService {

    private static final String INDEX = "itemdata";
    private static final String TYPE = "item";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Item insertItem(Item item) throws IOException {
        ConnectionService.makeConnection();
        item.setId(UUID.randomUUID().toString());
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("id", item.getId());
        dataMap.put("site_id", item.getSite_id());
        dataMap.put("title", item.getTitle());
        dataMap.put("subtitle", item.getSubtitle());
        dataMap.put("seller_id", item.getSeller_id());
        dataMap.put("category_id", item.getCategory_id());
        dataMap.put("price", item.getPrice());
        dataMap.put("currency_id", item.getCurrency_id());
        dataMap.put("available_quantity", item.getAvailable_quantity());
        dataMap.put("condition", item.getCondition());
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, item.getId())
                .source(dataMap);
        try {
            IndexResponse response = ConnectionService.restHighLevelClient.index(indexRequest);
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
        } finally {
            ConnectionService.closeConnection();
        }
        return item;
    }

    public static Item updateItemById(String id, Item item) throws IOException {
        ConnectionService.makeConnection();

        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
                .fetchSource(true);    // Fetch Object after its update
        try {
            String itemJson = objectMapper.writeValueAsString(item);
            updateRequest.doc(itemJson, XContentType.JSON);
            UpdateResponse updateResponse = ConnectionService.restHighLevelClient.update(updateRequest);
            return objectMapper.convertValue(updateResponse.getGetResult().sourceAsMap(), Item.class);
        } catch (JsonProcessingException e) {
            e.getMessage();
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        } finally {
            ConnectionService.closeConnection();
        }
        System.out.println("Unable to update person");
        return null;
    }

    public static Item getItemById(String id) throws IOException {
        ConnectionService.makeConnection();
        GetRequest getPersonRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = ConnectionService.restHighLevelClient.get(getPersonRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        } finally {
            ConnectionService.closeConnection();
        }
        return getResponse != null ?
                objectMapper.convertValue(getResponse.getSourceAsMap(), Item.class) : null;
    }

    public static String deleteItemById(String id) throws IOException {
        ConnectionService.makeConnection();
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        try {
            DeleteResponse deleteResponse = ConnectionService.restHighLevelClient.delete(deleteRequest);
            System.out.println(deleteResponse.getResult().toString());
            return deleteResponse.getResult().toString();
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        } finally {
            ConnectionService.closeConnection();
        }
        return "";
    }

}
