package com.hydroceder.hgbg.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BonemealOakLeavesTrigger extends AbstractCriterion<BonemealOakLeavesTrigger.Conditions> {
    public static final Identifier ID = new Identifier("hunger-begone", "bonemeal_oak_leaves_trigger");
    private static final BonemealOakLeavesTrigger INSTANCE = new BonemealOakLeavesTrigger();

    public static BonemealOakLeavesTrigger getInstance() {
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
