package dohyun22.jst3.tiles.interfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface IGenericGUIMTE {
	default int getPrg() {return -1;}
	default int getMxPrg() {return -1;}
	default int getFuel() {return -1;}
	default int getMxFuel() {return -1;}
	default int[] getGuiData() {return null;}
	default String guiDataToStr(int id, int dat) {return Integer.toString(dat);}
	default void handleBtn(int id, EntityPlayer pl) {}
}
