package org.jgrasstools.hortonmachine.externals.epanet.core;

@SuppressWarnings("nls")
public enum Components {
    EN_NODECOUNT(0, "Nodes"), //
    EN_TANKCOUNT(1, "Reservoirs and tank nodes"), //
    EN_LINKCOUNT(2, "Links"), //
    EN_PATCOUNT(3, "Time patterns"), //
    EN_CURVECOUNT(4, "Curves"), //
    EN_CONTROLCOUNT(5, "Simple controls");

    private int code;
    private String type;
    Components( int code, String type ) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
