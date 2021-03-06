package mod;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class RecipeCrafter extends Block{
    public static final float defaultCraftingTime = 10.0f;
    
    public @Nullable ItemStack[] outputItems;
    public @Nullable LiquidStack outputLiquid;

    public float craftTime = 80;
    public Effect craftEffect = Fx.pulverizeMedium;
    public Effect updateEffect = Fx.pulverizeSmall;
    public float updateEffectChance = 0.04f;
    public float warmupSpeed = 0.019f;

    public DrawBlockImpl drawer = new DrawBlockImpl();
    public Seq<Recipe> recipes = Seq.with(Recipe.recipes.values()).filter(r -> r.getId().startsWith("craft"));
    
    public int[] capacities = {};
    
    public RecipeCrafter(String name){
        super(name);
        update = true;
        solid = true;
        hasPower = true;
        hasItems = true;
        acceptsItems = true;
        ambientSound = Sounds.machine;
        ambientSoundVolume = 0.03f;
        sync = true;
        configurable = true;
        flags = EnumSet.of(BlockFlag.factory);
        //TODO: group = BlockGroup. ...;
        
        config(Integer.class, (MultiCrafterBuild tile, Integer i) -> {
            if(tile.currentPlan == i) return;
            tile.currentPlan = i < 0 || i >= recipes.size ? -1 : i;
            tile.progress = 0;
        });

        config(ItemStack.class, (MultiCrafterBuild tile, ItemStack val) -> {
            int next = recipes.indexOf(p -> p.getCraftedItem().item == val.item);
            if(tile.currentPlan == next) return;
            tile.currentPlan = next;
            tile.progress = 0;
        });
        
        consumePower(0.25f);
        consume(new ConsumeItemDynamic((MultiCrafterBuild e) -> e.currentPlan != -1 ? recipes.get(e.currentPlan).getReqs() : ItemStack.empty));
        
    }
    
    @Override
    public void setStats(){//TODO:Better Stats
        stats.timePeriod = craftTime;
        super.setStats();
        stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquid != null){
            stats.add(Stat.output, outputLiquid.liquid, outputLiquid.amount * (60f / craftTime), true);
        }
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public void init(){
        outputsLiquid = outputLiquid != null;
        capacities = new int[Vars.content.items().size];
        for(Recipe recipe : recipes){
            for(ItemStack stack : recipe.getReqs()){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }
        super.init();
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.icons(this);
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    public class MultiCrafterBuild extends Building{
        public float progress;
        public float totalProgress;
        public float warmup;
        public int currentPlan = -1;

        public float fraction(){
            return currentPlan == -1 ? 0 : progress / recipes.get(currentPlan).getCraftingTime();
        }
        
        @Override
        public void buildConfiguration(Table table){
            Seq<Item> items = Seq.with(recipes).map(u -> u.getCraftedItem().item).filter(u -> u.unlockedNow());
            
            if(items.any()){
                ItemSelection.buildTable(table, items, () -> currentPlan == -1 ? null : recipes.get(currentPlan).getCraftedItem().item, item -> configure(recipes.indexOf(i -> i.getCraftedItem().item == item)));
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.config) return currentPlan == -1 ? null : recipes.get(currentPlan).getCraftedItem();
            return super.senseObject(sensor);
        }
        
        @Override
        public void display(Table table){
            super.display(table);

            TextureRegionDrawable reg = new TextureRegionDrawable();

            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(currentPlan == -1 ? Icon.cancel : reg.set(recipes.get(currentPlan).getCraftedItem().item.uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(currentPlan == -1 ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> currentPlan == -1 ? "@none" : recipes.get(currentPlan).getCraftedItem().item.localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
        }
        
        @Override
        public Object config(){
            return currentPlan;
        }
        
        @Override
        public void draw(){
        	drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
        
        @Override
        public boolean shouldConsume(){//TODO:OUTITEMS
            if(currentPlan == -1) return false;
            ItemStack output = recipes.get(currentPlan).getCraftedItem();
            if(items.get(output.item) + output.amount > itemCapacity) return false;
            return enabled;
        }
        
        @Override
        public void updateTile(){
            if(currentPlan < 0 || currentPlan >= recipes.size){
                currentPlan = -1;
            }
            
            if(efficiency > 0 && currentPlan != -1){
            	progress += getProgressIncrease(craftTime);
                totalProgress += delta();
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);

                if(Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }else{
            	warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            if(progress >= 1f){
                consume();

                if(recipes.get(currentPlan).getCraftedItem() != null){//TODO:OUTITEMS
                    //for(ItemStack output : outputItems){
                	ItemStack output = recipes.get(currentPlan).getCraftedItem();
                        for(int i = 0; i < output.amount; i++){
                            offload(output.item);
                        }
                    //}
                }

                if(outputLiquid != null){
                    handleLiquid(this, outputLiquid.liquid, outputLiquid.amount);
                }

                if(wasVisible){
                    craftEffect.at(x, y);
                }
                progress %= 1f;
            
            }/*else{
                progress = 0f;
            }*/
            dumpOutputs();
        }
        
        public void dumpOutputs(){
        	if(currentPlan==-1)return;
        	
        	ItemStack current = recipes.get(currentPlan).getCraftedItem();
            if(current!=null && timer(timerDump, dumpTime / timeScale)){
                for(int i = 0;i < current.amount;i++){
                    dump(current.item);
                }
            }

            /*if(outputLiquids != null){
                for(int i = 0; i < outputLiquids.length; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                    dumpLiquid(outputLiquids[i].liquid, 2f, dir);
                }
            }*/
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return Mathf.clamp(progress);
            return super.sense(sensor);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return capacities[item.id];
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }
        
        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }
        
        @Override
        public boolean acceptItem(Building source, Item item){
            return currentPlan != -1 && items.get(item) < getMaximumAccepted(item) &&
                Structs.contains(recipes.get(currentPlan).getReqs(), stack -> stack.item == item);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            write.i(currentPlan);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            currentPlan = read.i();
        }
    }
}

class DrawBlockImpl extends DrawBlock{

    /** Draws the block. */
    public void draw(mod.RecipeCrafter.MultiCrafterBuild build){
        Draw.rect(build.block.region, build.x, build.y, build.block.rotate ? build.rotdeg() : 0);
    }

    /** Draws any extra light for the block. */
    public void drawLight(mod.RecipeCrafter.MultiCrafterBuild build){

    }
}
