package dohyun22.jst3.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import cofh.api.block.IDismantleable;
//import cofh.api.block.IDismantleable;
import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTPotions;
import dohyun22.jst3.utils.JSTSounds;
import dohyun22.jst3.utils.JSTUtils;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Optional.InterfaceList({
@Optional.Interface(iface = "cofh.api.block.IDismantleable", modid = "cofhapi"),
@Optional.Interface(iface="ic2.api.tile.IWrenchable", modid="ic2")
})
public class BlockJST1 extends BlockBase implements IDismantleable, IWrenchable {

	public BlockJST1() {
		super("blockjst1", MaterialJST.INSTANCE);
		setHardness(1.0F);
		setResistance(1.0F);
		boolean[] disabledMetas = new boolean[] {true, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false};
		for (int i = 0; i < disabledMetas.length; i++) {
			if (disabledMetas[i]) continue;
			allowedMetas.add(i);
		}
	}

	@Override
	public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion x) {
		switch (getMeta(w, p)) {
		case 1:
			return 10.0F;
		case 2:
		case 3:
		case 4:
			return 5.0F;
		case 5:
		case 6:
			return 500.0F;
		case 7:
			return 36000000.0F;
		case 8:
			return 0.8F;
		case 12:
			return 150000000000.0F;
		default:
			return this.blockResistance;
		}
	}

	@Override
	public float getBlockHardness(IBlockState s, World w, BlockPos p) {
		switch (getMeta(s)) {
		case 1:
			return 2.0F;
		case 2:
		case 3:
		case 4:
			return 2.5F;
		case 5:
			return 10.0F;
		case 6:
			return 5.0F;
		case 7:
		case 12:
			return -1.0F;
		case 8:
			return 0.8F;
		default:
			return this.blockResistance;
		}
	}

	@Override
	public int getLightValue(IBlockState s, IBlockAccess w, BlockPos p) {
		int m = getMeta(s);
		if (m == 5)
			return 15;
		if (m == 6)
			return 7;
		if (m == 7)
			return 3;
		return 0;
	}

	@Override
	public int getFlammability(IBlockAccess w, BlockPos p, EnumFacing f) {
		int m = getMeta(w, p);
		return m == 13 || m == 14 ? 30 : 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess w, BlockPos p, EnumFacing f) {
		int m = getMeta(w, p);
		return m == 13 || m == 14 ? 15 : 0;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess w, BlockPos p, BlockPos b) {
		int m = getMeta(w, p);
		return m == 5 || (m <= 7 && m >= 12);
	}

	@Override
	public boolean canEntityDestroy(IBlockState s, IBlockAccess w, BlockPos p, Entity e) {
		int m = getMeta(s);
		if (m == 5 || m == 6 || m == 12) {
			return false;
		}
		return super.canEntityDestroy(s, w, p, e);
	}

	@Override
	public void onBlockExploded(World w, BlockPos p, Explosion va_5) {
		int m = getMeta(w, p);
		if (m != 12 && m != 7) {
			super.onBlockExploded(w, p, va_5);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess w, BlockPos p, IBlockState s, int f) {
		List arr = new ArrayList();
		int m = getMeta(s);
		switch (m) {
		case 12:
			break;
		case 15:
			arr.add(new ItemStack(Items.FLINT, new Random().nextInt(2) + 3));
			break;
		default:
			arr.add(new ItemStack(this, 1, m));
		}
		return arr;
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos p, IBlockState s, EntityPlayer pl, EnumHand h, EnumFacing sd, float hx, float hy, float hz) {
		int m = getMeta(s);
		if (w.isRemote) {
			if (!pl.isSneaking() && m == 6 && sd == EnumFacing.UP) {
				return true;
			} else {
				return false;
			}
		} else {
			if (!pl.isSneaking() && m == 6 && sd == EnumFacing.UP) {
		        if (w.provider.getDimension() == 0 && !pl.isRiding() && !pl.isBeingRidden()) {
		        	w.playSound((EntityPlayer)null, p, JSTSounds.TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		        	pl.changeDimension(1);
		        }
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState s, World w, BlockPos p, Random r) {
    	int m = getMeta(s);
    	if (m == 6) {
            if (r.nextInt(100) == 0)
            	w.playSound(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, r.nextFloat() * 0.4F + 0.8F, false);
            double d0 = (double)((float)p.getX() + r.nextFloat());
            double d1 = (double)((float)p.getY() + 1.0F);
            double d2 = (double)((float)p.getZ() + r.nextFloat());
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            d3 = ((double)r.nextFloat() - 0.5D) * 0.5D;
            d4 = ((double)r.nextFloat() - 0.5D) * 0.5D;
            d5 = ((double)r.nextFloat() - 0.5D) * 0.5D;
            w.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5, new int[0]);
    	} else if (m == 7) {
    		if (r.nextInt(100) == 0)
    			w.playSound(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D, JSTSounds.GEIGER, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
    		if (r.nextInt(2) == 0)
    			w.spawnParticle(EnumParticleTypes.REDSTONE, p.getX() + r.nextFloat(), p.getY() + 0.1D + r.nextFloat() * 0.8D, p.getZ() + r.nextFloat(), 0.3D, 1.0D, 0.5D);
    	}
	}
	
	@Override
    public MapColor getMapColor(IBlockState s, IBlockAccess w, BlockPos p) {
		int m = getMeta(s);
		switch (m) {
		case 1:
			return MapColor.QUARTZ;
		case 2:
			return MapColor.PINK;
		case 3:
			return MapColor.GRAY;
		case 4:
			return MapColor.LAPIS;
		case 5:
			return MapColor.LIME;
		case 6:
			return MapColor.BLACK;
		case 7:
			return MapColor.LIME;
		case 8:
			return MapColor.SAND;
		case 11:
			return MapColor.CYAN;
		case 12:
			return MapColor.SNOW;
		case 13:
			return MapColor.BROWN;
		case 14:
			return MapColor.WOOD;
		case 15:
			return MapColor.STONE;
		}
		return MapColor.BLUE;
    }
	
	@Override
    public String getHarvestTool(IBlockState s) {
        switch (getMeta(s)) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 12:
        case 15:
        	return "pickaxe";
        case 13:
        case 14:
        	return "axe";
        }
        return null;
    }
	
	@Override
    public int getHarvestLevel(IBlockState s) {
        switch (getMeta(s)) {
        case 5:
        case 6:
        	return 3;
        case 12:
        	return 100;
        case 13:
        case 14:
        	return -1;
        }
        return 0;
    }
	
	@Override
    public boolean canHarvestBlock(IBlockAccess w, BlockPos p, EntityPlayer pl) {
		int m = getMeta(w, p);
		return (m == 13 || m == 14 || m == 15) ? true : JSTUtils.canHarvest(this, pl, w.getBlockState(p));
    }
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState bs, EntityPlayer pl, World w, BlockPos p) {
	    int m = getMeta(bs);
	    if (m == 13 || m == 14 || m == 15) {
	    	if (!JSTUtils.canHarvest(this, pl, bs))
	    		return 0.06666F;
	    }
	    if (m == 7) return 0.001F;
		return super.getPlayerRelativeBlockHardness(bs, pl, w, p);
	}
	
	@Override
    public int getLightOpacity(IBlockState bs, IBlockAccess w, BlockPos p) {
		int m = getMeta(bs);
		if (m == 7) return 0;
		return 255;
	}
	
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState bs) {
		int m = getMeta(bs);
		if (m == 7) return true;
		return false;
    }
	
	@Override
    public void onEntityCollidedWithBlock(World w, BlockPos p, IBlockState bs, Entity e) {
		int m = getMeta(bs);
		if (m == 7 && !w.isRemote && e instanceof EntityLivingBase && w.rand.nextInt(10) == 0) {
			((EntityLivingBase)e).addPotionEffect(new PotionEffect(JSTPotions.radioactivity, 200, 5));
		}
    }
	
	@Override
    public boolean isOpaqueCube(IBlockState bs) {
        return getMeta(bs) != 7;
    }
	
    @Override
    public boolean isFullCube(IBlockState bs) {
    	return getMeta(bs) != 7;
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState bs, IBlockAccess w, BlockPos p) {
		int m = getMeta(bs);
		if (m == 7) {
			float a_2 = 0.0625F;
			try {
				for (EnumFacing f : EnumFacing.VALUES) {
					if (w.isSideSolid(p.offset(f), f.getOpposite(), false)) {
						AxisAlignedBB ret = FULL_BLOCK_AABB;
						switch (f) {
						case DOWN: 
							ret = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, a_2, 1.0F); break;
						case UP: 
							ret = new AxisAlignedBB(0.0F, 1.0F - a_2, 0.0F, 1.0F, 1.0F, 1.0F); break;
						case NORTH: 
							ret = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, a_2); break;
						case SOUTH: 
							ret = new AxisAlignedBB(0.0F, 0.0F, 1.0F - a_2, 1.0F, 1.0F, 1.0F); break;
						case EAST: 
							ret = new AxisAlignedBB(0.0F, 0.0F, 0.0F, a_2, 1.0F, 1.0F); break;
						case WEST: 
							ret = new AxisAlignedBB(1.0F - a_2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
						}
						return ret;
					}
				}
			}
			catch (Throwable a_5) {}
			return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, a_2, 1.0F);
		}
		return FULL_BLOCK_AABB;
	}
	
    @Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState bs, IBlockAccess w, BlockPos p) {
		return getMeta(bs) == 7 ? NULL_AABB : FULL_BLOCK_AABB;
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState bs) {
    	int m = getMeta(bs);
    	if (m == 7)
    		return EnumBlockRenderType.INVISIBLE;
    	return super.getRenderType(bs);
    }
	
	@Override
    public boolean isPassable(IBlockAccess w, BlockPos p) {
    	int m = getMeta(w.getBlockState(p));
    	if (m == 7)
    		return true;
    	return super.isPassable(w, p);
    }
	
	@Override
	public boolean isSideSolid(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f) {
    	int m = getMeta(bs);
    	if (m == 7)
    		return false;
		return super.isSideSolid(bs, w, p, f);
	}

	@Override
	@Method(modid = "ic2")
	public EnumFacing getFacing(World w, BlockPos p) {
		return null;
	}

	@Override
	@Method(modid = "ic2")
	public boolean setFacing(World w, BlockPos pos, EnumFacing f, EntityPlayer pl) {
		return false;
	}

	@Override
	@Method(modid = "ic2")
	public boolean wrenchCanRemove(World w, BlockPos p, EntityPlayer pl) {
		int m = getMeta(w.getBlockState(p));
		return m == 1 || m == 2 || m == 3 || m == 4 || m == 6;
	}

	@Override
	@Method(modid = "ic2")
	public List<ItemStack> getWrenchDrops(World w, BlockPos p, IBlockState bs, TileEntity te, EntityPlayer pl, int fortune) {
	    ArrayList<ItemStack> ret = new ArrayList();
	    ret.addAll(getDrops(w, p, bs, 0));
	    return ret;
	}

	@Override
	@Optional.Method(modid = "cofhapi")
	public boolean canDismantle(World w, BlockPos p, IBlockState bs, EntityPlayer pl) {
		int m = getMeta(bs);
		return m == 1 || m == 2 || m == 3 || m == 4 || m == 6;
	}

	@Override
	@Optional.Method(modid = "cofhapi")
	public ArrayList<ItemStack> dismantleBlock(World w, BlockPos p, IBlockState bs, EntityPlayer pl, boolean drop) {
	    ArrayList<ItemStack> ret = new ArrayList();
	    ret.addAll(getDrops(w, p, bs, 0));
	    if (!drop) {
	    	for (ItemStack st : ret) JSTUtils.dropEntityItemInPos(w, p, st);
	    }
	    w.setBlockToAir(p);
	    return ret;
	}
}
