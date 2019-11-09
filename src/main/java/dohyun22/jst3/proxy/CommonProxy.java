package dohyun22.jst3.proxy;

import dohyun22.jst3.client.gui.GUICfg;
import dohyun22.jst3.container.ContainerCfg;
import dohyun22.jst3.entity.IGUIEntity;
import dohyun22.jst3.items.ItemJST1;
import dohyun22.jst3.items.ItemMetaBase;
import dohyun22.jst3.tiles.MetaTileBase;
import dohyun22.jst3.tiles.TileEntityMeta;
import dohyun22.jst3.tiles.interfaces.IConfigurable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
	
	public void preinit() {
	}

	public void init() {
	}
	
	public void postinit() {
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer p, World w, int x, int y, int z) {
		if (ID >= 2000) {
			Entity e = w.getEntityByID(x);
			if (e instanceof IGUIEntity)
				return ((IGUIEntity)e).getServerGUI(ID, p, w);
			return null;
		}
		if (ID >= 1000) {
			for (ItemStack st : p.getHeldEquipment())
				if (st != null && st.getItem() instanceof ItemJST1)
					return ((ItemJST1)st.getItem()).getBehaviour(st).getServerGUI(ID, p, w, new BlockPos(x, y, z));
			return null;
		}
		TileEntity te = w.getTileEntity(new BlockPos(x, y, z));
		if (te instanceof TileEntityMeta) {
			MetaTileBase mte = ((TileEntityMeta)te).mte;
			if (mte != null) {
				if (mte instanceof IConfigurable) {
					if (ID == 998) return new ContainerCfg((TileEntityMeta)te, true);
					if (ID == 999) return new ContainerCfg((TileEntityMeta)te, false);
				}
				return mte.getServerGUI(ID, p.inventory, (TileEntityMeta)te);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer p, World w, int x, int y, int z) {
		if (ID >= 2000) {
			Entity e = w.getEntityByID(x);
			if (e instanceof IGUIEntity)
				return ((IGUIEntity)e).getClientGUI(ID, p, w);
			return null;
		}
		if (ID >= 1000) {
			for (ItemStack st : p.getHeldEquipment())
				if (st != null && st.getItem() instanceof ItemJST1)
					return ((ItemJST1)st.getItem()).getBehaviour(st).getClientGUI(ID, p, w, new BlockPos(x, y, z));
			return null;
		}
		TileEntity te = w.getTileEntity(new BlockPos(x, y, z));
		if (te instanceof TileEntityMeta) {
			MetaTileBase mte = ((TileEntityMeta)te).mte;
			if (mte != null) {
				if (mte instanceof IConfigurable) {
					if (ID == 998) return new GUICfg((TileEntityMeta)te, true);
					if (ID == 999) return new GUICfg((TileEntityMeta)te, false);
				}
				return mte.getClientGUI(ID, p.inventory, (TileEntityMeta)te);
			}
		}
		return null;
	}
}
