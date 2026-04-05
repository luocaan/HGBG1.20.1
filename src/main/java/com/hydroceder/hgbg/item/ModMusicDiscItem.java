package com.hydroceder.hgbg.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModMusicDiscItem extends MusicDiscItem {
    private final String tooltipKey;
    
    public ModMusicDiscItem(int comparatorOutput, SoundEvent sound, Settings settings, int lengthInSeconds) {
        super(comparatorOutput, sound, settings, lengthInSeconds);
        this.tooltipKey = null;
    }
    
    public ModMusicDiscItem(int comparatorOutput, SoundEvent sound, Settings settings, int lengthInSeconds, String tooltipKey) {
        super(comparatorOutput, sound, settings, lengthInSeconds);
        this.tooltipKey = tooltipKey;
    }
    
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (tooltipKey != null) {
            tooltip.add(Text.translatable(tooltipKey).formatted(net.minecraft.util.Formatting.GRAY));
        } else {
            if (this == ModItems.MUSIC_DISC_THE_NEWAGE) {
                tooltip.add(Text.translatable("item.hunger-begone.music_disc_the_newage.tooltip").formatted(net.minecraft.util.Formatting.GRAY));
            } else if (this == ModItems.MUSIC_DISC_AFTERNOON) {
                tooltip.add(Text.translatable("item.hunger-begone.music_disc_afternoon.tooltip").formatted(net.minecraft.util.Formatting.GRAY));
            }
        }
    }
}
