package dohyun22.jst3.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.utils.JSTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Second Block of JST. Formerly known as "World Blocks" */
public class BlockJST2 extends BlockBase {

	protected BlockJST2() {
		super("blockjst2", MaterialJST.INSTANCE);
		setHardness(1.5F);
		setResistance(10.0F);
		setCreativeTab(JustServerTweak.JSTTab);
		for (int i = 0; i < 10; i++) {
			allowedMetas.add(i);
		}
	}

	@Override
    public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion x) {
    	if (w == null) return 0.0F;
    	int m = getMeta(w, p);
    	switch (m) {
    	case 0:
    	case 1:
    		return 0.5F;
    	case 2:
    	case 3:
    	case 4:
    	case 5:
    		return 27.0F;
    	default:
    		return super.getExplosionResistance(w, p, e, x);
    	}
    }
	
	@Override
    public float getBlockHardness(IBlockState s, World w, BlockPos p) {
    	if (w == null) return 0.0F;
		int m = getMeta(s);
    	switch (m) {
    	case 0:
    	case 1:
    		return 0.5F;
    	case 2:
    	case 3:
    	case 4:
    	case 5:
    		return 2.5F;
    	case 6:
    	case 7:
    	case 8:
    	case 9:
    		return 1.0F;
    	default:
    		return super.getBlockHardness(s, w, p);
    	}
    }
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess w, BlockPos p, IBlockState s, int f) {
		List arr = new ArrayList();
		int m = getMeta(s);
		switch (m) {
		case 2:
			ItemStack st = JSTUtils.getModItemStack("ic2:resource", 1, 0);
			if (!st.isEmpty()) {
				arr.add(st);
				break;
			}
		default:
			arr.add(new ItemStack(this, 1, m));
		}
		return arr;
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess w, BlockPos p, EntityPlayer pl) {
		int m = getMeta(w, p);
		return (m == 0 || m == 1) ? true : JSTUtils.canHarvest(this, pl, w.getBlockState(p));
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState bs, EntityPlayer pl, World w, BlockPos p) {
	    int m = getMeta(bs);
	    if (m == 0 || m == 1) {
	    	if (!JSTUtils.canHarvest(this, pl, bs))
	    		return 0.06666F;
	    }
		return super.getPlayerRelativeBlockHardness(bs, pl, w, p);
	}
	
	@Override
	public int damageDropped(IBlockState bs) {
		return getMeta(bs);
	}
	
	@Override
	public boolean canEntityDestroy(IBlockState bs, IBlockAccess w, BlockPos p, Entity e) {
		int m = getMeta(bs);
		switch (m) {
		case 2:
		case 3:
		case 4:
		case 5:
			return false;
		}
		return super.canEntityDestroy(bs, w, p, e);
	}
	
    @Override
	public boolean canCreatureSpawn(IBlockState bs, IBlockAccess w, BlockPos p, EntityLiving.SpawnPlacementType type) {
		int m = getMeta(bs);
		switch (m) {
		case 3:
		case 4:
		case 5:
		case 7:
		case 8:
		case 9:
			return false;
		}
    	return true;
	}
    
	@Override
	public boolean canSustainPlant(IBlockState bs, IBlockAccess w, BlockPos p, EnumFacing f, IPlantable pl) {
		int m = getMeta(bs);
		EnumPlantType pt = pl.getPlantType(w, p.up());
		return (m == 0 || m == 1) && (pl instanceof BlockDeadBush || pt == EnumPlantType.Cave);
	}
    
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState bs, World w, BlockPos p, Random r) {
		int m = getMeta(bs);
		if ((m == 0 || m == 1) && r.nextInt(10) == 0 && !w.isBlockNormalCube(p.up(), true))
			w.spawnParticle(EnumParticleTypes.TOWN_AURA, p.getX() + r.nextFloat(), p.getY() + 1.1F, p.getZ() + r.nextFloat(), 0.0D, 0.0D, 0.0D);
	}
	
	@Override
	public void onBlockAdded(World w, BlockPos p, IBlockState bs) {
		int m = getMeta(bs);
		if (m == 1)
			JSTUtils.tryToFall(w, p);
	}
		  
	@Override
	public void neighborChanged(IBlockState bs, World w, BlockPos p, Block b, BlockPos f) {
		int m = getMeta(w, p);
		if (m == 1)
			JSTUtils.tryToFall(w, p);
	}
	
	@Override
    public String getHarvestTool(IBlockState s) {
        switch (getMeta(s)) {
        case 1:
        	return "shovel";
        }
        return "pickaxe";
    }
	
	@Override
    public int getHarvestLevel(IBlockState s) {
        switch (getMeta(s)) {
        case 0:
        case 1:
        	return -1;
        case 2:
        case 3:
        case 4:
        case 5:
        	return 1;
        }
        return 0;
    }
	
    @Override
    public SoundType getSoundType(IBlockState bs, World w, BlockPos p, Entity e) {
        switch (getMeta(bs)) {
        case 1:
        	return SoundType.SAND;
        }
        return super.getSoundType(bs, w, p, e);
    }
}
