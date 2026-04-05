package com.hydroceder.hgbg.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class DistanceTrigger extends AbstractCriterion<DistanceTrigger.Conditions> {
    public static final Identifier ID = new Identifier("hunger-begone", "distance_trigger");
    private static final DistanceTrigger INSTANCE = new DistanceTrigger();

    public static DistanceTrigger getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate player, AdvancementEntityPredicateDeserializer deserializer) {
        return new Conditions(player);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(LootContextPredicate player) {
            super(ID, player);
        }
    }
}