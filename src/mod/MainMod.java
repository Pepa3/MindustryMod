package mod;

import arc.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import static mindustry.type.ItemStack.*;

public class MainMod extends Mod{
	public static Item frogMetal, frogRaw;
	public static Block oreFrog;
	public static GenericCrafter frogSmelter;

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
        loadBlocks();
        loadUnits();
    }
    
    private void loadItems() {
    	frogRaw = new Item("frog-raw", Color.valueOf("ebeef5")){{
    		alwaysUnlocked = true;
            cost = 1.5f;
            hardness = 1;
        }};
    	frogMetal = new Item("frog-metal", Color.valueOf("ebefe5")){{
    		
            cost = 1.5f;
            hardness = 1;
        }};
    }
    private void loadBlocks() {
    	oreFrog = new OreBlock(frogRaw){{
    		
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 25.380953f;
        }};
        
        frogSmelter = new GenericCrafter("frog-smelter"){{
            requirements(Category.crafting, with(frogRaw, 50, Items.copper, 30));
            
            craftEffect = Fx.pulverizeMedium;
            outputItem = new ItemStack(frogMetal, 1);
            craftTime = 120f;
            size = 2;
            hasItems = true;
            
            consumes.power(0.25f);
            consumes.item(frogRaw, 2);
        }};
    }
    private void loadUnits() {}
}
