package Simulation;

import battlecode.common.*;

/**
 * Created by fred on 12/8/15.
 */
public class MockRobotController implements RobotController
{
    private Team team;
    private RobotType robotType;
    private MapLocation location;
    private Map map;

    public MockRobotController(Team team, RobotType robotType, MapLocation location, Map map)
    {
        this.team = team;
        this.robotType = robotType;
        this.location = location;
        this.map = map;
    }

    /**
     * Adds a custom observation to the match file, such that when it is analyzed, this observation will appear.
     *
     * @param observation
     */
    public void addMatchObservation(String observation)
    {
        throw new Error("addMatchObservation Not implemented");
    }

    /**
     * Queues an attack on the given location to be performed at the end of this turn.
     *
     * @param loc
     */
    public void attackLocation(MapLocation loc)
    {
        throw new Error("attackLocation Not implemented");
    }

    /**
     * If breakpoints are enabled, calling this method causes the game engine to pause execution at the end of this round, until the user decides to resume execution.
     */
    public void	breakpoint()
    {
        throw new Error("breakpoint Not implemented");
    }

    /**
     * Broadcasts a message to the global message board.
     *
     * @param channel
     * @param data
     */
    public void broadcast(int channel, int data)
    {
        throw new Error("broadcast Not implemented");
    }

    /**
     * Builds a structure in the given direction, queued for the end of the turn.
     *
     * @param dir
     * @param type
     */
    public void	build(Direction dir, RobotType type)
    {
        throw new Error("build Not implemented");
    }

    /**
     * Returns whether the given location is within the robot's attack range.
     *
     * @param loc
     * @return
     */
    public boolean canAttackLocation(MapLocation loc)
    {
        throw new Error("canAttackLocation Not implemented");
    }

    /**
     * Returns whether the robot can build a structure of the given type in the given direction, without taking delays into account.
     *
     * @param dir
     * @param type
     * @return
     */
    public boolean canBuild(Direction dir, RobotType type)
    {
        throw new Error("canBuild Not implemented");
    }

    /**
     * Returns whether the direction is valid for launching (LAUNCHER only).
     *
     * @param dir
     * @return
     */
    public boolean canLaunch(Direction dir)
    {
        throw new Error("canLaunch Not implemented");
    }

    /**
     * Returns whether the robot is able to mine, without taking delays into account.
     *
     * @return
     */
    public boolean canMine()
    {
        throw new Error("canMine Not implemented");
    }

    /**
     * Tells whether this robot can move in the given direction, without taking any sort of delays into account.
     *
     * @param dir
     * @return
     */
    public boolean canMove(Direction dir)
    {
        throw new Error("canMove Not implemented");
    }

    /**
     * Returns true if the given location is within the robot's sensor range, or within the sensor range of some ally.
     *
     * @param loc
     * @return
     */
    public boolean canSenseLocation(MapLocation loc)
    {
        throw new Error("canSenseLocation Not implemented");
    }

    /**
     * Returns true if the given robot is within the robot's sensor range.
     *
     * @param id
     * @return
     */
    public boolean	canSenseRobot(int id)
    {
        throw new Error("canSenseRobot Not implemented");
    }

    /**
     * returns whether the spawn action is valid, without taking delays into account.
     *
     * @param dir
     * @param type
     * @return
     */
    public boolean canSpawn(Direction dir, RobotType type)
    {
        throw new Error("canSpawn Not implemented");
    }

    /**
     * Casts Flash at the given location (COMMANDER only).
     *
     * @param loc
     */
    public void castFlash(MapLocation loc)
    {
        throw new Error("castFlash Not implemented");
    }

    /**
     * Gets the current progress of a dependency (relevant for building structures).
     *
     * @param type
     * @return
     */
    public DependencyProgress checkDependencyProgress(RobotType type)
    {
        throw new Error("Dependency Progress Not implemented");
    }

    /**
     * Kills your robot and ends the current round.
     */
    public void	disintegrate()
    {
        throw new Error("Disintegrate Not implemented");
    }

    /**
     * Attacks all surrounding enemies (MISSILE only).
     */
    public void explode()
    {
        throw new Error("explode Not implemented");
    }

    /**
     * Gets this robot's 'control bits' for debugging purposes.
     *
     * @return
     */
    public long	getControlBits()
    {
        throw new Error("getControlBits Not implemented");
    }

    /**
     * Returns the amount of core delay a robot has accumulated.
     *
     * @return
     */
    public double getCoreDelay()
    {
        throw new Error("getCoreDelay Not implemented");
    }

    /**
     * Returns the current cooldown of FLASH (COMMANDER only).
     *
     * @return
     */
    public int getFlashCooldown()
    {
        throw new Error("getFlashCooldown Not implemented");
    }

    /**
     * Gets the robot's current health.
     *
     * @return
     */
    public double getHealth()
    {
        throw new Error("getHealth Not implemented");
    }

