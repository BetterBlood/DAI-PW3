package ch.heigvd;

public enum ProtectionType {
    HEAL("HEAL", 0),
    DEFEND("DEFEND", 1);

    private static String correspondTo[] = {"h", "d"};

    ProtectionType(String value, int code) { }


    public static ProtectionType findByName(String name)
    {
        for (ProtectionType protectionType : values())
            if (protectionType.name().equalsIgnoreCase(name))
                return protectionType;
        return null;
    }

    public static ProtectionType getByDim(String diminutive)
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

    public static ProtectionType getByDimOrName(String diminutiveOrName)
    {
        ProtectionType protectionType = getByDim(diminutiveOrName);
        if (protectionType == null) return findByName(diminutiveOrName);
        return protectionType;
    }
}
