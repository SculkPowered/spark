/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.sculk;

import me.lucko.spark.common.sampler.source.ClassSourceLookup;

import java.util.HashMap;
import java.util.Map;

public class SculkClassSourceLookup extends ClassSourceLookup.ByClassLoader {
    private final Map<ClassLoader, String> classLoaderToExtensions;

    public SculkClassSourceLookup() {
        this.classLoaderToExtensions = new HashMap<>();
        /*for (Extension extension : MinecraftServer.getExtensionManager().getExtensions()) {
            this.classLoaderToExtensions.put(extension.getClass().getClassLoader(), extension.getOrigin().getName());
        }*/
    }

    @Override
    public String identify(ClassLoader loader) {
        /*if (loader instanceof ExtensionClassLoader) {
            return this.classLoaderToExtensions.get(loader);
        }*/
        return null;
    }
}
