package aldeanoforaflut.aldeanoforaflut.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhoraMerchantScreen extends AbstractContainerScreen<MerchantMenu> {
    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private static final int NUMBER_OF_OFFER_BUTTONS = 7;
    private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
    private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");

    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;

    // Access button state (synced from entity via title parsing or packet)
    private String accessModeText = "Todos";
    private boolean isOwner = false;

    public PhoraMerchantScreen(MerchantMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    // Allow setting access info from outside (e.g. ModClientForgeEvents)
    public void setAccessInfo(String modeText, boolean owner) {
        this.accessModeText = modeText;
        this.isOwner = owner;
    }

    private void postButtonClick() {
        this.menu.setSelectionHint(this.shopItem);
        this.menu.tryMoveItems(this.shopItem);
        this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; ++l) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new TradeOfferButton(i + 5, k, l, (btn) -> {
                if (btn instanceof TradeOfferButton tradeBtn) {
                    this.shopItem = tradeBtn.getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }));
            k += 20;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        // 1. Title above GUI (fuera del panel, centrado)
        int titleWidth = this.font.width(this.title);
        gfx.drawString(this.font, this.title, this.imageWidth / 2 - titleWidth / 2, -12, 0xFFFFFF, true);

        // 2. "Nivel X" text above progress bar
        int level = this.menu.getTraderLevel();
        if (level > 0 && level <= 5 && this.menu.showProgressBar()) {
            Component nivelComp = Component.literal("Nivel " + level);
            int nivelW = this.font.width(nivelComp);
            int nivelX = 136 + 51 - nivelW / 2;
            gfx.drawString(this.font, nivelComp, nivelX, 6, 0xFFDA6A, false);
        }

        // 3. "Inventario" label
        gfx.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xAAAAAA, false);

        // 4. "Trades" label on left
        int tradesW = this.font.width(TRADES_LABEL);
        gfx.drawString(this.font, TRADES_LABEL, 5 - tradesW / 2 + 48, 6, 0xFFFFFF, false);

        // 5. Access button text (only for owner)
        if (this.isOwner) {
            Component accessComp = Component.literal("Acceso: " + this.accessModeText + " >");
            int accessW = this.font.width(accessComp);
            // Position below the trade slots area
            gfx.drawString(this.font, accessComp, 136 + 51 - accessW / 2, 60, 0xFF8C00, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        // Draw dark background behind the entire GUI area
        gfx.fill(i - 2, j - 16, i + this.imageWidth + 2, j + this.imageHeight + 2, 0xEE111111);
        // Orange border
        drawBorder(gfx, i - 2, j - 16, this.imageWidth + 4, this.imageHeight + 18, 0xFFFF8C00);

        // Use vanilla texture for the actual GUI content (slots, buttons, etc.)
        gfx.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);

        // Tint vanilla background darker by drawing dark overlay
        gfx.fill(i + 1, j + 1, i + this.imageWidth - 1, j + this.imageHeight - 1, 0xCC0A0A0A);

        // Redraw trade list panel area (left side background)
        gfx.fill(i + 4, j + 15, i + 99, j + 158, 0xFF0D0D0D);
        drawBorder(gfx, i + 4, j + 15, 95, 143, 0xFFFF8C00);

        // Redraw right panel (trade + inventory area)
        gfx.fill(i + 100, j + 2, i + 274, j + 164, 0xFF141414);

        // Trade input/output slots background
        // Slot 1 (input 1)
        drawSlot(gfx, i + 136, j + 37);
        // Slot 2 (input 2)
        drawSlot(gfx, i + 162, j + 37);
        // Arrow between inputs and result
        gfx.drawString(this.font, "->", i + 185, j + 42, 0xAAAAAA, false);
        // Result slot (bigger)
        drawSlot(gfx, i + 220, j + 37);

        // Player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotX = i + 108 + col * 18;
                int slotY = j + 84 + row * 18;
                drawSlot(gfx, slotX, slotY);
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            int slotX = i + 108 + col * 18;
            int slotY = j + 142;
            drawSlot(gfx, slotX, slotY);
        }

        // Barra naranja/amarilla de nivel (above trade slots)
        int level = this.menu.getTraderLevel();
        if (level > 0 && level <= 5 && this.menu.showProgressBar()) {
            int barX = i + 136;
            int barY = j + 16;
            int barW = 102;
            int barH = 5;
            // Bar background
            gfx.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);
            drawBorder(gfx, barX, barY, barW, barH, 0xFFFF8C00);
            // Bar fill
            int xp = this.menu.getTraderXp();
            int minXp = VillagerData.getMinXpPerLevel(level);
            int maxXp = VillagerData.getMaxXpPerLevel(level);
            if (maxXp > minXp && xp >= minXp && VillagerData.canLevelUp(level)) {
                float ratio = (float)(xp - minXp) / (float)(maxXp - minXp);
                int fillW = Math.min(Mth.floor(ratio * barW), barW);
                gfx.fill(barX + 1, barY + 1, barX + 1 + fillW, barY + barH - 1, 0xFFFFAA00);
            }
        }

        // Cross-out red if selected trade is out of stock
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k >= 0 && k < merchantoffers.size()) {
                MerchantOffer merchantoffer = merchantoffers.get(k);
                if (merchantoffer.isOutOfStock()) {
                    gfx.blit(VILLAGER_LOCATION, this.leftPos + 83 + 99, this.topPos + 35, 0, 311.0F, 0.0F, 28, 21, 512, 256);
                }
            }
        }
    }

    private void drawSlot(GuiGraphics gfx, int x, int y) {
        gfx.fill(x - 1, y - 1, x + 17, y + 17, 0xFF555555);
        gfx.fill(x, y, x + 16, y + 16, 0xFF1A1A1A);
    }

    private void drawBorder(GuiGraphics gfx, int x, int y, int w, int h, int color) {
        gfx.fill(x, y, x + w, y + 1, color);       // top
        gfx.fill(x, y + h - 1, x + w, y + h, color); // bottom
        gfx.fill(x, y, x + 1, y + h, color);         // left
        gfx.fill(x + w - 1, y, x + w, y + h, color); // right
    }

    private void renderScroller(GuiGraphics gfx, int guiLeft, int guiTop, MerchantOffers offers) {
        int i = offers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }
            // Orange scrollbar
            gfx.fill(guiLeft + 94, guiTop + 18 + i1, guiLeft + 100, guiTop + 18 + i1 + 27, 0xFFFF8C00);
        } else {
            // No scroll needed, draw inactive scroller
            gfx.fill(guiLeft + 94, guiTop + 18, guiLeft + 100, guiTop + 18 + 27, 0xFF555555);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTick);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScroller(gfx, i, j, merchantoffers);
            int i1 = 0;

            for (MerchantOffer merchantoffer : merchantoffers) {
                if (!this.canScroll(merchantoffers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack1 = merchantoffer.getCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();

                    gfx.pose().pushPose();
                    gfx.pose().translate(0.0F, 0.0F, 100.0F);
                    int j1 = k + 2;

                    // Check if locked
                    int reqLvl = merchantoffer.getResult().hasTag() ? merchantoffer.getResult().getTag().getInt("RequiredLevel") : 0;
                    boolean isLocked = reqLvl > 0 && merchantoffer.isOutOfStock();

                    if (isLocked) {
                        // Draw red background BEHIND the items
                        gfx.fill(i + 5, j1 - 2, i + 5 + 88, j1 + 18, 0xCC880000);
                    }

                    this.renderAndDecorateCostA(gfx, itemstack1, itemstack, l, j1);
                    if (!itemstack2.isEmpty()) {
                        gfx.renderFakeItem(itemstack2, i + 5 + 35, j1);
                        gfx.renderItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    // Arrow or "Nv> X" text
                    if (isLocked) {
                        String nvText = "Nv> " + reqLvl;
                        int textWidth = this.font.width(nvText);
                        gfx.drawString(this.font, nvText, i + 5 + 35 + 25 - textWidth / 2, j1 + 4, 0xFFFFFF, true);
                    } else {
                        this.renderButtonArrows(gfx, merchantoffer, i, j1);
                    }

                    gfx.renderFakeItem(itemstack3, i + 5 + 68, j1);
                    gfx.renderItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    gfx.pose().popPose();
                    k += 20;
                    ++i1;
                } else {
                    ++i1;
                }
            }

            int k1 = this.shopItem;
            if (k1 >= 0 && k1 < merchantoffers.size()) {
                MerchantOffer merchantoffer1 = merchantoffers.get(k1);
                if (this.menu.showProgressBar()) {
                    this.renderProgressBar(gfx, i, j, merchantoffer1);
                }

                if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double) mouseX, (double) mouseY) && this.menu.canRestock()) {
                    gfx.renderTooltip(this.font, DEPRECATED_TOOLTIP, mouseX, mouseY);
                }
            }

            for (TradeOfferButton btn : this.tradeOfferButtons) {
                if (btn.isHoveredOrFocused()) {
                    btn.renderToolTip(gfx, mouseX, mouseY);
                }
                btn.visible = btn.index < this.menu.getOffers().size();
            }

            RenderSystem.enableDepthTest();
        }

        this.renderTooltip(gfx, mouseX, mouseY);
    }

    private void renderButtonArrows(GuiGraphics gfx, MerchantOffer offer, int guiLeft, int y) {
        RenderSystem.enableBlend();
        if (offer.isOutOfStock()) {
            gfx.blit(VILLAGER_LOCATION, guiLeft + 5 + 35 + 20, y + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            gfx.blit(VILLAGER_LOCATION, guiLeft + 5 + 35 + 20, y + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
        }
    }

    private void renderAndDecorateCostA(GuiGraphics gfx, ItemStack costA, ItemStack baseCostA, int x, int y) {
        gfx.renderFakeItem(costA, x, y);
        if (baseCostA.getCount() == costA.getCount()) {
            gfx.renderItemDecorations(this.font, costA, x, y);
        } else {
            gfx.renderItemDecorations(this.font, baseCostA, x, y, baseCostA.getCount() == 1 ? "1" : null);
            gfx.pose().pushPose();
            gfx.pose().translate(0.0F, 0.0F, 200.0F);
            String count = costA.getCount() == 1 ? "1" : String.valueOf(costA.getCount());
            font.drawInBatch(count, (float) (x + 14) + 19 - 2 - font.width(count), (float) y + 6 + 3, 0xFFFFFF, true, gfx.pose().last().pose(), gfx.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880, false);
            gfx.pose().popPose();
            gfx.pose().pushPose();
            gfx.pose().translate(0.0F, 0.0F, 300.0F);
            gfx.blit(VILLAGER_LOCATION, x + 7, y + 12, 0, 0.0F, 176.0F, 9, 2, 512, 256);
            gfx.pose().popPose();
        }
    }

    private void renderProgressBar(GuiGraphics gfx, int guiLeft, int guiTop, MerchantOffer offer) {
        // We draw our own orange bar in renderBg, so this is a no-op
    }

    private boolean canScroll(int offerCount) {
        return offerCount > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int i = this.menu.getOffers().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.scrollOff = Mth.clamp((int) ((double) this.scrollOff - delta), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int i = this.menu.getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float) mouseY - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.scrollOff = Mth.clamp((int) f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.menu.getOffers().size()) && mouseX > (double) (i + 94) && mouseX < (double) (i + 94 + 6) && mouseY > (double) (j + 18) && mouseY <= (double) (j + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        // Check if "Acceso" text area was clicked (only for owner)
        if (this.isOwner) {
            int accessX = i + 136 + 51 - 40;
            int accessY = j + 60;
            if (mouseX >= accessX && mouseX <= accessX + 80 && mouseY >= accessY && mouseY <= accessY + 12) {
                // TODO: Send packet to server to cycle access mode
                // For now just cycle locally
                if (this.accessModeText.equals("Todos")) {
                    this.accessModeText = "Solo yo";
                } else if (this.accessModeText.equals("Solo yo")) {
                    this.accessModeText = "Mi Hermandad";
                } else {
                    this.accessModeText = "Todos";
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(int x, int y, int index, Button.OnPress onPress) {
            super(x, y, 88, 20, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(GuiGraphics gfx, int mouseX, int mouseY) {
            if (this.isHovered && PhoraMerchantScreen.this.menu.getOffers().size() > this.index + PhoraMerchantScreen.this.scrollOff) {
                if (mouseX < this.getX() + 20) {
                    ItemStack itemstack = PhoraMerchantScreen.this.menu.getOffers().get(this.index + PhoraMerchantScreen.this.scrollOff).getCostA();
                    gfx.renderTooltip(PhoraMerchantScreen.this.font, itemstack, mouseX, mouseY);
                } else if (mouseX < this.getX() + 50 && mouseX > this.getX() + 30) {
                    ItemStack itemstack2 = PhoraMerchantScreen.this.menu.getOffers().get(this.index + PhoraMerchantScreen.this.scrollOff).getCostB();
                    if (!itemstack2.isEmpty()) {
                        gfx.renderTooltip(PhoraMerchantScreen.this.font, itemstack2, mouseX, mouseY);
                    }
                } else if (mouseX > this.getX() + 65) {
                    ItemStack itemstack1 = PhoraMerchantScreen.this.menu.getOffers().get(this.index + PhoraMerchantScreen.this.scrollOff).getResult();
                    gfx.renderTooltip(PhoraMerchantScreen.this.font, itemstack1, mouseX, mouseY);
                }
            }
        }
    }
}
