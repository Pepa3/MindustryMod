package mod;

import arc.*;
import arc.graphics.*;
import arc.struct.Seq;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.content.TechTree.TechNode;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import static mindustry.type.ItemStack.*;
import static mindustry.content.TechTree.*;

public class MainMod extends Mod{
	public static Item frogMetal, frogRaw;
	public static Block oreFrog;
	public static RecipeCrafter frogSmelter;
	public static TechNode techNode;
	public static Recipe frogMetalRecipe;

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
    	frogRaw = new Item("frog-raw", Color.valueOf("00ee00")){{
    		alwaysUnlocked = true;
            cost = 1.5f;
            hardness = 1;
        }};
        
    	frogMetal = new Item("frog-metal", Color.valueOf("11ee22")){{
            cost = 1.5f;
            hardness = 1;
        }};
    }
    
    private void loadRecipes() {
    	new Recipe("smelt-frogRaw",new ItemStack(frogMetal,1)).addRequirement(new ItemStack(frogRaw, 2));
    	new Recipe("smelt-frogRaw2",new ItemStack(frogRaw,2)).addRequirement(new ItemStack(frogMetal, 1));
    }
    
    private void loadBlocks() {
    	oreFrog = new OreBlock(frogRaw){{
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 25.380953f;
        }};
        
        frogSmelter = new RecipeCrafter("frog-smelter", Seq.with(Recipe.getRecipe("smelt-frogRaw"),Recipe.getRecipe("smelt-frogRaw2"))){{
            requirements(Category.crafting, with(frogRaw, 50, Items.copper, 30));
            
            craftEffect = Fx.pulverizeMedium;
            //outputItems = new ItemStack[] {new ItemStack(frogMetal, 1)};
            craftTime = 120f;
            size = 2;
            
            /*consumes.power(0.25f);*/
            //consumes.item(frogRaw, 2);
        }};
    }
    
    private void loadUnits() {}
    
    private void loadResearch() {
    	techNode = new TechNode(TechTree.root,frogSmelter,frogSmelter.researchRequirements()) {};
    }
}
