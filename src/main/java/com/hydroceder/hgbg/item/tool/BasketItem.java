package com.hydroceder.hgbg.item.tool;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class BasketItem extends Item {

    private static final String STORED_KEY = "StoredItems";
    private static final String STORED_ENTITY_KEY = "StoredEntity";
    private static final int MAX_CAPACITY = 6;

    public static String getStoredKey() {
        return STORED_KEY;
    }

    public BasketItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Only handle when sneaking
        if (!user.isSneaking()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack basketStack = user.getStackInHand(hand);

        // Respect cooldown: no basket functions during cooldown
        if (user.getItemCooldownManager().isCoolingDown(basketStack.getItem())) {
            return TypedActionResult.fail(basketStack);
        }

        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack otherStack = user.getStackInHand(otherHand);

        // If basket has a stored entity, placing it takes highest priority when sneaking
        if (hasStoredEntity(basketStack)) {
            // If other hand is empty, do not retrieve items but place the stored entity
            if (otherStack.isEmpty()) {
                if (!world.isClient) {
                    return placeStoredEntity((ServerWorld) world, user, hand, basketStack);
                }
                return TypedActionResult.success(basketStack);
            }
            // else fall through to item retrieval when other hand is not empty
        }

        if (otherStack.isEmpty()) {
            return retrieveItems(world, user, hand, basketStack);
        } else {
            return storeItems(world, user, otherHand, basketStack, otherStack);
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.isSneaking() || user.getWorld().isClient) return ActionResult.PASS;
        if (entity instanceof PlayerEntity) return ActionResult.PASS;

        // Respect cooldown
        if (user.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            return ActionResult.FAIL;
        }

        ItemStack basketStack = user.getStackInHand(hand);

        // If basket has a stored entity, place it riding the target entity
        if (hasStoredEntity(basketStack)) {
            if (user.getWorld() instanceof ServerWorld serverWorld) {
                boolean placed = placeEntityOnTarget(serverWorld, user, hand, basketStack, entity);
                return placed ? ActionResult.success(false) : ActionResult.PASS;
            }
            return ActionResult.success(false);
        }

        // Otherwise, capture the target entity
        boolean captured = attemptCaptureEntity(basketStack, user, entity, hand);
        return captured ? ActionResult.success(user.getWorld().isClient()) : ActionResult.PASS;
    }

    // Helper used by global entity-use callback to attempt capturing an entity into the basket.
    public static boolean attemptCaptureEntity(ItemStack basketStack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getWorld().isClient) return false;
        if (entity instanceof PlayerEntity) return false;

        if (hasStoredEntity(basketStack)) {
            user.sendMessage(Text.translatable("item.hunger-begone.basket.full").formatted(Formatting.RED), true);
            return false;
        }

    // If entity is tameable / has an owner, ensure the player is the owner before allowing capture
    java.util.UUID detectedOwner = null;
    try {
            // Check common tameable interface
            if (entity instanceof net.minecraft.entity.passive.TameableEntity) {
                net.minecraft.entity.passive.TameableEntity tame = (net.minecraft.entity.passive.TameableEntity) entity;
                if (tame.isTamed()) {
                    java.util.UUID owner = tame.getOwnerUuid();
                    // fallback: some entities/mods store owner uuid under different NBT keys
                    if (owner == null) {
                        try {
                            NbtCompound tmp = new NbtCompound();
                            entity.writeNbt(tmp);
                            if (tmp.containsUuid("Owner")) {
                                owner = tmp.getUuid("Owner");
                            } else if (tmp.containsUuid("OwnerUUID")) {
                                owner = tmp.getUuid("OwnerUUID");
                            } else if (tmp.contains("OwnerUUIDMost") && tmp.contains("OwnerUUIDLeast")) {
                                long most = tmp.getLong("OwnerUUIDMost");
                                long least = tmp.getLong("OwnerUUIDLeast");
                                owner = new java.util.UUID(most, least);
                            }
                        } catch (Exception ignored2) {}
                    }

                    detectedOwner = owner;
                    if (owner != null && !owner.equals(user.getUuid())) {
                        user.sendMessage(Text.translatable("item.hunger-begone.basket.not_your_pet").formatted(Formatting.RED), true);
                        return false;
                    }
                }
            }
        } catch (Exception ignored) {}

        NbtCompound entNbt = new NbtCompound();
        entity.writeNbt(entNbt);

        // Strip data that should NOT be persisted across capture/release:
        //   Passengers — never capture riders, they remain in the world and would be duplicated on release
        //   Motion     — velocity is transient and irrelevant when placed stationary
        //   Leash      — leashed fence/entity is NOT captured, so this reference is invalid when released
        //   HurtByTimestamp / DeathLootTime — combat context that is meaningless after re-spawn
        entNbt.remove("Passengers");
        entNbt.remove("Motion");
        entNbt.remove("Leash");
        entNbt.remove("HurtByTimestamp");
        entNbt.remove("DeathLootTime");
        try {
            Identifier entId = Registries.ENTITY_TYPE.getId(entity.getType());
            if (entId != null) entNbt.putString("id", entId.toString());
        } catch (Exception ignored) {}

    // If we detected an owner, try to store a display name for the owner so tooltip can show it client-side
        try {
            if (detectedOwner != null) {
                String ownerName = null;
                // if the capturer is the owner, use their name
                if (detectedOwner.equals(user.getUuid())) {
                    ownerName = user.getName().getString();
                } else {
                    try {
                        if (user.getWorld() instanceof ServerWorld) {
                            ServerWorld sw = (ServerWorld) user.getWorld();
                            net.minecraft.server.network.ServerPlayerEntity sp = sw.getServer().getPlayerManager().getPlayer(detectedOwner);
                            if (sp != null) ownerName = sp.getName().getString();
                        }
                    } catch (Exception ignored2) {}
                }

                if (ownerName != null) {
                    entNbt.putString("OwnerName", ownerName);
                }
            }
        } catch (Exception ignored) {}

        // Record collider dimensions so other systems can react to stored entity size
        try {
            double width = entity.getWidth();
            double height = entity.getHeight();
            entNbt.putDouble("ColliderWidth", width);
            entNbt.putDouble("ColliderHeight", height);
        } catch (Exception ignored) {}

        NbtCompound nbt = basketStack.getOrCreateNbt();
        nbt.put(STORED_ENTITY_KEY, entNbt);

    entity.remove(Entity.RemovalReason.DISCARDED);
    user.getItemCooldownManager().set(basketStack.getItem(), 10);
        return true;
    }

    private TypedActionResult<ItemStack> storeItems(World world, PlayerEntity user, Hand itemHand, ItemStack basketStack, ItemStack itemStack) {
        if (world.isClient) {
            return TypedActionResult.success(basketStack);
        }

        if (itemStack.getItem() instanceof BasketItem) {
            user.sendMessage(Text.translatable("item.hunger-begone.basket.cannot_store_basket").formatted(Formatting.RED), true);
            return TypedActionResult.fail(basketStack);
        }

        NbtCompound nbt = basketStack.getOrCreateNbt();
        NbtList storedList;
        if (nbt.contains(STORED_KEY)) {
            storedList = nbt.getList(STORED_KEY, 10);
        } else {
            storedList = new NbtList();
        }

        if (storedList.size() >= MAX_CAPACITY) {
            user.sendMessage(Text.translatable("item.hunger-begone.basket.full").formatted(Formatting.RED), true);
            return TypedActionResult.fail(basketStack);
        }

        NbtCompound itemNbt = new NbtCompound();
        itemStack.writeNbt(itemNbt);
        storedList.add(0, itemNbt);
        nbt.put(STORED_KEY, storedList);

        user.setStackInHand(itemHand, ItemStack.EMPTY);
        user.getItemCooldownManager().set(this, 10);

        return TypedActionResult.success(basketStack);
    }

    private TypedActionResult<ItemStack> retrieveItems(World world, PlayerEntity user, Hand hand, ItemStack basketStack) {
        if (world.isClient) {
            return TypedActionResult.success(basketStack);
        }
        NbtCompound nbt = basketStack.getOrCreateNbt();
    // If basket has stored entity and other hand is empty, placing is handled elsewhere
    // Continue with item retrieval behavior
        if (!nbt.contains(STORED_KEY)) {
            return TypedActionResult.pass(basketStack);
        }

        NbtList storedList = nbt.getList(STORED_KEY, 10);
        if (storedList.isEmpty()) {
            return TypedActionResult.pass(basketStack);
        }

        ItemStack retrieved = ItemStack.fromNbt(storedList.getCompound(0));
        storedList.remove(0);

        if (storedList.isEmpty()) {
            nbt.remove(STORED_KEY);
        }

        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (user.getStackInHand(otherHand).isEmpty()) {
            user.setStackInHand(otherHand, retrieved);
        } else {
            if (!user.giveItemStack(retrieved)) {
                user.dropItem(retrieved, false);
            }
        }

        user.getItemCooldownManager().set(this, 10);

        return TypedActionResult.success(basketStack);
    }

    private TypedActionResult<ItemStack> placeStoredEntity(ServerWorld world, PlayerEntity user, Hand hand, ItemStack basketStack) {
        NbtCompound nbt = basketStack.getOrCreateNbt();
        if (!nbt.contains(STORED_ENTITY_KEY)) {
            return TypedActionResult.pass(basketStack);
        }

        NbtCompound entNbt = nbt.getCompound(STORED_ENTITY_KEY);

        // If stored entity has an owner, only the owner may place it (creative players may still place but get warned)
        try {
            java.util.UUID ownerUuid = extractOwnerUuid(entNbt, world, user);
            if (ownerUuid != null && !ownerUuid.equals(user.getUuid())) {
                // not the owner
                if (user.isCreative()) {
                    // allow but warn
                    user.sendMessage(Text.translatable("item.hunger-begone.basket.not_your_pet").formatted(Formatting.YELLOW), true);
                } else {
                    user.sendMessage(Text.translatable("item.hunger-begone.basket.not_your_pet").formatted(Formatting.RED), true);
                    return TypedActionResult.fail(basketStack);
                }
            }
        } catch (Exception ignored) {}

        // Determine target position: block under crosshair (place above it) or player's position forward
        HitResult hit = user.raycast(5.0D, 0.0F, false);
        Vec3d spawnPosVec;
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hit;
            BlockPos pos = bhr.getBlockPos();
            // place on top of the block center to avoid spawning inside the block
            spawnPosVec = Vec3d.ofCenter(pos.up());
        } else {
            // place a bit in front of the player to avoid overlapping
            Vec3d look = user.getRotationVector();
            spawnPosVec = user.getPos().add(look.multiply(1.0D));
        }

        // Attempt to load entity from NBT (use a copy with UUIDs stripped to avoid collisions)
        Entity ent = loadEntityFromBasket(world, entNbt);

        if (ent == null) {
            return TypedActionResult.fail(basketStack);
        }

        // Place entity at spawn position and check suffocation / space
        ent.refreshPositionAndAngles(spawnPosVec.x, spawnPosVec.y, spawnPosVec.z, ent.getYaw(), ent.getPitch());
        ent.setVelocity(0,0,0);

        // Update bounding box and check collisions at the target position
        if (!world.isSpaceEmpty(ent.getBoundingBox())) {
            user.sendMessage(Text.literal("我没法在这里放下它").formatted(Formatting.RED), true);
            return TypedActionResult.fail(basketStack);
        }

        // Use server spawn that also handles passengers
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnEntityAndPassengers(ent);
        } else {
            world.spawnEntity(ent);
        }

        // remove stored entity from NBT
        nbt.remove(STORED_ENTITY_KEY);

        user.getItemCooldownManager().set(this, 10);
        return TypedActionResult.success(basketStack);
    }

    /**
     * Creates an Entity from the stored NBT, stripping UUIDs to avoid collisions.
     * Returns null if the entity could not be loaded.
     */
    private static Entity loadEntityFromBasket(World world, NbtCompound entNbt) {
        // Remove UUID keys recursively to prevent duplicate-UUID issues
        NbtCompound cleanNbt = entNbt.copy();
        stripEntityUuids(cleanNbt);

        Entity ent = null;
        try {
            ent = EntityType.loadEntityWithPassengers(cleanNbt, world, (entity) -> entity);
        } catch (Exception ignored) {}

        // Fallback: create from stored "id" field
        if (ent == null && cleanNbt.contains("id")) {
            try {
                Identifier storedId = new Identifier(cleanNbt.getString("id"));
                EntityType<?> type = Registries.ENTITY_TYPE.get(storedId);
                if (type != null) {
                    ent = type.create(world);
                    if (ent != null) {
                        ent.readNbt(cleanNbt);
                    }
                }
            } catch (Exception ignored) {}
        }

        return ent;
    }

    private static void stripEntityUuids(NbtCompound compound) {
        compound.remove("UUID");
        compound.remove("UUIDMost");
        compound.remove("UUIDLeast");
        if (compound.contains("Passengers")) {
            try {
                NbtList passengers = compound.getList("Passengers", 10);
                for (int i = 0; i < passengers.size(); i++) {
                    stripEntityUuids(passengers.getCompound(i));
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * Extracts the stored entity's owner UUID from various NBT key formats.
     * Returns null if no owner was recorded.
     */
    private static java.util.UUID extractOwnerUuid(NbtCompound entNbt, ServerWorld world, PlayerEntity user) {
        java.util.UUID ownerUuid = null;
        if (entNbt.containsUuid("Owner")) {
            ownerUuid = entNbt.getUuid("Owner");
        } else if (entNbt.contains("OwnerUUID")) {
            try {
                ownerUuid = java.util.UUID.fromString(entNbt.getString("OwnerUUID"));
            } catch (Exception ignored) {}
        } else if (entNbt.contains("OwnerUUIDMost") && entNbt.contains("OwnerUUIDLeast")) {
            try {
                long most = entNbt.getLong("OwnerUUIDMost");
                long least = entNbt.getLong("OwnerUUIDLeast");
                ownerUuid = new java.util.UUID(most, least);
            } catch (Exception ignored) {}
        }

        // Fallback: Owner as plain string (UUID string or player name)
        if (ownerUuid == null && entNbt.contains("Owner")) {
            try {
                String ownerStr = entNbt.getString("Owner");
                try {
                    ownerUuid = java.util.UUID.fromString(ownerStr);
                } catch (Exception ex) {
                    // Try resolving the name to an online player
                    net.minecraft.server.MinecraftServer server = world.getServer();
                    if (server != null) {
                        net.minecraft.server.network.ServerPlayerEntity sp = server.getPlayerManager().getPlayer(ownerStr);
                        if (sp != null) {
                            ownerUuid = sp.getUuid();
                        } else if (ownerStr.equals(user.getName().getString())) {
                            ownerUuid = user.getUuid();
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return ownerUuid;
    }

    /**
     * Places the stored entity from the basket riding the target entity.
     */
    private boolean placeEntityOnTarget(ServerWorld world, PlayerEntity user, Hand hand,
                                         ItemStack basketStack, LivingEntity target) {
        NbtCompound nbt = basketStack.getOrCreateNbt();
        if (!nbt.contains(STORED_ENTITY_KEY)) return false;

        NbtCompound entNbt = nbt.getCompound(STORED_ENTITY_KEY);

        // Owner check: only the capturer can release
        java.util.UUID ownerUuid = extractOwnerUuid(entNbt, world, user);
        if (ownerUuid != null && !ownerUuid.equals(user.getUuid()) && !user.isCreative()) {
            user.sendMessage(Text.translatable("item.hunger-begone.basket.not_your_pet").formatted(Formatting.RED), true);
            return false;
        }

        Entity ent = loadEntityFromBasket(world, entNbt);
        if (ent == null) return false;

        // Spawn at target's position, then immediately mount
        ent.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), ent.getYaw(), ent.getPitch());
        world.spawnEntityAndPassengers(ent);

        // Make the placed entity ride the target
        ent.startRiding(target, true);

        // Cleanup
        nbt.remove(STORED_ENTITY_KEY);
        user.getItemCooldownManager().set(this, 10);
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getNbt();
        boolean hasEntity = nbt != null && nbt.contains(STORED_ENTITY_KEY);
        boolean hasItems = nbt != null && nbt.contains(STORED_KEY) && !nbt.getList(STORED_KEY, 10).isEmpty();

        // If there is a stored entity, display it first in dark green
        if (hasEntity) {
            NbtCompound entNbt = nbt.getCompound(STORED_ENTITY_KEY);
            Text entText = Text.literal("");
            if (entNbt.contains("id")) {
                try {
                    Identifier entId = new Identifier(entNbt.getString("id"));
                    EntityType<?> type = Registries.ENTITY_TYPE.get(entId);
                    if (type != null) {
                        entText = Text.translatable(type.getTranslationKey()).formatted(Formatting.DARK_GREEN);
                    } else {
                        entText = Text.literal("[" + entNbt.getString("id") + "]").formatted(Formatting.DARK_GREEN);
                    }
                } catch (Exception e) {
                    entText = Text.literal("[unknown]").formatted(Formatting.DARK_GREEN);
                }
            } else {
                entText = Text.literal("[unknown]").formatted(Formatting.DARK_GREEN);
            }
            // If owner info exists in NBT, append owner name
            try {
                String ownerDisplay = null;
                if (entNbt.contains("OwnerName")) {
                    ownerDisplay = entNbt.getString("OwnerName");
                } else if (entNbt.containsUuid("Owner")) {
                    try {
                        java.util.UUID ownerUuid = entNbt.getUuid("Owner");
                        if (world instanceof ServerWorld) {
                            net.minecraft.server.network.ServerPlayerEntity sp = ((ServerWorld) world).getServer().getPlayerManager().getPlayer(ownerUuid);
                            if (sp != null) ownerDisplay = sp.getName().getString();
                        }
                    } catch (Exception ignored) {}
                } else if (entNbt.contains("OwnerUUID")) {
                    try {
                        java.util.UUID ownerUuid = java.util.UUID.fromString(entNbt.getString("OwnerUUID"));
                        if (world instanceof ServerWorld) {
                            net.minecraft.server.network.ServerPlayerEntity sp = ((ServerWorld) world).getServer().getPlayerManager().getPlayer(ownerUuid);
                            if (sp != null) ownerDisplay = sp.getName().getString();
                        }
                    } catch (Exception ignored) {}
                }

                if (ownerDisplay != null && !ownerDisplay.isEmpty()) {
                    entText = entText.copy().append(Text.literal(" - ")).append(Text.literal(ownerDisplay).formatted(Formatting.WHITE));
                }
            } catch (Exception ignored) {}

            tooltip.add(entText);
        }

        if (hasItems) {
            NbtList storedList = nbt.getList(STORED_KEY, 10);
            tooltip.add(Text.translatable("item.hunger-begone.basket.stored_header",
                storedList.size()
            ).formatted(Formatting.GRAY));
            for (int i = 0; i < storedList.size(); i++) {
                ItemStack stored = ItemStack.fromNbt(storedList.getCompound(i));
                tooltip.add(Text.literal("  - ")
                    .append(stored.getName())
                    .append(Text.literal(" x" + stored.getCount()))
                    .formatted(Formatting.GRAY));
            }
        }

        // If the basket is completely empty (no items and no entity), show the multi-line hint
        if (!hasEntity && !hasItems) {
            tooltip.add(Text.literal("空"));
            // 紫色 蹲下 白色 将物品置于 紫色 副手
            tooltip.add(Text.literal("蹲下").formatted(Formatting.LIGHT_PURPLE)
                .append(Text.literal("将物品置于").formatted(Formatting.WHITE))
                .append(Text.literal("副手").formatted(Formatting.LIGHT_PURPLE)));
            tooltip.add(Text.literal("或").formatted(Formatting.WHITE));
            tooltip.add(Text.literal("与 ").formatted(Formatting.WHITE)
                .append(Text.literal("生物").formatted(Formatting.LIGHT_PURPLE))
                .append(Text.literal(" 互动来存储").formatted(Formatting.WHITE)));
        }

        // If the basket has stored items or a stored entity, add a final line explaining retrieval
        if (hasEntity || hasItems) {
            tooltip.add(Text.literal("蹲下互动来取出").formatted(Formatting.WHITE));
        }
    }

    public static boolean hasStoredItems(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(STORED_KEY)) {
            return false;
        }
        return !nbt.getList(STORED_KEY, 10).isEmpty();
    }

    public static boolean hasStoredEntity(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;
        return nbt.contains(STORED_ENTITY_KEY);
    }
}
