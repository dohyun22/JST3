package dohyun22.jst3.compat.ic2;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTPotions;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@net.minecraftforge.fml.common.Optional.Interface(iface="ic2.api.reactor.IReactorComponent", modid="ic2")
public class ItemReactorComponent extends Item implements IReactorComponent {
	public final LinkedHashMap<Integer, ReactorItemBehaviour> behaviours = new LinkedHashMap<Integer, ReactorItemBehaviour>();

	public ItemReactorComponent() {
		setRegistryName(JustServerTweak.MODID, "jstnuclear");
		setUnlocalizedName("jstnuclear");
		setCreativeTab(JustServerTweak.JSTTab);
		setNoRepair();
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack st) {
		return super.getUnlocalizedName(st) + "." + st.getItemDamage();
	}

	@Override
	@Method(modid = "ic2")
	public boolean canBePlacedIn(ItemStack st, IReactor re) {
		return getBehaviour(st).canBePlaced(st, re);
	}

	@Override
	@Method(modid = "ic2")
	public void processChamber(ItemStack st, IReactor re, int x, int y, boolean hr) {
		getBehaviour(st).processChamber(st, re, x, y, hr);
	}

	@Override
	@Method(modid = "ic2")
	public boolean acceptUraniumPulse(ItemStack st, IReactor re, ItemStack ps, int yX, int yY, int pX, int pY, boolean hr) {
		return getBehaviour(st).acceptPulse(st, re, ps, yX, yY, pX, pY, hr);
	}

	@Override
	@Method(modid = "ic2")
	public boolean canStoreHeat(ItemStack st, IReactor re, int x, int y) {
		return getBehaviour(st).canStoreHeat(st, re, x, y);
	}

	@Override
	@Method(modid = "ic2")
	public int getMaxHeat(ItemStack st, IReactor re, int x, int y) {
		return getBehaviour(st).getMaxHeat(st, re, x, y);
	}

	@Override
	@Method(modid = "ic2")
	public int getCurrentHeat(ItemStack st, IReactor re, int x, int y) {
		return getBehaviour(st).getCurrentHeat(st, re, x, y);
	}

	@Override
	@Method(modid = "ic2")
	public int alterHeat(ItemStack st, IReactor re, int x, int y, int h) {
		return getBehaviour(st).alterHeat(st, re, x, y, h);
	}

	@Override
	@Method(modid = "ic2")
	public float influenceExplosion(ItemStack st, IReactor re) {
		return getBehaviour(st).influenceExplosion(st, re);
	}
	
    @Override
    public void onUpdate(ItemStack st, World w, Entity e, int sl, boolean select) {
    	int i = getBehaviour(st).getRadiation(st);
		if (i > 0 && e instanceof EntityLivingBase)
			JSTPotions.setRadiation((EntityLivingBase) e, i * 30, i / 10, false, false);
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack st) {
        return getBehaviour(st).showDurability(st);
    }
    
    @Override
	public double getDurabilityForDisplay(ItemStack st) {
		return getBehaviour(st).getDisplayDamage(st);
	}
    
    @Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    	if (!isInCreativeTab(tab)) return;
		for (Integer i : behaviours.keySet()) subItems.add(new ItemStack(this, 1, i.intValue()));
	}
    
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack st, @Nullable World w, List<String> ls, ITooltipFlag adv) {
		getBehaviour(st).addInformation(st, w, ls, adv);
	}

    @Nonnull
    public ReactorItemBehaviour getBehaviour(ItemStack s) {
    	return getBehaviour(s == null || s.isEmpty() ? 0 : s.getItem().getDamage(s));
    }
    
    @Nonnull
    public ReactorItemBehaviour getBehaviour(int m) {
    	ReactorItemBehaviour b = behaviours.get(m);
    	if (b == null)
    		return ReactorItemBehaviour.DEFAULT;
    	return b;
    }
    
	public void registerBehaviour(int meta, ReactorItemBehaviour behaviour) {
		try {
			if (behaviour != null)
				behaviours.put(meta, behaviour);
		} catch (Exception e) {
			System.err.println("Cannot add #" + meta);
			throw e;
		}
	}
}
