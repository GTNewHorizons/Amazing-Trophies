Condition Handlers define *when* an achievement or a trophy is awarded. Custom Condition Handlers can be added by calling `AmazingTrophiesAPI::registerConditionHandler` during the preInit phase of your mod. These handlers are available by default:

### `achievement`
Triggers when the player gains the specified achievement.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.AchievementEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/AchievementEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|id|String|Stat ID|*Required*|Stat ID for vanilla achievements is `achievement.` followed by its resource location. You can see a list of all achievements [here](https://minecraft.wiki/w/Achievement/Java_Edition).|

#### Example
Triggers when the Inventory is opened for the first time.
```json
"type": "achievement",
"id": "achievement.openInventory"
```


### `attack.entity`
Triggers when the player attacks an living entity.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.LivingAttackEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/LivingAttackEvent.java).*

#### Properties
Any combination of these properties is allowed.
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|damage|float||0.0|Minimum raw damage, before accounting for invulnerability, resistance, armour, etc.|
|damageType|String array|Damage type|[]|Vanilla damage types are: `inFire`, `onFire`, `lava`, `inWall`, `drown`, `starve`, `cactus`, `fall`, `outOfWorld`, `generic`, `magic`, `wither`, `anvil`, `fallingBlock`.|
|isDamageTypesAllowList|boolean||false|true = allow list, false = deny list|
|entities|String array|entity name or fully qualified class name|[]|Combining entity names and classes is allowed.|
|isEntitiesAllowList|boolean||false|true = allow list, false = deny list|

#### Examples
*Example 1:* Triggers if the player deals at least 5 damage to any living entity.
```json
"type": "attack.entity",
"damage": 5.0
```

*Example 2:* Triggers only if the player shoots any living entity with an Arrow.
```json
"type": "attack.entity",
"damageTypes": [
    "arrow"
],
"isDamageTypesAllowList": true
```

*Example 3:* Triggers only if the player is attacking a Pig.
```json
"type": "attack.entity",
"entities": [
    "Pig"
],
"isEntitiesAllowList": true
```

*Example 4:* Triggers only if the player is attacking a Cow or a Sheep.
```json
"type": "attack.entity",
"entities": [
    "net.minecraft.entity.passive.EntityCow",
    "net.minecraft.entity.passive.EntitySheep"
],
"isEntitiesAllowList": true
```


### `attack.player`
Triggers when a living entity attacks the player.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.LivingAttackEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/LivingAttackEvent.java).*

#### Properties
*See `attack.entity`.*

#### Examples
*See `attack.entity`.*


### `block.break`
Triggers when the player breaks the specified block.
<br />*Implementation note: Listens to [`net.minecraftforge.event.world.BlockEvent$BreakEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/world/BlockEvent.java#L67-L120).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|block|String|registry name|*Required*|`minecraft:` can be omitted.|
|meta|int|0 - 32767|32767|32767 is the wildcard value. If it's specified, all variants of the block are accepted.|

#### Example
Triggers if the player breaks either normal or Red Sand.
```json
"type": "block.break",
"block": "minecraft:sand"
```


### `block.interact`
Triggers when the player interacts with the specified block.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.PlayerInteractEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/PlayerInteractEvent.java).*

#### Properties
*See `block.break`.*

#### Example
*See `block.break`.*


### `block.place`
Triggers when the player places the specified block.
<br />*Implementation note: Listens to [`net.minecraftforge.event.world.BlockEvent$PlaceEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/world/BlockEvent.java#L122-L148).*

#### Properties
*See `block.break`.*

#### Example
*See `block.break`.*


### `container.open`
Triggers when the player opens the specified container.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.PlayerOpenContainerEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/PlayerOpenContainerEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|containers|String array|fully qualified class name|*Required*||

#### Example
Triggers if the player opens a Beacon's inventory.
```json
"type": "container.open",
"containers": "net.minecraft.inventory.ContainerBeacon"
```


### `death`
Triggers when the player dies.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.LivingDeathEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/LivingDeathEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|sources|String array|Damage type|[]|Vanilla damage types are: `inFire`, `onFire`, `lava`, `inWall`, `drown`, `starve`, `cactus`, `fall`, `outOfWorld`, `generic`, `magic`, `wither`, `anvil`, `fallingBlock`.|
|isSourcesAllowList|boolean||false|true = allow list, false = deny list|

#### Example
Triggers only if the player uses /kill or falls out of the world.
```json
"type": "death",
"sources": "outOfWorld",
"isSourcesAllowList": true
```


### `dimension.join`
Triggers when the player enters the specified dimension.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.EntityJoinWorldEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/EntityJoinWorldEvent.java).*

