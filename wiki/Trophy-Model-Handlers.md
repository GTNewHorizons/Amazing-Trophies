Trophy Model Handlers define how a Trophy looks. Custom Trophy Model Handlers can be added by calling `AmazingTrophiesAPI::registerTrophyModelHandlerProvider` (client-side only) during the preInit phase of your mod. These handlers are available by default:

### `pedestal`
Renders only the trophie's pedestal.


### `basic`
Renders any model on top of the pedestal. Wavefront (`.obj`) and Techne (`.tcn`) models are supported by default. Other mods may add support for additional model formats.

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|model|String|[Resource Location](https://minecraft.wiki/w/Resource_location)|*Required*|If no namespace is specified, `amazingtrophies:models/` is prepended.|
|texture|String|[Resource Location](https://minecraft.wiki/w/Resource_location)|*Required*|If no namespace is specified, `amazingtrophies:textures/blocks/` is prepended.|

#### Example
```json
"type": "basic",
"model": "model.obj",
"texture": "texture.png"
```
Source: [model.obj](https://github.com/GTNewHorizons/Amazing-Trophies/blob/master/run/config/amazingtrophies/models/model.obj) | [texture.png](https://github.com/GTNewHorizons/Amazing-Trophies/blob/master/run/config/amazingtrophies/textures/blocks/texture.png)
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_basic.png)

### `entity`
Renders any entity on top of the pedestal.

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|entity|String|entity name or fully qualified class name|*Required*||
|yOffset|double||-0.1875|The offset is applied before scaling.|
|yawOffset|float||180.0||
|scale|float||0.34375||
|nbt|String|[SNBT](https://minecraft.wiki/w/NBT_format#SNBT_format) Compound|"{}"||

#### Examples
*Example 1:*
```json
"type": "entity",
"entity": "Zombie"
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_entity1.png)

*Example 2:*
```json
"type": "entity",
"entity": "net.minecraft.entity.projectile.EntityFishHook",
"yOffset": 0.0625,
"scale": 1
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_entity2.png)

*Example 3:*
```json
"type": "entity",
"entity": "MinecartSpawner",
"nbt": "{EntityId:MinecartCommandBlock}",
"yawOffset": 90,
"yOffset": -0.04296875
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_entity3.png)

### `item`
Renders any item (or block with an item representation) on top of the pedestal.

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|registryName|String|registry name|*Required*||
|meta|int|0 - 32766|0||
|nbt|String|[SNBT](https://minecraft.wiki/w/NBT_format#SNBT_format) Compound|null||
|xOffset|double||0.0|The offset is applied before scaling.|
|yOffset|double||scale \* 0.125 - 0.1875|The offset is applied before scaling.|
|zOffset|double||0.0|The offset is applied before scaling.|
|yawOffset|float||0.0||
|scale|float||0.6875 for items, 1.375 for blocks||

#### Examples
*Example 1:*
```json
"type": "item",
"registryName": "minecraft:diamond_pickaxe"
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_item1.png)

*Example 2:*
```json
"type": "item",
"registryName": "minecraft:potion",
"meta": 16417
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_item2.png)

*Example 3:*
```json
"type": "item",
"registryName": "minecraft:firework_charge",
"nbt": "{Explosion:{Colors:[2437522]}}"
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_item3.png)

### `complex`
Renders any number of blocks on top of the pedestal.

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|keys|Map|Keys: chars used in `structure`, Values: registry names|*Required*||
|metadata|Map|Keys: chars used in `structure`, Values: metadata|{}||
|structure|2D String array|See [StructureLib](https://www.gtnewhorizons.com/StructureLib/1.4.10/javadoc/com/gtnewhorizon/structurelib/structure/StructureDefinition.Builder.html#addShape(java.lang.String,java.lang.String%5B%5D%5B%5D))|*Required*|Unlike in StructureLib, only the space character is treated as a special character (air).|
|transpose|boolean||false|See [StructureLib](https://www.gtnewhorizons.com/StructureLib/1.4.10/javadoc/com/gtnewhorizon/structurelib/structure/StructureUtility.html#transpose(java.lang.String%5B%5D%5B%5D))|
|skipHalfOffset|char array||[]|Specify all blocks by their char which are misaligned by 0.5 in X, Y and Z.|

#### Examples
*Example 1:*
```json
"type": "complex",
"metadata": {
    "y" : 1
},
"keys" : {
    "x" : "minecraft:stone",
    "y" : "minecraft:wool"
},
"structure":
[ [
    "xxx",
    "xyx"
] ]
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_cpmplex1.png)

*Example 2:*
```json
"type": "complex",
"keys": {
    "x": "minecraft:web"
},
"skipHalfOffset": [
    "x"
],
"structure": [
    [
        "x     x",
        "       ",
        "       ",
        "       ",
        "       ",
        "       ",
        "x     x"
    ],
    [
        "       ",
        " x   x ",
        "       ",
        "       ",
        "       ",
        " x   x ",
        "       "
    ],
    [
        "       ",
        "       ",
        "  x x  ",
        "       ",
        "  x x  ",
        "       ",
        "       "
    ],
    [
        "       ",
        "       ",
        "       ",
        "   x   ",
        "       ",
        "       ",
        "       "
    ],
    [
        "       ",
        "       ",
        "  x x  ",
        "       ",
        "  x x  ",
        "       ",
        "       "
    ],
    [
        "       ",
        " x   x ",
        "       ",
        "       ",
        "       ",
        " x   x ",
        "       "
    ],
    [
        "x     x",
        "       ",
        "       ",
        "       ",
        "       ",
        "       ",
        "x     x"
    ]
]
```
![](https://raw.githubusercontent.com/GTNewHorizons/Amazing-Trophies/refs/heads/master/wiki/img/trophy_complex2.png)
