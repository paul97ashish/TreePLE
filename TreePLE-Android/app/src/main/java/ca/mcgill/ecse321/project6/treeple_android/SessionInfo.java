package ca.mcgill.ecse321.project6.treeple_android;

/*
Is there a better way to do this? Yes.
Does this work regardless of the current activity? Also yes.
 */
class SessionInfo {
    private static String sessionGuid = "";

    public static void unsetGuid() {
        sessionGuid = "";
    }

    public static void setGuid(String guid) {
        sessionGuid = guid;
    }

    public static String getGuid() {
        return sessionGuid;
    }
}
