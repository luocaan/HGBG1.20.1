package com.hydroceder.hgbg.client.render.entity;

import com.hydroceder.hgbg.HgbgMod;
import com.hydroceder.hgbg.client.model.SofaModel;
import com.hydroceder.hgbg.entity.SofaEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SofaEntityRenderer extends LivingEntityRenderer<SofaEntity, SofaModel> {
    private static final Identifier TEXTURE = new Identifier(HgbgMod.MOD_ID, "textures/entity/sofa.png");

    public SofaEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SofaModel(ctx.getPart(SofaModel.LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(SofaEntity entity) {
        return TEXTURE;
    }

    @Override
    protected boolean hasLabel(SofaEntity entity) {
        return false;
    }
}
