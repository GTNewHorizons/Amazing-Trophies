Custom Achievements can be defined by calling `AmazingTrophiesAPI::registerAchievement` during the init phase of your mod or via JSON config files. These config files must be put in `./config/amazingtrophies/`. You can order the files by using as many subdirectories as you wish.

> [!NOTE]
>  The file tree is traversed depth-first and in lexicographical order (ignoring upper/lower cases). Example:
> 1. `./a/a/apple.json`
> 2. `./a/plum.json`
> 3. `./B/fig.json`
> 4. `./c/a/kiwi.json`
> 5. `./BANANA.json`
> 6. `./durian.json`

### Achievement Properties
|Name|Type|Default|Notes|
|:---:|:---:|:---:|:---|
|id|String|*Required*|The id is used when registering the achievement and for localization. **Once set, do not change this property unless you know what you are doing!**|
|condition|Object|null|The [condition](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Condition-Handlers) which must be met in order to complete this achievement. If null, the achievement can never be completed.|
|page|String|null|The name of the achievement page to display this on. If null, the achievement will be displayed on the default Minecraft page. If the specified page does not exist, it will be created.|
|x|int|*Required*|The horizontal coordinate of the achievment on the achievement page<sup>1</sup>. Increasing this value moves the achievement to the right.|
|y|int|*Required*|The vertical coordinate of the achievment on the achievement page<sup>1</sup>. Increasing this value moves the achievement down.|
|parent|String|null|The stat ID of the achievement which must be completed before this is unlocked. If null, this achievement can always be completed. The stat ID for vanilla achievements is `achievement.` followed by its resource location. You can see a list of all vanilla achievements [here](https://minecraft.wiki/w/Achievement/Java_Edition). To set another custom achievement as parent, simply use their id. **The parent achievement must be registered before this!**|
|isSpecial|boolean|false|Special achievements get a different frame on their achievement page and a purple name.|
|icon|Object|*Required*|The item to render on the achievement page.|

<sup>1</sup> For reference: the "Open Inventory" achievement is at (0, 0). The vanilla achievements are in the range of -4 to 10 (x) and -5 to 13 (y), however it's possible to choose smaller/larger values.

#### `condition` Properties
The required String property `type` specifies the [Condition Handler](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Condition-Handlers) to use. The handler may require addtional properties.

#### `icon` Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|registryName|String|registry name|*Required*|`minecraft:` can be omitted.|
|meta|int|0 - 32766|0||
|nbt|String|[SNBT](https://minecraft.wiki/w/NBT_format#SNBT_format) compound|null||

#### Example
The achievement is awarded when the player picks up Bedrock. The "Beaconator" achievement is the prerequisite to this and located directly below. The achievement's icon is a Bedrock block with holo effect.
```json
{
  "id": "bedrock",
  "condition": {
    "type": "item.pickup",
    "item": "minecraft:bedrock"
  },
  "page": null,
  "x": 7,
  "y": 6,
  "parent": "achievement.fullBeacon",
  "icon": {
    "registryName": "minecraft:bedrock",
	"nbt": "{ench:[{id:0,lvl:1}]}"
  }
}
```
