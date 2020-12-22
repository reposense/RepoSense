package reposense;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import reposense.model.FileType;

import java.io.FileReader;
import java.io.FileWriter;

public class JsonWriter {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(FileType.class, new FileType.FileTypeSerializer())
                .create();

        JsonElement json = gson.fromJson(new FileReader("E:\\RepoSense\\RepoSense\\src\\systemtest\\resources\\30daysFromUntilDate\\expected\\reposense_testrepo-Charlie_master\\authorship.json"), JsonElement.class);
        gson.toJson(json, new FileWriter("E:\\try\\reposense_testrepo-Alpha_master\\authorship.json"));
    }
}
