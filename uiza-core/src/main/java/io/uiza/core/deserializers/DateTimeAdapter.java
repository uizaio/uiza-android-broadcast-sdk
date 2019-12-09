package io.uiza.core.deserializers;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Date Time adapter for moshi
 */
public class DateTimeAdapter extends JsonAdapter<Date> {
    private static final String[] DATE_FORMATS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'hh:mm:ss'Z'",
            "yyyy/MM/dd",
            "yyyy-MM-dd"
    };

    @FromJson
    @Override
    public Date fromJson(JsonReader reader) throws IOException {
        for (String format : DATE_FORMATS) {
            try {
                return new SimpleDateFormat(format, Locale.getDefault()).parse(reader.nextString());
            } catch (ParseException e) {
                Timber.e(e, format);
            }
        }
        return null;
    }

    @ToJson
    @Override
    public void toJson(JsonWriter writer, Date value) throws IOException {
        if (value != null) {
            writer.value(value.toString());
        }
    }
}
