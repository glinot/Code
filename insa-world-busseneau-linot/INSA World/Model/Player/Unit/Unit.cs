using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace INSAWorld
{
    /// <summary>
    /// Abstract implementation of a unit of the game.
    /// Each race has its own unit type.
    /// </summary>
    [Serializable]
    public abstract class Unit
    {
        /// <summary>
        /// The max move points that an unit can have.
        /// At the beginning, the move is set to this value.
        /// </summary>
        public const double MAX_MOVE_POINTS = 3.0;

        /// <summary>
        /// The initial health points of the unit.
        /// An unit cannot have more health points than the initial health points.
        /// </summary>
        public int InitialHealthPoints { get; }

        /// <summary>
        /// The unit attack points.
        /// </summary>
        public int AttackPoints { get; }

        /// <summary>
        /// The unit defense points.
        /// </summary>
        public int DefensePoints { get; }

        private int healthPoints;

        /// <summary>
        /// The current unit health points.
        /// Cannot be higher than the initial health points.
        /// When the value is 0, the unit is dead.
        /// </summary>
        /// <exception cref="ArgumentOutOfRangeException">If the new health points is lower than 0 or higher than the max health points.</exception>
        public int HealthPoints
        {
            get
            {
                return healthPoints;
            }

            set {
                if (value < 0 || value > InitialHealthPoints)
                    throw new ArgumentOutOfRangeException("Health points was out of range. Must be non-negative and lower than or equals to the initial life points (" + InitialHealthPoints + "). value = " + value, "value");

                healthPoints = value;
            }
        }

        private double movePoints;

        /// <summary>
        /// The unit moves points.
        /// Cannot be higher than the max move points.
        /// When the value is 0, the unit cannot attack or move.
        /// </summary>
        /// <exception cref="ArgumentOutOfRangeException">If the new move points is lower than 0 or higher than the max move points.</exception>
        public double MovePoints
        {
            get
            {
                return movePoints;
            }

            set
            {
                if (value < 0 || value > MAX_MOVE_POINTS)
                    throw new ArgumentOutOfRangeException("Move points was out of range. Must be non-negative and lower than or equals to the maximum move points (" + MAX_MOVE_POINTS + ").", "value");

                movePoints = value;
            }
        }
        
        private Position position;

        /// <summary>
        /// The unit position.
        /// The position shall be a valid position on the map.
        /// </summary>
        /// <exception cref="ArgumentNullException">If the position is null.</exception>
        public Position Position
        {
            get
            {
                return position;
            }

            set
            {
                if (value == null)
                    throw new ArgumentNullException("value");

                position = value;
            }
        }

        /// <summary>
        /// Constructs an unit with its characteristics initialized thanks to the specified values.
        /// </summary>
        /// <param name="initialHealthPoints">The initial unit health points.</param>
        /// <param name="attackPoints">The unit attack points.</param>
        /// <param name="defensePoints">The unit defense points.</param>
        /// <param name="initialPosition">The initial unit position.</param>
        /// <exception cref="ArgumentOutOfRangeException">If the initial health points is lower than 1.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If the attack points is negative.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If the defense points is negative.</exception>
        /// <exception cref="ArgumentNullException">If the initial position is null.</exception>
        protected Unit(int initialHealthPoints, int attackPoints, int defensePoints, Position initialPosition)
        {
            if (initialHealthPoints < 1)
                throw new ArgumentOutOfRangeException("Initial health points was out of range. Must be higher than or equals to 1.", "initialHealthPoints");
            if (attackPoints < 0)
                throw new ArgumentOutOfRangeException("Attack points was out of range. Must be non-negative.", "attackPoints");
            if (defensePoints < 0)
                throw new ArgumentOutOfRangeException("Defense points was out of range. Must be non-negative.", "defensePoints");
            if (initialPosition == null)
                throw new ArgumentNullException("initialPosition");

            InitialHealthPoints = initialHealthPoints;
            AttackPoints = attackPoints;
            DefensePoints = defensePoints;
            HealthPoints = InitialHealthPoints;
            MovePoints = MAX_MOVE_POINTS;
            Position = initialPosition;
        }

        /// <summary>
        /// Resets the unit move points to the max move points.
        /// This method is ignored if the unit is dead.
        /// </summary>
        /// <returns>True if the unit move points has been reset, false otherwise.</returns>
        public bool ResetMove()
        {
            if (!IsDead())
            {
                MovePoints = MAX_MOVE_POINTS;
                return true;
            }

            return false;
        }

        /// <summary>
        /// Recovers one health point.
        /// This method is ignored if the unit is dead.
        /// </summary>
        /// <returns>True if the unit has recovered one health point, false otherwise.</returns>
        public bool RecoverHealth()
        {
            if (!IsDead() && MovePoints == MAX_MOVE_POINTS && HealthPoints < InitialHealthPoints)
            {
                HealthPoints++;
                return true;
            }

            return false;
        }
        
        /// <summary>
        /// Checks whether the unit is dead.
        /// </summary>
        /// <returns>True is the unit is dead, false otherwise.</returns>
        public bool IsDead()
        {
            return HealthPoints == 0;
        }

        /// <summary>
        /// Computes real unit attack points according to its current health points value.
        /// An unit which has the max health points has no penalty.
        /// </summary>
        /// <returns>The real unit attack points.</returns>
        public double GetRealAttackPoints()
        {
            return AttackPoints * HealthPoints / (double) InitialHealthPoints;
        }

        /// <summary>
        /// Computes real unit defense points according to its current health points value.
        /// An unit which has the max health points has no penalty.
        /// </summary>
        /// <returns>The real unit defense points.</returns>
        public double GetRealDefensePoints()
        {
            return DefensePoints * HealthPoints / (double) InitialHealthPoints;
        }

        private class PathItem
        {
            private Position position;
            public Position Position
            {
                get
                {
                    return position;
                }

                set
                {
                    if (value == null)
                        throw new ArgumentNullException("value");

                    position = value;
                }
            }
            public double Cost { get; set;  }

            public PathItem(Position position, double cost)
            {
                Position = position;
                Cost = cost;
            }
        }

        /// <summary>
        /// Gives the move points needed for the Unit to go on the Tile pointed by the position
        /// </summary>
        /// <param name="map"></param>
        /// <param name="position"></param>
        /// <returns></returns>
        protected virtual double GetMovePoints(Map map, Position position)
        {
            if (map == null)
                throw new ArgumentNullException("map");
            if (position == null)
                throw new ArgumentNullException("position");

            return 1;
        }


        /// <summary>
        ///  Modified Djikstra method for finding the best way between two points 
        /// </summary>
        /// <param name="map"></param>
        /// <param name="paths"></param>
        /// <param name="lastPosition"></param>
        /// <param name="position"></param>
        /// <param name="target"></param>
        private void FindBestWay(Map map, PathItem[,] paths, Position lastPosition, Position position, Position target)
        {
            var oldCost = paths[lastPosition.X, lastPosition.Y].Cost;
            var cost = GetMovePoints(map, position);
            var newCost = cost + oldCost;

            if (newCost > movePoints)
                return;
            if (paths[position.X, position.Y] == null)
                paths[position.X, position.Y] = new PathItem(lastPosition, newCost);
            else
                if (newCost > paths[position.X, position.Y].Cost)
                    return;
                else
                {
                    paths[position.X, position.Y].Position = lastPosition;
                    paths[position.X, position.Y].Cost = newCost;
                }

            if (position.Equals(target))
                return;

            var size = map.Size;

            if (position.X - 1 >= 0)
                FindBestWay(map, paths, position, new Position(position.X - 1, position.Y), target);
            if (position.X + 1 < size)
                FindBestWay(map, paths, position, new Position(position.X + 1, position.Y), target);
            if (position.Y - 1 >= 0)
                FindBestWay(map, paths, position, new Position(position.X, position.Y - 1), target);
            if (position.Y + 1 < size)
                FindBestWay(map, paths, position, new Position(position.X, position.Y + 1), target);
        }

        /// <summary>
        /// Djikstra entry point 
        /// </summary>
        /// <param name="map"></param>
        /// <param name="position"></param>
        /// <returns></returns>
       
        private PathItem GetBestWay(Map map, Position position)
        {
            var size = map.Size;
            var paths = new PathItem[size, size];

            paths[position.X, position.Y] = new PathItem(position, 0);
            if (position.X - 1 >= 0)
                FindBestWay(map, paths, position, new Position(position.X - 1, position.Y), this.position);
            if (position.X + 1 < size)
                FindBestWay(map, paths, position, new Position(position.X + 1, position.Y), this.position);
            if (position.Y - 1 >= 0)
                FindBestWay(map, paths, position, new Position(position.X, position.Y - 1), this.position);
            if (position.Y + 1 < size)
                FindBestWay(map, paths, position, new Position(position.X, position.Y + 1), this.position);

            // Find best path.
            return paths[this.position.X, this.position.Y];
        }

        /// <summary>
        /// Compute Path from unit point to position
        /// </summary>
        /// <param name="map"></param>
        /// <param name="p"></param>
        /// <returns>List of positions including the beginning and the end positions</returns>
        public IList<Position> GetWay(Map map , Position p)
        {
            var size = map.Size;
            var paths = new PathItem[size, size];

            paths[position.X, position.Y] = new PathItem(position, 0);
            if (position.X - 1 >= 0)
                FindBestWay(map, paths, position, new Position(position.X - 1, position.Y), this.position);
            if (position.X + 1 < size)
                FindBestWay(map, paths, position, new Position(position.X + 1, position.Y), this.position);
            if (position.Y - 1 >= 0)
                FindBestWay(map, paths, position, new Position(position.X, position.Y - 1), this.position);
            if (position.Y + 1 < size)
                FindBestWay(map, paths, position, new Position(position.X, position.Y + 1), this.position);

            IList<Position> path = new List<Position>();
            path.Add(p);
            Position cpos = paths[p.X, p.Y].Position;
            while (!cpos.Equals(position))
            {
                path.Add(cpos);
                cpos = paths[cpos.X, cpos.Y].Position;
            }
            path.Add(cpos);
            return path.AsEnumerable().Reverse().ToList();

        }
        /// <summary>
        /// Computes the cost in move points to do the action.
        /// </summary>
        /// <param name="map">The map where the unit is positioned.</param>
        /// <param name="position">The unit position.</param>
        /// <returns>The cost in move points of the action.</returns>
        /// <exception cref="ArgumentNullException">If the map is null.</exception>
        /// <exception cref="ArgumentNullException">If the position is null.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If the position is not valid.</exception>
        public double CostAction(Map map, Position position)
        {
            if (map == null)
                throw new ArgumentNullException("map");
            if (position == null)
                throw new ArgumentNullException("position");
            if (position.X < 0 || position.X >= map.Size || position.Y < 0 || position.Y >= map.Size)
                throw new ArgumentOutOfRangeException("Position is invalid, it is not a map position.", "position");

            var bestWay = GetBestWay(map, position);
            return bestWay == null ?  -1 : bestWay.Cost;
        }

        public IList<Position> FindReachablePositions(Map map)
        {
            var list = new List<Position>();
            for (var i = 0; i < map.Size; i++)
                for (var j = 0; j < map.Size; j++)
                {
                    var position = new Position(i, j);
                    if (CostAction(map, position) >= 0)
                        list.Add(position); 
                }

            return list;
        }
    }
}