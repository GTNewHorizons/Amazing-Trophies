Trophy Model Handlers define how a Trophy looks. Custom Trophy Model Handlers can be added by calling `AmazingTrophiesAPI::registerTrophyModelHandlerProvider` (client-side only) during the preInit phase of your mod. These handlers are available by default:

### `pedestal`
Renders only the trophie's pedestal.


### `basic`
Renders any model on top of the pedestal. Wavefront (`.obj`) and Techne (`.tcn`) models are supported by default.

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|model|String|[Resource Location](https://minecraft.wiki/w/Resource_location)|*Required*|If no namespace is specified, `amazingtrophies:models/` is prepended.|
|texture|String|[Resource Location](https://minecraft.wiki/w/Resource_location)|*Required*|If no namespace is specified, `amazingtrophies:textures/blocks/` is prepended.|


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

### `item`
Renders any item (or block with an item representation) on top of the pedestal.

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|registryName|String|registry name|*Required*||
|meta|int|0 - 32766|0||
|nbt|String|[SNBT](https://minecraft.wiki/w/NBT_format#SNBT_format) Compound|null||
|xOffset|double||0.0|The offset is applied before scaling.|
|yOffset|double||-0.1015625 for items, -0.015625 for blocks|The offset is applied before scaling.|
|zOffset|double||0.0|The offset is applied before scaling.|
|yawOffset|float||0.0||
|scale|float||0.6875 for items, 1.375 for blocks||
