package br.puc.tgc.kcenters;

import java.util.Map;

public final class OptimalRadii {

    private static final Map<Integer, Integer> VALUES = Map.ofEntries(
            Map.entry(1, 127), Map.entry(2, 98), Map.entry(3, 93), Map.entry(4, 74), Map.entry(5, 48),
            Map.entry(6, 84), Map.entry(7, 64), Map.entry(8, 55), Map.entry(9, 37), Map.entry(10, 20),
            Map.entry(11, 59), Map.entry(12, 51), Map.entry(13, 35), Map.entry(14, 26), Map.entry(15, 18),
            Map.entry(16, 47), Map.entry(17, 39), Map.entry(18, 28), Map.entry(19, 18), Map.entry(20, 13),
            Map.entry(21, 40), Map.entry(22, 38), Map.entry(23, 22), Map.entry(24, 15), Map.entry(25, 11),
            Map.entry(26, 38), Map.entry(27, 32), Map.entry(28, 18), Map.entry(29, 13), Map.entry(30, 9),
            Map.entry(31, 30), Map.entry(32, 29), Map.entry(33, 15), Map.entry(34, 11), Map.entry(35, 30),
            Map.entry(36, 27), Map.entry(37, 15), Map.entry(38, 29), Map.entry(39, 23), Map.entry(40, 13)
    );

    private OptimalRadii() {
    }

    public static int get(int instanceId) {
        return VALUES.get(instanceId);
    }
}
