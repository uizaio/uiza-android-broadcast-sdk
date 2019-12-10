package io.uiza.core.deserializers;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;

import io.uiza.core.helpers.DateHelper;

/**
 * Date Time adapter for moshi
 */
public class DateTimeAdapter extends JsonAdapter<Date> {

    @FromJson
    @Override
    public Date fromJson(JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NULL) return reader.nextNull();
        return DateHelper.parse(reader.nextString()).toDate();
    }

    @ToJson
    @Override
    public void toJson(JsonWriter writer, Date value) throws IOException {
        if (value != null) {
            writer.value(DateHelper.toString(new DateTime(value.getTime())));
        }
    }
}
