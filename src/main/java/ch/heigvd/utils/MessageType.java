package ch.heigvd.utils;

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
    ERROR,

    // Other
    DEFAULT;

    private final static String[] correspondTo = {"att", "pro", "get", "ans", "ecd", "glo", "err", "def"};

    /**
     * try to find the corresponding enum
     * @param name string (case-insensitive) with is the name of the enum needed
     * @return MessageType enum corresponding to the given name, null otherwise
     */
    public static MessageType findByName(String name)
    {
        for (MessageType messageType : values())
            if (messageType.name().equalsIgnoreCase(name))
                return messageType;
        return null;
    }

    /**
     * try to find the corresponding enum
     * @param diminutive string (case-insensitive) with is the diminutive of the enum needed
     * @return MessageType enum corresponding to the given diminutive name, null otherwise
     */
    public static MessageType getByDim(String diminutive)
    {
        for (int i = 0; i < correspondTo.length; ++i)
            if (correspondTo[i].equalsIgnoreCase(diminutive))
                return values()[i];
        return null;
    }

    /**
     * check if a enum could correspond to the given input
     * @param diminutiveOrName string (case-insensitive) with is the diminutive or the name
     *                         of the enum needed
     * @return true if found, false otherwise
     */
    public static boolean isIn(String diminutiveOrName)
    {
        return getByDim(diminutiveOrName) != null || findByName(diminutiveOrName) != null;
    }

    /**
     * try to find the corresponding enum
     * @param diminutiveOrName string (case-insensitive) with is the diminutive or the name
     *                         of the enum needed
     * @return MessageType enum corresponding to the given diminutive or name, null otherwise
     */
    public static MessageType getByDimOrName(String diminutiveOrName)
    {
        MessageType messageType = getByDim(diminutiveOrName);
        if (messageType == null) return findByName(diminutiveOrName);
        return messageType;
    }
}
