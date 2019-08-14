package dohyun22.jst3.client.gui;

import java.util.ArrayList;
import java.util.Arrays;

import dohyun22.jst3.compat.jei.JEISupport;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GUIBase extends GuiContainer {
	public GUIBase(Container c) {
		super(c);
	}

	@Override
    public void drawScreen(int mx, int my, float pt) {
        this.drawDefaultBackground();
        super.drawScreen(mx, my, pt);
        this.renderHoveredToolTip(mx, my);
    }

	public void playSound(SoundEvent s, float p) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(s, p));
	}
}
