package com.hydroceder.hgbg.client.render.block;

import com.hydroceder.hgbg.HgbgMod;
import com.hydroceder.hgbg.block.MetronomeBlock;
import com.hydroceder.hgbg.block.entity.MetronomeBlockEntity;
import com.hydroceder.hgbg.client.model.MetronomePendulumModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class MetronomeBlockEntityRenderer implements BlockEntityRenderer<MetronomeBlockEntity> {

    private static final float MAX_ANGLE = 45f;

    private static final float PIVOT_Y = 0.0469f;

    private static final float NORTH_PIVOT_X = 0.5156f;
    private static final float NORTH_PIVOT_Z = 0.3516f;

    private static final float EAST_PIVOT_X = 0.6484f;
    private static final float EAST_PIVOT_Z = 0.5156f;

    private static final float SOUTH_PIVOT_X = 0.4844f;
    private static final float SOUTH_PIVOT_Z = 0.6484f;

    private static final float WEST_PIVOT_X = 0.3516f;
    private static final float WEST_PIVOT_Z = 0.4844f;

    private final MetronomePendulumModel model;

    public MetronomeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new MetronomePendulumModel(MetronomePendulumModel.createRoot());
    }

    @Override
    public void render(MetronomeBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        Direction facing = entity.getCachedState().get(MetronomeBlock.FACING);

        float time;
        if (entity.getWorld() != null) {
            time = entity.getWorld().getTime() + tickDelta;
        } else {
            time = (float)(System.currentTimeMillis() / 50.0) + tickDelta;
        }

        int bpmIndex = entity.getCachedState().get(MetronomeBlock.BPM);
        float bpm = MetronomeBlockEntity.BPM_LEVELS[bpmIndex];
        float swingPeriod = (2400.0f / bpm);
        float angle = MAX_ANGLE * (float) Math.sin(time * Math.PI * 2f / swingPeriod);

        matrices.push();

        float pivotX = switch (facing) {
            case NORTH -> NORTH_PIVOT_X;
            case EAST -> EAST_PIVOT_X;
            case SOUTH -> SOUTH_PIVOT_X;
            case WEST -> WEST_PIVOT_X;
            default -> NORTH_PIVOT_X;
        };
        float pivotZ = switch (facing) {
            case NORTH -> NORTH_PIVOT_Z;
            case EAST -> EAST_PIVOT_Z;
            case SOUTH -> SOUTH_PIVOT_Z;
            case WEST -> WEST_PIVOT_Z;
            default -> NORTH_PIVOT_Z;
        };

        matrices.translate(pivotX, PIVOT_Y, pivotZ);

        float yRotation = getYRotation(facing);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRotation));

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));

        matrices.translate(0, -1.5f, 0);

        VertexConsumer consumer = vertexConsumers.getBuffer(
                RenderLayer.getEntityCutoutNoCull(new Identifier(HgbgMod.MOD_ID, "textures/block/metronome_pendulum.png"))
        );

        model.renderArm(matrices, consumer, light, overlay);

        matrices.pop();
    }

    private float getYRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 0f;
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };
    }
}
