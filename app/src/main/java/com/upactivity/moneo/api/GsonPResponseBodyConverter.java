package com.upactivity.moneo.api;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * http://stackoverflow.com/a/41235653
 */
class GsonPResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;

    private final TypeAdapter<T> adapter;

    GsonPResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader reader = value.charStream();
        int item;
        do {
            item = reader.read();
        } while (item != '(' && item != -1);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            reader.close();
        }
    }

}
