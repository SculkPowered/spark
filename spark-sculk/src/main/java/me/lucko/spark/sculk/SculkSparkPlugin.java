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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sculkpowered.server.Server;
import io.github.sculkpowered.server.command.CommandSource;
import io.github.sculkpowered.server.event.Subscribe;
import io.github.sculkpowered.server.event.lifecycle.ServerInitializeEvent;
import io.github.sculkpowered.server.event.lifecycle.ServerShutdownEvent;
import io.github.sculkpowered.server.plugin.Plugin;
import io.github.sculkpowered.server.plugin.PluginDescription;
import java.util.Objects;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.sampler.source.SourceMetadata;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Stream;

@PluginDescription(name = "spark", version = "")
public class SculkSparkPlugin extends Plugin implements SparkPlugin {
    private SparkPlatform platform;

    @Subscribe
    public void handle(final ServerInitializeEvent event) {
        this.platform = new SparkPlatform(this);
        this.platform.enable();
        this.server().commandHandler().register(sparkCommand(this.server(), this.platform));
    }

    @Subscribe
    public void handle(final ServerShutdownEvent event) {
        this.platform.disable();
    }

    @Override
    public String getVersion() {
        return this.description().version();
    }

    @Override
    public Path getPluginDirectory() {
        return this.dataDirectory();
    }

    @Override
    public String getCommandName() {
        return "spark";
    }

    @Override
    public Stream<SculkCommandSender> getCommandSenders() {
        return Stream.concat(
                this.server().onlinePlayers().stream(),
                Stream.of(this.server().consoleCommandSource())
        ).map(commandSource -> new SculkCommandSender(this.server(), commandSource));
    }

    @Override
    public void executeAsync(Runnable task) {
        this.executorService().execute(task);
    }

    @Override
    public void log(Level level, String msg) {
        if (level == Level.INFO) {
            this.logger().info(msg);
        } else if (level == Level.WARNING) {
            this.logger().warn(msg);
        } else if (level == Level.SEVERE) {
            this.logger().error(msg);
        } else {
            throw new IllegalArgumentException(level.getName());
        }
    }

    @Override
    public PlatformInfo getPlatformInfo() {
        return new SculkPlatformInfo();
    }

    @Override
    public ClassSourceLookup createClassSourceLookup() {
        return new SculkClassSourceLookup();
    }

    @Override
    public Collection<SourceMetadata> getKnownSources() {
        return SourceMetadata.gather(
                this.server().pluginHandler().plugins(),
                plugin -> plugin.description().name(),
                plugin -> plugin.description().version(),
                plugin -> ""
        );
    }

    @Override
    public PlayerPingProvider createPlayerPingProvider() {
        return new SculkPlayerPingProvider(this.server());
    }

    @Override
    public TickReporter createTickReporter() {
        return new SculkTickReporter();
    }

    @Override
    public TickHook createTickHook() {
        return new SculkTickHook();
    }

    public static LiteralCommandNode<CommandSource> sparkCommand(
        final Server server, final SparkPlatform platform
    ) {
      return LiteralArgumentBuilder.<CommandSource>literal("spark")
          .then(RequiredArgumentBuilder.<CommandSource, String>
                  argument("arguments", StringArgumentType.greedyString())
              .executes(context -> {
                platform.executeCommand(new SculkCommandSender(server, context.getSource()),
                    processArgs(context, false));
                return Command.SINGLE_SUCCESS;
              })
              .suggests((context, builder) -> {
                  for (String s : Objects.requireNonNull(processArgs(context, true))) {
                      builder.suggest(s);
                  }
                  return builder.buildFuture();
              })
          )
          .executes(context -> {
            platform.executeCommand(new SculkCommandSender(server, context.getSource()), new String[0]);
            return Command.SINGLE_SUCCESS;
          })
          .build();
    }

    private static String [] processArgs(CommandContext<CommandSource> context, boolean tabComplete) {
      String[] split = context.getInput().split(" ", tabComplete ? -1 : 0);
      if (split.length == 0 || !split[0].equals("/spark") && !split[0].equals("spark")) {
        return null;
      }

      return Arrays.copyOfRange(split, 1, split.length);
    }
}
