package ch.heigvd;

import java.util.Arrays;

public enum MessageType {
    // Enemies
    ATTACK,

    // Allies
    PROTECT,
    GET_INFO,

    // Server
    ANSWER,
    ERROR_CD,
    GAME_LOST,

    // Other
    DEFAULT;

    private static String correspondTo[] = {"att", "pro", "get", "ans", "err", "glo", "def"};

    public static MessageType findByName(String name)
    {
        for (MessageType messageType : values())
            if (messageType.name().equalsIgnoreCase(name))
                return messageType;
        return null;
    }

    public static MessageType getByDim(String diminutive)
    {
        for (int i = 0; i < correspondTo.length; ++i)
            if (correspondTo[i].equalsIgnoreCase(diminutive))
                return values()[i];
        return null;
    }

    public static boolean isIn(String diminutiveOrName)
    {
        return getByDim(diminutiveOrName) != null || findByName(diminutiveOrName) != null;
    }

    public static MessageType getByDimOrName(String diminutiveOrName)
    {
        MessageType messageType = getByDim(diminutiveOrName);
        if (messageType == null) return findByName(diminutiveOrName);
        return messageType;
    }
}
