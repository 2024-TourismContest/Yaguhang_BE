package _4.TourismContest.tour.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ItemsDeserializer extends JsonDeserializer<TourApiDetailImageResponseDto.Items> {

    @Override
    public TourApiDetailImageResponseDto.Items deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = p.getCodec().readTree(p);
        TourApiDetailImageResponseDto.Items items = new TourApiDetailImageResponseDto.Items();

        if (node.isTextual() && node.asText().isEmpty()) {
            items.setItem(Collections.emptyList());
        } else if (node.has("item")) {
            items.setItem(ctxt.readValue(node.get("item").traverse(p.getCodec()),
                    ctxt.getTypeFactory().constructCollectionType(List.class,
                            TourApiDetailImageResponseDto.Item.class)));
        } else {
            items.setItem(Collections.emptyList());
        }

        return items;
    }
}