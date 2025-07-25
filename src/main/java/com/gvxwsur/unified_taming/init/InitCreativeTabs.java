package com.gvxwsur.unified_taming.init;

import com.gvxwsur.unified_taming.UnifiedTaming;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import vazkii.patchouli.common.item.ItemModBook;

import static com.gvxwsur.unified_taming.init.InitItems.*;

public class InitCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UnifiedTaming.MOD_ID);

    public static RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.unified_taming.main"))
            .icon(() -> CONTROLLING_STAFF.get().getDefaultInstance())
            .displayItems((par, output) -> {
                if (ModList.get().isLoaded("patchouli")) {
                    output.accept(ItemModBook.forBook(new ResourceLocation(UnifiedTaming.MOD_ID, "unified_taming_tutorial")));
                }
                output.accept(CONTROLLING_STAFF.get());
                output.accept(MAGIC_POPSICLE.get());
                output.accept(MAGIC_POPSICLE_CREATIVE.get());
            }).build());
}
