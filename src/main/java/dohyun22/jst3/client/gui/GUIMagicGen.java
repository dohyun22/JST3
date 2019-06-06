package dohyun22.jst3.client.gui;

import org.lwjgl.opengl.GL11;

import dohyun22.jst3.JustServerTweak;
import dohyun22.jst3.container.ContainerMagicGen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIMagicGen extends GUIBase {
	private static final ResourceLocation gui = new ResourceLocation(JustServerTweak.MODID, "textures/gui/magicgen.png");
	
	public GUIMagicGen(ContainerMagicGen con) {
		super(con);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2){
		ContainerMagicGen con = (ContainerMagicGen) this.inventorySlots;
		this.fontRenderer.drawString(con.energy + " / " + con.mxenergy + " EU", 55, 55, 4210752);
		this.fontRenderer.drawString((con.energy * 4) + " / " + (con.mxenergy * 4) + " RF", 55, 65, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(gui);
	    int x = (this.width - this.xSize) / 2;
	    int y = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	    ContainerMagicGen con = (ContainerMagicGen) this.inventorySlots;
	    if (con.energy > 0) {
	    	int i = (int) ((Math.min(Math.max(con.energy, 0), con.mxenergy) * 24L) / Math.max(con.mxenergy, 1L));
	    	drawTexturedModalRect(x + 59, y + 32, 176, 14, i, 16);
	    }
	    if (con.fuelleft > 0) {
	    	int i = ((Math.min(Math.max(con.fuelleft, 0), con.fuelpower) * 24) / Math.max(con.fuelpower, 1));
	    	drawTexturedModalRect(x + 11, y + 32, 176, 31, i, 16);
	    }
	    if (con.collectEC)
	    	drawTexturedModalRect(x + 151, y + 7, 176, 48, 16, 16);
	}
}
