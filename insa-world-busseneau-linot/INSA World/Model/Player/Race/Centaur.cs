using System;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of the centaur race.
    /// </summary>
    [Serializable]
    public class Centaur : Race
    {
        /// <summary>
        /// Constructs a centaur race with the number of victory points on each tile.
        /// </summary>
        public Centaur() : base(2, 3, 1, 0) {}

        /// <summary>
        /// Produces a centaur warrior.
        /// </summary>
        /// <param name="initialPosition">The initial position of the warrior.</param>
        /// <returns>A centaur warrior</returns>
        public override Unit ProduceWarrior(Position initialPosition)
        {
            return new CentaurWarrior(initialPosition);
        }
    }
}