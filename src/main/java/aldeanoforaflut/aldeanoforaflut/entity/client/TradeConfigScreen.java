package aldeanoforaflut.aldeanoforaflut.entity.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

public class TradeConfigScreen extends Screen {
    
    private final CompoundTag configData;
    
    public TradeConfigScreen(CompoundTag configData) {
        super(Component.literal("Trade Configuration"));
        this.configData = configData;
    }
    
    @Override
    protected void init() {
        super.init();
        // TODO: Agregar botones interactivos y lista deslizable de items configurados.
        // Por ahora se mostrará una interfaz básica para validar la apertura.
    }
    
    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTick);
        
        gfx.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        gfx.drawCenteredString(this.font, Component.literal("Interfaz en desarrollo. Configs cargadas: " + configData.getList("Trades", 10).size()), this.width / 2, 40, 0xAAAAAA);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
