package dohyun22.jst3.blocks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.api.block.IDismantleable;
import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.recipes.MRecipes;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.utils.JSTUtils;
import dohyun22.jst3.utils.UnlistedProperty;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Optional.InterfaceList({
@Optional.Interface(iface="ic2.api.tile.IWrenchable", modid="ic2"),
@Optional.Interface(iface="cofh.api.block.IDismantleable", modid="cofhapi")
})
public class BlockTileEntity extends Block implements ITileEntityProvider, IWrenchable, IDismantleable {
	public static final IUnlistedProperty<WeakReference<MetaTileBase>> TE = new UnlistedProperty("tile");
	private static ThreadLocal<TileEntityMeta> temp = new ThreadLocal();

	public BlockTileEntity() {
		super(Material.IRON);
		setUnlocalizedName("blockte");
		setRegistryName("blockte");
		setCreativeTab(JustServerTweak.JSTTab);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
	}

	@Override
    public int getLightOpacity(IBlockState bs, IBlockAccess w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			return mte.getLightOpacity();
        return 255;
    }

	@Override
    public int getLightValue(IBlockState bs, IBlockAccess w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			return mte.getLightValue();
        return 0;
    }

	@Override
	public float getBlockHardness(IBlockState bs, World w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			return mte.getHardness();
		return super.getBlockHardness(bs, w, p);
	}

