package dev.dubhe.gugle.carpet.mixin;

import dev.dubhe.gugle.carpet.GcaSetting;
import dev.dubhe.gugle.carpet.tools.FastPingFriend;
import dev.dubhe.gugle.carpet.tools.SimpleInGameCalculator;
import dev.dubhe.gugle.carpet.tools.TriConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Fix injection target for MC 1.21.1 + NeoForge
// The handleChat method signature has changed in this version.
// Original injection target:
//   Lnet/minecraft/server/network/ServerGamePacketListenerImpl;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V
// may not exist anymore.

@Mixin(ServerGamePacketListenerImpl.class)
abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public abstract ServerPlayer getPlayer();

    @Inject(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V", shift = At.Shift.AFTER))
    private void handleChat(PlayerChatMessage playerChatMessage, Component component, FilteredText filteredText, CallbackInfo ci) {
        this.gca$handleChat(GcaSetting.simpleInGameCalculator, "==", component, (server, player, msg) -> SimpleInGameCalculator.handleChat(server, msg));
        this.gca$handleChat(GcaSetting.fastPingFriend, "@ ", component, FastPingFriend::handleChat);
        this.gca$handleChat(GcaSetting.fastPingFriend, "@@ ", component, FastPingFriend::handleChatUrgent);
    }

    @Unique
    private void gca$handleChat(boolean rule, String prefix, Component component, TriConsumer<MinecraftServer, ServerPlayer, String> handle) {
        if (!rule) return;
        String string = component.getString();
        if (!string.startsWith(prefix)) return;
        string = string.substring(prefix.length());
        ServerPlayer player = this.getPlayer();
        MinecraftServer server = player.getServer();
        handle.accept(server, player, string);
    }
}
