package com.gvxwsur.unified_taming.mixin;

import com.gvxwsur.unified_taming.config.CommonConfig;
import com.gvxwsur.unified_taming.config.subconfig.MiscConfig;
import com.gvxwsur.unified_taming.entity.api.MerchantHelper;
import com.gvxwsur.unified_taming.util.UnifiedTamingUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Iterator;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends AgeableMob implements InventoryCarrier, Npc, Merchant, MerchantHelper {
    protected AbstractVillagerMixin(EntityType<? extends AgeableMob> p_146738_, Level p_146739_) {
        super(p_146738_, p_146739_);
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    public void canBeLeashed(Player p_35272_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(super.canBeLeashed(p_35272_));
    }

    @Inject(method = "setTradingPlayer", at = @At("HEAD"))
    public void setTradingPlayer(@Nullable Player p_35314_, CallbackInfo ci) {
        this.unified_taming$updateSpecialPrices(p_35314_);
    }

    public int unified_taming$getPlayerReputation(Player p_35533_) {
        return UnifiedTamingUtils.isOwnedBy(this, p_35533_) ? MiscConfig.merchantTamedReputation.get() : 0;
    }

    public void unified_taming$updateSpecialPrices(Player p_35541_) {
        int i = this.unified_taming$getPlayerReputation(p_35541_);
        if (i != 0) {
            Iterator var3 = this.getOffers().iterator();

            while(var3.hasNext()) {
                MerchantOffer merchantoffer = (MerchantOffer)var3.next();
                merchantoffer.addToSpecialPriceDiff(-Mth.floor((float)i * merchantoffer.getPriceMultiplier()));
            }
        }
    }
}
