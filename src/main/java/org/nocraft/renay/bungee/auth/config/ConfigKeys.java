/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package org.nocraft.renay.bungee.auth.config;

import com.google.common.collect.ImmutableMap;
import org.nocraft.renay.bungee.auth.storage.StorageType;
import org.nocraft.renay.bungee.auth.storage.misc.StorageCredentials;
import org.nocraft.renay.bungee.auth.util.ImmutableCollectors;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import static org.nocraft.renay.bungee.auth.config.ConfigKeyTypes.*;

/**
 * All of the {@link ConfigKey}s used by LuckPerms.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public final class ConfigKeys {
    private ConfigKeys() {}

    /**
     * The database settings, username, password, etc for use by any database
     */
    public static final ConfigKey<StorageCredentials> DATABASE_VALUES = enduringKey(customKey(c -> {
        int maxPoolSize = c.getInteger("data.pool-settings.maximum-pool-size", c.getInteger("data.pool-size", 10));
        int minIdle = c.getInteger("data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = c.getInteger("data.pool-settings.maximum-lifetime", 1800000);
        int connectionTimeout = c.getInteger("data.pool-settings.connection-timeout", 5000);
        Map<String, String> props = ImmutableMap.copyOf(c.getStringMap("data.pool-settings.properties", ImmutableMap.of()));

        return new StorageCredentials(
                c.getString("data.address", null),
                c.getString("data.database", null),
                c.getString("data.username", null),
                c.getString("data.password", null),
                maxPoolSize, minIdle, maxLifetime, connectionTimeout, props
        );
    }));

    /**
     * The prefix for any SQL tables
     */
    public static final ConfigKey<String> SQL_TABLE_PREFIX = enduringKey(customKey(c -> c.getString("data.table-prefix", c.getString("data.table_prefix", "luckperms_"))));

    /**
     * The name of the storage method being used
     */
    public static final ConfigKey<StorageType> STORAGE_METHOD = enduringKey(customKey(c -> StorageType.parse(c.getString("storage-method", "postgresql"), StorageType.POSTGRESQL)));

    /**
     * The URL of the tree viewer
     */
    public static final ConfigKey<String> TREE_VIEWER_URL_PATTERN = stringKey("tree-viewer-url", "https://luckperms.net/treeview/#");

    private static final List<ConfigKeyTypes.BaseConfigKey<?>> KEYS;

    static {
        // get a list of all keys
        KEYS = Arrays.stream(ConfigKeys.class.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> ConfigKey.class.equals(f.getType()))
                .map(f -> {
                    try {
                        return (ConfigKeyTypes.BaseConfigKey<?>) f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(ImmutableCollectors.toList());

        // set ordinal values
        for (int i = 0; i < KEYS.size(); i++) {
            KEYS.get(i).ordinal = i;
        }
    }

    /**
     * Gets a list of the keys defined in this class.
     *
     * @return the defined keys
     */
    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }

}