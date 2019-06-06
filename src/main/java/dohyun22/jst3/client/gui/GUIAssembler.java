package dohyun22.jst3.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerGeneric;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIAssembler extends GUIGeneric {
	public GUIAssembler(ContainerGeneric con) {
		super(con);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 128, (this.height - this.ySize) / 2 + 50, 20, 20, "\u2192"));
	
	    for (int r = 0; r < 3; r++)
	    	for (int c = 0; c < 3; c++)
	    		addSlot(44 + c * 18, 7 + r * 18, 0);
	    
	    addSlot(130, 25, 0);
	    addSlot(148, 25, 0);
		
	    addSlot(62, 63, 3);
	    
	    addPrg(101, 25);
		
		addSlot(8, 53, 2);
		addPwr(12, 31);
	}

	@Override
	protected void actionPerformed(GuiButton gb) throws IOException {
		if (gb.id == 0)
			handleMouseClick(null, 100, 0, ClickType.QUICK_CRAFT);
	}
	
	@Override
    public void drawScreen(int mX, int mY, float pt) {
		super.drawScreen(mX, mY, pt);
		if (isPointInRegion(128, 50, 20, 20, mX, mY))
			drawHoveringText(I18n.format("jst.msg.com.build"), mX, mY);
	}
}
