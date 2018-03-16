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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final DateFormat iso_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static Date date(String date) {
        try {
            return iso_8601.parse(date);
        } catch (Throwable exception) {
            return null;
        }
    }

    public static String format(Date date) {
        return iso_8601.format(date);
    }

    public static <T> T safe(UnsafeSupplier<T, ? extends Throwable> supplier) {
        try {
            return supplier.get();
        } catch (Throwable thr) {
            return null;
        }
    }


    public static <T> T safeElse(UnsafeSupplier<T, ? extends Throwable> supplier, T t) {
        try {
            return supplier.get();
        } catch (Throwable thr) {
            return t;
        }
    }

    @FunctionalInterface
    public interface UnsafeSupplier<T, E extends Throwable> {
        T get() throws E;
    }
}
