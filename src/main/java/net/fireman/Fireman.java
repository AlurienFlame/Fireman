package net.fireman;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Fireman implements ClientModInitializer {

    private static KeyBinding keyToggleFireman;
    private static boolean isFiremanEnabled = false;

    @Override
    public void onInitializeClient() {
        // Setup keybind
        keyToggleFireman = new KeyBinding("key.fireman.togglefireman", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P,
                "category.fireman.Fireman");

        KeyBindingHelper.registerKeyBinding(keyToggleFireman);

        // Keybind functionality
        ClientTickCallback.EVENT.register(client -> {
            if (keyToggleFireman.wasPressed()) {
                isFiremanEnabled = !isFiremanEnabled;
                if (isFiremanEnabled) {
                    client.player.sendMessage(
                            new LiteralText(new TranslatableText("notify.fireman.enabled").getString()), true);
                } else {
                    client.player.sendMessage(
                            new LiteralText(new TranslatableText("notify.fireman.disabled").getString()), true);
                }
            }
        });

        // Register onTick event
        ClientTickCallback.EVENT.register(client -> {
            onTick(client);
        });
    }

    private void onTick(MinecraftClient client) {

        // Do nothing if disabled
        if (!isFiremanEnabled) {
            return;
        }

        // Avoid crashing before a world has been loaded
        if (client.player == null) {
            return;
        }

        // Find player location
        int X = (int) Math.floor(client.player.getX());
        int Y = (int) Math.floor(client.player.getY());
        int Z = (int) Math.floor(client.player.getZ());

        // Loop through nearby blocks
        for (int deltaY = 3; deltaY >= -2; --deltaY) {
            for (int deltaX = -3; deltaX <= 3; ++deltaX) {
                for (int deltaZ = -3; deltaZ <= 3; ++deltaZ) {

                    BlockPos pos = new BlockPos(X + deltaX, Y + deltaY, Z + deltaZ);

                    // Punch fires
                    // 1.17 moved isIn() to BlockState from Block, so we don't need getBlock() anymore.
                    if (client.world.getBlockState(pos).isIn(BlockTags.FIRE)) {
                        client.interactionManager.attackBlock(pos, Direction.UP);
                        return;
                    }
                }
            }
        }
    }

}