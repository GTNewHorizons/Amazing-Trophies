Custom Trophies can be defined by calling `AmazingTrophiesAPI::registerTrophy` during the init phase of your mod or via JSON config files. These config files must be put in `./config/trophies/`. You can order the files by using as many subdirectories as you wish.

> [!NOTE]
>  The file tree is traversed depth-first and in lexicographical order (ignoring upper/lower cases). Example:
> 1. `./a/a/apple.json`
> 2. `./a/plum.json`
> 3. `./B/fig.json`
> 4. `./c/a/kiwi.json`
> 5. `./BANANA.json`
> 6. `./durian.json`

### Trophy Properties
|Name|Type|Default|Notes|
|:---:|:---:|:---:|:---|
|id|String|*Required*|The id is used in Tile Entities, Item Stacks and for localization. **Once set, do not change this property unless you know what you are doing!**|
|condition|Object|null|The [condition](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Condition-Handlers) which must be met in order to gain this trophy. If null, the player can not get this trophy.|
|model|Object|null|The [model](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Trophy-Model-Handlers) which is used when rendering the trophy. If null, only the pedestal will be rendered.|

Both the `condition` and `model` property require the String property `type` to specify the [Condition Handler](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Condition-Handlers) / [Trophy Model Handler](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Trophy-Model-Handlers) to use. The handler may require addtional properties.

#### Example
The trophy is given to the player when they get the Bedrock achievement given as an example [here](https://github.com/GTNewHorizons/Amazing-Trophies/wiki/Achievements#example). Its model is a single Bedrock block on a pedestal.
```json
{
  "id": "bedrock",
  "condition": {
    "type": "achievement",
    "id": "bedrock"
  },
  "model": {
    "type": "item",
    "registryName": "minecraft:bedrock"
  }
}
```
