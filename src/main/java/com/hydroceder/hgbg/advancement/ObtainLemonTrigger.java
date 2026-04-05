package com.hydroceder.hgbg.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ObtainLemonTrigger extends AbstractCriterion<ObtainLemonTrigger.Conditions> {
    public static final Identifier ID = new Identifier("hunger-begone", "obtain_lemon_trigger");
    private static final ObtainLemonTrigger INSTANCE = new ObtainLemonTrigger();

    public static ObtainLemonTrigger getInstance() {
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
