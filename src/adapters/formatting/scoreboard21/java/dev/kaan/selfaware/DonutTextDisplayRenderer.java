package dev.kaan.selfaware;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class DonutTextDisplayRenderer {
    private DonutTextDisplayRenderer() {}

    public static void render(AbstractClientPlayer player, Component name, Component money, PoseStack poseStack,
            MultiBufferSource buffers, int light, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        Vec3 attachment = player.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0,
                player.getViewYRot(partialTick));
        if (attachment == null) {
            return;
        }

        Font font = client.font;
        int nameWidth = font.width(name);
        int moneyWidth = font.width(money);
        int width = Math.max(nameWidth, moneyWidth);
        boolean sneaking = player.isDiscrete();
        ServerFormatting.Appearance appearance = ServerFormatting.observedAppearance();
        boolean shadow = appearance == null || appearance.shadow;
        int background = backgroundColor(client, appearance);

        poseStack.pushPose();
        // Keep the first line on the same anchor as vanilla and Simple Voice Chat.
        poseStack.translate(attachment.x, attachment.y + 0.5, attachment.z);
        poseStack.mulPose(client.getEntityRenderDispatcher().cameraOrientation());
        Matrix4f matrix = poseStack.last().pose();
        matrix.rotate((float) Math.PI, 0.0F, 1.0F, 0.0F);
        matrix.scale(-0.025F, -0.025F, -0.025F);
        matrix.translate(-width / 2.0F, 0.0F, 0.0F);

        if (background != 0) {
            VertexConsumer backgroundBuffer = buffers.getBuffer(
                    sneaking ? RenderType.textBackground() : RenderType.textBackgroundSeeThrough());
            backgroundBuffer.addVertex(matrix, -1.0F, -1.0F, 0.0F).setColor(background).setLight(light);
            backgroundBuffer.addVertex(matrix, -1.0F, 20.0F, 0.0F).setColor(background).setLight(light);
            backgroundBuffer.addVertex(matrix, width, 20.0F, 0.0F).setColor(background).setLight(light);
            backgroundBuffer.addVertex(matrix, width, -1.0F, 0.0F).setColor(background).setLight(light);
        }

        Font.DisplayMode dimMode = sneaking ? Font.DisplayMode.NORMAL : Font.DisplayMode.SEE_THROUGH;
        float nameX = width / 2.0F - nameWidth / 2.0F;
        float moneyX = width / 2.0F - moneyWidth / 2.0F;
        float moneyY = 10.0F;
        drawLine(font, name, nameX, 0.0F, 0x20FFFFFF, shadow, matrix, buffers, dimMode, light);
        drawLine(font, money, moneyX, moneyY, 0x20FFFFFF, shadow, matrix, buffers, dimMode, light);

        if (!sneaking) {
            drawLine(font, name, nameX, 0.0F, -1, shadow, matrix, buffers, Font.DisplayMode.NORMAL, light);
            drawLine(font, money, moneyX, moneyY, -1, shadow, matrix, buffers, Font.DisplayMode.NORMAL, light);
        }
        poseStack.popPose();
    }

    private static void drawLine(Font font, Component text, float x, float y, int color, boolean shadow,
            Matrix4f matrix, MultiBufferSource buffers, Font.DisplayMode mode, int light) {
        font.drawInBatch(text.getVisualOrderText(), x, y, color, shadow, matrix, buffers, mode, 0, light);
    }

    private static int backgroundColor(Minecraft client, ServerFormatting.Appearance appearance) {
        if (appearance != null && !appearance.defaultBackground) {
            return appearance.background;
        }
        return (int) (client.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
    }
}
