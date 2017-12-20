using System;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of the cerberus race.
    /// </summary>
    [Serializable]
    public class Cerberus : Race
    {
        /// <summary>
        /// Constructs a cerberus race with the number of victory points on each tile.
        /// </summary>
        public Cerberus() : base(1, 0, 2, 3) {}

        /// <summary>
        /// Produces a cerberus warrior.
        /// </summary>
        /// <param name="initialPosition">The initial position of the warrior.</param>
        /// <returns>A cerberus warrior.</returns>
        public override Unit ProduceWarrior(Position initialPosition)
        {
            return new CerberusWarrior(initialPosition);
        }
    }
}