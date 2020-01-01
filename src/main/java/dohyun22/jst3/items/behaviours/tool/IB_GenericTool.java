package dohyun22.jst3.items.behaviours.tool;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import dohyun22.jst3.items.behaviours.IB_Damageable;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.oredict.OreDictionary;

public class IB_GenericTool extends IB_Damageable {
	@Nonnull
	private final EnumToolType type;
	private final float dmg;
	private final int lvl;
	private final float speed;
	private final byte ench;

	public IB_GenericTool(int maxdmg, EnumToolType type, float dmg, int lvl, float speed, int ench) {
		super(maxdmg);
		this.type = type;
		this.dmg = dmg;
		this.lvl = lvl;
		this.speed = speed;
		this.ench = (byte)ench;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockState bs, ItemStack st) {
		if (bs == null) return true;
		Material mat = bs.getMaterial();
		String tool = bs.getBlock().getHarvestTool(bs);
		switch (type) {
		case AXE:
			return mat == Material.WOOD || "axe".equals(tool);
		case PICK:
			return mat == Material.IRON || mat == Material.ROCK || mat == Material.ANVIL || "pickaxe".equals(tool);
		case SHOVEL:
			return mat == Material.SNOW || "shovel".equals(tool);
		case SICKLE:
			return mat == Material.LEAVES || mat == Material.PLANTS || mat == Material.VINE;
		case SWORD:
			return mat == Material.WEB || mat == Material.PLANTS || mat == Material.VINE || mat == Material.CORAL || mat == Material.LEAVES || mat == Material.GOURD;
		case HOE:
		default:
		}
		return false;
	}
	
	@Override
	public void addToolClasses(ItemStack st, Set<String> list) {
		switch (type) {
		case AXE:
			list.add("axe");
			break;
		case PICK:
			list.add("pickaxe");
			break;
		case SHOVEL:
			list.add("shovel");
			break;
		case SICKLE:
			list.add("sickle");
			break;
		default:
		}
	}
	
	@Override
	public int harvestLevel(ItemStack st, String tc, @Nullable EntityPlayer pl, @Nullable IBlockState bs) {
		return this.canHarvestBlock(bs, st) ? lvl : -1;
	}
	
	@Override
	public float getDigSpeed(ItemStack st, IBlockState bs) {
		if (bs == null || st == null || st.isEmpty()) return 1.0F;
		if (type == EnumToolType.SWORD) {
			Material mat = bs.getMaterial();
			return mat == Material.WEB ? 15.0F : mat != Material.PLANTS && mat != Material.VINE && mat != Material.CORAL && mat != Material.LEAVES && mat != Material.GOURD ? 1.0F : 1.5F;
		}
		return canHarvestBlock(bs, st) ? speed : 1.0F;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack st, World w, IBlockState bs, BlockPos p, EntityLivingBase el) {
		if (bs == null) return true;
		if (type == EnumToolType.SICKLE) {
		    if (!(el instanceof EntityPlayer)) {
		        return false;
		    }
		    EntityPlayer pl = (EntityPlayer)el;
		    boolean flag = false;
		    if (bs != null && bs.getBlock().isLeaves(bs, w, p)) {
		    	for (int px = -1; px <= 1; px++) {
		    		for (int py = -1; py <= 1; py++) {
		    			for (int pz = -1; pz <= 1; pz++) {
		    				BlockPos p2 = p.add(px, py, pz);
		    				IBlockState bs2 = w.getBlockState(p2);
		    				Block bl = bs2.getBlock();
		    				if (bl.isLeaves(bs2, w, p) && JSTUtils.canPlayerBreakThatBlock(pl, p2)) {
		    					if (bl.canHarvestBlock(w, p2, pl)) {
		    						bl.harvestBlock(w, pl, p2, bs2, w.getTileEntity(p2), st);
		    					}
		    					w.setBlockToAir(p2);
		    					flag = true;
		    				}
		    			}
		    		}
		    	}
		    	if (flag) this.doDamage(st, el);
		    	return flag;
		    }
		    for (int px = -1; px <= 1; px++) {
		    	for (int pz = -1; pz <= 1; pz++) {
		    		BlockPos p2 = p.add(px, 0, pz);
		    		IBlockState bs2 = w.getBlockState(p2);
		    		if (!bs2.getBlock().isAir(bs2, w, p2)) {
		    			Block bl = bs2.getBlock();
						if (bl != Blocks.WATERLILY && bl instanceof IPlantable && JSTUtils.canPlayerBreakThatBlock(pl, p2)) {
	    					if (bl.canHarvestBlock(w, p2, pl)) {
	    						bl.harvestBlock(w, pl, p2, bs2, w.getTileEntity(p2), st);
	    					}
	    					w.setBlockToAir(p2);
		    				flag = true;
		    			}
		    		}
		    	}
		    }
			if (flag) this.doDamage(st, el);
			return flag;
		}
		if (bs.getBlockHardness(w, p) != 0.0F) {
			int d = 0;
			switch (type) {
			case SWORD:
				d = 2;
				break;
			case HOE:
				d = 0;
				break;
			default:
				d = 1;
			}
			this.doDamage(st, d, el);
		}
		return true;
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack st, EntityPlayer pl, Entity e) {
		int d;
		switch (type) {
		case SWORD:
			pl.spawnSweepParticles();
			d = 1;
			break;
		case HOE:
			d = 0;
			break;
		default:
			d = 2;
		}
		this.doDamage(st, d, pl);
		return super.onLeftClickEntity(st, pl, e);
	}
	
