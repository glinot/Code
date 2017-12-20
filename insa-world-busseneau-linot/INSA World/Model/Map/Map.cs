using System;

namespace INSAWorld
{
    /// <summary>
    /// Map of a game.
    /// </summary>
    [Serializable]
    public class Map
    {
        /// <summary>
        /// Gets the size of the map.
        /// </summary>
        /// <returns>The size of the map.</returns>
        public int Size
        {
            get
            {
                return grid.GetLength(0);
            }
        }

        /// <summary>
        /// The 2D grid of tiles of the map.
        /// </summary>
        private readonly ITile[,] grid;

        /// <summary>
        /// Constructs a map from an array of tiles returned by the DLL <see cref="MapGenerator"/>.
        /// </summary>
        /// <param name="grid"></param>
        public Map(ITile[,] grid)
        {
            if (grid == null)
                throw new ArgumentNullException("grid");
            if (grid.GetLength(0) < 2 || grid.GetLength(1) < 2)
                throw new ArgumentOutOfRangeException("Size of the array was out of range. Must be higher than 2.", "grid");
            for (var i = 0; i < grid.GetLength(0); i++)
                for (var j = 0; j < grid.GetLength(1); j++)
                    if (grid[i, j] == null)
                        throw new ArgumentNullException("All elements of the grid must be not null.", "grid[" + i + ", " + j + "]");

            this.grid = grid;
        }

        /// <summary>
        /// Gets the tile at the specified position.
        /// </summary>
        /// <param name="position">The tile position.</param>
        /// <returns>The tile at the specified position.</returns>
        /// <exception cref="ArgumentNullException">If the position is null.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If X is negative or higher than the size of the map.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If Y is negative or higher than the size of the map.</exception>
        public ITile GetTile(Position position)
        {
            if (position == null)
                throw new ArgumentNullException("position");
            if (position.X >= Size)
                throw new ArgumentOutOfRangeException("X position was out of range. Must be lower than the size of the map (" + Size + ").", "position.X");
            if (position.Y >= Size)
                throw new ArgumentOutOfRangeException("Y position was out of range. Must be lower than the size of the map (" + Size + ").", "position.Y");

            return grid[position.X, position.Y];
        }
    }
}