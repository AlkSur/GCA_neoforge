package dev.dubhe.gugle.carpet.mixin;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Command registration is handled by {@link dev.dubhe.gugle.carpet.GcaExtension#registerCommands}
 * via the standard CarpetExtension API. This empty injection point is retained for compatibility.
 */
@Mixin(Commands.class)
abstract class CommandsMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V"), remap = false)
    private void register(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
    }
}