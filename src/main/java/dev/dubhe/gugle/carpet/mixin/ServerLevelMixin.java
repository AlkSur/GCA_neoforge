package dev.dubhe.gugle.carpet.mixin;

import dev.dubhe.gugle.carpet.GcaExtension;
import dev.dubhe.gugle.carpet.api.Consumer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
abstract class ServerLevelMixin {
    @Unique
    ServerLevel gca$self = (ServerLevel) (Object) this;

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        long gameTime = gca$self.getLevel().getGameTime();
        GcaExtension.planFunction.removeIf(pair -> {
            if (pair.getKey() == gameTime) {
                pair.getValue().accept();
                return true;
            }
            return pair.getKey() < gameTime;
        });
    }
}