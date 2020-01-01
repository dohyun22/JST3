package dohyun22.jst3.items.behaviours;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import dohyun22.jst3.items.ItemJST1;
import dohyun22.jst3.items.ItemMetaBase;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBehaviour {
	protected static final UUID DamageUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID SpeedUUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	
	public static final ItemBehaviour INSTANCE = new ItemBehaviour();
	protected long maxEnergy;
	protected int maxUse;

	/** Called when the block is right clicked with this item. */
	public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
		return EnumActionResult.PASS;
	}

	/** Called when the item is right clicked. */
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		return new ActionResult(EnumActionResult.PASS, st);
	}
	
	public EnumActionResult onUseFirst(ItemStack st, EntityPlayer ep, World w, BlockPos p, EnumFacing s, float hx, float hy, float hz, EnumHand h) {
		return EnumActionResult.PASS;
	}
	
    @Nullable
    public ItemStack onUseFinish(ItemStack st, World w, EntityLivingBase elb) {
        return st;
    }
    
    public void update(ItemStack st, World w, Entity e, int sl, boolean select) {
    }
    
    public void onCreate(ItemStack st, World w, EntityPlayer pl) {
    }
    
    public EnumAction useAction(ItemStack st) {
        return EnumAction.NONE;
    }
    
    public int harvestLevel(ItemStack st, String tc, @Nullable EntityPlayer pl, @Nullable IBlockState bs) {
    	return -1;
    }
    
    public boolean onLeftClickEntity(ItemStack st, EntityPlayer pl, Entity e) {
        return false;
    }
    
    public void onUsingTick(ItemStack st, EntityLivingBase pl, int cnt) {}

	public void onStopUsing(ItemStack st, World w, EntityLivingBase pl, int t) {}
    
    public int getItemStackLimit(ItemStack st) {
    	return 64;
    }
    
    public boolean canHarvestBlock(IBlockState bs, ItemStack st) {
        return false;
    }
    
    public float getDigSpeed(ItemStack st, IBlockState bs) {
    	return 1.0F;
    }
    
	public boolean hitEntity(ItemStack st, EntityLivingBase e1, EntityLivingBase e2) {
		return false;
	}
	
	public int getMaxItemUseDuration(ItemStack st) {
		return 0;
	}

	public void getInformation(ItemStack st, @Nullable World w, List<String> ls, boolean adv) {}

	public long getMaxEnergy(ItemStack st) {
		return maxEnergy;
	}

	public boolean canCharge(ItemStack st) {
		return false;
	}
	
	public boolean canDischarge(ItemStack st) {
		return false;
	}
	
	public int getTier(ItemStack st) {
		return Integer.MAX_VALUE;
	}
	
	public long transferLimit(ItemStack st) {
		return 0;
	}
	
	/** Determine whether an item can be stored in a toolbox or not. Does nothing when IC2 is not loaded */
	public boolean canBeStoredInToolbox(ItemStack st) {
		return false;
	}
	
    public void doDamage(@Nonnull ItemStack st, @Nullable EntityLivingBase el) {
    	doDamage(st, 1, el);
    }
    
    public void doDamage(@Nonnull ItemStack st, int amt, @Nullable EntityLivingBase el) {
    	if (this.maxUse <= 0 || amt <= 0 || (el instanceof EntityPlayer && ((EntityPlayer)el).isCreative()))
    		return;
		if (el != null) {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, st);
			int j = 0;
			for (int k = 0; i > 0 && k < amt; ++k)
				if (EnchantmentDurability.negateDamage(st, i, el.getRNG()))
					j++;
			amt -= j;
			if (amt <= 0) return;
		}
    	NBTTagCompound tag = JSTUtils.getOrCreateNBT(st);
    	int d = tag.getInteger("damage") + amt;
    	tag.setInteger("damage", d);
    	if (d >= this.maxUse) this.onBreak(st, el);
    }
	
	public boolean showDurability(ItemStack st) {
		NBTTagCompound tag = st.getTagCompound();
		if (tag == null) return false;
		if (this.maxEnergy > 0) {
			return tag.getLong("energy") > 0;
		}
		if (this.maxUse > 0) {
			return tag.getInteger("damage") > 0;
		}
		return false;
	}
	
	public double getDisplayDamage(ItemStack st) {
		if (!st.hasTagCompound()) return 1.0D;
		if (maxEnergy > 0)
			return 1.0D - (double)st.getTagCompound().getLong("energy") / (double)maxEnergy;
		if (maxUse > 0)
			return (double)st.getTagCompound().getInteger("damage") / (double)maxUse;
		return 1.0D;
	}
	
	public int getRGBDamage(ItemStack st) {
		if (maxEnergy > 0)
	        return 0x64C8FF;
		else
			return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDisplayDamage(st))) / 3.0F, 1.0F, 1.0F);
	}
    
    public void onBreak(ItemStack st, @Nullable EntityLivingBase el) {
    	if (el != null)
    		el.renderBrokenItemStack(st);
    	if (st.getCount() > 0)
    		st.shrink(1);
	}
    
    public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
        return true;
    }
    
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return null;
	}

    /** Adds creative Tab sub-items. useful for adding items with NBT tag. 
     * All parameters MUST NOT be null.
     * */
	public void getSubItems(@Nonnull ItemStack st, @Nonnull List<ItemStack> sub) {
		sub.add(st);
	}

    /**
     * Called before a block is broken. Also called when the player is hitting unbreakable blocks.<br>
     * return true to prevent normal block harvesting.
     */
	public boolean onBlockStartBreak(ItemStack st, BlockPos p, EntityPlayer ep) {
		return false;
	}

	@Nullable
	public Object getServerGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return null;
	}

	@Nullable
	public Object getClientGUI(int id, EntityPlayer pl, World w, BlockPos p) {
		return null;
	}

	public byte shouldReequip(ItemStack os, ItemStack ns, boolean sc) {
		return -1;
	}

	public boolean hasContainerItem(ItemStack st) {
		return false;
	}

	public ItemStack getContainerItem(ItemStack st) {
		if (st.isEmpty()) return ItemStack.EMPTY;
		return st;
	}

	public void chargeFromArmor(ItemStack st, EntityLivingBase e) {
		if (e == null) return;
		boolean flag = false;
		for (EntityEquipmentSlot sl : EntityEquipmentSlot.values()) {
			if (sl.getSlotType() != EntityEquipmentSlot.Type.ARMOR)
				continue;
			ItemStack src = e.getItemStackFromSlot(sl);
			if (src != null && !src.isEmpty()) {
				long eu = JSTUtils.dischargeItem(src, Math.max(0, maxEnergy - getEnergy(st)), getTier(st), true, false);
				if (eu > 0) {
					flag = true;
					setEnergy(st, Math.min(maxEnergy, getEnergy(st) + eu));
				}
			}
		}
		if (!JSTUtils.isClient() && flag && e instanceof EntityPlayer) {
			((EntityPlayer) e).openContainer.detectAndSendChanges();
		}
	}

	public boolean isWrench(ItemStack st) {
		return false;
	}

	public void onWrenchUsed(ItemStack st, EntityLivingBase el) {}

	public boolean isScrewdriver(ItemStack st) {
		return false;
	}

	public void onScrewdriverUsed(ItemStack st, EntityLivingBase el) {}

	public boolean isCrowbar(ItemStack st) {
		return false;
	}

	public void onCrowbarUsed(ItemStack st, EntityLivingBase el) {}

	public boolean isGrafter(ItemStack st) {
		return false;
	}

	public void onHitBlock(ItemStack st, BlockPos p, EntityPlayer ep) {
	}

	public void addToolClasses(ItemStack st, Set<String> list) {
	}
	
    public boolean isEnchantable(ItemStack st) {
        return this.getItemStackLimit(st) == 1 && this.getEnchantability(st) > 0;
    }
	
	public int getEnchantability(ItemStack st) {
		return 0;
	}

	public boolean canEnchant(ItemStack st, Enchantment en) {
		return false;
	}
	
	public boolean interactEntity(ItemStack st, EntityPlayer pl, EntityLivingBase elb, EnumHand h) {
		return false;
	}
	
	public boolean onEntityItemUpdate(EntityItem ei) {
		return false;
	}

	public boolean canDestroyBlockInCreative(World w, BlockPos p, ItemStack st, EntityPlayer pl) {
		return true;
	}

	public boolean doesSneakBypassUse(ItemStack st, IBlockAccess w, BlockPos p, EntityPlayer pl) {
		return false;
	}

	public long charge(ItemStack st, long amt, int tier, boolean itl, boolean sim) {
		if (this.getMaxEnergy(st) <= 0 || this.getTier(st) > tier || st.getCount() != 1 || !this.canCharge(st)) return 0;
		NBTTagCompound tg = JSTUtils.getOrCreateNBT(st);
		long eng = tg.getLong("energy");
		long chg = Math.min(getMaxEnergy(st), JSTUtils.safeAddLong(eng, (itl ? amt : Math.min(this.transferLimit(st), amt))));
	    if (!sim) tg.setLong("energy", chg);
		return chg - eng;
	}
	
	public long discharge(ItemStack st, long amt, int tier, boolean itl, boolean sim) {
		if (this.getMaxEnergy(st) <= 0 || this.getTier(st) > tier || !this.canDischarge(st) || !st.hasTagCompound()) return 0;
		NBTTagCompound tg = st.getTagCompound();
		long eng = tg.getLong("energy");
		long dis = Math.max(0, eng - (itl ? amt : Math.min(this.transferLimit(st), amt)));
		if (!sim) {
			if (dis <= 0) {
				tg.removeTag("energy");
	    		if (tg.hasNoTags())
	    			st.setTagCompound(null);
			} else {
				tg.setLong("energy", dis);
			}
		}
		return eng - dis;
	}
	
	public long getEnergy(ItemStack st) {
		if (getMaxEnergy(st) <= 0) return 0;
		return st.hasTagCompound() ? st.getTagCompound().getLong("energy") : 0L;
	}
	
	public void setEnergy(ItemStack st, long e) {
		NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
		nbt.setLong("energy", Math.max(0, e));
	}

	public boolean useEnergy(ItemStack st, long use, @Nullable EntityLivingBase en, boolean sim) {
		long e = getEnergy(st);
		if (e >= use) {
			if (!sim) {
				setEnergy(st, e - use);
				if (en != null && canCharge(st)) chargeFromArmor(st, en);
			}
			return true;
		}
		return false;
	}

	public boolean useEnergy(ItemStack st, long use, boolean sim) {
		return useEnergy(st, use, null, sim);
	}

	public boolean hasCapability(Capability<?> cap, ItemStack st) {
		if (cap == CapabilityEnergy.ENERGY && (this.canCharge(st) || this.canDischarge(st)))
			return true;
		return this.getCapability(cap, st) != null;
	}

	@Nullable
	public <T> T getCapability(Capability<T> cap, ItemStack st) {
		if (cap == CapabilityEnergy.ENERGY && (this.canCharge(st) || this.canDischarge(st))) return (T) new InternalStroage(st);
		return null;
	}

	public int getBurnTime(ItemStack st) {
		return 0;
	}

	protected static void addFluidTip(@Nonnull ItemStack s, @Nullable List<String> l) {
		if (l == null) return;
		IFluidHandlerItem fh = FluidUtil.getFluidHandler(s);
		if (fh != null) {
			IFluidTankProperties[] tank = fh.getTankProperties();
			if (tank != null && tank.length > 0) {
				FluidStack fs = tank[0].getContents();
				l.add(JSTUtils.formatNum(fs == null ? 0 : fs.amount) + " / " + JSTUtils.formatNum(tank[0].getCapacity()) + "mB " + (fs == null ? "" : fs.getFluid().getLocalizedName(fs)));
			}
		}
	}

	protected static void addEnergyTip(@Nonnull ItemStack s, @Nullable List<String> l) {
		if (l == null) return;
		ItemBehaviour b = JSTItems.item1.getBehaviour(s);
		long e = b.getEnergy(s);
		l.add(I18n.format("jst.tooltip.energy.eu", JSTUtils.formatNum(e), JSTUtils.formatNum(b.maxEnergy)));
		BigInteger bi = BigInteger.valueOf(JSTCfg.RFPerEU);
		l.add(I18n.format("jst.tooltip.energy.rf", JSTUtils.formatNum(BigInteger.valueOf(e).multiply(bi)), JSTUtils.formatNum(BigInteger.valueOf(b.maxEnergy).multiply(bi))));
	}

	protected class InternalStroage implements net.minecraftforge.energy.IEnergyStorage {
		private final ItemStack st;
		
		public InternalStroage(ItemStack st) {
			this.st = st;
		}
		
		@Override
		public int receiveEnergy(int inp, boolean sim) {
			return JSTUtils.convLongToInt(ItemBehaviour.this.charge(st, inp / JSTCfg.RFPerEU, Integer.MAX_VALUE, false, sim) * JSTCfg.RFPerEU);
		}

		@Override
		public int extractEnergy(int out, boolean sim) {
			return JSTUtils.convLongToInt(ItemBehaviour.this.discharge(st, out / JSTCfg.RFPerEU, Integer.MAX_VALUE, false, sim) * JSTCfg.RFPerEU);
		}

		@Override
		public int getEnergyStored() {
			return JSTUtils.convLongToInt(JSTUtils.safeMultiplyLong(ItemBehaviour.this.getEnergy(st), JSTCfg.RFPerEU));
		}

		@Override
		public int getMaxEnergyStored() {
			return JSTUtils.convLongToInt(JSTUtils.safeMultiplyLong(ItemBehaviour.this.getMaxEnergy(st), JSTCfg.RFPerEU));
		}

		@Override
		public boolean canExtract() {
			return ItemBehaviour.this.canDischarge(st);
		}

		@Override
		public boolean canReceive() {
			return ItemBehaviour.this.canCharge(st);
		}
	}
	
	//Client-Only Methods
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@Nonnull ItemStack st) {
        return st.isItemEnchanted();
    }
}
