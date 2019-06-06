package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerFurnaceGen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIFurnaceGen extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/furnacegen.png");
	
	public GUIFurnaceGen(ContainerFurnaceGen con) {
		super(con);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerFurnaceGen con = (ContainerFurnaceGen) this.inventorySlots;
		this.fontRenderer.drawString(con.energy + " / " + con.mxenergy + " EU", 80, 16, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    ContainerFurnaceGen con = (ContainerFurnaceGen) this.inventorySlots;
	    if (con.energy > 0) {
	    	int i = (int) ((Math.min(Math.max(con.energy, 0), con.mxenergy) * 24L) / Math.max(con.mxenergy, 1L));
	    	drawTexturedModalRect(x + 116, y + 42, 176, 14, i, 16);
	    }
	    if (con.fuelleft > 0) {
	    	int i = (Math.min(Math.max(con.fuelleft, 0), con.fuelpower) * 14) / Math.max(con.fuelpower, 1);
	    	drawTexturedModalRect(x + 39, y + 49 - i, 176, 14 - i, 14, i);
	    }
	}

}
