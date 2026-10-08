import sys, re
with open(r'src\main\java\aldeanoforaflut\aldeanoforaflut\entity\client\PhoraMerchantScreen.java', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Texture location
text = text.replace('new ResourceLocation("textures/gui/container/villager2.png")', 'new ResourceLocation("aldeanoforaflut", "textures/gui/phora_gui.png")')

# 2. Inject red background in render method
render_target = '''                 int j1 = k + 2;
                 this.renderAndDecorateCostA(p_283487_, itemstack1, itemstack, l, j1);'''
render_replacement = '''                 int j1 = k + 2;
                 int reqLvl = merchantoffer.getResult().hasTag() ? merchantoffer.getResult().getTag().getInt("RequiredLevel") : 0;
                 if (reqLvl > 0) {
                     p_283487_.fill(i + 5, j1 - 2, i + 5 + 88, j1 + 18, 0x88FF0000);
                 }
                 this.renderAndDecorateCostA(p_283487_, itemstack1, itemstack, l, j1);'''
text = text.replace(render_target, render_replacement)

# 3. Fix renderButtonArrows
arrows_target = '''          if (reqLvl > 0) {
              p_283020_.fill(p_282752_ + 5, p_282179_ - 2, p_282752_ + 5 + 88, p_282179_ + 18, 0x88FF0000);
              String text = "Nv> " + reqLvl;
              int textWidth = this.font.width(text);
              p_283020_.drawString(this.font, text, p_282752_ + 5 + 35 + 25 - textWidth/2, p_282179_ + 3, 0xFFFFFF, true);
          } else {'''
arrows_replacement = '''          if (reqLvl > 0) {
              String text = "Nv> " + reqLvl;
              int textWidth = this.font.width(text);
              p_283020_.drawString(this.font, text, p_282752_ + 5 + 35 + 25 - textWidth/2, p_282179_ + 3, 0xFFFFFF, true);
          } else {'''
text = text.replace(arrows_target, arrows_replacement)

with open(r'src\main\java\aldeanoforaflut\aldeanoforaflut\entity\client\PhoraMerchantScreen.java', 'w', encoding='utf-8') as f:
    f.write(text)
