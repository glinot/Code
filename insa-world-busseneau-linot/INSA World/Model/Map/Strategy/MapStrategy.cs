using System;

namespace INSAWorld
{
    /// <summary>
    /// Abstract strategy to initialize a map.
    /// </summary>
    public abstract class MapStrategy
    {
        /// <summary>
        /// 
        /// </summary>
        public int MapSize { get; }

        /// <summary>
        /// 
        /// </summary>
        public int MaximumNumberTurns { get; }

        /// <summary>
        /// 
        /// </summary>
        public int NumberUnits { get; }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="maximumNumberTurns"></param>
        /// <param name="numberUnits"></param>
        protected MapStrategy(int mapSize, int maximumNumberTurns, int numberUnits)
        {
            MapSize = mapSize;
            MaximumNumberTurns = maximumNumberTurns;
            NumberUnits = numberUnits;
        }

        /// <summary>
        /// Gets the initialized map.
        /// </summary>
        /// <returns>The created map.</returns>
        public Map BuildMap()
        {
            return new MapGenerator()
                //.SetGenerator(MapGenerator.StrategyType.Random)
                .GenerateMap(MapSize);
        }
    }
}