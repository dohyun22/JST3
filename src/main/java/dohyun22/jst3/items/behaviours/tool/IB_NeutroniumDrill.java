package dohyun22.jst3.items.behaviours.tool;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.blocks.JSTBlocks;
import dohyun22.jst3.items.JSTItems;
import dohyun22.jst3.items.behaviours.ItemBehaviour;
import dohyun22.jst3.loader.JSTCfg;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IB_NeutroniumDrill extends ItemBehaviour {
	//0 = bedrock, 1 = ore, 2 = block
	private static HashMap<String, Byte> whitelist = null;
	private static final int hardBlockMiningEnergy = 50000;

	public IB_NeutroniumDrill() {
		maxEnergy = 20000000;
		if (whitelist == null) {
			whitelist = new HashMap();
			addAllowedBlock("minecraft:bedrock", 0);
			addAllowedBlock(JustServerTweak.MODID + ":blockjst1", 2);
			addAllowedBlock(JustServerTweak.MODID + ":blockore1", 1);
		}
	}
	
	@Override
	public int getTier(ItemStack st) {
		return 4;
	}
	
	@Override
	public boolean canCharge(ItemStack st) {
		return true;
	}
	
	@Override
	public boolean canDischarge(ItemStack st) {
		return false;
	}
	
	@Override
	public long transferLimit(ItemStack st) {
		return 32768L;
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockState bs, ItemStack st) {
		if (getEnergy(st) < 500) return false;
		if (bs == null) return true;
		Material mat = bs.getMaterial();
		return mat == Material.IRON || mat == Material.ROCK || mat == Material.ANVIL || mat == Material.SNOW || "pickaxe".equals(bs.getBlock().getHarvestTool(bs)) || "shovel".equals(bs.getBlock().getHarvestTool(bs));
	}
	
	@Override
	public int harvestLevel(ItemStack st, String tc, @Nullable EntityPlayer pl, @Nullable IBlockState bs) {
		return this.canHarvestBlock(bs, st) ? 100 : -1;
	}
	
	@Override
	public float getDigSpeed(ItemStack st, IBlockState bs) {
		return this.canHarvestBlock(bs, st) ? 0.0F : 1.0F;
	}
	
	@Override
	public void getInformation(ItemStack st, World w, List<String> ls, boolean adv) {
		addEnergyTip(st, ls);
		int m = st.hasTagCompound() ? st.getTagCompound().getInteger("mode") : 0;
		ls.add(I18n.format("jst.tooltip.neutdrill.mode" + m));
	}

	@Override
	public void getSubItems(ItemStack st, List<ItemStack> sub) {
		sub.add(st.copy());
		setEnergy(st, this.maxEnergy);
		sub.add(st);
	}
	
	@Override
	public void onHitBlock(ItemStack st, BlockPos p, EntityPlayer ep) {
		World w = ep.world;
		if (!w.isRemote && !ep.capabilities.isCreativeMode) {
			IBlockState bs = w.getBlockState(p);
			long e = getEnergy(st);
			int use = 0;
			if (bs.getBlockHardness(w, p) < 0) {
				if (e >= hardBlockMiningEnergy && isMineableBlock(w, p.getY(), bs.getBlock()) && JSTUtils.canPlayerBreakThatBlock(ep, p)) {
					byte m = whitelist.get(Block.REGISTRY.getNameForObject(bs.getBlock()).toString());
					if (!isAllowedHeightOre(w, p.getY()) && m == 1) {
						JSTUtils.dropEntityItemInPos(w, p.up(), new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs)));
						w.setBlockState(p, Blocks.BEDROCK.getDefaultState());
					} else {
						JSTUtils.dropEntityItemInPos(w, p, m == 1 || m == 2 ? new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs)) : ep.world.rand.nextInt(10) == 0 ? new ItemStack(JSTItems.item1, 1, 20) : ItemStack.EMPTY);
						w.setBlockToAir(p);
					}
					w.playEvent(null, 2001, p, Block.getIdFromBlock(bs.getBlock()) + (bs.getBlock().getMetaFromState(bs) << 12));
					use = hardBlockMiningEnergy;
				} 
			} else if (bs.getBlockHardness(w, p) > 0 && this.canHarvestBlock(bs, st)) {
				int exp = JSTUtils.canPlayerBreakThatBlockExp(ep, p);
				if (exp >= 0) {
					use = 500;
					if (exp != 0) bs.getBlock().dropXpOnBlockBreak(w, p, exp);
					bs.getBlock().harvestBlock(w, ep, p, bs, w.getTileEntity(p), st);
					w.setBlockToAir(p);
					w.playEvent(null, 2001, p, Block.getIdFromBlock(bs.getBlock()) + (bs.getBlock().getMetaFromState(bs) << 12));
				}
			}
			if (use > 0) {
				setEnergy(st, e - use);
				chargeFromArmor(st, ep);
			}
		}
	}
	
	/**isMineableBlock
	 * @param va_1 World
	 * @param y Height, Y Coord
	 * @param bl Block
	 * @return true if Y1~4 in overworld or other mod's dimension, Y1~4/Y123~126 in nether,
	 * always true in The End.
	 * */
	protected boolean isMineableBlock(World w, int y, Block bl) {
		String str = Block.REGISTRY.getNameForObject(bl).toString();
		if (whitelist.containsKey(str)) {
			//System.out.println(whitelist.get(str));
			byte m = whitelist.get(str);
			if (m == 0) {
				return isAllowedHeight(w, y);
			} else if (m == 1 || m == 2) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	protected boolean isAllowedHeight(World w, int y) {
		switch (w.provider.getDimension()) {
		case -1:
			return (y > 0 && y < 5 || y > 122 && y < 127);
		case 0:
			return (y > 0 && y < 5);
		case 1:
			return true;
		default:
			return (y > 0 && y < 5);
		}
	}
	
	protected boolean isAllowedHeightOre(World w, int y) {
		switch (w.provider.getDimension()) {
		case -1:
			return (y > 0 && y != 127);
		case 0:
			return (y > 0);
		case 1:
			return true;
		default:
			return (y > 0);
		}
	}
	
	public static void addAllowedBlock(String s, int m) {
		if (s == null || m < 0 || m > 2 || whitelist.containsKey(s)) return;
		whitelist.put(s, (byte)m);
	}
	
	@Override
	public boolean canBeStoredInToolbox(ItemStack st) {
		return true;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) {
			return null;
		}

		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", 40.0D, 0));
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", 2.0D, 0));
	    return ret;
	}
	
	@Override
	public ActionResult<ItemStack> onRightClick(ItemStack st, World w, EntityPlayer pl, EnumHand h) {
		if (JSTUtils.isClient()) return new ActionResult(EnumActionResult.PASS, st);
		if (pl.isSneaking()) {
			NBTTagCompound nbt = JSTUtils.getOrCreateNBT(st);
			int m = (nbt.getByte("mode") + 1) % 3;
			nbt.setByte("mode", (byte) m);
			JSTUtils.sendMessage(pl, "jst.tooltip.neutdrill.mode" + m);
			switch (m) {
			case 1:
				JSTUtils.setEnchant(Enchantments.FORTUNE, 4, st);
				break;
			case 2:
				JSTUtils.setEnchant(Enchantments.SILK_TOUCH, 4, st);
				break;
			default:
				JSTUtils.setEnchant(null, 0, st);
			}
			return new ActionResult(EnumActionResult.SUCCESS, st);
		}
		return new ActionResult(EnumActionResult.PASS, st);
	}
	
	@Override
	public void addToolClasses(ItemStack st, Set<String> list) {
		if (getEnergy(st) > 500) {
			list.add("pickaxe");
			list.add("shovel");
		}
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@Nonnull ItemStack st) {
        return false;
    }
}
