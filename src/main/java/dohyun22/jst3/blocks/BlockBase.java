package dohyun22.jst3.blocks;

import java.util.ArrayList;
import java.util.List;

import dohyun22.jst3.JustServerTweak;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends Block {
	public static final IProperty<Integer> META = PropertyInteger.create("meta", 0, 15);
	public final String name;
	public List<Integer> allowedMetas = new ArrayList();

	protected BlockBase(String aName, Material aMaterial) {
		super(aMaterial);
		setUnlocalizedName(this.name = aName);
		setRegistryName(aName);
		setDefaultState(blockState.getBaseState().withProperty(META, Integer.valueOf(0)));
		setCreativeTab(JustServerTweak.JSTTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList list) {
		for (Integer i : allowedMetas) {
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition,
			IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, META);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(META, Integer.valueOf(meta));
	}

	@Override
	public int getMetaFromState(IBlockState bs) {
		return ((Integer) bs.getValue(META)).intValue();
	}

	@Override
	public ItemStack getPickBlock(IBlockState bs, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(this, 1, ((Integer) bs.getValue(META)).intValue());
	}
	
	public boolean isEnabled(int n) {
		return allowedMetas.contains(n);
	}
	
	public static int getMeta(IBlockAccess w, BlockPos p) {
		return getMeta(w.getBlockState(p));
	}

	public static int getMeta(IBlockState bs) {
		return ((Integer) bs.getValue(META)).intValue();
	}
	
	public void setHarvestLevel (String tool, int lv, int i) {
		super.setHarvestLevel(tool, lv, getStateFromMeta(i));
	}
	
    public int damageDropped(IBlockState bs) {
    	return getMeta(bs);
    }
}
