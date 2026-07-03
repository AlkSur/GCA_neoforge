package dev.dubhe.gugle.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import dev.dubhe.gugle.carpet.api.Consumer;
import dev.dubhe.gugle.carpet.api.tools.text.ComponentTranslate;
import dev.dubhe.gugle.carpet.commands.BlistCommand;
import dev.dubhe.gugle.carpet.commands.BotCommand;
import dev.dubhe.gugle.carpet.commands.HereCommand;
import dev.dubhe.gugle.carpet.commands.LocCommand;
import dev.dubhe.gugle.carpet.commands.SopCommand;
import dev.dubhe.gugle.carpet.commands.TodoCommand;
import dev.dubhe.gugle.carpet.commands.WhereisCommand;
import dev.dubhe.gugle.carpet.commands.WlistCommand;
import dev.dubhe.gugle.carpet.tools.DimTypeSerializer;
import dev.dubhe.gugle.carpet.tools.FakePlayerEnderChestContainer;
import dev.dubhe.gugle.carpet.tools.FakePlayerInventoryContainer;
import dev.dubhe.gugle.carpet.tools.FakePlayerResident;
import net.neoforged.fml.common.Mod;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Mod("gca_neoforge")
public class GcaExtension implements CarpetExtension {
    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(ResourceKey.class, new DimTypeSerializer())
        .create();
    public static final String MOD_ID = "gca_neoforge";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static @NotNull ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final HashMap<Player, Map.Entry<FakePlayerInventoryContainer, FakePlayerEnderChestContainer>> fakePlayerInventoryContainerMap = new HashMap<>();
    public static final HashMap<String, java.util.function.Consumer<ServerPlayer>> ON_PLAYER_LOGGED_IN = new HashMap<>();

    public static final List<Map.Entry<Long, Consumer>> planFunction = new ArrayList<>();

    public GcaExtension() {
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        GcaExtension.fakePlayerInventoryContainerMap.put(player, Map.entry(
            new FakePlayerInventoryContainer(player), new FakePlayerEnderChestContainer(player)
        ));
        java.util.function.Consumer<ServerPlayer> consumer = ON_PLAYER_LOGGED_IN.remove(player.getGameProfile().getName());
        if (consumer != null) consumer.accept(player);
    }

    @Override
    public void onPlayerLoggedOut(ServerPlayer player) {
        GcaExtension.fakePlayerInventoryContainerMap.remove(player);
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(GcaSetting.class);
        BlistCommand.PERMISSION.init(CarpetServer.minecraft_server);
        BotCommand.BOT_INFO.init(CarpetServer.minecraft_server);
        LocCommand.LOC_POINT.init(CarpetServer.minecraft_server);
        TodoCommand.TODO.init(CarpetServer.minecraft_server);
        WlistCommand.PERMISSION.init(CarpetServer.minecraft_server);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        if (GcaSetting.fakePlayerResident) {
            JsonObject fakePlayerList = new JsonObject();
            fakePlayerInventoryContainerMap.keySet().forEach(player -> {
                if (!(player instanceof EntityPlayerMPFake)) return;
                if (player.saveWithoutId(new CompoundTag()).contains("gca.NoResident")) return;
                String username = player.getGameProfile().getName();
                fakePlayerList.add(username, FakePlayerResident.save(player));
            });
            File file = server.getWorldPath(LevelResource.ROOT).resolve("fake_player.gca.json").toFile();
            try (BufferedWriter bfw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                bfw.write(GSON.toJson(fakePlayerList));
            } catch (IOException e) {
                GcaExtension.LOGGER.error(e.getMessage(), e);
            }
        }
        fakePlayerInventoryContainerMap.clear();
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        if (GcaSetting.fakePlayerResident) {
            File file = server.getWorldPath(LevelResource.ROOT).resolve("fake_player.gca.json").toFile();
            if (!file.isFile()) {
                return;
            }
            try (BufferedReader bfr = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                JsonObject fakePlayerList = GSON.fromJson(bfr, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : fakePlayerList.entrySet()) {
                    FakePlayerResident.load(entry, server);
                }
            } catch (IOException e) {
                GcaExtension.LOGGER.error(e.getMessage(), e);
            }
            file.delete();
        }
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        BotCommand.register(dispatcher);
        LocCommand.register(dispatcher);
        HereCommand.register(dispatcher);
        WhereisCommand.register(dispatcher);
        TodoCommand.register(dispatcher);
        WlistCommand.register(dispatcher);
        BlistCommand.register(dispatcher);
        SopCommand.register(dispatcher);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return ComponentTranslate.getTranslations(lang);
    }
}
