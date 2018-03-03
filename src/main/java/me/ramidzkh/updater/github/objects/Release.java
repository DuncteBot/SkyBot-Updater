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

package me.ramidzkh.updater.github.objects;

import lombok.Data;

import java.util.List;

/**
 * A simple data class that will be deserialized by Gson
 *
 * @author ramidzkh
 */
@Data
public class Release {

    // Strings
    private final String
            // URLs
            url, assets_url, upload_url, html_url,
    // Metadata
    tag_name, target_commitish, name, body,
    // Time
    created_at, published_at,
    // Downloads
    tarball_url, zipball_url;

    // Integers
    private final int id;

    // Booleans
    private final boolean draft, prerelease;

    // ---- Other Github Objects ----

    // Author
    private final User author;

    // Assets
    private final List<Asset> assets;
}
