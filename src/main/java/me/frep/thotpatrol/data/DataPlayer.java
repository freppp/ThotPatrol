package me.frep.thotpatrol.data;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DataPlayer {
	
	public static List<String> lastNearSlime;
	public static List<String> lastAir;
	private static int wasFlying = 0;
	private static int wasSpider = 0;
	private boolean alerts = false;
	private double fallDistance = 0D;
	private int aboveBlockTicks = 0;
	private int waterTicks = 0;
	private long LastBlockPlacedTicks = 0;
	private boolean LastBlockPlaced_GroundSpoof = false;
	public int velXTicks, velYTicks, velZTicks;
	public int airTicks = 0;
	public int groundTicks = 0;
	private boolean ShouldSetBack = false;
	private int setBackTicks = 0;
	private long LastVelMS = 0;
	private boolean DidTakeVelocity = false;
	private long lastDelayedPacket;
	private long lastPlayerPacket;
	private Location setbackLocation;
	private double GoingUp_Blocks;
	private double LastY_Gravity;
	private int Gravity_VL;
	private int AntiCactus_VL;
	private double lastVelocityFlyY = 0;
	private double lastKillauraPitch = 0;
	private double lastKillauraYaw = 0;
	private long lastPacket = 0;
	private long lastAimTime = System.currentTimeMillis();
	private final long Speed_Ticks = 0;
	private boolean Speed_TicksSet = false;
	private boolean isNearIce = false;
	private long isNearIceTicks = 0;
	private long LastVelUpdate = 0;
	private boolean LastVelUpdateBoolean = false;
	private double lastKillauraYawDif = 0;
	private long lastPacketTimer = 0;
	private long LastTimeTimer = 0;
	private int LastPACKETSTimer = 0;
	private long WebFloatMS = 0;
	private boolean WebFloatMS_Set = false;
	private int WebFloat_BlockCount = 0;
	private long AboveSpeedTicks = 0;
	private boolean AboveSpeedSet = false;
	private long HalfBlocks_MS = 0;
	private boolean HalfBlocks_MS_Set = false;
	private boolean Speed_C_2_Set = false;
	private long Speed_C_2_MS = 0;
	private long GlideTicks = 0;
	private long Speed_PistonExpand_MS = 0;
	private boolean Speed_PistonExpand_Set = false;
	private long BlockAbove = 0;
	private boolean BlockAbove_Set = false;
	private long Speed_YPORT_MS = 0;
	private boolean Speed_YPORT_Set = false;
	public int iceTicks = 0;
	private long Speed_YPort2_MS = 0;
	private boolean Speed_YPort2_Set = false;
	private long speedGroundReset = 0;
	public static int slimeTicks = 0;
	public float lastDeltaXZ;
	public Player player;
	public boolean onGround;
	public boolean inLiquid;
	public boolean onStairSlab;
	public boolean onIce;
	public boolean onClimbable;
	public boolean underBlock;
	public int liquidTicks = 0;
	public int blockTicks = 0;
	public long lastVelocityTaken, lastAttack;
	public LivingEntity lastHitEntity;

	public List<Float> patterns = Lists.newArrayList();
	public float lastRange;

	public int speedThreshold = 10;

	public DataPlayer(Player player) {
		this.player = player;
		lastNearSlime = new ArrayList<>();
		lastAir = new ArrayList<>();
	}
	private int criticalsVerbose = 0;
	private int flyHoverVerbose = 0;
	private int flyVelocityVerbose = 0;
	private int GroundSpoofVL = 0;
	private int killauraAVerbose = 0;
	private int Speed2Verbose = 0;
	private int Speed_OnGround_Verbose = 0;
	private int TimerVerbose = 0;
	private int SpeedAC2_Verbose = 0;
	private int SpeedC_Verbose = 0;
	private int Speed_C_3_Verbose = 0;
	private int Speed_YPORT_Verbose = 0;
	private int Speed_YPort2_Verbose = 0;
	private int NEWSpeed_Verbose = 0;
	private int speedAVerbose = 0;
	private int Speed_C3_Verbose = 0;
	private int Jesus_Verbose = 0;

	public Player getPlayer() {
		return player;
	}

	public double getFallDistance() {
		return fallDistance;
	}

	public void setFallDistance(double fallDistance) {
		this.fallDistance = fallDistance;
	}

	public int getIceTicks() {
		return iceTicks;
	}

	public void setIceTicks(int iceTicks) {
		this.iceTicks = iceTicks;
	}

	public int getAboveBlockTicks() {
		return aboveBlockTicks;
	}

	public void setAboveBlockTicks(int aboveBlockTicks) {
		this.aboveBlockTicks = aboveBlockTicks;
	}

	public int getWaterTicks() {
		return waterTicks;
	}

	public void setWaterTicks(int waterTicks) {
		this.waterTicks = waterTicks;
	}

	public int getSpeedAVerbose() {
		return speedAVerbose;
	}

	public void setSpeedAVerbose(int speedAVerbose) {
		this.speedAVerbose = speedAVerbose;
	}

	public int getCriticalsVerbose() {
		return criticalsVerbose;
	}

	public void setCriticalsVerbose(int criticalsVerbose) {
		this.criticalsVerbose = criticalsVerbose;
	}

	public double getLastKillauraYawDif() {
		return lastKillauraYawDif;
	}

	public void setLastKillauraYawDif(double lastKillauraYawDif) {
		this.lastKillauraYawDif = lastKillauraYawDif;
	}

	public double getLastKillauraPitch() {
		return lastKillauraPitch;
	}

	public void setLastKillauraPitch(double lastKillauraPitch) {
		this.lastKillauraPitch = lastKillauraPitch;
	}

	public double getLastKillauraYaw() {
		return lastKillauraYaw;
	}

	public void setLastKillauraYaw(double lastKillauraYaw) {
		this.lastKillauraYaw = lastKillauraYaw;
	}

	public int getKillauraAVerbose() {
		return killauraAVerbose;
	}

	public void setKillauraAVerbose(int killauraAVerbose) {
		this.killauraAVerbose = killauraAVerbose;
	}

	public long getLastPacket() {
		return lastPacket;
	}

	public void setLastPacket(long lastPacket) {
		this.lastPacket = lastPacket;
	}

	public long getLastAimTime() {
		return lastAimTime;
	}

	public void setLastAimTime(long lastAimTime) {
		this.lastAimTime = lastAimTime;
	}

	public long getLastBlockPlacedTicks() {
		return LastBlockPlacedTicks;
	}

	public void setLastBlockPlacedTicks(long lastBlockPlacedTicks) {
		LastBlockPlacedTicks = lastBlockPlacedTicks;
	}

	public boolean isLastBlockPlaced_GroundSpoof() {
		return LastBlockPlaced_GroundSpoof;
	}

	public void setLastBlockPlaced_GroundSpoof(boolean lastBlockPlaced_GroundSpoof) {
		LastBlockPlaced_GroundSpoof = lastBlockPlaced_GroundSpoof;
	}
	public int getAirTicks() {
		return airTicks;
	}

	public void setAirTicks(int airTicks) {
		this.airTicks = airTicks;
	}

	public int getGroundTicks() {
		return groundTicks;
	}

	public void setGroundTicks(int groundTicks) {
		this.groundTicks = groundTicks;
	}

	public int getFlyHoverVerbose() {
		return flyHoverVerbose;
	}

	public void setFlyHoverVerbose(int flyHoverVerbose) {
		this.flyHoverVerbose = flyHoverVerbose;
	}

	public int getGroundSpoofVL() {
		return GroundSpoofVL;
	}

	public void setGroundSpoofVL(int groundSpoofVL) {
		GroundSpoofVL = groundSpoofVL;
	}

	public double getLastVelocityFlyY() {
		return lastVelocityFlyY;
	}

	public void setLastVelocityFlyY(double lastVelocityFlyY) {
		this.lastVelocityFlyY = lastVelocityFlyY;
	}

	public long getLastVelMS() {
		return LastVelMS;
	}

	public int getFlyVelocityVerbose() {
		return flyVelocityVerbose;
	}

	public void setFlyVelocityVerbose(int flyVelocityVerbose) {
		this.flyVelocityVerbose = flyVelocityVerbose;
	}

	public void setLastDelayedPacket(long l) {
		this.lastDelayedPacket = l;
	}
	public long getLastPlayerPacketDiff() {
		return System.currentTimeMillis() - this.getLastPlayerPacket();
	}

	public long getLastPlayerPacket() {
		return this.lastPlayerPacket;
	}
	public void setLastPlayerPacket(long l) {
		this.lastPlayerPacket = l;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Location getSetbackLocation() {
		return setbackLocation;
	}

	public double getGoingUp_Blocks() {
		return GoingUp_Blocks;
	}

	public void setGoingUp_Blocks(double goingUp_Blocks) {
		GoingUp_Blocks = goingUp_Blocks;
	}

	public double getLastY_Gravity() {
		return LastY_Gravity;
	}

	public void setLastY_Gravity(double lastY_Gravity) {
		LastY_Gravity = lastY_Gravity;
	}

	public int getGravity_VL() {
		return Gravity_VL;
	}

	public void setGravity_VL(int gravity_VL) {
		Gravity_VL = gravity_VL;
	}

	public boolean isNearIce() {
		return isNearIce;
	}

	public void setNearIce(boolean nearIce) {
		isNearIce = nearIce;
	}

	public long getIsNearIceTicks() {
		return isNearIceTicks;
	}

	public void setIsNearIceTicks(long isNearIceTicks) {
		this.isNearIceTicks = isNearIceTicks;
	}

	public long getLastVelUpdate() {
		return LastVelUpdate;
	}

	public void setLastVelUpdate(long lastVelUpdate) {
		LastVelUpdate = lastVelUpdate;
	}

	public boolean isLastVelUpdateBoolean() {
		return LastVelUpdateBoolean;
	}

	public void setLastVelUpdateBoolean(boolean lastVelUpdateBoolean) {
		LastVelUpdateBoolean = lastVelUpdateBoolean;
	}

	public int getSpeed2Verbose() {
		return Speed2Verbose;
	}

	public void setSpeed2Verbose(int speed2Verbose) {
		Speed2Verbose = speed2Verbose;
	}

	public long getHalfBlocks_MS() {
		return HalfBlocks_MS;
	}

	public void setHalfBlocks_MS(long halfBlocks_MS) {
		HalfBlocks_MS = halfBlocks_MS;
	}

	public boolean isHalfBlocks_MS_Set() {
		return HalfBlocks_MS_Set;
	}

	public void setHalfBlocks_MS_Set(boolean halfBlocks_MS_Set) {
		HalfBlocks_MS_Set = halfBlocks_MS_Set;
	}

	public long getGlideTicks() {
		return GlideTicks;
	}

	public void setGlideTicks(long glideTicks) {
		GlideTicks = glideTicks;
	}

	public int getSpeed_C_3_Verbose() {
		return Speed_C_3_Verbose;
	}

	public void setSpeed_C_3_Verbose(int speed_C_3_Verbose) {
		Speed_C_3_Verbose = speed_C_3_Verbose;
	}

	public long getSpeed_PistonExpand_MS() {
		return Speed_PistonExpand_MS;
	}

	public void setSpeed_PistonExpand_MS(long speed_PistonExpand_MS) {
		Speed_PistonExpand_MS = speed_PistonExpand_MS;
	}

	public boolean isSpeed_PistonExpand_Set() {
		return Speed_PistonExpand_Set;
	}

	public void setSpeed_PistonExpand_Set(boolean speed_PistonExpand_Set) {
		Speed_PistonExpand_Set = speed_PistonExpand_Set;
	}

	public long getBlockAbove() {
		return BlockAbove;
	}

	public boolean isBlockAbove_Set() {
		return BlockAbove_Set;
	}

	public void setBlockAbove(long blockAbove) {
		BlockAbove = blockAbove;
	}

	public void setBlockAbove_Set(boolean blockAbove_Set) {
		BlockAbove_Set = blockAbove_Set;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public boolean isInLiquid() {
		return inLiquid;
	}


	public boolean isOnIce() {
		return onIce;
	}

	public boolean isOnClimbable() {
		return onClimbable;
	}

	public void setOnClimbable(boolean onClimbable) {
		this.onClimbable = onClimbable;
	}

	public boolean isUnderBlock() {
		return underBlock;
	}

	public static int getWasFlying() {
		return wasFlying;
	}
	public static void setWasFlying(int flying) {
		wasFlying = flying;
	}

	public static int getWasSpider() {
		return wasSpider;
	}

	public static void setWasSpider(int wasSpider) {
		DataPlayer.wasSpider = wasSpider;
	}
}