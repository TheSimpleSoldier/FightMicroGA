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
    private double coreDelay;
    private double weaponsDelay;
    private double health;
    private double supply;
    private int xp;
    private int missileCount;
    private double totalDamageDealt;

    public MockRobotController(Team team, RobotType robotType, MapLocation location, Map map)
    {
        this.team = team;
        this.robotType = robotType;
        this.location = location;
        this.map = map;

        if (robotType == RobotType.SOLDIER)
        {
            this.health = RobotType.SOLDIER.maxHealth;
        }
        else
        {
            throw new Error("Robot Type does not get health assigned: " + robotType);
        }

        this.missileCount = 0;
        this.xp = 0;
        this.coreDelay = 0;
        this.weaponsDelay = 0;
        this.supply = 0;
        this.totalDamageDealt = 0;
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
        if (loc.distanceSquaredTo(getLocation()) <= getType().attackRadiusSquared)
        {
            totalDamageDealt += getType().attackPower;
            map.attackLocation(loc, getType().attackPower);
//            System.out.println("Robot on Team: " + getTeam() + " had dealt: " + getType().attackPower + " damage");
        }
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
        if (loc.distanceSquaredTo(getLocation()) <= getType().attackRadiusSquared)
        {
            return true;
        }
        return false;
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
        if (this.coreDelay >= 1)
        {
            return false;
        }

        if (dir == Direction.OMNI || dir == Direction.NONE)
        {
            return false;
        }

        if (map.locationOccupied(getLocation().add(dir)))
        {
            return false;
        }

        return map.terranTraversalbe(getLocation().add(dir), getType());
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
        return this.coreDelay;
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
        return this.health;
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
        return this.missileCount;
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
        return this.supply;
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
        return this.weaponsDelay;
    }

    /**
     * Gets the experience a robot has.
     *
     * @return
     */
    public int getXP()
    {
        return this.xp;
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
        return this.coreDelay < 1;
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
        if (this.weaponsDelay < 1)
        {
            return true;
        }
        else
        {
            return false;
        }
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
        if (dir == Direction.NONE || dir == Direction.OMNI)
        {
            System.out.println("Moving in dir None or Omni");
        }
        else if (canMove(dir))
        {
            map.moveRobot(getLocation(), getLocation().add(dir));
            location = getLocation().add(dir);

            if (robotType == RobotType.SOLDIER)
            {
                coreDelay += RobotType.SOLDIER.movementDelay;
                weaponsDelay += RobotType.SOLDIER.loadingDelay;
            }
        }
        else
        {
            System.out.println("Trying to move where we can't");
        }
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
        return map.getHQLocation(team.opponent());
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
        return map.getAllRobotsInRange(getLocation(), 1000000);
    }

    /**
     * Returns all robots that can be sensed within a certain radius of the robot.
     *
     * @param radiusSquared
     * @return
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared)
    {
        return map.getAllRobotsInRange(getLocation(), radiusSquared);
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
        RobotInfo[] allBots = map.getAllRobotsInRange(getLocation(), radiusSquared);
        RobotInfo[] teamBots;
        int count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                count++;
            }
        }

        teamBots = new RobotInfo[count];
        count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                teamBots[count] = allBots[i];
                count++;
            }
        }

        return teamBots;
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
        RobotInfo[] allBots = map.getAllRobotsInRange(center, radiusSquared);
        RobotInfo[] teamBots;
        int count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                count++;
            }
        }

        teamBots = new RobotInfo[count];
        count = 0;

        for (int i = 0; i < allBots.length; i++)
        {
            if (allBots[i].team == team)
            {
                count++;
            }
        }

        return teamBots;
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
        if (loc.x < 0 || loc.x >= map.mapLayout.length)
        {
            return null;
        }
        if (loc.y < 0 || loc.y >= map.mapLayout[loc.x].length)
        {
            return null;
        }

        return map.mapLayout[loc.x][loc.y].getTerrain();
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
        if (coreDelay > 1)
        {
            coreDelay--;
        }
        else
        {
            coreDelay = 0;
        }

        if (weaponsDelay > 1)
        {
            weaponsDelay--;
        }
        else
        {
            weaponsDelay = 0;
        }
    }


    public void takeDamage(double damage)
    {
        this.health -= damage;
    }

    public double getTotalDamageDealt()
    {
        return this.totalDamageDealt;
    }
}
