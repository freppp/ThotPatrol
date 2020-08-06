package me.frep.thotpatrol.checks.combat.velocity;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;

public class VelocityA extends Check {

    public VelocityA(ThotPatrol ThotPatrol) {
        super("VelocityA", "Velocity (Type A) [#]", ThotPatrol);
        setEnabled(false);
        setBannable(false);
        setMaxViolations(6);
    }
}
