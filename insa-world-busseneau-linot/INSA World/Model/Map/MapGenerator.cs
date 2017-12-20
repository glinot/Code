using System;
using System.Runtime.InteropServices;

namespace INSAWorld
{
    /// <summary>
    /// Map generator to create a map with different strategy.
    /// This class is a wrapper of a C++ DLL.
    /// </summary>
    public class MapGenerator : IDisposable
    {
        /// <summary>
        /// The different strategy types of the map generator.
        /// </summary>
        public enum StrategyType
        {
            Random, Mirror
        };

        /// <summary>
        /// The different tile types available.
        /// </summary>
        private enum TileType
        {
            Desert, Plain, Swamp, Volcano
        };

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static IntPtr MapGenerator_new(TileType[] tileTypes, int length);

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static IntPtr MapGenerator_delete(IntPtr mapGenerator);

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static IntPtr MapGenerator_setGenerator(IntPtr mapGenerator, StrategyType strategy);

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static void MapGenerator_generateMap(IntPtr mapGenerator, TileType[,] grid, int rows, int columns);

        /// <summary>
        /// Tracker to know whether Dispose has been called.
        /// </summary>
        private bool disposed;

        /// <summary>
        /// The pointer to the DLL.
        /// </summary>
        private readonly IntPtr nativeMapGenerator;

        /// <summary>
        /// Constructs the wrapper and create a DLL reference.
        /// </summary>
        public MapGenerator()
        {
            disposed = false;
            // Get the enum values contained in TileType
            var tileTypes = (TileType[]) Enum.GetValues(typeof(TileType));
            nativeMapGenerator = MapGenerator_new(tileTypes, tileTypes.Length);
        }

        /// <summary>
        /// Destructor of the wrapper which delete the DLL reference.
        /// </summary>
        ~MapGenerator()
        {
            Dispose(false);
            MapGenerator_delete(nativeMapGenerator);
        }

        /// <summary>
        /// Prevents multiple executions.
        /// </summary>
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>
        /// Avoids to reference another DLL object.
        /// </summary>
        /// <param name="disposing"></param>
        protected virtual void Dispose(bool disposing)
        {
            if (disposed)
                return;
            if (disposing)
                MapGenerator_delete(nativeMapGenerator);
            disposed = true;
        }

        /// <summary>
        /// Sets the strategy to initialize the map.
        /// </summary>
        /// <param name="strategy">The strategy to set.</param>
        /// <returns>The instance of the map generator.</returns>
        public MapGenerator SetGenerator(StrategyType strategy)
        {
            MapGenerator_setGenerator(nativeMapGenerator, strategy);

            return this;
        }

        /// <summary>
        /// Generates the map from the chosen strategy.
        /// </summary>
        /// <param name="size">The size of the map.</param>
        /// <returns>The generated map.</returns>
        /// <exception cref="ArgumentOutOfRangeException">If the size of the map is lower than 2.</exception>
        /// <exception cref="NotImplementedException">If a tile returned by the DLL is not known in the wrapper.</exception>
        public Map GenerateMap(int size)
        {
            if (size < 2)
                throw new ArgumentOutOfRangeException("Size of the map was out of range. Must be higher than 2.", "size");

            var grid = new TileType[size, size];
            MapGenerator_generateMap(nativeMapGenerator, grid, size, size);

            var tilesGrid = new ITile[size, size];
            for (var i = 0; i < size; i++)
                for (var j = 0; j < size; j++)
                    if (grid[i, j] == TileType.Desert)
                        tilesGrid[i, j] = TileFactory.Instance.Desert;
                    else if (grid[i, j] == TileType.Plain)
                        tilesGrid[i, j] = TileFactory.Instance.Plain;
                    else if (grid[i, j] == TileType.Swamp)
                        tilesGrid[i, j] = TileFactory.Instance.Swamp;
                    else if (grid[i, j] == TileType.Volcano)
                        tilesGrid[i, j] = TileFactory.Instance.Volcano;
                    else
                        throw new NotImplementedException("The tile [" + i + "," + j + "] doesn't match to an implemented tile.");
            return new Map(tilesGrid);
        }
    }
}