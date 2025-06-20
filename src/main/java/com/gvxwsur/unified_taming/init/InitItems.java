package com.gvxwsur.unified_taming.init;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.item.ControllingStaffItem;
import com.gvxwsur.unified_taming.item.MagicPopsicleItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, UnifiedTaming.MOD_ID);

    public static RegistryObject<Item> CONTROLLING_STAFF = ITEMS.register("controlling_staff", ControllingStaffItem::new);

    public static RegistryObject<Item> MAGIC_POPSICLE = ITEMS.register("magic_popsicle", MagicPopsicleItem::new);

    public static RegistryObject<Item> MAGIC_POPSICLE_CREATIVE = ITEMS.register("magic_popsicle_creative", MagicPopsicleItem::new);
}
