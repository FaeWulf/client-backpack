package xyz.faewulf.backpack.inter;

import java.util.List;

public record getDataRes(boolean success, List<DataBackPack> data) {
}
