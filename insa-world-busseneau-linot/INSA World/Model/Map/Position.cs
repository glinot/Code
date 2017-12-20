using System;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of a 2D map position.
    /// <seealso cref="Map"/>
    /// </summary>
    [Serializable]
    public class Position
    {
        /// <summary>
        /// The zero position.
        /// It is the lowest position.
        /// </summary>
        public static readonly Position ZERO = new Position(0, 0);

        /// <summary>
        /// The X position.
        /// The value cannot be outside of the map.
        /// </summary>
        public int X { get; }

        /// <summary>
        /// The Y position
        /// The value cannot be outside of the map.
        /// </summary>
        public int Y { get; }

        /// <summary>
        /// Constructs a position with the specified coordinates.
        /// </summary>
        /// <param name="x">The X position.</param>
        /// <param name="y">The Y position.</param>
        /// <exception cref="ArgumentOutOfRangeException">If x is negative.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If y is negative.</exception>
        public Position(int x, int y)
        {
            if (x < 0)
                throw new ArgumentOutOfRangeException("X was out of range. Must be non-negative.", "x");
            if (y < 0)
                throw new ArgumentOutOfRangeException("Y was out of range. Must be non-negative.", "y");

            X = x;
            Y = y;
        }

        /// <summary>
        /// Indicates whether some other position is "equal to" this one.
        /// </summary>
        /// <param name="obj">The reference object with which to compare.</param>
        /// <returns>True if this position is the same as the obj argument, false otherwise.</returns>
        public override bool Equals(object obj)
        {
            if (obj == null || !(obj is Position) )
                return false;
            var position = obj as Position;
            return X == position.X && Y == position.Y;
        }

        /// <summary>
        /// Computes the square distance between this position and another.
        /// </summary>
        /// <param name="position"></param>
        /// <returns>The squared distance.</returns>
        /// <exception cref="ArgumentNullException">If position is null.</exception>
        public int SquaredDistance(Position position)
        {
            if (position == null)
                throw new ArgumentNullException("position");

            var dx = X - position.X;
            var dy = Y - position.Y;
            return dx * dx + dy * dy;
        }

        /// <summary>
        /// Gets the hashcode of this position.
        /// </summary>
        /// <returns>The hashcode.</returns>
        public override int GetHashCode()
        {
            const int prime = 31;
            var result = 1;
            result = prime * result + X;
            result = prime * result + Y;
            return X*31+Y;
        }

        /// <summary>
        /// Checks if the position p is one position close to the actual one 
        /// </summary>
        /// <param name="p"></param>
        /// <returns></returns>
        public bool IsClose(Position p)
        {
            return (p.X == X + 1 && p.Y == Y) || (p.X == X - 1 && p.Y == Y) || (p.X == X && p.Y == Y + 1) ||
                   (p.X == X && p.Y == Y - 1);
        }
    }   
}