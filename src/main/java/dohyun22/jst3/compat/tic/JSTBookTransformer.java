package dohyun22.jst3.compat.tic;

import dohyun22.jst3.utils.JSTUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.sectiontransformer.SectionTransformer;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class JSTBookTransformer extends SectionTransformer {

	public JSTBookTransformer() {
		super("modifiers");
	}

	@Override
	public void transform(BookData b, SectionData s) {
		try {
			String[] n = new String[] {
					"jst_el_rs1", "jst_nano", "jst_solar"
			};
			for (String t : n) {
				ContentListing l = (ContentListing) s.pages.get(0).content;
				PageData p = new PageData();
				p.source = s.source;
				p.parent = s;
				p.type = "modifier";
				p.data = t + ".json";
				s.pages.add(p);
				p.load();
				l.addEntry(t, p);
			}
		} catch (Throwable t) {
			JSTUtils.LOG.error("Can't add info pages to TCon book.");
			JSTUtils.LOG.catching(t);
		}
	}
}
