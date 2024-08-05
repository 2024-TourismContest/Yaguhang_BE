package _4.TourismContest.tour.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemsDeserializer extends JsonDeserializer<TourApiDetailImageResponseDto.Items> {

    @Override
    public TourApiDetailImageResponseDto.Items deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = p.getCodec().readTree(p);
        TourApiDetailImageResponseDto.Items items = new TourApiDetailImageResponseDto.Items();

        if (node.isTextual() && node.asText().isEmpty()) {
            // Handle the case where items is an empty string
            items.setItem(Collections.emptyList());
        } else if (node.isNull()) {
            // Handle the case where items is null
            items.setItem(Collections.emptyList());
        } else {
            // Use the default deserialization for normal cases
            JsonNode itemNode = node.get("item");
            if (itemNode != null && itemNode.isArray()) {
                List<TourApiDetailImageResponseDto.Item> item = new ArrayList<>();
                for (JsonNode i : itemNode) {
                    item.add(new TourApiDetailImageResponseDto.Item(i.get("originimgurl").asText()));
                }
                items.setItem(item);
            } else {
                // If item node is not an array, we treat it as an empty list
                items.setItem(Collections.emptyList());
            }
        }

        return items;
    }
}