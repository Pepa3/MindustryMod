package mod;

import arc.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;

import static mindustry.type.ItemStack.*;
//import static mindustry.content.TechTree.*;

public class MainMod extends Mod{
	public static Item iron_ore, iron_ingot, iron_plate, iron_rod, iron_gear;
	public static Block ironOre;
	public static RecipeCrafter crafterV1;
	public static GenericCrafter smelter;
	public static TechNode techNode;

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
        loadBlocks();
        loadUnits();
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
    }
    
    private void loadRecipes() {
    	new Recipe("smelt_ironOre",new ItemStack(iron_ingot,1)).addRequirement(new ItemStack(iron_ore, 2));
    	new Recipe("craft_iron_plate",new ItemStack(iron_plate,1)).addRequirement(new ItemStack(iron_ingot, 1));
    	new Recipe("craft_iron_gear",new ItemStack(iron_gear,3)).addRequirement(new ItemStack(iron_plate, 2));
    	new Recipe("craft_iron_rod",new ItemStack(iron_rod,4)).addRequirement(new ItemStack(iron_plate, 1));
    	
    }
    
    private void loadBlocks() {
    	ironOre = new OreBlock(iron_ore){{
    		localizedName="Iron Ore";
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 25.380953f;
        }};
        
        crafterV1 = new RecipeCrafter("crafterV1"){{
            requirements(Category.crafting, with(Items.copper, 30, iron_ingot, 50));
            
            craftEffect = Fx.pulverizeMedium;
            size = 2;
            localizedName="Crafter Tier 1";
        }};
        
        smelter = new GenericCrafter("smelter"){{
        	requirements(Category.crafting, with(Items.lead, 30, Items.copper, 50));
            
        	size = 2;
        	outputItem = new ItemStack(iron_ingot,2);
        	consumeItem(iron_ore,3);
        	localizedName="Smelter";
        }};
    }
    
    private void loadUnits() {}
    
    private void loadResearch() {
    	techNode = new TechNode(Planets.serpulo.techTree,smelter,smelter.researchRequirements()) {};
    	/*techNode1 =*/ new TechNode(techNode,crafterV1,crafterV1.researchRequirements()) {};
    }
}
