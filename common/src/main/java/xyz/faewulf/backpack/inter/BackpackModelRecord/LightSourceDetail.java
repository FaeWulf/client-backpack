package xyz.faewulf.backpack.inter.BackpackModelRecord;

import com.google.gson.*;

import java.lang.reflect.Type;


public class LightSourceDetail {
    public String item;
    public DetailTransform transform;

    @Override
    public String toString() {
        return "LightSourceDetail{" +
                "item='" + item + '\'' +
                ", transform=" + transform +
                '}';
    }

    // Custom deserializer to treat empty JSON objects `{}` as null
    public static class LightSourceDeserializer implements JsonDeserializer<LightSourceDetail> {
        @Override
        public LightSourceDetail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject() && json.getAsJsonObject().entrySet().isEmpty()) {
                return null; // Treat {} as null
            }
            return new Gson().fromJson(json, LightSourceDetail.class);
        }
    }
}
