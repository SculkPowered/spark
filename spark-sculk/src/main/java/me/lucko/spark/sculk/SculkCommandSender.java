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
import io.github.sculkpowered.server.command.CommandSource;
import io.github.sculkpowered.server.entity.player.Player;
import me.lucko.spark.common.command.sender.AbstractCommandSender;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public class SculkCommandSender extends AbstractCommandSender<CommandSource> {
    private final Server server;

    public SculkCommandSender(Server server, CommandSource delegate) {
        super(delegate);
        this.server = server;
    }

    @Override
    public String getName() {
        if (this.delegate instanceof Player player) {
            return player.name();
        } else if (this.delegate == this.server.consoleCommandSource()) {
            return "Console";
         }else {
            return "unknown:" + this.delegate.getClass().getSimpleName();
        }
    }

    @Override
    public UUID getUniqueId() {
        if (super.delegate instanceof Player player) {
            return player.uniqueId();
        }
        return null;
    }

    @Override
    public void sendMessage(Component message) {
        this.delegate.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.delegate.hasPermission(permission);
    }
}
