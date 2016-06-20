package alexndr.plugins.Fusion.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import alexndr.plugins.Fusion.FusionFurnaceRecipes;

/**
 * this class is the FusionFurnace version of SlotFurnaceOutput, and is almost
 * identical.
 * @author AleXndrTheGr8st
 */
public class SlotFusionFurnace extends Slot
{
	/** The player that is using the GUI where this slot resides. */
    protected EntityPlayer thePlayer;
    protected int removeCount;

    public SlotFusionFurnace(EntityPlayer par1EntityPlayer, IInventory inventoryIn, 
    						 int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
        this.thePlayer = par1EntityPlayer;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    @Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
	public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.removeCount += Math.min(amount, this.getStack().stackSize);
        }
        return super.decrStackSize(amount);
    }

    @Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        this.onCrafting(stack);
        super.onPickupFromSlot(playerIn, stack);
     }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    @Override
	protected void onCrafting(ItemStack stack, int par2)
    {
        this.removeCount += par2;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
	protected void onCrafting(ItemStack stack)
    {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.removeCount);

        if (!this.thePlayer.worldObj.isRemote)
        {
            int i = this.removeCount;
            float f = FusionFurnaceRecipes.getExperience(stack);
            int j;

            if (f == 0.0F)
            {
                i = 0;
            }
            else if (f <= 100.0F)
            {
                j = MathHelper.floor_float(i * f);

                if (j < MathHelper.ceiling_float_int(i * f) && (float)Math.random() < i * f - j)
                {
                    ++j;
                }

                i = j;
            }

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.thePlayer.worldObj.spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5D, this.thePlayer.posZ + 0.5D, j));
            }
        }

        this.removeCount = 0;
        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerSmeltedEvent(thePlayer, stack);

    } // end-if !isRemote
} // end onCrafting()
