package ma.ensa.ecoshop.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ma.ensa.ecoshop.model.Product;

public class ComponentConverter {

    @TypeConverter
    public static String fromComponentList(List<Product.Points.Component> components) {
        if (components == null || components.isEmpty()) return null;
        Gson gson = new Gson();
        return gson.toJson(components); // Convertit la liste en chaîne JSON.
    }

    @TypeConverter
    public static List<Product.Points.Component> toComponentList(String value) {
        if (value == null) return null;
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product.Points.Component>>() {}.getType();
        return gson.fromJson(value, type); // Convertit la chaîne JSON en liste.
    }
}

