package Simulation;

import battlecode.common.*;

public class MockMapLocation
{
    public int x;
    public int y;

    public MockMapLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a new MapLocation object representing a location one square from this one in the given direction.
     *
     * @param direction
     * @return
     */
    public MockMapLocation add(Direction direction)
    {
        int newX = x;
        int newY = y;

        if (direction == Direction.EAST || direction == Direction.NORTH_EAST || direction == Direction.SOUTH_EAST)
        {
            newX++;
        }
        else if (direction == Direction.NORTH_WEST || direction == Direction.SOUTH_WEST || direction == Direction.WEST)
        {
            newX--;
        }

        if (direction == Direction.NORTH || direction == Direction.NORTH_EAST || direction == Direction.NORTH_WEST)
        {
            newY--;
        }
        else if (direction == Direction.SOUTH || direction == Direction.SOUTH_EAST || direction == Direction.SOUTH_WEST)
        {
            newY++;
        }

        return new MockMapLocation(newX, newY);
    }

    /**
     * Returns a new MapLocation object representing a location multiple squares from this one in the given direction.
     *
     * @param direction
     * @param multiple
     * @return
     */
    public MockMapLocation add(Direction direction, int multiple)
    {
        MockMapLocation newSpot = new MockMapLocation(x, y);

        for (int i = 0; i < multiple; i++)
        {
            newSpot = newSpot.add(direction);
        }

        return newSpot;
    }

    /**
     * Returns a new MapLocation object translated from this location by a fixed amount
     *
     * @param dx
     * @param dy
     * @return
     */
    public MockMapLocation add(int dx, int dy)
    {
        return new MockMapLocation(x + dx, y + dy);
    }

    /**
     * A comparison function for MapLocations.
     *
     * @param other
     * @return
     */
    public int	compareTo(MockMapLocation other)
    {
        throw new Error("CompareTo on MockMapLocation not implemented");
    }

    /**
     * Returns the Direction from this MapLocation to location.
     *
     * @param location
     * @return
     */
    public Direction directionTo(MockMapLocation location)
    {
        Direction dir;
        int diffX = location.x - this.x;
        int diffY = location.y - this.y;

        if (diffX == 0 && diffY == 0)
        {
            dir = Direction.NONE;
        }

        // Options are North, South, West
        if (diffX < 0)
        {
            // Options are South or West
            if (diffY < 0)
            {
                // options are SW or S
                if ((2 * diffY) < diffX)
                {
                    if (diffY < (diffX * 2))
                    {
                        dir = Direction.SOUTH;
                    }
                    else
                    {
                        dir = Direction.SOUTH_WEST;
                    }
                }
                else
                {
                    dir = Direction.WEST;
                }
            }
            // Options are North or West
            else
            {
                // options are SE or S
                if ((-2 * diffY) < diffX)
                {
                    if (diffY < (diffX * -2))
                    {
                        dir = Direction.NORTH;
                    }
                    else
                    {
                        dir = Direction.NORTH_WEST;
                    }
                }
                else
                {
                    dir = Direction.WEST;
                }
            }
        }
        // could be North, East, or South
        else
        {
            // Options are South or East
            if (diffY < 0)
            {
                // options are SE or S
                if ((-2 * diffY) < diffX)
                {
                    if (diffY < (diffX * -2))
                    {
                        dir = Direction.SOUTH;
                    }
                    else
                    {
                        dir = Direction.SOUTH_EAST;
                    }
                }
                else
                {
                    dir = Direction.EAST;
                }
            }
            // Options are North or East
            else
            {
                // options are N or NE
                if ((2 * diffY) < diffX)
                {
                    if (diffY < (diffX * 2))
                    {
                        dir = Direction.NORTH;
                    }
                    else
                    {
                        dir = Direction.NORTH_EAST;
                    }
                }
                else
                {
                    dir = Direction.EAST;
                }
            }
        }

        return dir;
    }

    /**
     * Computes the square of the distance from this location to the specified location.
     *
     * @param location
     * @return
     */
    public int distanceSquaredTo(MockMapLocation location)
    {
        return (location.x - this.x) * (location.x - this.x) + (location.y - this.y) * (location.y - this.y);
    }

    /**
     * Two MapLocations are regarded as equal iff their coordinates are the same.
     *
     * @param obj
     * @return
     */
    public boolean equals(MockMapLocation obj)
    {
        return this.x == obj.x && this.y == obj.y;
    }

    /**
     * Returns an array of all MapLocations within a certain radius squared of a specified
     * location (cannot be called with radiusSquared greater than 100).
     *
     * @param center
     * @param radiusSquared
     * @return
     */
    public static MockMapLocation[] getAllMapLocationsWithinRadiusSq(MockMapLocation center, int radiusSquared)
    {
        MockMapLocation[] area = new MockMapLocation[(int) (radiusSquared * Math.PI)];
        int currentIndex = 0;

        if (radiusSquared <= 100)
        {
            for (int i = (int) (-1 * Math.sqrt(radiusSquared)); i <= Math.sqrt(radiusSquared); i++)
            {
                for (int j = (int) (-1 * Math.sqrt(radiusSquared / (i*i))); (j*j) + (i*i) <= radiusSquared; j++)
                {
                    area[currentIndex] = new MockMapLocation(i, j);
                    currentIndex++;
                }
            }
        }

        return area;
    }

    /**
     * Determines whether this location is adjacent to the specified location.
     *
     * @param location
     * @return
     */
    public boolean isAdjacentTo(MockMapLocation location)
    {
        if (Math.abs(location.x - this.x) <= 1 && Math.abs(location.y - this.y) < 1)
        {
            return true;
        }

        return false;
    }

    /**
     * Returns a new MapLocation object representing a location one square
     * from this one in the opposite of the given direction.
     *
     * @param direction
     * @return
     */
    public MockMapLocation subtract(Direction direction)
    {
        return this.add(direction.opposite());
    }

    public String toString()
    {
        return "MockMapLocation x: " + this.x + ", y: " + y;
    }
}
