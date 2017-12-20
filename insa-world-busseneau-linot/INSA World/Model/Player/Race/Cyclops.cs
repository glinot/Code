using System;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of the cyclops race.
    /// </summary>
    [Serializable]
    public class Cyclops : Race
    {
        /// <summary>
        /// Constructs a cylcops race with the number of victory points on each tile.
        /// </summary>
        public Cyclops() : base(3, 2, 1, 0) {}

        /// <summary>
        /// Produces a cyclops warrior.
        /// </summary>
        /// <param name="initialPosition">The initial position of the warrior.</param>
        /// <returns>A cyclops warrior.</returns>
        public override Unit ProduceWarrior(Position initialPosition)
        {
            return new CyclopsWarrior(initialPosition);
        }
    }
}