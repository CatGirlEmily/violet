package violet.misc;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class NBTUtils {
    public static <T> String encodeComponent(DataComponentType<T> type, Object value) {
    try {
        Tag nbt = type.codec().encodeStart(NbtOps.INSTANCE, (T) value).getOrThrow();
        return nbt.toString();
    } catch (Exception e) {
        return value.toString();
    }
}
}
