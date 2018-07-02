package reposense.parser;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import reposense.model.Author;

/**
 * Ensures proper deserialization of {@code Author}
 */
public class AuthorAdapter extends TypeAdapter<Author> {
    public Author read(JsonReader reader) throws IOException {
        String gitId = reader.nextString();
        return new Author(gitId);
    }

    @Override
    public void write(JsonWriter writer, Author author) throws IOException {
        writer.value(author.toString());
    }
}
