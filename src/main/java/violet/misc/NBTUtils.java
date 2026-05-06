package violet.misc;

import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public class NBTUtils {
    public static <T> String encodeComponent(ComponentType<T> type, Object value) {
    try {
        NbtElement nbt = type.getCodec().encodeStart(NbtOps.INSTANCE, (T) value).getOrThrow();
        return nbt.toString();
    } catch (Exception e) {
        return value.toString();
    }
}
}
