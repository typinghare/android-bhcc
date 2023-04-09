package io;

import java.lang.reflect.Type;

import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TableSerializer implements JsonSerializer<Table<?, ?, ?>> {

	@Override
	public JsonElement serialize(Table<?, ?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src.rowMap());
	}

}
