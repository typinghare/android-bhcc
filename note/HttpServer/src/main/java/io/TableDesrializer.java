package io;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class TableDesrializer implements JsonDeserializer<Table<?, ?, ?>>{


	@Override
	public Table<?, ?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

		Table<Object, Object, Object> table = HashBasedTable.create();

		final Map<Object, Map<Object, Object>> rowMap = context.deserialize(json, Map.class);

		for (final Map.Entry<Object, Map<Object, Object>> rowEntry : rowMap.entrySet()) {
            final Object rowKey = rowEntry.getKey();
            for (final Map.Entry<Object, Object> cellEntry : rowEntry.getValue().entrySet()) {
                final Object colKey = cellEntry.getKey();
                final Object val = cellEntry.getValue();
                table.put(rowKey, colKey, val);
            }
        }

		return table;
	}
}