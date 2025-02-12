package xyz.faewulf.backpack.inter.API;

import java.util.List;

public record getDataRes(boolean success, List<DataBackPack> data) {
}
