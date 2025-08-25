package com.demo.cryptotrading.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static final String BTCUSDT = "BTCUSDT";
    public static final String ETHUSDT = "ETHUSDT";
    public static final Set<String> ALLOWED_SYMBOLS = new HashSet<>(Arrays.asList(BTCUSDT, ETHUSDT));
}
