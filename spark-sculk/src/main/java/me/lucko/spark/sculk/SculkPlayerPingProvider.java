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

import io.github.sculkpowered.server.Server;
import java.util.HashMap;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;

import java.util.Map;

public class SculkPlayerPingProvider implements PlayerPingProvider {

    private final Server server;

    public SculkPlayerPingProvider(final Server server) {
        this.server = server;
    }

    @Override
    public Map<String, Integer> poll() {
        final var map = new HashMap<String, Integer>(this.server.playerCount());
        for (final var player : this.server.onlinePlayers()) {
            map.put(player.name(), player.ping());
        }
        return map;
    }
}
