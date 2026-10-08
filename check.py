import json
data = json.load(open('src/main/resources/assets/aldeanoforaflut/models/block/transformable_merchant_block.json'))
for i, e in enumerate(data['elements']):
    if 'rotation' in e:
        if 'axis' not in e['rotation']:
            print(f'Missing axis in element {i}: {e}')

