package dohyun22.jst3.api.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import dohyun22.jst3.api.recipe.IRecipeItem;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictStack implements IRecipeItem {
	public final int count;
	public final String name;
	
	public OreDictStack(String name) {
		this(name, 1);
	}
	
	public OreDictStack(String name, int cnt) {
		this.name = name == null ? "" : name;
		this.count = cnt;
	}
	
	@Override
	public String toString() {
		return name + "@" + count;
	}
	
	@Override
	public int hashCode() {
		return count + name.hashCode() * 31;
	}
	
	@Override
	public boolean equals(Object in) {
		if (!(in instanceof OreDictStack)) return false;
		return this.count == ((OreDictStack)in).count && this.name.equals(in);
	}

	@Override
	public int getcount() {
		return count;
	}

	@Override
	public boolean matches(ItemStack in) {
		if (in != null && !in.isEmpty())
			for (int id : OreDictionary.getOreIDs(in))
				if (id == OreDictionary.getOreID(name) && count <= in.getCount())
					return true;
		return false;
	}
	
	@Override
	public boolean isValid() {
		return count > 0 && JSTUtils.oreValid(this);
	}

	@Override
	public List<ItemStack> getAllMatchingItems() {
		List<ItemStack> ret = new ArrayList();
		for (ItemStack st : OreDictionary.getOres(name)) {
			ItemStack st2 = st.copy();
			st2.setCount(count);
			ret.add(st2);
		}
		return ret;
	}

	@Override
	public String getJEITooltip() {
		return null;
	}
}
