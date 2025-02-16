package xyz.faewulf.backpack.inter.BackpackModelRecord;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DetailBackpack {
    @Nullable
    public LightSourceDetail light_source;
    @Nullable
    public DetailTransform banner;
    @Nullable
    public DetailTransform base;
    @Nullable
    public DetailTransform global;
    @Nullable
    public List<DetailTransform> back_tool;
    @Nullable
    public List<DetailTransform> tool;
    @Nullable
    public List<DetailTransform> container;
    @Nullable
    public List<DetailTransform> pocket;

    @Override
    public String toString() {
        return "DetailBackpack{" +
                "\nlight_source=" + light_source +
                ", \nglobal=" + global +
                ", \nbanner=" + banner +
                ", \nbase=" + base +
                ", \nback_tool=" + back_tool +
                ", \ntool=" + tool +
                ", \ncontainer=" + container +
                ", \npocket=" + pocket +
                '}';
    }
}

