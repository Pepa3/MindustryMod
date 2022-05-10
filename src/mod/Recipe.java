package mod;

import java.util.ArrayList;

import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.type.*;

public class Recipe{
    public static ObjectMap<String, Recipe> recipes = new ObjectMap<>();
    
	private float time = 0.0f;
	private ArrayList<ItemStack> reqs = new ArrayList<ItemStack>();
	private String id = "";
	private ItemStack out;
	
	public Recipe(String id, float time, ItemStack out) {
		this.id = id;
		this.time = time;
		this.out = out;
		
		if(!recipes.containsKey(this.id)) {
			recipes.put(this.id, this);
		}else {
			Log.warn("Identical recipe ID, is this an override? {}", id);
		}
	}
	public Recipe(String id, ItemStack out) {
		this(id, RecipeCrafter.defaultCraftingTime, out);
	}
	
	public static Recipe getRecipe(String id) {
		Log.info("Recipe.getRecipe "+id);
		return recipes.get(id);
	}
	
	public static String getId(Recipe r) {
		Log.info("Recipe.getId "+r.getId());
		return r.getId();
	}
	
	public static float getCraftingTime(Recipe r) {
		Log.info("Recipe.getCraftingTime "+r.getId());
		return r.getCraftingTime();
	}
	
	public String getId() {
		return this.id;
	}
	
	public ItemStack getCraftedItem() {
		return this.out;
	}
	
	public float getCraftingTime() {
		return this.time;
	}
	
	public Recipe addRequirement(ItemStack i) {
		this.reqs.add(i);
		return this;
	}
	
	public int getReqNum() {
		return this.reqs.size();
	}
	
	public ItemStack[] getReqs() {
		return this.reqs.toArray(new ItemStack[this.reqs.size()]);
	}
	
	public ItemStack getReq(int n){
		return this.reqs.get(n);
	}
	
}
