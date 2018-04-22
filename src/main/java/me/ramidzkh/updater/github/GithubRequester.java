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

package me.ramidzkh.updater.github;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ramidzkh.updater.github.objects.Asset;
import me.ramidzkh.updater.github.objects.NotFound;
import me.ramidzkh.updater.github.objects.Release;
import me.ramidzkh.updater.github.objects.RepositoryRef;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static me.ramidzkh.updater.github.objects.Utils.safeElse;

@Data
@NoArgsConstructor
public class GithubRequester {

    public static final Pattern ASSET_NAME = Pattern.compile("skybot-(\\d+\\.)*\\d+(_[0-9A-Fa-f]{8})?\\.jar");

    private OkHttpClient client = new OkHttpClient();
    private String appName = "Github-App";
    private Gson gson = new Gson();

    public List<Release> getReleases(RepositoryRef repo) throws NotFound, IOException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", appName)
                .url("https://api.github.com/repos/" + repo.getOwner() + "/" + repo.getName() + "/releases")
                .get()
                .build();

        @Cleanup
        Response response = client.newCall(request).execute();

        // Not found
        if (response.code() == 404)
            throw safeElse(() ->
                            gson.fromJson(response.body().string(), NotFound.class),
                    new NotFound());

        if (!response.isSuccessful())
            throw new IOException(response.code() + " " + response.message());

        @Cleanup
        ResponseBody body = response.body();

        @Cleanup
        BufferedReader reader = new BufferedReader(new InputStreamReader(body.byteStream(), "UTF-8"));

        try {
            Release[] releases = gson.fromJson(reader, Release[].class);

            // Wrap it in an ArrayList
            return new ArrayList<>(Arrays.asList(releases));
        } catch (JsonParseException e) {
            throw new JsonParseException("Error while parsing; " + e.getLocalizedMessage(), e);
        }
    }

    public void download(Release release, OutputStream out) throws IOException {
        List<Asset> assets = release.getAssets();

        if (assets.size() == 0)
            return;

        Asset toDownload = assets.parallelStream()
                .filter(a -> ASSET_NAME.matcher(a.getName()).matches())
                .findAny().orElse(null);

        if (toDownload == null)
            return;

        final String url = toDownload.getBrowser_download_url();

        Response response = client.newCall(new Request.Builder()
                .url(url)
                .header("Content-Type", toDownload.getContent_type())
                .header("Connection", "keep-alive")
                .build()).execute();

        if (!response.isSuccessful())
            throw new IOException(response.code() + " " + response.message());

        InputStream in = response.body().byteStream();

        byte[] bytes = new byte[1024];
        int len, totalLength = 0;

        while ((len = in.read(bytes)) > 0) {
            out.write(bytes, 0, len);
            totalLength += len;

            System.out.print(buildBar(totalLength, toDownload.getSize()));
        }

        System.out.println("\n\nDownloaded\n\n");
    }

    private String buildBar(long totalLength, long size) {
        long equals = totalLength * 100 / size;
        long spaces = Math.abs(100 - equals);

        String equal = String.join("", Collections.nCopies((int) (equals & 0x7FFFFFFF), "="));
        String space = String.join("", Collections.nCopies((int) (spaces & 0x7FFFFFFF), " "));

        return String.format("\r[%s%s] %.2f%%", equal, space, (((double) totalLength) / ((double) size)) * 100F);
    }

    public Gson getGson() {
        return gson;
    }
}
