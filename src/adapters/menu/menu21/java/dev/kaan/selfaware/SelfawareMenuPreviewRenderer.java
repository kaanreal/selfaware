package dev.kaan.selfaware;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class SelfawareMenuPreviewRenderer {
    private static final Method DIRECT_RENDER = findDirectRender();

    private SelfawareMenuPreviewRenderer() {}

    static void render(GuiGraphics graphics, Font font, int width, int height, int mouseX, int mouseY,
                       float openingSpin) {
        SelfawareMenuStyle.render(canvas(graphics, font), width, height, mouseX, mouseY);

        int left = SelfawareMenu.previewLeft(width);
        int right = SelfawareMenu.previewRight(width);
        int top = SelfawareMenu.previewTop();
        int bottom = SelfawareMenu.previewBottom(height);
        LivingEntity player = Minecraft.getInstance().player;
        if (player == null) {
            graphics.drawCenteredString(font, Component.nullToEmpty("Join a world to preview"),
                    (left + right) / 2, (top + bottom) / 2, 0xFFBBBBBB);
            return;
        }

        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;
        float yaw = (float) Math.toDegrees(Math.atan((centerX - mouseX) / 90.0F)) * 0.38F;
        float pitch = (float) Math.toDegrees(Math.atan((centerY - mouseY) / 90.0F)) * 0.22F;
        if (!renderDirect(graphics, player, left, top, right, bottom, height, yaw, pitch, openingSpin)) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, left + 12, top + 42, right - 12,
                    bottom - 8, SelfawareMenu.playerScale(height), 0.0625F, mouseX, mouseY, player);
        }
    }

    private static boolean renderDirect(GuiGraphics graphics, LivingEntity player, int left, int top, int right,
                                        int bottom, int height, float yaw, float pitch, float openingSpin) {
        if (DIRECT_RENDER == null) {
            return false;
        }
        float oldBodyRot = player.yBodyRot;
        float oldYRot = player.getYRot();
        float oldXRot = player.getXRot();
        float oldHeadRot = player.yHeadRot;
        float oldHeadRotO = player.yHeadRotO;
        player.yBodyRot = 180.0F + yaw + openingSpin;
        player.setYRot(180.0F + yaw + openingSpin);
        player.setXRot(-pitch);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX((float) Math.toRadians(pitch));
        pose.mul(camera);
        Vector3f offset = new Vector3f(0.0F, player.getBbHeight() / 2.0F + 0.0625F, 0.0F);
        try {
            Class<?>[] types = DIRECT_RENDER.getParameterTypes();
            if (types.length == 8) {
                Object scale = types[3] == int.class ? SelfawareMenu.playerScale(height)
                        : (float) SelfawareMenu.playerScale(height);
                DIRECT_RENDER.invoke(null, graphics, (float) (left + right) / 2.0F, (float) bottom - 10.0F,
                        scale, offset, pose, camera, player);
            } else {
                DIRECT_RENDER.invoke(null, graphics, left + 12, top + 42, right - 12, bottom - 8,
                        (float) SelfawareMenu.playerScale(height), offset, pose, camera, player);
            }
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        } finally {
            player.yBodyRot = oldBodyRot;
            player.setYRot(oldYRot);
            player.setXRot(oldXRot);
            player.yHeadRot = oldHeadRot;
            player.yHeadRotO = oldHeadRotO;
        }
    }

    private static Method findDirectRender() {
        for (Method method : InventoryScreen.class.getDeclaredMethods()) {
            Class<?>[] types = method.getParameterTypes();
            if (!Modifier.isStatic(method.getModifiers()) || (types.length != 8 && types.length != 10)) {
                continue;
            }
            boolean vector = false;
            boolean quaternion = false;
            for (Class<?> type : types) {
                vector |= type == Vector3f.class;
                quaternion |= type == Quaternionf.class;
            }
            if (vector && quaternion && types[0] == GuiGraphics.class
                    && LivingEntity.class.isAssignableFrom(types[types.length - 1])) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }

    private static SelfawareMenuStyle.Canvas canvas(GuiGraphics graphics, Font font) {
        return new SelfawareMenuStyle.Canvas() {
            @Override
            public void fill(int left, int top, int right, int bottom, int color) {
                graphics.fill(left, top, right, bottom, color);
            }

            @Override
            public void text(Component text, int x, int y, int color) {
                graphics.drawString(font, text, x, y, color);
            }

            @Override
            public void centeredText(Component text, int x, int y, int color) {
                graphics.drawCenteredString(font, text, x, y, color);
            }
        };
    }
}
