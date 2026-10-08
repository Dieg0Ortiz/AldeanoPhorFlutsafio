package aldeanoforaflut.aldeanoforaflut.entity.client;

import aldeanoforaflut.aldeanoforaflut.network.ModMessages;
import aldeanoforaflut.aldeanoforaflut.network.SaveTradeConfigPacket;
import aldeanoforaflut.aldeanoforaflut.trade.TradeConfigData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class TradeConfigScreen extends Screen {

    private final List<TradeConfigData.TradeEntry> entries = new ArrayList<>();
    private double scrollX = 50;
    private double scrollY = 50;
    private boolean isDragging = false;

    public TradeConfigScreen(CompoundTag configData) {
        super(Component.literal("Trade Configuration"));
        loadFromTag(configData);
    }

    private void loadFromTag(CompoundTag tag) {
        entries.clear();
        ListTag list = tag.getList("Trades", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            entries.add(TradeConfigData.TradeEntry.load(list.getCompound(i)));
        }
    }

    private void saveAndSend() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (TradeConfigData.TradeEntry entry : entries) {
            list.add(entry.save());
        }
        tag.put("Trades", list);
        ModMessages.sendToServer(new SaveTradeConfigPacket(tag));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.scrollX += dragX;
        this.scrollY += dragY;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Find if we clicked on any button
        int colWidth = 140;
        int nodeHeight = 36;
        int colSpacing = 60;
        int nodeSpacing = 10;

        for (int level = 1; level <= 5; level++) {
            int levelX = (int) scrollX + (level - 1) * (colWidth + colSpacing);
            int levelY = (int) scrollY;

            // "Add Item" button at the top of the column
            if (mouseX >= levelX && mouseX <= levelX + colWidth && mouseY >= levelY - 20 && mouseY <= levelY - 5) {
                // Agregar item de la mano secundaria
                ItemStack offhand = this.minecraft.player.getOffhandItem();
                if (!offhand.isEmpty()) {
                    String itemId = ForgeRegistries.ITEMS.getKey(offhand.getItem()).toString();
                    entries.add(new TradeConfigData.TradeEntry(itemId, 1, offhand.getCount(), 10, level));
                    saveAndSend();
                    return true;
                }
            }

            // Trades for this level
            int nodeIndex = 0;
            for (int i = 0; i < entries.size(); i++) {
                TradeConfigData.TradeEntry entry = entries.get(i);
                if (entry.requiredLevel != level) continue;

                int nx = levelX;
                int ny = levelY + nodeIndex * (nodeHeight + nodeSpacing);

                // Minus Weight
                if (isHovering(mouseX, mouseY, nx + 100, ny + 4, 14, 12)) {
                    entry.weight = Math.max(1, entry.weight - 1);
                    saveAndSend();
                    return true;
                }
                // Plus Weight
                if (isHovering(mouseX, mouseY, nx + 120, ny + 4, 14, 12)) {
                    entry.weight = Math.min(100, entry.weight + 1);
                    saveAndSend();
                    return true;
                }
                // Delete
                if (isHovering(mouseX, mouseY, nx + 120, ny + 20, 14, 12)) {
                    entries.remove(i);
                    saveAndSend();
                    return true;
                }

                nodeIndex++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isHovering(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        // Dark background
        gfx.fill(0, 0, this.width, this.height, 0xFF111111);

        int colWidth = 140;
        int nodeHeight = 36;
        int colSpacing = 60;
        int nodeSpacing = 10;

        // Draw connections (tree lines)
        for (int level = 1; level < 5; level++) {
            int startX = (int) scrollX + (level - 1) * (colWidth + colSpacing) + colWidth;
            int endX = (int) scrollX + level * (colWidth + colSpacing);
            int startY = (int) scrollY + 10;
            gfx.fill(startX, startY, endX, startY + 2, 0xFF555555);
        }

        // Draw nodes per level
        for (int level = 1; level <= 5; level++) {
            int levelX = (int) scrollX + (level - 1) * (colWidth + colSpacing);
            int levelY = (int) scrollY;

            gfx.drawString(this.font, "Nivel " + level, levelX, levelY - 35, 0xFFAA00, false);
            
            // Draw "Add from Offhand" button
            boolean hoverAdd = isHovering(mouseX, mouseY, levelX, levelY - 20, colWidth, 15);
            gfx.fill(levelX, levelY - 20, levelX + colWidth, levelY - 5, hoverAdd ? 0xFF225522 : 0xFF113311);
            gfx.drawCenteredString(this.font, "+ Item Offhand", levelX + colWidth / 2, levelY - 16, 0x55FF55);

            int nodeIndex = 0;
            for (TradeConfigData.TradeEntry entry : entries) {
                if (entry.requiredLevel != level) continue;

                int nx = levelX;
                int ny = levelY + nodeIndex * (nodeHeight + nodeSpacing);

                // Node background
                gfx.fill(nx, ny, nx + colWidth, ny + nodeHeight, 0xFF222222);
                gfx.fill(nx, ny, nx + colWidth, ny + 1, 0xFF444444); // top border
                gfx.fill(nx, ny + nodeHeight - 1, nx + colWidth, ny + nodeHeight, 0xFF111111); // bottom border
                gfx.fill(nx, ny, nx + 1, ny + nodeHeight, 0xFF444444); // left border
                gfx.fill(nx + colWidth - 1, ny, nx + colWidth, ny + nodeHeight, 0xFF111111); // right border

                // Item Icon
                ItemStack displayStack = new ItemStack(entry.getItem() != null ? entry.getItem() : Items.BARRIER);
                gfx.renderItem(displayStack, nx + 4, ny + 10);

                // Item name / count
                String name = displayStack.getHoverName().getString();
                if (name.length() > 12) name = name.substring(0, 11) + ".";
                gfx.drawString(this.font, name, nx + 26, ny + 6, 0xFFFFFF, false);
                gfx.drawString(this.font, "Cant: " + entry.minCount + "-" + entry.maxCount, nx + 26, ny + 18, 0xAAAAAA, false);

                // Weight UI
                gfx.drawString(this.font, entry.weight + "%", nx + 76, ny + 6, 0xFFFFAA, false);

                // Buttons
                boolean hMinus = isHovering(mouseX, mouseY, nx + 100, ny + 4, 14, 12);
                gfx.fill(nx + 100, ny + 4, nx + 114, ny + 16, hMinus ? 0xFF555555 : 0xFF333333);
                gfx.drawCenteredString(this.font, "-", nx + 107, ny + 6, 0xFFFFFF);

                boolean hPlus = isHovering(mouseX, mouseY, nx + 120, ny + 4, 14, 12);
                gfx.fill(nx + 120, ny + 4, nx + 134, ny + 16, hPlus ? 0xFF555555 : 0xFF333333);
                gfx.drawCenteredString(this.font, "+", nx + 127, ny + 6, 0xFFFFFF);

                // Delete Button
                boolean hDel = isHovering(mouseX, mouseY, nx + 120, ny + 20, 14, 12);
                gfx.fill(nx + 120, ny + 20, nx + 134, ny + 32, hDel ? 0xFFAA3333 : 0xFF772222);
                gfx.drawCenteredString(this.font, "x", nx + 127, ny + 22, 0xFFFFFF);

                nodeIndex++;
            }
        }

        // Overlay Instructions
        gfx.drawString(this.font, "Arrastra para mover la vista. Usa la mano secundaria para agregar.", 10, 10, 0xAAAAAA, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
