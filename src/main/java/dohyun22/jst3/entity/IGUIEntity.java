package dohyun22.jst3.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IGUIEntity {
	
	public Object getServerGUI(int id, EntityPlayer pl, World w);

	public Object getClientGUI(int id, EntityPlayer pl, World w);
}
