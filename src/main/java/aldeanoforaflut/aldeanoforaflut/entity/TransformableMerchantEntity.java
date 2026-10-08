package aldeanoforaflut.aldeanoforaflut.entity;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import aldeanoforaflut.aldeanoforaflut.trade.DilitioTrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
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

    // 1 = Turning On (animacion de encendido)
    // 2 = Active Villager (camina y tradea)
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(TransformableMerchantEntity.class, EntityDataSerializers.INT);

    private int turnOnTicks = 0;

    // Niveles de XP estilo aldeano vanilla
    private int phoraLevel = 1;
    private int phoraXp = 0;

    public TransformableMerchantEntity(EntityType<? extends WanderingTrader> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true); // Empieza sin IA (encendiendose)
        this.updateTrades(); // Inicializar tradeos de Nivel 1
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, 1); // Empieza en Estado 1 (Encendiendose)
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
                if (turnOnTicks >= 40) {
                    setEntityState(2);
                    this.setNoAi(false);
                }
            }
        }
    }

    @Override
    public boolean isPushable() {
        return getEntityState() == 2;
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

    // --- LOGICA DE TRADEO Y NIVELES ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) return InteractionResult.sidedSuccess(true);

        if (getEntityState() == 2) {
            if (this.getTradingPlayer() != null) {
                return InteractionResult.FAIL;
            }
            if (this.offers != null && !this.offers.isEmpty()) {
                this.setTradingPlayer(player);
                // Pasar phoraLevel para que la UI muestre el nivel y progreso correctamente
                this.openTradingScreen(player, this.getDisplayName(), this.phoraLevel);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void updateTrades() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            // Generar todos los trades hasta el nivel 5 desde el inicio
            for (int lvl = 1; lvl <= 5; lvl++) {
                DilitioTrades.addTradesForLevel(this.offers, this.random, lvl, this.phoraLevel);
            }
        } else {
            // Desbloquear tradeos del nuevo nivel alcanzado
            for (MerchantOffer offer : this.offers) {
                net.minecraft.world.item.ItemStack result = offer.getResult();
                if (result.hasTag() && result.getTag().contains("RequiredLevel")) {
                    int reqLvl = result.getTag().getInt("RequiredLevel");
                    if (reqLvl <= this.phoraLevel) {
                        offer.resetUses(); // Desbloqueado!
                    }
                }
            }
        }
    }

    @Override
    public int getVillagerXp() {
        return this.phoraXp;
    }

    @Override
    public void overrideXp(int xp) {
        this.phoraXp = xp;
    }

    @Override
    public boolean showProgressBar() {
        return true; // Mostrar barra de XP en la UI
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        super.notifyTrade(offer); // Esto internamente llama a rewardTradeXp
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.phoraXp += offer.getXp();
        
        if (this.canLevelUp()) {
            this.phoraLevel++;
            this.updateTrades(); // Desbloquea los tradeos de este nuevo nivel
            i += 5;
        }

        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }

    private boolean canLevelUp() {
        int targetXp = getXpForLevel(this.phoraLevel + 1);
        return this.phoraLevel < 5 && this.phoraXp >= targetXp;
    }

    private int getXpForLevel(int lvl) {
        return switch (lvl) {
            case 2 -> 10;
            case 3 -> 70;
            case 4 -> 150;
            case 5 -> 250;
            default -> 0;
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PhoraState", getEntityState());
        compound.putInt("PhoraLevel", this.phoraLevel);
        compound.putInt("PhoraXp", this.phoraXp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("PhoraState")) {
            int savedState = compound.getInt("PhoraState");
            setEntityState(savedState);
            this.setNoAi(savedState != 2);
        }
        if (compound.contains("PhoraLevel")) {
            this.phoraLevel = compound.getInt("PhoraLevel");
        }
        if (compound.contains("PhoraXp")) {
            this.phoraXp = compound.getInt("PhoraXp");
        }
    }

    // --- GECKOLIB ANIMATIONS ---
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<TransformableMerchantEntity> state) {
        int entityState = getEntityState();

        if (entityState == 1) {
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


