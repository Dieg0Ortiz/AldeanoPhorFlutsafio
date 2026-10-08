import sys, re
import os

# 1. GENERATE TEXTURE
from PIL import Image, ImageDraw
os.makedirs(r'src/main/resources/assets/aldeanoforaflut/textures/gui', exist_ok=True)
out = Image.new('RGBA', (512, 256), (0,0,0,0))
draw = ImageDraw.Draw(out)

w, h = 276, 166
# Base dark grey background
draw.rectangle([0, 0, w-1, h-1], fill=(24, 24, 24, 255))
# Outer orange border
draw.rectangle([0, 0, w-1, h-1], outline=(255, 140, 0, 255), width=2)
draw.rectangle([2, 2, w-3, h-3], outline=(200, 100, 0, 255), width=1)
# Small notches
draw.rectangle([w//2 - 10, 0, w//2 + 10, 3], fill=(255, 140, 0, 255))
draw.rectangle([w//2 - 10, h-4, w//2 + 10, h-1], fill=(255, 140, 0, 255))
draw.rectangle([0, h//2 - 10, 3, h//2 + 10], fill=(255, 140, 0, 255))
draw.rectangle([w-4, h//2 - 10, w-1, h//2 + 10], fill=(255, 140, 0, 255))

# Left panel for trades
draw.rectangle([4, 15, 98, 157], fill=(16, 16, 16, 255), outline=(255, 140, 0, 255), width=1)
# Right panel for inventory area
draw.rectangle([102, 70, 270, 157], fill=(20, 20, 20, 255), outline=(100, 100, 100, 255))
# Player inventory slots
for row in range(3):
    for col in range(9):
        x = 108 + col * 18
        y = 84 + row * 18
        draw.rectangle([x-1, y-1, x+16, y+16], fill=(30, 30, 30, 255), outline=(70, 70, 70, 255))
# Hotbar slots
for col in range(9):
    x = 108 + col * 18
    y = 142
    draw.rectangle([x-1, y-1, x+16, y+16], fill=(30, 30, 30, 255), outline=(70, 70, 70, 255))

# Top right box for percentage
draw.rectangle([280, 10, 310, 30], fill=(24, 24, 24, 255), outline=(255, 140, 0, 255), width=1)

# Arrows
draw.polygon([(15, 171+4), (15+6, 171+4), (15+6, 171), (15+10, 171+5), (15+6, 171+10), (15+6, 171+6), (15, 171+6)], fill=(100, 100, 100, 255))
draw.polygon([(25, 171+4), (25+6, 171+4), (25+6, 171), (25+10, 171+5), (25+6, 171+10), (25+6, 171+6), (25, 171+6)], fill=(100, 100, 100, 255))
draw.line([25, 171, 35, 180], fill=(255, 0, 0, 255), width=2)
draw.line([35, 171, 25, 180], fill=(255, 0, 0, 255), width=2)

# Progress bar empty: 0, 186 to 102, 191
draw.rectangle([0, 186, 102, 191], fill=(40, 40, 40, 255), outline=(255, 140, 0, 255))
# Progress bar full (yellow): 0, 191 to 102, 196
draw.rectangle([1, 192, 101, 195], fill=(255, 200, 0, 255))
# Scrollbar thumb
draw.rectangle([0, 199, 5, 225], fill=(255, 140, 0, 255), outline=(200, 100, 0, 255))

out.save(r'src/main/resources/assets/aldeanoforaflut/textures/gui/phora_gui.png')

# 2. PATCH PhoraMerchantScreen.java
java_file = r'src\main\java\aldeanoforaflut\aldeanoforaflut\entity\client\PhoraMerchantScreen.java'
with open(java_file, 'r', encoding='utf-8') as f:
    text = f.read()

# Replace texture
text = text.replace('new ResourceLocation("textures/gui/container/villager2.png")', 'new ResourceLocation("aldeanoforaflut", "textures/gui/phora_gui.png")')

# Inject background behind items (FIX FOR IMAGE 1)
render_target = '''                 int j1 = k + 2;
                 this.renderAndDecorateCostA(p_283487_, itemstack1, itemstack, l, j1);'''
render_replacement = '''                 int j1 = k + 2;
                 int reqLvl = merchantoffer.getResult().hasTag() ? merchantoffer.getResult().getTag().getInt("RequiredLevel") : 0;
                 if (reqLvl > 0) {
                     p_283487_.fill(i + 5, j1 - 2, i + 5 + 88, j1 + 18, 0xFF880000); // Fondo rojo SOLIDO antes de renderizar los items
                 }
                 this.renderAndDecorateCostA(p_283487_, itemstack1, itemstack, l, j1);'''
text = text.replace(render_target, render_replacement)

# Fix Nv> text rendering so it replaces arrow and has no background box
arrows_target = '''          if (reqLvl > 0) {
              String text = "Nv> " + reqLvl;
              int textWidth = this.font.width(text);
              p_283020_.drawString(this.font, text, p_282752_ + 5 + 35 + 25 - textWidth/2, p_282179_ + 3, 0xFFFFFF, true);
          } else {'''
arrows_replacement = '''          if (reqLvl > 0) {
              String text = "Nv> " + reqLvl;
              int textWidth = this.font.width(text);
              p_283020_.drawString(this.font, text, p_282752_ + 5 + 35 + 25 - textWidth/2, p_282179_ + 4, 0xFFFFFF, true);
          } else {'''
text = text.replace(arrows_target, arrows_replacement)

# Replace renderLabels logic entirely!
labels_target_regex = r'protected void renderLabels\(GuiGraphics p_283337_, int p_282009_, int p_283691_\) \{.*?int l = this\.font\.width\(TRADES_LABEL\);.*?\}'
new_labels = '''protected void renderLabels(GuiGraphics p_283337_, int p_282009_, int p_283691_) {
      // 1. Title above GUI
      int titleWidth = this.font.width(this.title);
      p_283337_.drawString(this.font, this.title, this.imageWidth / 2 - titleWidth / 2, -12, 0xFFFFFF, false);

      int i = this.menu.getTraderLevel();
      if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
         // 2. Nivel text
         Component component = Component.literal("Nivel " + i);
         int j = this.font.width(component);
         int k = 136 + 51 - j / 2; // centered above progress bar
         p_283337_.drawString(this.font, component, k, 6, 0xFFDA6A, false); // Light yellow text
         
         // 3. Percentage box content
         int xp = this.menu.getTraderXp();
         int minXp = VillagerData.getMinXpPerLevel(i);
         int nextXp = VillagerData.getMinXpPerLevel(i + 1);
         int pct = 0;
         if (nextXp > minXp) {
             pct = (xp - minXp) * 100 / (nextXp - minXp);
         }
         if (i >= 5) pct = 100;
         String pctStr = pct + "%";
         int pctW = this.font.width(pctStr);
         p_283337_.drawString(this.font, pctStr, 280 + 15 - pctW / 2, 16, 0xFFDA6A, false);
      }
      p_283337_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xAAAAAA, false);
      int l = this.font.width(TRADES_LABEL);
   }'''
text = re.sub(labels_target_regex, new_labels, text, flags=re.DOTALL)

# Add custom render bg blit for the percentage box
bg_target = '''p_283072_.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);'''
bg_replace = '''p_283072_.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        // Draw the percentage box texture we injected at 280, 10
        p_283072_.blit(VILLAGER_LOCATION, i + 280, j + 10, 280, 10, 30, 20, 512, 256);'''
text = text.replace(bg_target, bg_replace)


with open(java_file, 'w', encoding='utf-8') as f:
    f.write(text)
