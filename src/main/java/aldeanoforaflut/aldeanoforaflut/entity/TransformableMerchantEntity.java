package aldeanoforaflut.aldeanoforaflut.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public class TransformableMerchantEntity extends Villager {

    private static final EntityDataAccessor<Boolean> IS_BLOCK_FORM = SynchedEntityData.defineId(TransformableMerchantEntity.class, EntityDataSerializers.BOOLEAN);

    public TransformableMerchantEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_BLOCK_FORM, true);
    }

    public boolean isBlockForm() {
        return this.entityData.get(IS_BLOCK_FORM);
    }

    public void setBlockForm(boolean blockForm) {
        this.entityData.set(IS_BLOCK_FORM, blockForm);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (this.isBlockForm()) {
            if (itemStack.is(Items.STICK)) {
                if (!this.level().isClientSide) {
                    this.setBlockForm(false);
                    // Opcional: Agregar partículas o sonido de transformación
                    
                    // Configurar tradeos personalizados (ejemplo)
                    net.minecraft.world.item.trading.MerchantOffers offers = this.getOffers();
                    offers.clear();
                    offers.add(new net.minecraft.world.item.trading.MerchantOffer(
                            new ItemStack(Items.EMERALD, 5),
                            new ItemStack(Items.DIAMOND, 1),
                            10, 8, 0.02f
                    ));
                    offers.add(new net.minecraft.world.item.trading.MerchantOffer(
                            new ItemStack(Items.GOLD_INGOT, 10),
                            new ItemStack(Items.NETHERITE_INGOT, 1),
                            5, 10, 0.05f
                    ));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.PASS; // No permite abrir menú de tradeos ni interactuar si es bloque y no tiene un palo
        }
        
        // Comportamiento normal de aldeano (Tradeos)
        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsBlockForm", this.isBlockForm());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IsBlockForm")) {
            this.setBlockForm(compound.getBoolean("IsBlockForm"));
        }
    }
    
    @Override
    public boolean isPushable() {
        return !this.isBlockForm() && super.isPushable();
    }
    
    @Override
    protected void doPush(Entity entity) {
        if (!this.isBlockForm()) {
            super.doPush(entity);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.isBlockForm()) {
            // Prevenir movimiento en forma de bloque
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.setYRot(0);
            this.setXRot(0);
            this.yBodyRot = 0;
            this.yHeadRot = 0;
        }
    }
}