    /**
     * Use this method to access your ID.
     *
     * @return
     */
    public int getID()
    {
        throw new Error("getID Not implemented");
    }

    /**
     * Gets the robot's current location.
     *
     * @return
     */
    public MapLocation getLocation()
    {
        return location;
    }

    /**
     * Returns how many missiles the robot has.
     *
     * @return
     */
    public int getMissileCount()
    {
        throw new Error("getMissileCount Not implemented");
    }

    /**
     * Gets the number of rounds in the game.
     *
     * @return
     */
    public int getRoundLimit()
    {
        throw new Error("getRoundLimit Not implemented");
    }

    /**
     * Gets the robot's current supply level.
     *
     * @return
     */
    public double getSupplyLevel()
    {
        throw new Error("getSupplyLevel Not implemented");
    }

    /**
     * Gets the Team of this robot.
     *
     * @return
     */
    public Team	getTeam()
    {
        return this.team;
    }

    /**
     * Returns the team memory from the last game of the match.
     *
     * @return
     */
    public long[] getTeamMemory()
    {
        throw new Error("getTeamMemory Not implemented");
    }

    /**
     * Gets the team's total ore.
     *
     * @return
     */
    public double getTeamOre()
    {
        throw new Error("getTeamOre Not implemented");
    }

    /**
     * Gets this robot's type (SOLDIER, etc.).
     *
     * @return
     */
    public RobotType getType()
    {
        return this.robotType;
    }

    /**
     * Returns the amount of weapon delay a robot has accumulated.
     *
     * @return
     */
    public double getWeaponDelay()
    {
        throw new Error("getWeaponDelay Not implemented");
    }

    /**
     * Gets the experience a robot has.
     *
     * @return
     */
    public int getXP()
    {
        throw new Error("getXP Not implemented");
    }

    /**
     * Returns whether you have the ore and the dependencies to build the given robot, and that the robot can build structures.
     *
     * @param type
     * @return
     */
    public boolean hasBuildRequirements(RobotType type)
    {
        throw new Error("hasBuildRequirements Not implemented");
    }

    /**
     * Returns whether the team currently has a commander.
     *
     * @return
     */
    public boolean hasCommander()
    {
        throw new Error("hasCommander Not implemented");
    }

    /**
     * Returns whether the robot has learned a skill (only relevant if used by a COMMANDER).
     *
     * @param skill
     * @return
     */
    public boolean hasLearnedSkill(CommanderSkillType skill)
    {
        throw new Error("hasLearnedSkill Not implemented");
    }

    /**
     * Checks to make sure you have the ore requirements to spawn, and that the structure
     * can actually spawn the specified RobotType.
     *
     * @param type
     * @return
     */
    public boolean hasSpawnRequirements(RobotType type)
    {
        throw new Error("hasSpawnRequirements Not implemented");
    }

    /**
     * Returns whether this robot is currently building anything.
     *
     * @return
     */
    public boolean	isBuildingSomething()
    {
        throw new Error("isBuildingSomething Not implemented");
    }

    /**
     * Returns whether the core delay is strictly less than 1 (whether the robot can perform a core action in the given turn).
     *
     * @return
     */
    public boolean isCoreReady()
    {
        throw new Error("isCoreReady Not implemented");
    }

    /**
     * Returns whether there is a robot at the given location.
     *
     * @param loc
     * @return
     */
    public boolean isLocationOccupied(MapLocation loc)
    {
        throw new Error("isLocationOccupied Not implemented");
    }

    /**
     * Returns whether a robot of the given type can move into the given location, without taking any sort of delays into account.
     *
     * @param type
     * @param loc
     * @return
     */
    public boolean isPathable(RobotType type, MapLocation loc)
    {
        throw new Error("isPathable Not implemented");
    }

    /**
     * Returns whether the weapon delay is less than 1 (whether the robot can attack in the given turn).
     *
     * @return
     */
    public boolean isWeaponReady()
    {
        throw new Error("isWeaponReady Not implemented");
    }

    /**
     * Launches a missile in the given direction (LAUNCHER only).
     *
     * @param dir
     */
    public void launchMissile(Direction dir)
    {
        throw new Error("launchMissile Not implemented");
    }

    /**
     * Queues a move in the given direction to be performed at the end of this turn.
     *
     * @param dir
     */
    public void move(Direction dir)
    {
        throw new Error("move Not implemented");
    }

    /**
     * Mines the current square for ore.
     */
    public void	mine()
    {
        throw new Error("mine Not implemented");
    }

    /**
     * Retrieves the message stored at the given radio channel.
     *
     * @param channel
     * @return
     */
    public int readBroadcast(int channel)
    {
        throw new Error("readBroadcast Not implemented");
    }

    /**
     * Causes your team to lose the game.
     */
    public void	resign()
    {
        throw new Error("resign Not implemented");
    }

