package org.chubby.github.mobcontroller.common.items;

public class ControllerType
{
    public static final ControllerType COPPER = new ControllerType("copper",1200);
    public static final ControllerType IRON = new ControllerType("iron",1800);
    public static final ControllerType GOLD = new ControllerType("gold",900);
    public static final ControllerType DIAMOND = new ControllerType("diamond",2400);

    public String name;
    public ControllerType type;
    public int controlTime;
    public ControllerType(String name, int controlTime)
    {
        this.name = name;
        this.controlTime = controlTime;
        this.type = new ControllerType(getName(),getControlTime());
    }

    public String getName() {
        return name;
    }

    public int getControlTime() {
        return controlTime;
    }

    public ControllerType getType() {
        return type;
    }
}
