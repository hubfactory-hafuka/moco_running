package jp.hubfactory.moco.util;

public final class TableSuffixGenerator {

    public static int getUserIdSuffix(long userId) {
        long suffix = userId % 10;
        return Integer.parseInt(String.valueOf(suffix));
    }
}
