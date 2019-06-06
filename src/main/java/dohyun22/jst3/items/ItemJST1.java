package dohyun22.jst3.items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;

import cofh.api.item.IToolHammer;
import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.api.IItemJEU;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.utils.JSTUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

@Optional.InterfaceList({
	@Optional.Interface(iface="ic2.api.item.ISpecialElectricItem", modid="ic2"),
	@Optional.Interface(iface="ic2.api.item.IElectricItemManager", modid="ic2"),
	@Optional.Interface(iface="ic2.api.item.IBoxable", modid="ic2"),
	@Optional.Interface(iface="cofh.api.item.IToolHammer", modid="cofhapi")
})
public class ItemJST1 extends ItemMetaBase implements IItemJEU, ISpecialElectricItem, IElectricItemManager, IBoxable, IToolHammer {

	public ItemJST1() {
		setRegistryName(JustServerTweak.MODID, "itemjst1");
		setUnlocalizedName("itemjst1");
		setCreativeTab(JustServerTweak.JSTTab);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}
	
	@Override
    public EnumActionResult onItemUse(EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
		ItemStack st = pl.getHeldItem(h);
		try {
			return getBehaviour(st).onRightClickBlock(st, pl, w, p, h, f, hx, hy, hz);
		} catch (Throwable t) {
			return EnumActionResult.PASS;
		}
    }

