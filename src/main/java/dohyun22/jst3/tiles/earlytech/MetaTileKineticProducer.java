package dohyun22.jst3.tiles.earlytech;

import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.MetaTileKinetic;
import dohyun22.jst3.tiles.TileEntityMeta;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileKineticProducer extends MetaTileKinetic {
	//0 = manual, 1 = wind, 2 = water
	private final byte type;
	private int var;
	
	
	public MetaTileKineticProducer(int type) {
		this.type = (byte) type;
	}

	@Override
	public MetaTileBase newMetaEntity(TileEntityMeta tem) {
		return new MetaTileKineticProducer(this.type);
	}
	
	@Override
	public boolean canProvideKU(EnumFacing f) {
		return true;
	}

	@Override
	public boolean onRightclick(EntityPlayer pl, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (type == 0) {
			if (getWorld().isRemote) return true;
			if (pl instanceof FakePlayer || pl.getFoodStats().getFoodLevel() <= 6 || !(pl instanceof EntityPlayerMP))
		    	return true;
			
			int amt = sendKU(32, false);
			if (amt > 0)
				pl.addExhaustion(amt / 2000.0F);
		    
		    return true;
		}
	    return false;
	}
	
	@Override
	public void onPostTick() {
		if (type == 0) return;
		if (type == 1 || type == 2) {
			if (baseTile.getTimer() % 20 == 0)
				var = (type == 1 ? getWind() : getWaterNum());
			if (var > 0)
				sendKU(var, false);
		}
	}
	
	@Override
	@SideOnly(value=Side.CLIENT)
	public String getModelKey() {
		return "KP_" + type;
	}
	
	private int getWind() {
	    int o = -1;
	    for (int x = -4; x <= 4; x++)
	  	 	for (int y = -2; y <= 4; y++)
	  	 		for (int z = -4; z <= 4; z++)
	  	 			if (this.getWorld().getBlockState(this.getPos().add(x, y, z)).getMaterial() != Material.AIR)
	  	 				o ++;
	    
	    double sp = (getWorld().rand.nextInt(20) * (this.getPos().getY() - 64 - o) / 180.0D);
	    if (sp <= 0.0D) return 0;
	    
	    sp *= getWorld().isThundering() ? 1.5D : this.getWorld().isRaining() ? 1.2D : 1.0D;
		
	    if (sp > 16.0D) sp = 16.0D;
	    
		return (int)sp;
	}

	private int getWaterNum() {
		int cnt = 0;
		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++)
					if (this.getWorld().getBlockState(this.getPos().add(x, y, z)).getMaterial() == Material.WATER)
						cnt++;
		
		float ret = cnt * getWorld().rand.nextFloat() * 0.5F;
		Biome b = getWorld().getBiome(getPos());
		if (BiomeDictionary.hasType(b, BiomeDictionary.Type.RIVER)) {
			ret *= 2.0F;
		} else if (BiomeDictionary.hasType(b, BiomeDictionary.Type.OCEAN)) {
			ret *= 1.5F;
		}
		
		if (ret > 8.0F) ret = 8.0F;
		
		return (int) ret;
	}
}
