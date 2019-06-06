package dohyun22.jst3.compat.tic;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.util.RecipeMatch;

public class CustomCombination extends RecipeMatch {
    protected final NonNullList<ItemStack> itemStacks;
    
    public CustomCombination(int amountMatched, ItemStack... stacks) {
        super(amountMatched, 0);
        final NonNullList<ItemStack> nonNullStacks = NonNullList.withSize(stacks.length, ItemStack.EMPTY);
        for (int i = 0; i < stacks.length; ++i)
            if (!stacks[i].isEmpty())
                nonNullStacks.set(i, stacks[i].copy());
        this.itemStacks = nonNullStacks;
    }
    
    @Override
    public List<ItemStack> getInputs() {
        return ImmutableList.copyOf(itemStacks);
    }
    
    @Override
	public Optional<Match> matches(NonNullList<ItemStack> stacks) {
		List<ItemStack> found = Lists.newLinkedList();
		Set<Integer> needed = Sets.newHashSet();
		for (int i = 0; i < itemStacks.size(); ++i)
			if (!(itemStacks.get(i)).isEmpty())
				needed.add(i);
		for (ItemStack stack : stacks) {
			Iterator<Integer> iter = needed.iterator();
			while (iter.hasNext()) {
				int index = iter.next();
				ItemStack template = (ItemStack) itemStacks.get(index);
				if (OreDictionary.itemMatches(template, stack, false)) {
					ItemStack copy = stack.copy();
					copy.setCount(1);
					found.add(copy);
					iter.remove();
					break;
				}
			}
		}
		if (needed.isEmpty())
			return Optional.of(new Match(found, amountMatched));
		return Optional.empty();
	}
}