from PIL import Image, ImageDraw

# Create 512x256 image with transparent background
out = Image.new('RGBA', (512, 256), (0,0,0,0))
draw = ImageDraw.Draw(out)

# Main window is 276 x 166
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

# Left panel for trades: x=5, y=16, width=88, height=140
draw.rectangle([4, 15, 98, 157], fill=(16, 16, 16, 255), outline=(255, 140, 0, 255), width=1)

# Right panel for inventory area
draw.rectangle([102, 70, 270, 157], fill=(20, 20, 20, 255), outline=(100, 100, 100, 255))
# Player inventory slots 108, 84
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

# Trade slots background
draw.rectangle([135, 36, 135+18, 36+18], fill=(30, 30, 30, 255), outline=(70, 70, 70, 255))
draw.rectangle([161, 36, 161+18, 36+18], fill=(30, 30, 30, 255), outline=(70, 70, 70, 255))
# Result slot (larger)
draw.rectangle([218, 33, 218+24, 33+24], fill=(30, 30, 30, 255), outline=(100, 100, 100, 255))

# Arrows
# Empty arrow at 15, 171
draw.polygon([(15, 171+4), (15+6, 171+4), (15+6, 171), (15+10, 171+5), (15+6, 171+10), (15+6, 171+6), (15, 171+6)], fill=(100, 100, 100, 255))
# Red X arrow at 25, 171
draw.polygon([(25, 171+4), (25+6, 171+4), (25+6, 171), (25+10, 171+5), (25+6, 171+10), (25+6, 171+6), (25, 171+6)], fill=(100, 100, 100, 255))
draw.line([25, 171, 35, 180], fill=(255, 0, 0, 255), width=2)
draw.line([35, 171, 25, 180], fill=(255, 0, 0, 255), width=2)

# Progress bar empty: 0, 186 to 102, 191
draw.rectangle([0, 186, 102, 191], fill=(40, 40, 40, 255), outline=(255, 140, 0, 255))
# Progress bar full (orange!): 0, 191 to 102, 196
draw.rectangle([1, 192, 101, 195], fill=(255, 165, 0, 255))

# Scrollbar thumb: 0, 199 to 6, 226
draw.rectangle([0, 199, 5, 225], fill=(255, 140, 0, 255), outline=(200, 100, 0, 255))

out.save(r'src/main/resources/assets/aldeanoforaflut/textures/gui/phora_gui.png')