    /**
     * Returns location of the enemy team's HQ (unconstrained by sensor range or distance).
     *
     * @return
     */
    public MapLocation senseEnemyHQLocation()
    {
        throw new Error("senseEnemyHQLocation Not implemented");
    }

    /**
     * Returns the locations of surviving enemy towers, unconstrained by sensor range or distance.
     *
     * @return
     */
    public MapLocation[] senseEnemyTowerLocations()
    {
        throw new Error("senseEnemyTowerLocations Not implemented");
    }

    /**
     * Returns location of the allied team's HQ (unconstrained by sensor range or distance).
     *
     * @return
     */
    public MapLocation senseHQLocation()
    {
        if (team == Team.A)
        {
            return map.teamAHQ;
        }
        else
        {
            return map.teamBHQ;
        }
    }

    /**
     * Returns all robots that can be sensed on the map.
     *
     * @return
     */
    public RobotInfo[] senseNearbyRobots()
    {
        throw new Error("senseNearbyRobots Not implemented");
    }

    /**
     * Returns all robots that can be sensed within a certain radius of the robot.
     *
     * @param radiusSquared
     * @return
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared)
    {
        throw new Error("senseNearbyRobots Not implemented");
    }

    /**
     * Returns all robots of a given team that can be sensed within a certain radius of the robot.
     *
     * @param radiusSquared
     * @param team
     * @return
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared, Team team)
    {
        throw new Error("senseNearbyRobots Not implemented");
    }

    /**
     * Returns all robots of a givin team that can be sensed within a certain radius of a specified location.
     *
     * @param center
     * @param radiusSquared
     * @param team
     * @return
     */
    public RobotInfo[] senseNearbyRobots(MapLocation center, int radiusSquared, Team team)
    {
        throw new Error("senseNearbyRobots Not implemented");
    }

    /**
     * Returns the amount of ore at a given location (to within sensor capabilities).
     *
     * @param loc
     * @return
     */
    public double senseOre(MapLocation loc)
    {
        throw new Error("senseOre Not implemented");
    }

    /**
     * Senses information about a particular robot given its ID.
     *
     * @param id
     * @return
     */
    public RobotInfo senseRobot(int id)
    {
        throw new Error("senseRobot Not implemented");
    }

    /**
     * Returns the robot at the given location, or null if there is no object there.
     *
     * @param loc
     * @return
     */
    public RobotInfo senseRobotAtLocation(MapLocation loc)
    {
        throw new Error("senseRobotAtLocation Not implemented");
    }

    /**
     * Senses the terrain at the given location.
     *
     * @param loc
     * @return
     */
    public TerrainTile senseTerrainTile(MapLocation loc)
    {
        throw new Error("senseTerrainTile Not implemented");
    }

    /**
     * Returns the locations of your own towers, unconstrained by sensor range or distance.
     *
     * @return
     */
    public MapLocation[] senseTowerLocations()
    {
        throw new Error("senseTowerLocations Not implemented");
    }

    /**
     * Draws a dot on the game map, for debugging purposes.
     *
     * @param loc
     * @param red
     * @param green
     * @param blue
     */
    public void	setIndicatorDot(MapLocation loc, int red, int green, int blue)
    {
        throw new Error("setIndicatorDot Not implemented");
    }

    /**
     * Draws a line on the game map, for debugging purposes.
     *
     * @param from
     * @param to
     * @param red
     * @param green
     * @param blue
     */
    public void	setIndicatorLine(MapLocation from, MapLocation to, int red, int green, int blue)
    {
        throw new Error("setIndicatorLine Not implemented");
    }

    /**
     * Sets one of this robot's 'indicator strings' for debugging purposes.
     *
     * @param stringIndex
     * @param newString
     */
    public void	setIndicatorString(int stringIndex, String newString)
    {
        throw new Error("setIndicatorString Not implemented");
    }

    /**
     * Sets the team's "memory", which is saved for the next game in the match.
     *
     * @param index
     * @param value
     */
    public void	setTeamMemory(int index, long value)
    {
        throw new Error("setTeamMemory Not implemented");
    }

    /**
     * Sets this team's "memory".
     *
     * @param index
     * @param value
     * @param mask
     */
    public void	setTeamMemory(int index, long value, long mask)
    {
        throw new Error("setTeamMemory Not implemented");
    }

    /**
     * Queues a spawn action to be performed at the end of this robot's turn.
     *
     * @param dir
     * @param type
     */
    public void	spawn(Direction dir, RobotType type)
    {
        throw new Error("spawn Not implemented");
    }

    /**
     * Transfers supplies to a robot in a nearby location (queued for the end of the turn).
     *
     * @param amount
     * @param loc
     */
    public void transferSupplies(int amount, MapLocation loc)
    {
        throw new Error("transferSupplies Not implemented");
    }

    /**
     * Ends the current round.
     */
    public void	yield()
    {
        throw new Error("yield Not implemented");
    }
}