	@Override
    public float getExplosionResistance(World w, BlockPos p, Entity ee, Explosion ex) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			return mte.getResistance(ee, ex);
        return super.getExplosionResistance(w, p, ee, ex);
    }

	@Override
	public boolean onBlockActivated(World w, BlockPos p, IBlockState bs, EntityPlayer pl, EnumHand h, EnumFacing d, float hX, float hY, float hZ) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		ItemStack st = pl.getHeldItem(h);
		for (ItemStack wr : MRecipes.Wrenches)
			if (OreDictionary.itemMatches(wr, st, false))
				return mte.setFacing(d, pl);
	    if (mte != null && (mte.canRightclickIfSneaking(pl, st, d, hX, hY, hZ) || !pl.isSneaking()))
			return mte.onRightclick(pl, st, d, hX, hY, hZ);
		return false;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public boolean hasTileEntity(IBlockState bs) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World w, IBlockState bs) {
		return createNewTileEntity(w, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		return new TileEntityMeta();
	}

	@Override
	public boolean canCreatureSpawn(@Nonnull IBlockState bs, @Nonnull IBlockAccess w, @Nonnull BlockPos p, EntityLiving.SpawnPlacementType t) {
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess w, BlockPos p, IBlockState s, int f) {
		ArrayList<ItemStack> ret = new ArrayList();
		TileEntity te = w.getTileEntity(p);
		if (te instanceof TileEntityMeta && ((TileEntityMeta)te).hasValidMTE()) {
			((TileEntityMeta)te).mte.getDrops(ret);
			return ret;
		}
		TileEntityMeta tem = ((TileEntityMeta)temp.get());
		if (tem != null && tem.hasValidMTE()) {
			tem.mte.getDrops(ret);
			return ret;
		}
		ret.add(new ItemStack(JSTBlocks.blockTile));
		return ret;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> ls, IBlockAccess w, BlockPos p, IBlockState s, int f) {
		ls.addAll(getDrops(w, p, s, f));
	}

	@Override
	public void breakBlock(World w, BlockPos p, IBlockState bs) {
		MetaTileBase.causeMTEUpdate(w, p);
		TileEntity te = w.getTileEntity(p);
		if (te instanceof TileEntityMeta) {
			TileEntityMeta tem = (TileEntityMeta) te;
			temp.set(tem);
			if (tem.hasValidMTE()) tem.mte.onBreak();
		}
		super.breakBlock(w, p, bs);
		w.removeTileEntity(p);
	}

	@Override
	public void onBlockAdded(World w, BlockPos p, IBlockState bs) {
		MetaTileBase.causeMTEUpdate(w, p);
	}

	@Override
	public ItemStack getPickBlock(IBlockState bs, RayTraceResult tgt, World w, BlockPos p, EntityPlayer pl) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) {
			ArrayList<ItemStack> ls = new ArrayList();
			mte.getDrops(ls);
			if (!ls.isEmpty())
				return (ItemStack) ls.get(0);
		}
		return new ItemStack(this, 1, 0);
	}

	@Override
	public IBlockState getExtendedState(IBlockState bs, IBlockAccess w, BlockPos p) {
		bs = super.getExtendedState(bs, w, p);
		if (FMLCommonHandler.instance().getSide().isClient() && bs instanceof IExtendedBlockState) {
			IExtendedBlockState ebs = (IExtendedBlockState) bs;
			MetaTileBase tile = MetaTileBase.getMTE(w, p);
			if (tile != null)
				ebs = ebs.withProperty(TE, new WeakReference(tile));
			bs = ebs;
		}
		return bs;
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] { TE });
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (Entry<Integer, MetaTileBase> n : MetaTileBase.tes.entrySet()) {
			Integer i = n.getKey();
			MetaTileBase mte = n.getValue();
			if (i == null || mte == null) continue;
			mte.getSubBlocks(n.getKey().intValue(), list);
		}
	}

	@Override
	public void neighborChanged(IBlockState s, World w, BlockPos p, Block b, BlockPos f) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			mte.onBlockUpdate();
	}

	@Override
	public void onNeighborChange(IBlockAccess w, BlockPos p, BlockPos n) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			mte.onBlockUpdate();
	}

	@Override
	public boolean isSideSolid(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing s) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			return mte.isSideSolid(s);
		return true;
	}

	@Override
    public boolean isOpaqueCube(IBlockState bs) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState bs, IBlockAccess w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			return mte.getBoundingBox();
		return FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState bs, IBlockAccess w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			return mte.getCollisionBoundingBox();
        return FULL_BLOCK_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null && mte.isSideOpaque(f) && !super.shouldSideBeRendered(bs, w, p, f))
	        return false;
	    return super.shouldSideBeRendered(bs, w, p, f);
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			return mte.isSideOpaque(f);
		return true;
    }

    @Override
    public boolean isFullCube(IBlockState bs) {
    	return false;
    }

    @Override
    public boolean eventReceived(IBlockState bs, World w, BlockPos p, int id, int arg) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			return mte.receiveClientEvent(id, arg);
    	return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState bs, World w, BlockPos p, AxisAlignedBB ab, List<AxisAlignedBB> ls, Entity e, boolean b) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) {
			mte.addCollisionBoxesToList(ab, ls, e);
			return;
		}
		super.addCollisionBoxToList(bs, w, p, ab, ls, e, false);
    }

    @Override
    public SoundType getSoundType(IBlockState bs, World w, BlockPos p, Entity e) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.getSoundType(e);
        return blockSoundType;
    }

    @Override
    public boolean canConnectRedstone(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.canConnectRedstone(f);
    	return false;
    }

    @Override
    public int getWeakPower(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.getWeakRSOutput(f.getOpposite());
    	return 0;
    }

    @Override
    public int getStrongPower(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.getStrongRSOutput(f.getOpposite());
    	return 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
    	return true;
    }

	@Override
	public void onEntityCollidedWithBlock(World w, BlockPos p, IBlockState bs, Entity e) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) mte.onEntityCollided(e);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

	@Override
    public boolean recolorBlock(World w, BlockPos p, EnumFacing f, EnumDyeColor col) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.recolorTE(f, col);
        return false;
    }

	@Override
	public boolean removedByPlayer(IBlockState bs, World w, BlockPos p, EntityPlayer pl, boolean wh) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null && !mte.removedByPlayer(pl, wh)) return false;
		return super.removedByPlayer(bs, w, p, pl, wh);
	}

	@Override
	public boolean canEntityDestroy(IBlockState s, IBlockAccess w, BlockPos p, Entity e) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.canEntityDestroy(e);
		return super.canEntityDestroy(s, w, p, e);
	}

	@Override
	public void onBlockExploded(World w, BlockPos p, Explosion ex) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte.onBlockExploded(ex)) super.onBlockExploded(w, p, ex);
	}

	@Override
	public MapColor getMapColor(IBlockState bs, IBlockAccess w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		MapColor ret = null;
		if (mte != null) ret = mte.getMapColor();
		return ret == null ? MapColor.BLUE : ret;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess w, IBlockState bs, BlockPos p, EnumFacing s) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null)
			return mte.isSideSolid(s) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
		return BlockFaceShape.UNDEFINED;
	}

	@Override
    public boolean hasComparatorInputOverride(IBlockState bs) {
        return true;
    }

	@Override
    public int getComparatorInputOverride(IBlockState bs, World w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			return mte.getComparatorInput();
        return 0;
    }

	//IC2 Wrench API
	@Override
	@Method(modid = "ic2")
	public EnumFacing getFacing(World w, BlockPos p) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.getFacing();
		return null;
	}

	@Override
	@Method(modid = "ic2")
	public boolean setFacing(World w, BlockPos p, EnumFacing nd, EntityPlayer pl) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.setFacing(nd, pl);
		return false;
	}

	@Override
	@Method(modid = "ic2")
	public boolean wrenchCanRemove(World w, BlockPos p, EntityPlayer pl) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
	    if (mte != null) return mte.wrenchCanRemove(pl);
		return false;
	}

	@Override
	@Method(modid = "ic2")
	public List<ItemStack> getWrenchDrops(World w, BlockPos p, IBlockState bs, TileEntity te, EntityPlayer pl, int f) {
		return getDrops(w, p, bs, f);
	}

	//CoFH Wrench API
	@Override
	@Method(modid = "cofhapi")
	public boolean canDismantle(World w, BlockPos p, IBlockState bs, EntityPlayer pl) {
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (mte != null)
			return mte.wrenchCanRemove(pl);
		return true;
	}

	@Override
	@Method(modid = "cofhapi")
	public ArrayList<ItemStack> dismantleBlock(World w, BlockPos p, IBlockState bs, EntityPlayer pl, boolean drop) {
		ArrayList<ItemStack> ret = new ArrayList();
		MetaTileBase mte = MetaTileBase.getMTE(w, p);
		if (!drop && mte != null && mte.wrenchCanRemove(pl)) {
			mte.getDrops(ret);
			for (ItemStack st : ret)
				JSTUtils.dropEntityItemInPos(w, p, st);
		}
		w.setBlockToAir(p);
		return ret;
	}
}
