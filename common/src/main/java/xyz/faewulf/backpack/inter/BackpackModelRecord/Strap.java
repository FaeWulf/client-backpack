package xyz.faewulf.backpack.inter.BackpackModelRecord;

import com.google.gson.*;

import java.lang.reflect.Type;

public record Strap(boolean visible, String id) {

    public Strap() {
        this(false, "default");
    }

    // Custom deserializer to treat empty JSON objects `{}` as null
    public static class StrapDeserializer implements JsonDeserializer<Strap> {
        @Override
        public Strap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject() && json.getAsJsonObject().entrySet().isEmpty()) {
                return null; // Treat {} as null
            }

            return new Gson().fromJson(json, Strap.class);
        }

    }
}
