using System;

namespace INSAWorld
{
    /// <summary>
    /// A centaur warrior.
    /// This object is only created by the race.
    /// </summary>
    [Serializable]
    public class CentaurWarrior : Unit
    {
        /// <summary>
        /// Constructs a centaur warrior.
        /// </summary>
        /// <param name="initialPosition">The initial unit position.</param>
        public CentaurWarrior(Position initialPosition) : base(10, 8, 2, initialPosition) {}

        /// <summary>
        /// Gets the cost of move points on the tile.
        /// </summary>
        /// <param name="map">The map where the unit is placed.</param>
        /// <param name="position">The unit position.</param>
        /// <returns>The cost in move points of the action.</returns>
        /// <exception cref="ArgumentNullException">If the map is null.</exception>
        /// <exception cref="ArgumentNullException">If the position is null.</exception>
        protected override double GetMovePoints(Map map, Position position)
        {
            if (map == null)
                throw new ArgumentNullException("map");
            if (position == null)
                throw new ArgumentNullException("position");

            return map.GetTile(position) is Plain ? 0.5 : 1;
        }
    }
}