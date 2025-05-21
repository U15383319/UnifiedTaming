package com.gvxwsur.unified_taming.init;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.item.MultiToolItem;
import com.gvxwsur.unified_taming.item.TameMaterialItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, UnifiedTaming.MOD_ID);

    public static RegistryObject<Item> MULTI_TOOL_ITEM = ITEMS.register("multi_tool", MultiToolItem::new);

    public static RegistryObject<Item> TAME_MATERIAL = ITEMS.register("tame_material", TameMaterialItem::new);

    public static RegistryObject<Item> TAME_MATERIAL_CREATIVE = ITEMS.register("tame_material_creative", TameMaterialItem::new);
}