	@Override
	public int getItemStackLimit(ItemStack st) {
		return 1;
	}
	
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot sl, ItemStack st) {
		if (sl != EntityEquipmentSlot.MAINHAND) return null;
		float d = this.dmg;
		float s = 0.0F;
		switch (type) {
		case HOE:
			d = 1.0F;
			s = dmg - 3.0F;
			break;
		case PICK:
			s = -1.2F;
			break;
		case AXE:
		case SHOVEL:
			s = -1.0F;
			break;
		case SICKLE:
		case SWORD:
			s = -1.6F;
			break;
		default:
		}
		Multimap<String, AttributeModifier> ret = HashMultimap.create();
        ret.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(DamageUUID, "Weapon modifier", d, 0));
        ret.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SpeedUUID, "Weapon modifier", s, 0));
	    return ret;
	}
	
	@Override
    public EnumActionResult onRightClickBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, EnumHand h, EnumFacing f, float hx, float hy, float hz) {
    	if (!pl.canPlayerEdit(p.offset(f), f, st)) {
        	return EnumActionResult.FAIL;
        } else if (this.type == EnumToolType.HOE) {
            int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(st, pl, w, p);
            if (hook != 0) return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
            IBlockState iblockstate = w.getBlockState(p);
            Block block = iblockstate.getBlock();
            if (f != EnumFacing.DOWN && w.isAirBlock(p.up())) {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                    this.setBlock(st, pl, w, p, Blocks.FARMLAND.getDefaultState());
                    return EnumActionResult.SUCCESS;
                }

                if (block == Blocks.DIRT) {
                    switch ((BlockDirt.DirtType)iblockstate.getValue(BlockDirt.VARIANT)) {
                        case DIRT:
                            this.setBlock(st, pl, w, p, Blocks.FARMLAND.getDefaultState());
                            return EnumActionResult.SUCCESS;
                        case COARSE_DIRT:
                            this.setBlock(st, pl, w, p, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                            return EnumActionResult.SUCCESS;
                        default:
                        	break;
                    }
                }
            }
        } else if (this.type == EnumToolType.SHOVEL) {
            IBlockState iblockstate = w.getBlockState(p);
            Block block = iblockstate.getBlock();

            if (f != EnumFacing.DOWN && w.getBlockState(p.up()).getMaterial() == Material.AIR && block == Blocks.GRASS) {
                IBlockState iblockstate1 = Blocks.GRASS_PATH.getDefaultState();
                w.playSound(pl, p, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (!w.isRemote) {
                    w.setBlockState(p, iblockstate1, 11);
                    this.doDamage(st, pl);
                }

                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

	private void setBlock(ItemStack st, EntityPlayer pl, World w, BlockPos p, IBlockState bs) {
        w.playSound(pl, p, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!w.isRemote) {
            w.setBlockState(p, bs, 11);
            this.doDamage(st, pl);
        }
	}
	
	@Override
	public int getEnchantability(ItemStack st) {
		return type == EnumToolType.HOE ? 0 : this.ench;
	}

	@Override
	public boolean canEnchant(ItemStack st, Enchantment en) {
		switch(type) {
		case AXE:
		case PICK:
		case SHOVEL:
		case SICKLE:
			return en.type == EnumEnchantmentType.DIGGER;
		case SWORD:
			return en.type == EnumEnchantmentType.WEAPON;
		default:
			return false;
		}
	}

	@Override
	public boolean canDestroyBlockInCreative(World w, BlockPos p, ItemStack st, EntityPlayer pl) {
		return type != EnumToolType.SWORD;
	}
	
	public static enum EnumToolType {
		PICK, SHOVEL, AXE, HOE, SWORD, SICKLE;
	}
}
