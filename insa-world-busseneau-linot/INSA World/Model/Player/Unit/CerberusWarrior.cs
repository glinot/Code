using System;

namespace INSAWorld
{
    /// <summary>
    /// A cerberus warrior.
    /// This object is only created by the race.
    /// </summary>
    [Serializable]
    public class CerberusWarrior : Unit
    {
        /// <summary>
        /// Constructs a cerberus warrior.
        /// </summary>
        /// <param name="initialPosition">The initial unit position.</param>
        public CerberusWarrior(Position initialPosition) : base(10, 6, 4, initialPosition) {}
    }
}
