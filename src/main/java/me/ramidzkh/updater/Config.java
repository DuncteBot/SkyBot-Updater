/*
 * Skybot Updater, an updater application for SkyBot
 *      Copyright (C) 2017, 2018  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Sanduhr32
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.ramidzkh.updater;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

import static java.util.Optional.ofNullable;

@Data
@NoArgsConstructor
@Builder
public class Config {

    private Gson gson = new Gson();
    private JsonObject config = new JsonObject();

    public boolean load(File in) {
        if(in == null)
            throw new NullPointerException("in == null");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(in));
            config = gson.fromJson(reader, JsonObject.class);
            reader.close();
        } catch (Throwable exception) {
            new Exception("An error occured while reading the config from '" + in + "'", exception).printStackTrace();

            return false;
        }

        return true;
    }

    public Config(Gson gson, JsonObject jsonObject) {

    }

    public Config getNested(String path) {
        if(path == null)
            throw new NullPointerException("path == null");

        return new Config(gson,
                        ofNullable(config.get(path))
                            .orElseGet(() -> {
                                config.add(path, new JsonObject());
                                return config.getAsJsonObject(path);
                            }).getAsJsonObject());
    }

    public JsonElement getOrElse(String name, JsonElement orElse) {
        if(name == null)
            throw new NullPointerException("path == null");

        JsonElement element = config.get(name);
        if(element == null)
            config.add(name, orElse);

        return config.get(name);
    }

    public <T> T parseOrElse(String name, Class<T> type, T orElse)
    throws JsonSyntaxException, NullPointerException {
        JsonElement toParse = getOrElse(name, null);

        if(toParse == null)
            throw new NullPointerException("Key not found");

        T object = gson.fromJson(toParse, type);

        return object == null ? orElse : object;
    }

    public Gson getGson() {
        return gson;
    }
}
