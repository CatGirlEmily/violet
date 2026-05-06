package violet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import violet.events.TooltipRenderEvent;
import violet.features.render.NBTTooltip;
import violet.misc.NBTUtils;
import violet.misc.Utils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.List;

import static violet.Main.eventBus;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {
    @Shadow
    @Nullable
    protected Slot focusedSlot;
    @Shadow
    @Final
    protected T handler;
    @Shadow
    protected int y;
    @Shadow
    protected int x;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @ModifyExpressionValue(method = "drawMouseoverTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;getTooltipFromItem(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
    private List<Text> onGetTooltipFromItem(List<Text> original, @Local ItemStack itemStack) {
        if (!itemStack.isEmpty() && NBTTooltip.instance.isActive() && (!NBTTooltip.showWhileHeld.value() || NBTTooltip.isKeyHeld)) {
            eventBus.post(new TooltipRenderEvent(original, itemStack, Utils.getCustomData(itemStack), this.getTitle().getString()));
            List<Text> tooltip = new ArrayList<>(original);

            tooltip.add(Text.empty());
            tooltip.add(Text.literal("Components:").formatted(Formatting.GOLD));

            ComponentChanges changes = itemStack.getComponentChanges();
            changes.entrySet().forEach(entry -> {
                ComponentType<?> type = entry.getKey();
                String id = Registries.DATA_COMPONENT_TYPE.getId(type).toString();
                entry.getValue().ifPresent(value -> {
                    String nbtString = NBTUtils.encodeComponent(type, value);
                    tooltip.add(Text.literal(" " + id + ": " + nbtString).formatted(Formatting.DARK_GRAY));
                });
            });
            // if empty, add "none" text
            if (Math.abs(original.size() - tooltip.size()) == 2) tooltip.add(Text.literal("none").formatted(Formatting.DARK_GRAY));
            
            original = tooltip;
        }

        return original;
    }
}
