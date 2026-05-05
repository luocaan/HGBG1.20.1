package com.hydroceder.hgbg.client.model;

import com.hydroceder.hgbg.HgbgMod;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class MetronomePendulumModel {

    public static final EntityModelLayer LAYER =
            new EntityModelLayer(new Identifier(HgbgMod.MOD_ID, "metronome_pendulum"), "main");

    private final ModelPart arm;

    public MetronomePendulumModel(ModelPart root) {
        this.arm = root.getChild("arm");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData arm = root.addChild("arm",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-0.25F, 0.0F, -0.25F, 0.5F, 8.5F, 0.5F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        arm.addChild("weight",
                ModelPartBuilder.create()
                        .uv(0, 9)
                        .cuboid(-0.4F, 1.7F, -0.4F, 0.8F, 1.2F, 0.8F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 16, 16);
    }

    public static ModelPart createRoot() {
        return getTexturedModelData().createModel();
    }

    public void renderArm(net.minecraft.client.util.math.MatrixStack matrices,
                          net.minecraft.client.render.VertexConsumer consumer,
                          int light, int overlay) {
        arm.render(matrices, consumer, light, overlay);
    }
}
