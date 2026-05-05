package com.hydroceder.hgbg.client.model;

import com.hydroceder.hgbg.HgbgMod;
import com.hydroceder.hgbg.entity.SofaEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;

public class SofaModel extends EntityModel<SofaEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(new Identifier(HgbgMod.MOD_ID, "sofa"), "main");
    private final ModelPart bone;

    public SofaModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-13.0F, -6.0F, -8.0F, 26.0F, 6.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 22).cuboid(-13.0F, -20.0F, 8.0F, 26.0F, 14.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 41).cuboid(12.5F, -12.0F, -8.0F, 6.0F, 6.0F, 16.0F, new Dilation(0.0F))
                .uv(44, 41).cuboid(-17.5F, -12.0F, -8.0F, 5.0F, 6.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(SofaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