	@Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer pl, EnumHand h) {
		ItemStack st = pl.getHeldItem(h);
		ItemBehaviour b = getBehaviour(st);
		try {
			return b.onRightClick(st, w, pl, h);
		} catch (Throwable t) {
			return new ActionResult(EnumActionResult.PASS, st);
		}
    }
    
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer pl, World w, BlockPos p, EnumFacing f, float hX, float hY, float hZ, EnumHand h) {
		ItemStack st = pl.getHeldItem(h);
		ItemBehaviour b = getBehaviour(st);
		try {
			return b.onUseFirst(st, pl, w, p, f, hX, hY, hZ, h);
		} catch (Throwable t) {
			return EnumActionResult.PASS;
		}
	}
	
	@Override
    public boolean onLeftClickEntity(ItemStack st, EntityPlayer p, Entity e) {
		try {
			return getBehaviour(st).onLeftClickEntity(st, p, e);
		} catch (Throwable t) {
			return false;
		}
    }
	
    @Nullable
    @Override
    public ItemStack onItemUseFinish(ItemStack st, World w, EntityLivingBase e) {
        return getBehaviour(st).onUseFinish(st, w, e);
    }
    
    @Override
    public void onUpdate(ItemStack st, World w, Entity e, int sl, boolean select) {
    	try {
    		getBehaviour(st).update(st, w, e, sl, select);
    	} catch (Exception ex) {}
    }
    
    @Override
    public void onCreated(ItemStack st, World w, EntityPlayer pl) {
    	getBehaviour(st).onCreate(st, w, pl);
    }
	
    @Override
    public EnumAction getItemUseAction(ItemStack st) {
        return getBehaviour(st).useAction(st);
    }
    
    @Override
    public int getHarvestLevel(ItemStack st, String tc, @Nullable EntityPlayer pl, @Nullable IBlockState bs) {
    	return getBehaviour(st).harvestLevel(st, tc, pl, bs);
    }
    
    @Override
    public int getItemStackLimit(ItemStack st) {
    	return getBehaviour(st).getItemStackLimit(st);
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack st, @Nullable World w, List<String> ls, ITooltipFlag adv) {
		List str = getBehaviour(st).getInformation(st, w, adv);
		if (str == null)
			return;
		ls.addAll(str);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack st) {
        return getBehaviour(st).hasEffect(st);
    }
    
    @Override
    public void onUsingTick(ItemStack st, EntityLivingBase pl, int cnt) {
    	getBehaviour(st).onUsingTick(st, pl, cnt);
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
	public int getRGBDurabilityForDisplay(ItemStack st) {
		return getBehaviour(st).getRGBDamage(st);
	}
    
	@Override
    public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
        return getBehaviour(st).onBlockDestroyed(st, w, bs, p, el);
    }
	
	@Override
    public boolean canHarvestBlock(@Nonnull IBlockState bs, ItemStack st) {
        return getBehaviour(st).canHarvestBlock(bs, st);
    }
	
	@Override
	public float getDestroySpeed(ItemStack st, IBlockState bs) {
		return getBehaviour(st).getDigSpeed(st, bs);
	}
	
	@Override
	public boolean hitEntity(ItemStack st, EntityLivingBase e1, EntityLivingBase e2) {
		return getBehaviour(st).hitEntity(st, e1, e2);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack st) {
		return getBehaviour(st).getMaxItemUseDuration(st);
	}
    
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		Multimap map = getBehaviour(st).getAttributeModifiers(sl, st);
		if (map == null)
			return super.getAttributeModifiers(sl, st);
		return map;
	}
	
	@Override
    public boolean onBlockStartBreak(ItemStack st, BlockPos p, EntityPlayer ep) {
		return getBehaviour(st).onBlockStartBreak(st, p, ep);
    }
	
	@Override
    public java.util.Set<String> getToolClasses(ItemStack st) {
		Set<String> ret = new HashSet<String>();
		getBehaviour(st).addToolClasses(st, ret);
		return ret;
    }
	
	@Override
	public boolean hasContainerItem(ItemStack st) {
		return getBehaviour(st).hasContainerItem(st);
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack st) {
		return getBehaviour(st).getContainerItem(st);
	}
	
	@Override
    public boolean isEnchantable(ItemStack st) {
        return getBehaviour(st).isEnchantable(st);
    }
	
	@Override
    public int getItemEnchantability(ItemStack st) {
        return getBehaviour(st).getEnchantability(st);
    }
	
	@Override
    public boolean canApplyAtEnchantingTable(ItemStack st, Enchantment en) {
		return getBehaviour(st).canEnchant(st, en);
    }
	
	@Override
    public boolean getIsRepairable(ItemStack st, ItemStack st2) {
        return false;
    }
	
	@Override
    public boolean itemInteractionForEntity(ItemStack st, EntityPlayer pl, EntityLivingBase elb, EnumHand h) {
		st = pl.getHeldItem(h);
		return getBehaviour(st).interactEntity(st, pl, elb, h);
    }
	
	@Override
	public boolean onEntityItemUpdate(EntityItem ei) {
		if (ei == null) return false;
		return getBehaviour(ei.getItem()).onEntityItemUpdate(ei);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack st, World w, EntityLivingBase pl, int t) {
		getBehaviour(st).onStopUsing(st, w, pl, t);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack os, ItemStack ns, boolean sc) {
		byte n = getBehaviour(os).shouldReequip(os, ns, sc);
		if (n == 0) return false;
		if (n == 1) return true;
		return super.shouldCauseReequipAnimation(os, ns, sc);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack st, @Nullable NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			final ItemBehaviour ib = getBehaviour(st);

			@Override
			public boolean hasCapability(Capability<?> cap, EnumFacing f) {
				return ib.hasCapability(cap, st);
			}

			@Override
			public <T> T getCapability(Capability<T> cap, EnumFacing f) {
				return ib.getCapability(cap, st);
			}
		};
	}
	
	//EU
	@Override
	@Method(modid = "ic2")
	public IElectricItemManager getManager(ItemStack st) {
		return this;
	}
	
	@Override
	@Method(modid = "ic2")
	public double charge(ItemStack st, double amt, int tier, boolean itl, boolean sim) {
		return getBehaviour(st).charge(st, (long)amt, tier, itl, sim);
	}

	@Override
	@Method(modid = "ic2")
	public double discharge(ItemStack st, double amt, int tier, boolean itl, boolean ext, boolean sim) {
		return getBehaviour(st).discharge(st, (long)amt, tier, itl, sim);
	}

	@Override
	@Method(modid = "ic2")
	public double getCharge(ItemStack st) {
		return getBehaviour(st).getEnergy(st);
	}

	@Override
	@Method(modid = "ic2")
	public double getMaxCharge(ItemStack st) {
		return getBehaviour(st).getMaxEnergy(st);
	}

	@Override
	@Method(modid = "ic2")
	public boolean canUse(ItemStack st, double amt) {
		ItemBehaviour bh = getBehaviour(st);
		return bh.getEnergy(st) >= amt;
	}

	@Override
	@Method(modid = "ic2")
	public boolean use(ItemStack st, double eu, EntityLivingBase e) {
		if (e != null) {
			this.chargeFromArmor(st, e);
		}
		double tr = this.discharge(st, eu, Integer.MAX_VALUE, true, false, true);
		if (Math.abs(tr - eu) < 1.0E-5D) {
			this.discharge(st, eu, Integer.MAX_VALUE, true, false, false);
			if (e != null) {
				this.chargeFromArmor(st, e);
			}
			return true;
		}
		return false;
	}

	@Override
	@Method(modid = "ic2")
	public void chargeFromArmor(ItemStack st, EntityLivingBase e) {
		getBehaviour(st).chargeFromArmor(st, e);
	}

	@Override
	@Method(modid = "ic2")
	public String getToolTip(ItemStack st) {
		return null;
	}

	@Override
	@Method(modid = "ic2")
	public int getTier(ItemStack st) {
		return getBehaviour(st).getTier(st);
	}

	@Override
	@Method(modid = "ic2")
	public boolean canBeStoredInToolbox(ItemStack st) {
		return getBehaviour(st).canBeStoredInToolbox(st);
	}

	@Override
	@Method(modid = "cofhapi")
	public boolean isUsable(ItemStack st, EntityLivingBase el, BlockPos p) {
		return getBehaviour(st).isWrench();
	}

	@Override
	@Method(modid = "cofhapi")
	public boolean isUsable(ItemStack st, EntityLivingBase el, Entity e) {
		return getBehaviour(st).isWrench();
	}

	@Override
	@Method(modid = "cofhapi")
	public void toolUsed(ItemStack st, EntityLivingBase el, BlockPos p) {
		getBehaviour(st).onWrenchUsed(st, el);
	}

	@Override
	@Method(modid = "cofhapi")
	public void toolUsed(ItemStack st, EntityLivingBase el, Entity e) {
		getBehaviour(st).onWrenchUsed(st, el);
	}

	@Override
	public long injectEU(ItemStack st, long jeu, int tier, boolean itl, boolean sim) {
		return getBehaviour(st).charge(st, jeu, tier, itl, sim);
	}

	@Override
	public long extractEU(ItemStack st, long jeu, int tier, boolean itl, boolean sim) {
		return getBehaviour(st).discharge(st, jeu, tier, itl, sim);
	}

	@Override
	public long getEU(ItemStack st) {
		return getBehaviour(st).getEnergy(st);
	}

	@Override
	public long getMaxEU(ItemStack st) {
		return getBehaviour(st).getMaxEnergy(st);
	}
}
