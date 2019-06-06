package dohyun22.jst3.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nonnull;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMetaBase extends Item {
	protected static final int MAX = 32766;
	public final LinkedHashMap<Integer, String> names = new LinkedHashMap<Integer, String>();
	protected final LinkedHashMap<Integer, ItemBehaviour> behaviours = new LinkedHashMap<Integer, ItemBehaviour>();
	protected final HashMap<Integer, Integer> fuelValues = new HashMap<Integer, Integer>();

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		return super.getUnlocalizedName(stack) + "." + meta;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (!isInCreativeTab(tab)) return;
		for (Integer i : names.keySet()) {
			if (i == null) continue;
			ItemBehaviour b = behaviours.get(i);
			ItemStack st = new ItemStack(this, 1, i.intValue());
			if (b != null) {
				b.getSubItems(st, subItems);
			} else {
				subItems.add(st);
			}
		}
	}
	
	@Override
	public int getItemBurnTime(ItemStack st) {
		Integer fv = fuelValues.get(st.getMetadata());
		return fv == null ? getBehaviour(st).getBurnTime(st) : fv;
	}
	
    public void registerMetaItem(int meta, String name, ItemBehaviour behaviour) {
    	if (name != null && !name.equals("")) {
    		try {
    			this.addMeta(meta, name);
            	if (behaviour != null)
            		behaviours.put(meta, behaviour);
    		} catch (Exception e) {
    			System.err.println("Cannot add " + name);
    			throw e;
    		}
    	}
    }
    
    public void registerMetaItem(int meta, String name) {
    	registerMetaItem(meta, name, null);
    }
    
	private ItemStack addMeta(int meta, @Nonnull String name) {
		if (meta > MAX || meta < 0)
			throw new IllegalArgumentException("Invalid Metadata: " + meta);
		if (names.get(meta) != null)
			throw new RuntimeException("Metadata " + meta + " is already used!");
		names.put(meta, name);
		return new ItemStack(this, 1, meta);
	}
	
    @Nonnull
    public ItemBehaviour getBehaviour(ItemStack s) {
    	return getBehaviour(s == null || s.isEmpty() ? 0 : s.getItem().getDamage(s));
    }
    
    @Nonnull
    public ItemBehaviour getBehaviour(int m) {
    	ItemBehaviour b = behaviours.get(m);
    	if (b == null)
    		return ItemBehaviour.INSTANCE;
    	return b;
    }
    
    public void addFuelValue(int m, int v) {
    	fuelValues.put(m, v);
    } 
}
