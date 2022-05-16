package mod;

import arc.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.type.weapons.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.*;
import static mindustry.content.TechTree.*;

public class MainMod extends Mod{
	
	public static Item iron_ore, iron_ingot, iron_plate, iron_rod, iron_gear, stone_ore;
	public static Block ironOre, stoneOre, crafterV1, smelter, coreOne, coreTwo;
	public static TechNode techNode;
	public static Planet moon;
	public static SectorPreset moon_1;
	public static UnitType baseUnit1, baseUnit2;

    public MainMod(){
        Log.info("[AT]Loaded constructor.");
        
        Events.on(ClientLoadEvent.class, e -> {
        	
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("frog");
                dialog.cont.add("behold").row();
                dialog.cont.image(Core.atlas.find("allthings-frog")).pad(20f).row();
                dialog.cont.button("I see", dialog::hide).size(100f, 50f);
                dialog.show();
            });
        });
    }

    @Override
    public void loadContent(){
        Log.info("[AT]Loading content.");
        loadItems();
        loadRecipes();
        loadUnits();
        loadBlocks();
        loadPlanets();
        loadResearch();
    }
    
    private void loadItems() {
    	iron_ore = new Item("iron_ore", Color.valueOf("dd1010")){{
    		localizedName="Iron Ore";
            cost = 1.5f;
            hardness = 1;
        }};
        
    	iron_ingot = new Item("iron_ingot", Color.valueOf("1110dd")){{
    		localizedName="Iron Ingot";
            cost = 1.5f;
            hardness = 1;
        }};
        
        iron_plate = new Item("iron_plate", Color.valueOf("1110dd")){{
        	localizedName="Iron Plate";
            cost = 1.5f;
            hardness = 1;
        }};
        
        iron_rod = new Item("iron_rod", Color.valueOf("1110dd")){{
        	localizedName="Iron Rod";
            cost = 1.5f;
            hardness = 1;
        }};
        
        iron_gear = new Item("iron_gear", Color.valueOf("1110dd")){{
        	localizedName="Iron Gear";
            cost = 1.5f;
            hardness = 1;
        }};
        
        stone_ore = new Item("stone_ore", Color.valueOf("dd7010")){{
    		localizedName="Stone Ore";
            cost = 1.5f;
            hardness = 1;
        }};
    }
    
    private void loadRecipes() {
    	new Recipe("smelt_ironOre",new ItemStack(iron_ingot,1)).addRequirement(new ItemStack(iron_ore, 2));
    	new Recipe("craft_iron_plate",new ItemStack(iron_plate,1)).addRequirement(new ItemStack(iron_ingot, 1));
    	new Recipe("craft_iron_gear",new ItemStack(iron_gear,3)).addRequirement(new ItemStack(iron_plate, 2));
    	new Recipe("craft_iron_rod",new ItemStack(iron_rod,4)).addRequirement(new ItemStack(iron_plate, 1));
    	
    }
    
    private void loadUnits() {
    	EntityMapping.nameMap.put("allthings-main1", EntityMapping.idMap[24]);
    	EntityMapping.nameMap.put("allthings-main2", EntityMapping.idMap[21]);
    	
    	baseUnit1 = new UnitType("main1"){{
    		localizedName="Base Unit 1";
    		controller = u -> new BuilderAI(true, 500f);
            isEnemy = false;
            envDisabled = 0;
            speed = 4f;
            hitSize = 8f;
            health = 100f;
            buildSpeed = 1.0f;
            armor = 1f;
            mineFloor=true;
            mineWalls=true;
            mineTier=4;
            mineSpeed=2.0f;
            
            legCount = 6;
            legGroupSize = 6;
            legLength = 16f;
            legBaseOffset = 6f;
            legMoveSpace = 1.5f;
            lockLegBase = true;
            legContinuousMove = true;
            legForwardScl = 0.58f;
            hovering = true;
            shadowElevation = 0.2f;
            groundLayer = Layer.legUnit;
            targetable=false;
            killable=false;

            abilities.add(new RepairFieldAbility(10f, 60f * 4, 60f));

            weapons.add(new RepairBeamWeapon(){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 3.1f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                targetUnits = true;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 80f;
                }};
            }});
        }};
        baseUnit2 = new UnitType("main2"){{
    		localizedName="Base Unit 2";
    		controller = u -> new BuilderAI(true, 500f);
            isEnemy = false;
            envDisabled = 0;
            speed = 4f;
            hitSize = 8f;
            health = 100f;
            buildSpeed = 1.0f;
            armor = 1f;
            mineFloor=true;
            mineWalls=true;
            mineTier=5;
            mineSpeed=4.0f;
            
            legCount = 8;
            legGroupSize = 4;
            legLength = 18f;
            legBaseOffset = 8f;
            legMoveSpace = 1.5f;
            lockLegBase = true;
            legContinuousMove = true;
            legForwardScl = 0.58f;
            hovering = true;
            shadowElevation = 0.2f;
            groundLayer = Layer.legUnit;
            targetable=false;
            killable=false;

            abilities.add(new RepairFieldAbility(20f, 60f * 4, 60f));

            weapons.add(new RepairBeamWeapon(){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 4.2f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                targetUnits = true;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 80f;
                }};
            }});
        }};
    }
    
    private void loadBlocks() {
    	//Ores
    	ironOre = new OreBlock(iron_ore){{
    		localizedName="Iron Ore";
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 25.380953f;
        }};
        
        stoneOre = new OreBlock(stone_ore){{
    		localizedName="Stone Ore";
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 25.380953f;
        }};
        
        //Production
        crafterV1 = new RecipeCrafter("crafterV1"){{
            requirements(Category.crafting, with(Items.copper, 30, iron_ingot, 50));
            
            craftEffect = Fx.pulverizeMedium;
            size = 2;
            
            localizedName="Crafter Tier 1";
        }};
        
        smelter = new GenericCrafter("smelter"){{
        	requirements(Category.crafting, with(stone_ore, 50));
            
        	size = 2;
        	outputItem = new ItemStack(iron_ingot,2);
        	consumeItem(iron_ore,3);
        	
        	localizedName="Smelter";
        }};
        
        //Core
        coreOne = new CoreBlock("core-one"){{
            requirements(Category.effect, with(Items.copper, 1000, iron_ingot, 800));
            alwaysUnlocked = true;

            isFirstTier = true;
            unitType = baseUnit1;
            health = 1100;
            itemCapacity = 4000;
            size = 3;

            unitCapModifier = 8;
        }};

        coreTwo = new CoreBlock("core-two"){{
            requirements(Category.effect, with(Items.copper, 3000, iron_ingot, 3000, Items.silicon, 2000));

            unitType = baseUnit2;
            health = 3500;
            itemCapacity = 9000;
            size = 4;
            thrusterLength = 34/4f;

            unitCapModifier = 16;
            researchCostMultiplier = 0.07f;
        }};
    }
    
    private void loadPlanets() {
    	moon = new Planet("moon", Planets.serpulo, 0.75f, 2){{
    		localizedName="The Moon";
    		alwaysUnlocked = true;
            generator = new SerpuloPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 4);
            atmosphereColor = Color.valueOf("3db899");
            startSector = 1;
            atmosphereRadIn = -0.01f;
            atmosphereRadOut = 0.3f;
            defaultEnv = Env.groundWater | Env.terrestrial;
            launchCapacityMultiplier = 0.5f;
            sectorSeed = 2;
            
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = true;
            allowLaunchSchematics = true;
            allowLaunchLoadout = true;
            
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.attributes.clear();
                r.showSpawns = true;
            };
        }};
        moon_1 = new SectorPreset("moon_1", moon, 1){{
        	localizedName="Sector One";
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 10;
            difficulty = 1;
            startWaveTimeMultiplier = 3f;
        }};
        
        Planets.serpulo.launchCandidates.add(moon);
    }
    
    private void loadResearch() {
    	moon.techTree = nodeRoot("moon",coreOne,()->{
    		node(coreTwo,()->{});
    		node(smelter,()->{
    			node(crafterV1,()->{});
    		});
    		nodeProduce(iron_ore,()->{
    			nodeProduce(iron_ingot,()->{
    				nodeProduce(iron_plate,()->{
    					nodeProduce(iron_gear,()->{});
    					nodeProduce(iron_rod,()->{});
    				});
    			});
    		});
    		nodeProduce(stone_ore,()->{});
    	});
    }
}
