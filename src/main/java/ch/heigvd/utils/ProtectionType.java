package ch.heigvd.utils;

public enum ProtectionType {
    HEAL("HEAL", 0),
    DEFEND("DEFEND", 1);

    private final static String[] correspondTo = {"h", "d"};

    ProtectionType(String value, int code) { }

    /**
     * try to find the corresponding enum
     * @param name string (case-insensitive) with is the name of the enum needed
     * @return ProtectionType enum corresponding to the given name, null otherwise
     */
    public static ProtectionType findByName(String name)
    {
        for (ProtectionType protectionType : values())
            if (protectionType.name().equalsIgnoreCase(name))
                return protectionType;
        return null;
    }

    /**
     * try to find the corresponding enum
     * @param diminutive string (case-insensitive) with is the diminutive of the enum needed
     * @return ProtectionType enum corresponding to the given diminutive name, null otherwise
     */
    public static ProtectionType getByDim(String diminutive)
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
     * @return ProtectionType enum corresponding to the given diminutive or name, null otherwise
     */
    public static ProtectionType getByDimOrName(String diminutiveOrName)
    {
        ProtectionType protectionType = getByDim(diminutiveOrName);
        if (protectionType == null) return findByName(diminutiveOrName);
        return protectionType;
    }
}
