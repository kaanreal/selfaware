package dev.kaan.selfaware;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.font.EmptyArea;
import net.minecraft.client.gui.font.TextRenderable;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class PreparedTextRows implements Font.PreparedText {
    private final Font.PreparedText first;
    private final Font.PreparedText second;
    private final boolean stretchFirstEffect;
    private final ScreenRectangle bounds;

    public PreparedTextRows(Font.PreparedText first, Font.PreparedText second, boolean stretchFirstEffect) {
        this.first = first;
        this.second = second;
        this.stretchFirstEffect = stretchFirstEffect;
        ScreenRectangle a = first.bounds();
        ScreenRectangle b = second.bounds();
        int left = Math.min(a.left(), b.left());
        int top = Math.min(a.top(), b.top());
        int right = Math.max(a.right(), b.right());
        int bottom = Math.max(a.bottom(), b.bottom());
        bounds = new ScreenRectangle(left, top, right - left, bottom - top);
    }

    @Override
    public void visit(Font.GlyphVisitor visitor) {
        if (!stretchFirstEffect) {
            first.visit(visitor);
        } else {
            first.visit(new Font.GlyphVisitor() {
                private boolean firstEffect = true;

                @Override
                public void acceptEffect(TextRenderable effect) {
                    if (firstEffect) {
                        firstEffect = false;
                        visitor.acceptEffect(new ExpandedEffect(effect));
                    } else {
                        visitor.acceptEffect(effect);
                    }
                }

                @Override
                public void acceptGlyph(TextRenderable.Styled glyph) {
                    visitor.acceptGlyph(glyph);
                }

                @Override
                public void acceptRenderable(TextRenderable renderable) {
                    visitor.acceptRenderable(renderable);
                }

                @Override
                public void acceptEmptyArea(EmptyArea emptyArea) {
                    visitor.acceptEmptyArea(emptyArea);
                }
            });
        }
        second.visit(visitor);
    }

    @Override
    public ScreenRectangle bounds() {
        return bounds;
    }

    private static final class ExpandedEffect implements TextRenderable {
        private final TextRenderable delegate;

        private ExpandedEffect(TextRenderable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void render(Matrix4fc matrix, VertexConsumer vertices, int light, boolean seeThrough) {
            float top = delegate.top();
            Matrix4f expanded = new Matrix4f(matrix)
                    .translate(0.0F, top, 0.0F)
                    .scale(1.0F, 2.0F, 1.0F)
                    .translate(0.0F, -top, 0.0F);
            delegate.render(expanded, vertices, light, seeThrough);
        }

        @Override
        public RenderType renderType(Font.DisplayMode displayMode) {
            return delegate.renderType(displayMode);
        }

        @Override
        public GpuTextureView textureView() {
            return delegate.textureView();
        }

        @Override
        public RenderPipeline guiPipeline() {
            return delegate.guiPipeline();
        }

        @Override
        public float left() {
            return delegate.left();
        }

        @Override
        public float top() {
            return delegate.top();
        }

        @Override
        public float right() {
            return delegate.right();
        }

        @Override
        public float bottom() {
            return top() + (delegate.bottom() - top()) * 2.0F;
        }
    }
}
