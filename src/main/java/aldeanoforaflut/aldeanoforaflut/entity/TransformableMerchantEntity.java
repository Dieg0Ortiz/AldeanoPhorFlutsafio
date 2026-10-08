package aldeanoforaflut.aldeanoforaflut.entity;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import aldeanoforaflut.aldeanoforaflut.trade.DilitioTrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TransformableMerchantEntity extends WanderingTrader implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(TransformableMerchantEntity.class, EntityDataSerializers.INT);
    // 0 = Block Mode (Inactive)
    // 1 = Turning On Transition
    // 2 = Active Villager

    private int turnOnTicks = 0;

    public TransformableMerchantEntity(EntityType<? extends WanderingTrader> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true); // Empieza sin IA (Estado 0: Apagado)
        this.setNoGravity(false);
        this.initCustomTrades();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, 0);
    }

    public int getEntityState() {
        return this.entityData.get(STATE);
    }

    public void setEntityState(int state) {
        this.entityData.set(STATE, state);
    }

    public boolean isActive() {
        return getEntityState() == 2;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (getEntityState() == 1) {
                turnOnTicks++;
                // La animacion turn_on dura 2 segundos (40 ticks)
                if (turnOnTicks >= 40) {
                    setEntityState(2);
                    this.setNoAi(false); // Activar IA al encenderse
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Si esta en Estado 0 y lo golpean (ej: con click izquierdo), se "rompe" como un bloque
        if (getEntityState() == 0 && !this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(Aldeanoforaflut.MERCHANT_BLOCK_ITEM.get()));
            this.discard();
            return false;
        }
        if (getEntityState() == 0 || getEntityState() == 1) {
            return false; // Inmune al dano mientras es bloque o se enciende
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isPushable() {
        return getEntityState() == 2; // Solo se puede empujar si esta activo
    }

    @Override
    protected void doPush(net.minecraft.world.entity.Entity entityIn) {
        if (getEntityState() == 2) {
            super.doPush(entityIn);
        }
    }

    @Override
    public void push(double x, double y, double z) {
        if (getEntityState() == 2) {
            super.push(x, y, z);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) return InteractionResult.sidedSuccess(this.level().isClientSide);

        int state = getEntityState();
        if (state == 0) {
            // Evento de activacion: Click derecho con un Palo
            if (player.getItemInHand(hand).is(Items.STICK)) {
                setEntityState(1); // Empieza a encenderse
                turnOnTicks = 0;
                return InteractionResult.SUCCESS;
            }
        } else if (state == 2) {
            // Trading normal
            if (this.getTradingPlayer() != null) {
                return InteractionResult.FAIL;
            }
            if (this.offers != null && !this.offers.isEmpty()) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private void initCustomTrades() {
        MerchantOffers customOffers = new MerchantOffers();
        DilitioTrades.addAll(customOffers, this.random);
        this.offers = customOffers;
    }

    @Override
    protected void updateTrades() {} // Evitar que WanderingTrader borre los tradeos

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PhoraState", getEntityState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("PhoraState")) {
            int savedState = compound.getInt("PhoraState");
            setEntityState(savedState);
            this.setNoAi(savedState != 2); // Solo tiene IA si esta activo
        }
    }

    // --- GECKOLIB ANIMATIONS ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<TransformableMerchantEntity> state) {
        int entityState = getEntityState();

        if (entityState == 0) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.phora.inactive"));
            return PlayState.CONTINUE;
        } else if (entityState == 1) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("animation.phora.turn_on"));
            return PlayState.CONTINUE;
        } else {
            // Estado 2: Activo
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.phora.walk"));
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.phora.idle"));
            }
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
