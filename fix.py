import sys, re
with open(r'src\main\java\aldeanoforaflut\aldeanoforaflut\entity\client\PhoraMerchantScreen.java', 'r', encoding='utf-8') as f:
    text = f.read()

replacement = '''protected void renderLabels(GuiGraphics p_283337_, int p_282009_, int p_283691_) {
      Component name = Component.literal("Phora - Recursos");
      int i = this.menu.getTraderLevel();
      if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
         Component component = name.copy().append(" - ").append(Component.translatable("merchant.level." + i));
         int j = this.font.width(component);
         int k = 49 + this.imageWidth / 2 - j / 2;
         p_283337_.drawString(this.font, component, k, 6, 0xFFFFFF, false);
      } else {
         p_283337_.drawString(this.font, name, 49 + this.imageWidth / 2 - this.font.width(name) / 2, 6, 0xFFFFFF, false);
      }
      p_283337_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFF, false);
      int l = this.font.width(TRADES_LABEL);
      p_283337_.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 0xFFFFFF, false);
   }'''

text = re.sub(r'protected void renderLabels\(GuiGraphics p_283337_, int p_282009_, int p_283691_\) \{.*?     \}', replacement, text, flags=re.DOTALL)

with open(r'src\main\java\aldeanoforaflut\aldeanoforaflut\entity\client\PhoraMerchantScreen.java', 'w', encoding='utf-8') as f:
    f.write(text)