#### Properties
Either `id` or `provider` must be specified.
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|id|int||||
|provider|String|fully qualified class name|||

#### Examples
*Example 1: Triggers if the player enters the End dimension.*
```json
"type": "dimension.join",
"id": 1
```

*Example 2: Triggers if the player enters the Nether dimension.*
```json
"type": "dimension.join",
"provider": "net.minecraft.world.WorldProviderHell"
```


### `enderpearl`
Triggers when the player throws an Enderpearl the specified distance.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.EnderTeleportEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/EnderTeleportEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|distance|double|||Minimum distance the enderpearl is thrown|

#### Example
Triggers if the player teleports a distance of at least 5 blocks using an Enderpearl.
```json
"type": "enderpearl",
"distance": 5.0
```


### `explosion`
Triggers when the player causes an explosion.
<br />*Implementation note: Listens to [`net.minecraftforge.event.world.ExplosionEvent$Detonate`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/world/ExplosionEvent.java#L48-L75).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|size|float||0.0|Minimum explosion size|

#### Example
Triggers only if the player causes an explosion (note: igniting TNT only counts when done with Flint and Steel) of size 2 or larger.
```json
"type": "explosion",
"size": 2.0
```


### `fall`
Triggers when the player falls down.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.LivingFallEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/LivingFallEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|distance|float||0.0|Minimum fall distance|

#### Example
Triggers if the player falls at least 20 blocks (note: is not triggered if the player is able to fly).
```json
"type": "fall",
"distance": 20.0
```


### `heal`
Triggers when the player regains health points.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.LivingHealEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/LivingHealEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|amount|float||0.0|Minimum amount of restored health points|

#### Example
Triggers if the player heals themselves by at least 5 HP.
```json
"type": "heal",
"amount": 5.0
```


### `interact.entity`
Triggers when the player interacts with an entity.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.EntityInteractEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/EntityInteractEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|targets|String array|entity name or fully qualified class name|[]|Combining entity names and classes is allowed.|

#### Example
Triggers if the player interacts with an Item Frame.
```json
"type": "interact.entity",
"targets": "net.minecraft.entity.item.EntityItemFrame"
```


### `item.craft`
Triggers when the player crafts the specified item.
<br />*Implementation note: Listens to [`cpw.mods.fml.common.gameevent.PlayerEvent$ItemCraftedEvent`](https://github.com/MinecraftForge/FML/blob/1.7.10/src/main/java/cpw/mods/fml/common/gameevent/PlayerEvent.java#L25-L34).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|item|String|registry name|*Required*|`minecraft:` can be omitted.|
|meta|int|0 - 32767|32767|32767 is the wildcard value. If it's specified, all variants of the block are accepted.|
|nbt|String|[SNBT](https://minecraft.wiki/w/NBT_format#SNBT_format) compound|null||

#### Example
Triggers if the player crafts a Diamond Axe.
```json
"type": "item.craft",
"item": "minecraft:diamond_axe"
```


### `item.drop`
Triggers when the player crafts the specified item.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.item.ItemTossEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/item/ItemTossEvent.java).*

#### Properties
*See `item.craft`.*

#### Examples
*Example 1:* Triggers if the player drops any diamond sword.
```json
"type": "item.drop",
"item": "minecraft:diamond_sword"
```
*Example 2:* Triggers only if the player drops a Diamond Sword which was damaged exactly once.
```json
"type": "item.drop",
"item": "minecraft:diamond_sword",
"meta": 1
```
*Example 3:* Triggers only if the player drops a diamond sword with the given NBT (but any damage).
```json
"type": "item.drop",
"item": "minecraft:diamond_sword",
"nbt": "{display:{Lore:[Test]}}"
```


### `item.pickup`
Triggers when the player picks up the specified item.
<br />*Implementation note: Listens to [`cpw.mods.fml.common.gameevent.PlayerEvent$ItemPickupEvent`](https://github.com/MinecraftForge/FML/blob/1.7.10/src/main/java/cpw/mods/fml/common/gameevent/PlayerEvent.java#L16-L23).*

#### Properties
*See `item.craft`.*

#### Example
Triggers if the player picks up any Dirt itemstack.
```json
"type": "item.pickup",
"item": "minecraft:dirt"
```


### `item.smelt`
Triggers when the player crafts the specified item.
<br />*Implementation note: Listens to [`cpw.mods.fml.common.gameevent.PlayerEvent$ItemSmeltedEvent`](https://github.com/MinecraftForge/FML/blob/1.7.10/src/main/java/cpw/mods/fml/common/gameevent/PlayerEvent.java#L35-L42).*

#### Properties
*See `item.craft`.*

#### Example
Triggers if the player takes Charcoal from the output slot of a Furnace.
```json
"type": "item.smelt",
"item": "minecraft:coal",
"meta": 1
```


### `item.use.start`
Triggers when the player starts using the specified item.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.PlayerUseItemEvent$Start`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/PlayerUseItemEvent.java#L19-L37).*

#### Properties
*See `item.craft`.*

#### Example
Triggers if the player starts eating an Apple.
```json
"type": "item.use.start",
"item": "minecraft:apple"
```


### `item.use.stop`
Triggers when the player stops using the specified item before the use duration timed out (e.g. stop eating an Apple half way through).
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.PlayerUseItemEvent$Stop`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/PlayerUseItemEvent.java#L54-L73).*

#### Properties
*See `item.craft`.*

#### Example
Triggers if the player stops using a Bow before it is fully drawn.
```json
"type": "item.use.stop",
"item": "minecraft:bow"
```


### `item.use.finish`
Triggers when the player finishes using the specified item.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.PlayerUseItemEvent$Finish`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/PlayerUseItemEvent.java#L75-L93).*

#### Properties
*See `item.craft`.*

#### Example
Triggers if the player uses a potion.
```json
"type": "item.use.finish",
"item": "minecraft:potion"
```


### `kill`
Triggers when the player kills a living entity.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.living.LivingDeathEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/living/LivingDeathEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|targets|String array|entity name or fully qualified class name|[]|Combining entity names and classes is allowed.|
|isTargetsAllowList|boolean||false|true = allow list, false = deny list|

#### Example
Triggers if the player kills a Zombie or (Wither) Skeleton.
```json
"type": "kill",
"targets": [
    "Zombie",
    "Skeleton"
],
"isTargetsAllowList": true
```


### `lightning`
Triggers when the player is struck by lightning.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.EntityStruckByLightningEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/EntityStruckByLightningEvent.java).*

#### Properties
This condition handler doesn't have any properties.


### `xp`
Triggers when the player absorbs the specified amount of experience points via a single XP orb.
<br />*Implementation note: Listens to [`net.minecraftforge.event.entity.player.PlayerPickupXpEvent`](https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/event/entity/player/PlayerPickupXpEvent.java).*

#### Properties
|Name|Type|Format/Range|Default|Notes|
|:---:|:---:|:---:|:---:|:---|
|amount|float||0.0|Minimum amount of absorbed experience points|

#### Example
Triggers if the player picks up at least 5 XP in a single orb.
```json
"type": "xp",
"amount": 5.0
```
