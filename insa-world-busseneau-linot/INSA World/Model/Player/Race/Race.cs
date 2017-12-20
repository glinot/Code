using System;

namespace INSAWorld
{
    /// <summary>
    /// Abstract implementation of a race.
    /// This class is a factory to create the race units.
    /// </summary>
    [Serializable]
    public abstract class Race
    {
        /// <summary>
        /// The number of victory points on a desert tile.
        /// </summary>
        public int VictoryPointsOnDesert { get; }

        /// <summary>
        /// The number of victory points on a plain tile.
        /// </summary>
        public int VictoryPointsOnPlain { get; }

        /// <summary>
        /// The number of victory points on a swamp tile.
        /// </summary>
        public int VictoryPointsOnSwamp { get; }

        /// <summary>
        /// The number of victory points on a volcano tile.
        /// </summary>
        public int VictoryPointsOnVolcano { get; }

        /// <summary>
        /// Constructs a race with the number of victory points on the different tiles.
        /// </summary>
        /// <param name="victoryPointsOnDesert">The number of victory points on a desert tile.</param>
        /// <param name="victoryPointsOnPlain">The number of victory points on a plain tile.</param>
        /// <param name="victoryPointsOnSwamp">The number of victory points on a swamp tile.</param>
        /// <param name="victoryPointsOnVolcano">The number of victory points on a volcano tile.</param>
        /// <exception cref="ArgumentOutOfRangeException">If the victory points on desert is negative.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If the victory points on plain is negative.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If the victory points on swamp is negative.</exception>
        /// <exception cref="ArgumentOutOfRangeException">If the victory points on volcano is negative.</exception>
        protected Race(int victoryPointsOnDesert, int victoryPointsOnPlain, int victoryPointsOnSwamp, int victoryPointsOnVolcano)
        {
            if (victoryPointsOnDesert < 0)
                throw new ArgumentOutOfRangeException("Victory points on desert was out of range. Must be non-negative.", "victoryPointsOnDesert");
            if (victoryPointsOnPlain < 0)
                throw new ArgumentOutOfRangeException("Victory points on plain was out of range. Must be non-negative.", "victoryPointsOnPlain");
            if (victoryPointsOnSwamp < 0)
                throw new ArgumentOutOfRangeException("Victory points on swamp was out of range. Must be non-negative.", "victoryPointsOnSwamp");
            if (victoryPointsOnVolcano < 0)
                throw new ArgumentOutOfRangeException("Victory points on volcano was out of range. Must be non-negative.", "victoryPointsOnVolcano");

            VictoryPointsOnDesert = victoryPointsOnDesert;
            VictoryPointsOnPlain = victoryPointsOnPlain;
            VictoryPointsOnSwamp = victoryPointsOnSwamp;
            VictoryPointsOnVolcano = victoryPointsOnVolcano;
        }

        /// <summary>
        /// Computes the number of winned victory points from the unit position.
        /// </summary>
        /// <param name="map">The map where the unit is created.</param>
        /// <param name="unit">The unit to check.</param>
        /// <returns>The number of winned victory points.</returns>
        /// <exception cref="ArgumentNullException">If map is null.</exception>
        /// <exception cref="ArgumentNullException">If unit is null.</exception>
        /// <exception cref="NotImplementedException">If the tile from the unit position is not found.</exception>
        public int ComputeVictoryPoints(Map map, Unit unit)
        {
            if (map == null)
                throw new ArgumentNullException("map");
            if (unit == null)
                throw new ArgumentNullException("unit");

            var tile = map.GetTile(unit.Position);

            if (tile is Desert)
                return VictoryPointsOnDesert;
            else if (tile is Plain)
                return VictoryPointsOnPlain;
            else if (tile is Swamp)
                return VictoryPointsOnSwamp;
            else if (tile is Volcano)
                return VictoryPointsOnVolcano;
            else
                throw new NotImplementedException("This tile is not implemented.");
        }

        /// <summary>
        /// Produces a warrior unit.
        /// </summary>
        /// <param name="initialPosition">The initial position of the warrior.</param>
        /// <returns>The warrior unit.</returns>
        public abstract Unit ProduceWarrior(Position initialPosition);
    }
}